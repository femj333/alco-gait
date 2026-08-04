package wpics.alcogait.viewmodels

import wpics.alcogait.R
import wpics.alcogait.data.User
import wpics.alcogait.data.Walk
import wpics.alcogait.models.DrunkState

data class HomeUiState(
    val isRecording: Boolean = false,
    val drunkState: DrunkState = DrunkState(),
    val currentWalk: Walk? = null,
    val saveError: Boolean = false,
    val showMinRecordingAlert: Boolean = false,
    val elapsedMillis: Long = 0L,
    val currentUser: User? = null
)