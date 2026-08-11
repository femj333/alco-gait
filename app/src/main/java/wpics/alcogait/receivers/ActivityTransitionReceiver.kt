package wpics.alcogait.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import wpics.alcogait.data.WalkTrackingEvent
import wpics.alcogait.data.WalkTrackingEvents

class ActivityTransitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!ActivityTransitionResult.hasResult(intent)) return

        val result = ActivityTransitionResult.extractResult(intent) ?: return

        for (event in result.transitionEvents) {
            if (event.activityType != DetectedActivity.WALKING) continue

            when (event.transitionType) {
                ActivityTransition.ACTIVITY_TRANSITION_ENTER ->
                    WalkTrackingEvents.emit(WalkTrackingEvent.STARTED_WALKING)
                ActivityTransition.ACTIVITY_TRANSITION_EXIT ->
                    WalkTrackingEvents.emit(WalkTrackingEvent.STOPPED_WALKING)
            }
        }
    }
}