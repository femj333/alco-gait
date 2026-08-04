package wpics.alcogait.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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
import wpics.alcogait.AlcoGaitApp
import wpics.alcogait.data.GaitAnalyzer
import wpics.alcogait.data.User
import wpics.alcogait.data.Walk
import wpics.alcogait.data.WalkCSVWriter
import wpics.alcogait.data.WalkRepository
import wpics.alcogait.data.WalkSensorRecorder
import java.io.File

class HomeViewModel(
    application: Application,
    private val walkRepository: WalkRepository
) : AndroidViewModel(application) {

    companion object {
        // 20 second recordings
        private const val WINDOW_DURATION_MS = 20_000L
        // stopwatch tick delay
        private const val STOPWATCH_TICK_MS = 100L

        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AlcoGaitApp)
                HomeViewModel(
                    application = application,
                    walkRepository = application.container.walkRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val recorder = WalkSensorRecorder(application)
    private val analyzer = GaitAnalyzer()

    private var windowJob: Job? = null
    private var completedWindows = 0

    private var stopwatchJob: Job? = null
    private var recordingStartTime: Long = 0L

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
        val walk = Walk()
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
                val drunkState = analyzer.computeDrunkStateAndReset()
                completedWindows++
                if (drunkState != null) {
                    _uiState.update {
                        it.copy(
                            drunkState = drunkState
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
        val completedWalk = _uiState.value.currentWalk

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

            completedWalk?.let { saveWalk(it) }
        }
    }

    fun dismissMinRecordingAlert() {
        _uiState.update { it.copy(showMinRecordingAlert = false) }
    }

    private fun saveWalk(walk: Walk) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = WalkCSVWriter.write(walk, rootFolder.absolutePath)
            withContext(Dispatchers.Main) {
                if (!success) {
                    _uiState.update {
                        it.copy(saveError = true)
                        /* TODO -> do something on save error */
                    }
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