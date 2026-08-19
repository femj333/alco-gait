package wpics.alcogait.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import wpics.alcogait.R
import wpics.alcogait.models.DrunkState

object NotificationHelper {
    const val SERVICE_CHANNEL_ID = "walk_tracking_service"
    const val ALERT_CHANNEL_ID = "drunk_state_alerts"
    const val SERVICE_NOTIFICATION_ID = 1001
    private const val ALERT_NOTIFICATION_ID_BASE  = 2000

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Walk tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Shown while AlcoGait is monitoring your walk" }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                ALERT_CHANNEL_ID,
                "Drunk state alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Alerts when drunk state changes" }
        )
    }

    fun buildServiceNotification(context: Context) =
        NotificationCompat.Builder(context, SERVICE_CHANNEL_ID)
            .setContentTitle("AlcoGait is monitoring your walk")
            .setSmallIcon(R.drawable.alarm_icon)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    fun notifyDrunkStateChanged(context: Context, state: DrunkState) {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
            .setContentTitle("State updates: ${state.label}")
            .setContentText("AlcoGait detected a change in your gait pattern")
            .setSmallIcon(R.drawable.alarm_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(ALERT_NOTIFICATION_ID_BASE, notification)
    }
}