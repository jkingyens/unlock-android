// file: app/src/main/java/com/substrait/unlock/MainActivity.kt

package com.substrait.unlock

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import com.substrait.unlock.data.PacketContent
import com.substrait.unlock.databinding.ActivityMainBinding
import com.substrait.unlock.ui.PacketSidebarAdapter
import android.content.Intent // <-- Add this import at the top


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var sidebarAdapter: PacketSidebarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            binding.navView.getHeaderView(0).setPadding(0, insets.top, 0, 0)
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
        binding.navView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = sidebarAdapter
        }
    }

    private fun setupWebView() {
        binding.appBarMain.contentMain.webView.webViewClient = WebViewClient()
        binding.appBarMain.contentMain.webView.settings.javaScriptEnabled = true
    }

    private fun observeViewModel() {
        viewModel.currentPacket.observe(this) { packet ->
            packet?.let {
                supportActionBar?.title = it.title
                sidebarAdapter.submitList(it.sourceContent)
                if (it.sourceContent.isNotEmpty()) {
                    viewModel.setCurrentContent(it.sourceContent[0])
                }
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