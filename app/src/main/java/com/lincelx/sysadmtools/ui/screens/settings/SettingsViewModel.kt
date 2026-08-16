package com.lincelx.sysadmtools.ui.screens.settings

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lincelx.sysadmtools.data.model.AppSettings
import com.lincelx.sysadmtools.data.repository.SettingsRepository
import com.lincelx.sysadmtools.util.AlarmPermissionHelper
import com.lincelx.sysadmtools.util.SettingsSideEffects

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val appContext: Context,
) : ViewModel() {
    var settings by mutableStateOf(repository.getSettings())
        private set

    var showPermissionDialog by mutableStateOf(false)
        private set

    init {
        SettingsSideEffects.apply(appContext, settings)
        maybeShowInitialPermissionDialog()
    }

    fun updateSettings(newSettings: AppSettings) {
        repository.updateSettings { newSettings }
        settings = newSettings
        SettingsSideEffects.apply(appContext, newSettings)
        if ((newSettings.morningReminderEnabled || newSettings.persistentNotificationEnabled) && 
            shouldShowPermissionDialog()) {
            showPermissionDialog = true
        }
    }

    fun reapplySideEffects() {
        SettingsSideEffects.apply(appContext, settings)
    }

    fun confirmPermissionRequest() {
        markPermissionPromptShown()
        showPermissionDialog = false
        // The actual request will be handled by the Activity via launcher
    }

    fun dismissPermissionDialog() {
        markPermissionPromptShown()
        showPermissionDialog = false
    }

    private fun maybeShowInitialPermissionDialog() {
        if (settings.exactAlarmPromptShown) return // Reusing the same flag for simplicity
        if (shouldShowPermissionDialog()) {
            showPermissionDialog = true
        } else {
            markPermissionPromptShown()
        }
    }

    private fun shouldShowPermissionDialog(): Boolean {
        val needsAlarm = AlarmPermissionHelper.needsExactAlarmPermission(appContext)
        val needsNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                appContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        return needsAlarm || needsNotifications
    }

    private fun markPermissionPromptShown() {
        if (settings.exactAlarmPromptShown) return
        val updated = settings.copy(exactAlarmPromptShown = true)
        repository.updateSettings { updated }
        settings = updated
    }

    class Factory(
        private val repository: SettingsRepository,
        private val context: Context,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(repository, context.applicationContext) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
