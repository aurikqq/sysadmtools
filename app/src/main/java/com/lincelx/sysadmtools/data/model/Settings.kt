package com.lincelx.sysadmtools.data.model

enum class AppTheme(val displayName: String) {
    LIGHT("Светлая"),
    DARK("Тёмная"),
    SYSTEM("Как в системе"),
}

data class AppSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val morningReminderEnabled: Boolean = false,
    val persistentNotificationEnabled: Boolean = false,
    val morningReminderTime: String = "09:00",
    val workHoursStartWeekday: String = "09:00",
    val workHoursEndWeekday: String = "18:00",
    val workHoursStartWeekend: String = "10:00",
    val workHoursEndWeekend: String = "16:00",
    val notificationsPermissionGranted: Boolean = false,
    val exactAlarmPromptShown: Boolean = false,
)
