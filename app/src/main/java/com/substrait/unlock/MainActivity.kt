package com.substrait.unlock

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.substrait.unlock.data.PacketContent
import com.substrait.unlock.databinding.ActivityMainBinding
import com.substrait.unlock.ui.PacketSidebarAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var sidebarAdapter: PacketSidebarAdapter
    private lateinit var navHeaderTitle: TextView
    private lateinit var btnDeletePacket: Button

    private val qrCodeScanner = registerForActivityResult(ScanContract()) { result ->
        result.contents?.let { url ->
            viewModel.loadPacketFromUrl(url)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize drawer views
        navHeaderTitle = binding.navView.findViewById(R.id.nav_header_title)
        btnDeletePacket = binding.navView.findViewById(R.id.btn_delete_packet)
        btnDeletePacket.setOnClickListener {
            viewModel.closePacket()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Initialize empty state buttons
        binding.appBarMain.contentMain.root.findViewById<Button>(R.id.btn_load_from_url).setOnClickListener {
            showUrlInputDialog()
        }
        binding.appBarMain.contentMain.root.findViewById<Button>(R.id.btn_scan_qr).setOnClickListener {
            launchQrScanner()
        }

        applyWindowInsets()
        setGestureExclusion()
        setupToolbarAndDrawer()
        setupSidebar()
        setupWebView()
        observeViewModel()
        handleBackPress()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(insets.left, 0, insets.right, 0)
            binding.appBarMain.root.setPadding(0, insets.top, 0, 0)
            binding.navView.setPadding(0, insets.top, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setGestureExclusion() {
        binding.drawerLayout.doOnPreDraw { view ->
            val exclusionRect = Rect(0, 0, view.width / 12, view.height)
            ViewCompat.setSystemGestureExclusionRects(view, listOf(exclusionRect))
        }
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(binding.appBarMain.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupSidebar() {
        sidebarAdapter = PacketSidebarAdapter { packetContent ->
            viewModel.setCurrentContent(packetContent)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.navView.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sidebarAdapter
        }
    }

    private fun setupWebView() {
        binding.appBarMain.contentMain.webView.webViewClient = WebViewClient()
        binding.appBarMain.contentMain.webView.settings.javaScriptEnabled = true
    }

    private fun showUrlInputDialog() {
        val editText = EditText(this).apply {
            hint = "https://..."
        }

        AlertDialog.Builder(this)
            .setTitle("Load Packet from URL")
            .setView(editText)
            .setPositiveButton("Load") { dialog, _ ->
                val url = editText.text.toString()
                if (url.isNotBlank()) {
                    viewModel.loadPacketFromUrl(url)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun launchQrScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan a Packet QR Code")
            setBeepEnabled(true)
            setBarcodeImageEnabled(true)
        }
        qrCodeScanner.launch(options)
    }

    private fun observeViewModel() {
        viewModel.currentPacket.observe(this) { packet ->
            if (packet == null) {
                // EMPTY STATE
                supportActionBar?.title = getString(R.string.app_name)
                navHeaderTitle.text = ""
                sidebarAdapter.submitList(emptyList())
                binding.appBarMain.contentMain.webView.visibility = View.GONE
                binding.appBarMain.contentMain.root.findViewById<View>(R.id.empty_state_container).visibility = View.VISIBLE
                binding.navView.findViewById<View>(R.id.drawer_content_container).visibility = View.GONE
                btnDeletePacket.visibility = View.GONE
            } else {
                // LOADED STATE
                supportActionBar?.title = packet.title
                navHeaderTitle.text = packet.title
                sidebarAdapter.submitList(packet.sourceContent)
                if (packet.sourceContent.isNotEmpty()) {
                    viewModel.setCurrentContent(packet.sourceContent[0])
                }
                binding.appBarMain.contentMain.webView.visibility = View.VISIBLE
                binding.appBarMain.contentMain.root.findViewById<View>(R.id.empty_state_container).visibility = View.GONE
                binding.navView.findViewById<View>(R.id.drawer_content_container).visibility = View.VISIBLE
                btnDeletePacket.visibility = View.VISIBLE
            }
        }

        viewModel.currentContent.observe(this) { content ->
            content?.let {
                binding.appBarMain.contentMain.webView.loadUrl(it.url)
            }
        }
    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}