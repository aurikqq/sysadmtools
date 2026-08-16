package com.lincelx.sysadmtools.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.lincelx.sysadmtools.data.model.AppSettings
import com.lincelx.sysadmtools.receivers.MorningReminderReceiver
import com.lincelx.sysadmtools.services.PersistentNotificationService
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

object SettingsSideEffects {

    private const val MORNING_ALARM_REQUEST_CODE = 1001
    private const val PERSISTENT_CHANNEL_ID = "persistent_channel"
    private const val MORNING_CHANNEL_ID = "morning_reminder_channel"

    fun apply(context: Context, settings: AppSettings) {
        ensureNotificationChannels(context)
        if (settings.morningReminderEnabled) {
            scheduleMorningReminder(context, settings.morningReminderTime)
        } else {
            cancelMorningReminder(context)
        }
        refreshPersistentNotification(context, settings)
    }

    fun refreshPersistentNotification(context: Context, settings: AppSettings) {
        if (settings.persistentNotificationEnabled) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, PersistentNotificationService::class.java),
            )
        } else {
            context.stopService(Intent(context, PersistentNotificationService::class.java))
        }
    }

    fun ensureNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java)

        val persistentChannel = NotificationChannel(
            PERSISTENT_CHANNEL_ID,
            "Постоянное уведомление",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Быстрый доступ к приложению"
        }

        val morningChannel = NotificationChannel(
            MORNING_CHANNEL_ID,
            "Утренние напоминания",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Напоминание проверить расписание визитов"
        }

        manager.createNotificationChannel(persistentChannel)
        manager.createNotificationChannel(morningChannel)
    }

    fun scheduleMorningReminder(context: Context, time: String) {
        val parsedTime = parseTime(time) ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = morningReminderPendingIntent(context)
        val triggerAtMillis = nextTriggerMillis(parsedTime)

        if (AlarmPermissionHelper.canScheduleExactAlarms(context)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

    fun cancelMorningReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(morningReminderPendingIntent(context))
    }

    fun isWithinWorkHours(settings: AppSettings, date: LocalDate = LocalDate.now()): Boolean {
        val now = LocalTime.now()
        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
        val startTime = parseTime(
            if (isWeekend) settings.workHoursStartWeekend else settings.workHoursStartWeekday,
        ) ?: return false
        val endTime = parseTime(
            if (isWeekend) settings.workHoursEndWeekend else settings.workHoursEndWeekday,
        ) ?: return false
        return !now.isBefore(startTime) && now.isBefore(endTime)
    }

    fun workHoursLabel(settings: AppSettings, date: LocalDate = LocalDate.now()): String {
        val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
        val start = if (isWeekend) settings.workHoursStartWeekend else settings.workHoursStartWeekday
        val end = if (isWeekend) settings.workHoursEndWeekend else settings.workHoursEndWeekday
        return "$start–$end"
    }

    private fun nextTriggerMillis(parsedTime: LocalTime): Long {
        return Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.HOUR_OF_DAY, parsedTime.hour)
            set(Calendar.MINUTE, parsedTime.minute)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }.timeInMillis
    }

    private fun morningReminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MorningReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            MORNING_ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun parseTime(time: String): LocalTime? {
        val trimmed = time.trim()
        for (pattern in listOf("H:mm", "HH:mm")) {
            runCatching {
                return LocalTime.parse(trimmed, DateTimeFormatter.ofPattern(pattern))
            }
        }
        return null
    }
}
