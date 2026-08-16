package com.lincelx.sysadmtools.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.lincelx.sysadmtools.R
import com.lincelx.sysadmtools.data.prefs.SharedPreferencesManager
import com.lincelx.sysadmtools.util.SettingsSideEffects
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class PersistentNotificationService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            showPersistentNotification()
            handler.postDelayed(this, 60000) // Refresh every minute
        }
    }

    override fun onCreate() {
        super.onCreate()
        handler.post(refreshRunnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showPersistentNotification()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(refreshRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showPersistentNotification() {
        SettingsSideEffects.ensureNotificationChannels(this)

        val prefs = SharedPreferencesManager(this)
        val today = LocalDate.now()
        val now = LocalTime.now()
        
        val todayVisits = prefs.getVisitsForDate(today)
        val clients = prefs.clients

        val nextTimedVisit = todayVisits
            .filter { it.time != null && it.time.isAfter(now) }
            .minByOrNull { it.time!! }

        val untimedVisits = todayVisits
            .filter { it.time == null }

        val nextVisitText = if (nextTimedVisit != null) {
            val clientName = clients.find { it.id == nextTimedVisit.clientId }?.name ?: "???"
            "Следующий визит: $clientName в ${nextTimedVisit.time?.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        } else {
            "Следующий визит: Нет"
        }

        val untimedText = if (untimedVisits.isNotEmpty()) {
            val names = untimedVisits.map { visit ->
                clients.find { it.id == visit.clientId }?.name ?: "???"
            }.joinToString(", ")
            "Визиты без времени: $names"
        } else {
            "Визиты без времени: Нет"
        }

        val notificationContent = "$nextVisitText\n$untimedText"

        val notification = NotificationCompat.Builder(this, "persistent_channel")
            .setContentText(notificationContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
