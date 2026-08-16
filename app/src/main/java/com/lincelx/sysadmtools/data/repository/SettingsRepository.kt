package com.lincelx.sysadmtools.data.repository

import com.lincelx.sysadmtools.data.model.AppSettings
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager

class SettingsRepository(private val prefs: SharedPreferencesManager) {

    fun getSettings(): AppSettings = prefs.settings

    fun updateSettings(updates: AppSettings.() -> AppSettings) {
        prefs.updateSettings(updates)
    }
}
