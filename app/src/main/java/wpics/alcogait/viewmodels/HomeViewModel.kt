package wpics.alcogait.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wpics.alcogait.data.GaitAnalyzer
import wpics.alcogait.data.Walk
import wpics.alcogait.data.WalkCSVWriter
import wpics.alcogait.data.WalkHolder
import wpics.alcogait.data.WalkSensorRecorder
import wpics.alcogait.data.WalkType
import java.io.File

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val recorder = WalkSensorRecorder(application)
    private val analyzer = GaitAnalyzer()

    private var walkHolder = WalkHolder(1)
    private var currentWalkType: WalkType? = WalkType.NORMAL
    private val rootFolder = File(
        application.getExternalFilesDir(null), "AlcoGait/session_${System.currentTimeMillis()}"
    ).apply { mkdirs() }

    init {
        recorder.onAccelerometerSample = { data ->
            val label = analyzer.ingest(data)
            _uiState.update { it.copy(drunkStateText = label) }
        }
    }

    fun onStartClicked() {
        val walkType = currentWalkType ?: return
        val walk = Walk(walkHolder.walkNumber, bac = 0.0, walkType = walkType)
        recorder.startRecording(walk)
        _uiState.update { it.copy(isRecording = true, currentWalk = walk) }
    }

    fun onStopClicked() {
        recorder.stopRecording()

        // persist this walk, advance to next walk type
        val walk = uiState.value.currentWalk
        if (currentWalkType != null && walk != null){
            walkHolder = walkHolder.addWalk(walk)
        }

        _uiState.update { it.copy(isRecording = false, currentWalk = null) }

        currentWalkType = walkHolder.getNextWalkType()

        if (currentWalkType == null) {
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
    }

    override fun onCleared() {
        recorder.unregisterListeners()
        super.onCleared()
    }
}