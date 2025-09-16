package com.substrait.unlock

import android.app.Application
import com.substrait.unlock.data.SettingsManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class UnlockApp : Application() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate() {
        super.onCreate()
        settingsManager.applyTheme()
    }
}