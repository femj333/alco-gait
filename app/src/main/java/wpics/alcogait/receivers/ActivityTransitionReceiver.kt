package wpics.alcogait.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import wpics.alcogait.data.UserPreferences
import wpics.alcogait.service.WalkTrackingService

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        for (event in result.transitionEvents) {
            if (event.activityType != DetectedActivity.WALKING) continue

            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    val pending = goAsync()

                    CoroutineScope(Dispatchers.IO).launch {
                        val userId = UserPreferences(context).lastUserId.first()?.toLongOrNull() ?: 0L
                        val serviceIntent = Intent(context, WalkTrackingService::class.java)
                            .setAction(WalkTrackingService.ACTION_START)
                            .putExtra(WalkTrackingService.EXTRA_USER_ID, userId)
                        ContextCompat.startForegroundService(context, serviceIntent)
                        pending.finish()
                    }
                }

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {
                    val serviceIntent = Intent(context, WalkTrackingService::class.java)
                        .setAction(WalkTrackingService.ACTION_STOP)
                    context.startService(serviceIntent)
                }
            }
        }
    }
}