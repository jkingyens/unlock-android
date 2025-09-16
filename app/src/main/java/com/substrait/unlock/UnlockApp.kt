package com.substrait.unlock

import android.app.Application
import com.substrait.unlock.data.SettingsManager

class UnlockApp : Application() {

    lateinit var settingsManager: SettingsManager
        private set

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        settingsManager.applyTheme()
    }
}