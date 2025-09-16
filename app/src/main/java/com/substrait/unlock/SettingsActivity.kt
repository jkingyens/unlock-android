package com.substrait.unlock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.substrait.unlock.data.SettingsManager
import com.substrait.unlock.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets
        applyWindowInsets()

        setupToolbar()
        setupThemeSelector()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply horizontal and bottom padding to the root view
            view.setPadding(insets.left, 0, insets.right, insets.bottom)
            // Apply top padding to the toolbar
            binding.toolbar.setPadding(0, insets.top, 0, 0)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupThemeSelector() {
        binding.themeRadioGroup.check(when (settingsManager.themeSetting) {
            AppCompatDelegate.MODE_NIGHT_NO -> R.id.radio_light
            AppCompatDelegate.MODE_NIGHT_YES -> R.id.radio_dark
            else -> R.id.radio_system
        })

        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newTheme = when (checkedId) {
                R.id.radio_light -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.radio_dark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            settingsManager.themeSetting = newTheme
            AppCompatDelegate.setDefaultNightMode(newTheme)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}