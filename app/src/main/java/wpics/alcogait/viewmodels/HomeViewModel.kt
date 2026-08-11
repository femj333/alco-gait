package wpics.alcogait.viewmodels

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import wpics.alcogait.data.Drinks
import wpics.alcogait.data.GaitAnalyzer
import wpics.alcogait.data.LocationHelper
import wpics.alcogait.data.User
import wpics.alcogait.data.Walk
import wpics.alcogait.data.WalkCSVWriter
import wpics.alcogait.data.WalkRepository
import wpics.alcogait.data.WalkSensorRecorder
import wpics.alcogait.models.DrunkState
import java.io.File
import java.time.Instant
import kotlin.Boolean

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

    private var locationHelper = LocationHelper(application)
    private var recordingLocation: Location? = null

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

        viewModelScope.launch {
            recordingLocation = locationHelper.getCurrentLocation()
            Log.d("HomeViewModel", "Location: ${recordingLocation?.latitude}, Longitude: ${recordingLocation?.longitude}")
        }

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
        val timestamp = Instant.ofEpochMilli(recordingStartTime).toString()

        viewModelScope.launch{
            // write to csv
            val success = withContext(Dispatchers.IO) {
                WalkCSVWriter.write(walk, rootFolder.absolutePath)
            }

            if (success) {
                if (uiState.value.currentUser != null) {
                    Log.d("HomeViewModel", "User ID: ${uiState.value.currentUser?.userId}")
                } else {
                    Log.d("HomeViewModel", "User ID: null")
                }
                val userId = uiState.value.currentUser?.userId ?: 0

                // save in database
                walkRepository.logDrink(
                    Drinks(
                        userId = userId,
                        latitude = recordingLocation?.latitude?.toFloat() ?: 0f,
                        longitude = recordingLocation?.longitude?.toFloat() ?: 0f,
                        timestamp = timestamp,
                        drunkState = uiState.value.drunkState.label
                    )
                )
                _uiState.update {
                    it.copy(drinksList = walkRepository.getDrinksByUserId(userId))
                }

            } else {
                _uiState.update {
                    it.copy(saveError = true)
                    /* TODO -> do something on save error */
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

    fun registerUser(
        username: String,
        password: CharArray,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            val user = walkRepository.register(
                username,
                password,
                firstName,
                lastName,
                email,
                phoneNumber
            )
            _uiState.update {
                it.copy(currentUser = user)
            }

            if (user == null) {
                _uiState.update {
                    it.copy(errorMessage = "Username already exists")
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = null)
                }
            }
        }
    }

    fun loginUser(
        username: String,
        password: CharArray
    ) {
        viewModelScope.launch {
            val user = walkRepository.login(username, password)
            _uiState.update {
                it.copy(currentUser = user)
            }

            if (user == null) {
                _uiState.update {
                    it.copy(errorMessage = "Incorrect username or password")
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = null)
                }
            }
        }
    }

    fun getDrinksByUserId(userId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(drinksList = walkRepository.getDrinksByUserId(userId))
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentUser = null,
                    isRecording = false,
                    drunkState = DrunkState(),
                    currentWalk = null,
                    drinksList = null,
                    saveError= false,
                    showMinRecordingAlert = false,
                    elapsedMillis= 0L,
                    locationScreenLoading = false,
                    errorMessage= null
                )
            }
        }
    }

    fun loadLocationScreen(userId: Long) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    locationScreenLoading = true
                )
            }

            getDrinksByUserId(userId)

            _uiState.update {
                it.copy(
                    locationScreenLoading = false
                )
            }
        }
    }
}