package wpics.alcogait.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wpics.alcogait.models.DrunkState

object RecordingStateRepository {
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _elapsedMillis = MutableStateFlow(0L)
    val elapsedMillis = _elapsedMillis.asStateFlow()

    private val _drunkState = MutableStateFlow(DrunkState())
    val drunkState = _drunkState.asStateFlow()

    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
    }

    fun setElapsed(ms: Long) {
        _elapsedMillis.value = ms
    }

    fun setDrunkState(state: DrunkState) {
        _drunkState.value = state
    }

    fun reset() {
        _isRecording.value = false
        _elapsedMillis.value = 0L
        _drunkState.value = DrunkState()
    }
}