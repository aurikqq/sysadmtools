package com.lincelx.sysadmtools.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import com.lincelx.sysadmtools.util.SettingsSideEffects

class MorningReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        SettingsSideEffects.ensureNotificationChannels(context)

        val notification = NotificationCompat.Builder(context, "morning_reminder_channel")
            .setContentTitle("Доброе утро")
            .setContentText("Ваши клиенты на сегодня - в приложении")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(MORNING_NOTIFICATION_ID, notification)

        val settings = SharedPreferencesManager(context).settings
        if (settings.morningReminderEnabled) {
            SettingsSideEffects.scheduleMorningReminder(context, settings.morningReminderTime)
        }
    }

    companion object {
        private const val MORNING_NOTIFICATION_ID = 2
    }
}
