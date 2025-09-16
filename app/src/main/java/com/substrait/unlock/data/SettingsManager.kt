package com.substrait.unlock.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("unlock_prefs", Context.MODE_PRIVATE)

    var themeSetting: Int
        get() = prefs.getInt("theme_setting", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = prefs.edit().putInt("theme_setting", value).apply()

    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(themeSetting)
    }
}