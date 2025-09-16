package com.substrait.unlock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.substrait.unlock.data.SettingsManager
import com.substrait.unlock.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsManager = SettingsManager(this)

        setupToolbar()
        setupThemeSelector()
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