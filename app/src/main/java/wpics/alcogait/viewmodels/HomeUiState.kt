package wpics.alcogait.viewmodels

import wpics.alcogait.R
import wpics.alcogait.data.Walk

data class HomeUiState(
    val isRecording: Boolean = false,
    val drunkStateText: String = "SOBER",
    val drunkStateImage: Int = R.drawable.sober,
    val currentWalk: Walk? = null,
    val saveError: Boolean = false,
    val showMinRecordingAlert: Boolean = false
)