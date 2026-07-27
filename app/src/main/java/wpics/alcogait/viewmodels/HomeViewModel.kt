package wpics.alcogait.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wpics.alcogait.R
import wpics.alcogait.data.GaitAnalyzer
import wpics.alcogait.data.Walk
import wpics.alcogait.data.WalkCSVWriter
import wpics.alcogait.data.WalkHolder
import wpics.alcogait.data.WalkSensorRecorder
import wpics.alcogait.data.WalkType
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        // 20 second recordings
        private const val WINDOW_DURATION_MS = 20_000L
        // stopwatch tick delay
        private const val STOPWATCH_TICK_MS = 100L
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val recorder = WalkSensorRecorder(application)
    private val analyzer = GaitAnalyzer()

    private var windowJob: Job? = null
    private var completedWindows = 0

    private var stopwatchJob: Job? = null
    private var recordingStartTime: Long = 0L

    private var walkHolder = WalkHolder(1)
    private var currentWalkType: WalkType? = WalkType.NORMAL
    private val rootFolder = File(
        application.getExternalFilesDir(null), "AlcoGait/session_${System.currentTimeMillis()}"
    ).apply { mkdirs() }

    init {
        recorder.onAccelerometerSample = { data ->
            val x = data[1].toFloatOrNull()
            val y = data[2].toFloatOrNull()
            val z = data[3].toFloatOrNull()
            if (x!= null && y!= null && z!= null) analyzer.addSample(x, y, z)
        }
    }

    fun onStartClicked() {
        val walkType = currentWalkType ?: return
        val walk = Walk(walkHolder.walkNumber, bac = 0.0, walkType = walkType)
        recorder.startRecording(walk)

        completedWindows = 0
        recordingStartTime = System.currentTimeMillis()

        _uiState.update {
            it.copy(
                isRecording = true,
                currentWalk = walk,
                showMinRecordingAlert = false,
                elapsedMillis = 0L
            )
        }

        startWindowTimer()
        startStopwatch()
    }

    private fun startWindowTimer() {
        windowJob?.cancel()
        windowJob = viewModelScope.launch {
            while (isActive) {
                // wait for window to complete
                delay(WINDOW_DURATION_MS)
                // compute drunk state
                val pair = analyzer.computeDrunkStateAndReset()
                completedWindows++
                if (pair != null) {
                    _uiState.update {
                        it.copy(
                            drunkStateText = pair.first,
                            drunkStateImage = pair.second
                        )
                    }
                }
            }
        }
    }

    private fun startStopwatch() {
        stopwatchJob?.cancel()
        stopwatchJob = viewModelScope.launch {
            while (isActive) {
                // get current elapsed time
                val elapsed = System.currentTimeMillis() - recordingStartTime
                _uiState.update { it.copy(elapsedMillis = elapsed) }
                delay(STOPWATCH_TICK_MS)
            }
        }
    }

    fun onStopClicked() {
        windowJob?.cancel()
        windowJob = null
        stopwatchJob?.cancel()
        stopwatchJob = null
        recorder.stopRecording()

        // persist this walk, advance to next walk type
        val walk = uiState.value.currentWalk
        if (currentWalkType != null && walk != null){
            walkHolder = walkHolder.addWalk(walk)
        }

        // under 20 seconds recorded, not enough data
        if (completedWindows == 0) {
            _uiState.update {
                it.copy(
                    isRecording = false,
                    currentWalk = null,
                    showMinRecordingAlert = true,
                    elapsedMillis = 0L
                )
            }
        } else { // at least one full window recorded
            _uiState.update {
                it.copy(
                    isRecording = false,
                    currentWalk = null,
                    elapsedMillis = 0L
                )
            }
        }

        currentWalkType = walkHolder.getNextWalkType()
        // all walk types completed
        if (currentWalkType == null) {
            // save all walks
            saveWalkHolder()
        }
    }

    fun dismissMinRecordingAlert() {
        _uiState.update { it.copy(showMinRecordingAlert = false) }
    }

    private fun saveWalkHolder() {
        val holderToSave = walkHolder
        viewModelScope.launch(Dispatchers.IO) {
            val success = WalkCSVWriter.write(holderToSave, rootFolder.absolutePath)
            withContext(Dispatchers.Main) {
                if (success) {
                    walkHolder = WalkHolder(walkHolder.walkNumber + 1)
                    currentWalkType = walkHolder.getNextWalkType()
                } else {
                    _uiState.update { it.copy(saveError = true) }
                }
            }
        }
    }

    override fun onCleared() {
        windowJob?.cancel()
        stopwatchJob?.cancel()
        recorder.unregisterListeners()
        super.onCleared()
    }
}