package wpics.alcogait.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class WalkTrackingEvent { STARTED_WALKING, STOPPED_WALKING }

object WalkTrackingEvents {
    private val _events = MutableSharedFlow<WalkTrackingEvent>(extraBufferCapacity = 4)
    val events = _events.asSharedFlow()

    fun emit(event: WalkTrackingEvent) {
        _events.tryEmit(event)
    }
}