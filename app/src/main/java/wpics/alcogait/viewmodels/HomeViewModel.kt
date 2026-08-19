package wpics.alcogait.viewmodels

import android.Manifest
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wpics.alcogait.AlcoGaitApp
import wpics.alcogait.data.UserPreferences
import wpics.alcogait.data.WalkRepository
import wpics.alcogait.models.DrunkState
import wpics.alcogait.receivers.ActivityTransitionReceiver
import wpics.alcogait.repository.RecordingStateRepository
import wpics.alcogait.service.WalkTrackingService

class HomeViewModel(
    application: Application,
    private val walkRepository: WalkRepository
) : AndroidViewModel(application) {

    companion object {
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

    private var userPreferences = UserPreferences(application)

    private val myPendingIntent: PendingIntent by lazy {
        val intent = Intent(getApplication(), ActivityTransitionReceiver::class.java)
        PendingIntent.getBroadcast(
            getApplication(),
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    init {
        viewModelScope.launch {
            var wasRecording = false
            RecordingStateRepository.isRecording.collect { recording ->
                _uiState.update {
                    it.copy(
                        isRecording = recording
                    )
                }
                if (!recording && wasRecording) {
                    uiState.value.currentUser?.userId?.let { getDrinksByUserId(it) }
                }
                wasRecording = recording
            }
        }

        viewModelScope.launch {
            RecordingStateRepository.drunkState.collect { drunkState ->
                _uiState.update {
                    it.copy(
                        drunkState = drunkState
                    )
                }
            }
        }

        viewModelScope.launch {
            RecordingStateRepository.elapsedMillis.collect { elapsed ->
                _uiState.update {
                    it.copy(
                        elapsedMillis = elapsed
                    )
                }
            }
        }
    }

    fun onStartClicked() {
        val context = getApplication<AlcoGaitApp>()
        val intent = Intent(context, WalkTrackingService::class.java)
            .setAction(WalkTrackingService.ACTION_START)
            .putExtra("userId", uiState.value.currentUser?.userId ?: 0L)
        ContextCompat.startForegroundService(context, intent)
    }

    fun onStopClicked() {
        val context = getApplication<AlcoGaitApp>()
        val intent = Intent(context, WalkTrackingService::class.java)
            .setAction(WalkTrackingService.ACTION_STOP)
        context.startService(intent)
    }

    fun dismissMinRecordingAlert() {
        _uiState.update { it.copy(showMinRecordingAlert = false) }
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

            if (user == null) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Username already exists"
                    )
                }
            } else {
                Log.d("HomeViewModel", "Current user: $user")
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        errorMessage = null
                    )
                }

                userPreferences.saveLastUserId(user.userId.toString())
            }
        }
    }

    fun loginUser(
        username: String,
        password: CharArray
    ) {
        viewModelScope.launch {
            val user = walkRepository.login(username, password)

            if (user == null) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Incorrect username or password"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        errorMessage = null
                    )
                }

                userPreferences.saveLastUserId(user.userId.toString())
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

            userPreferences.clearLastUserId()
        }
    }

    fun tryAutoLogin(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            Log.d("AutoLogin", "started")
            val savedUserId = userPreferences.lastUserId.first()
            Log.d("AutoLogin", "saved user id: $savedUserId")

            if (savedUserId != null) {
                val user = walkRepository.getUserById(savedUserId.toLong())
                Log.d("AutoLogin", "User: $user")
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            currentUser = user
                        )
                    }
                    onResult(true)
                    return@launch
                }
                onResult(false)
            }
            onResult(false)
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

    fun saveCharacterDisplay(display: Boolean) {
        viewModelScope.launch {
            userPreferences.saveLastCharacterDisplay(display)
        }
    }

    fun getCharacterDisplay(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val display = userPreferences.lastCharacterDisplay.first() ?: true
            if (display) onResult(true) else onResult(false)
            return@launch
        }
    }

    fun saveLastMusicPreference(music: Boolean) {
        viewModelScope.launch {
            userPreferences.saveLastMusicPreference(music)
        }
    }

    fun getMusicPreference(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val display = userPreferences.lastMusicPreference.first() ?: true
            if (display) onResult(true) else onResult(false)
            return@launch
        }
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun startWalkTracking() {
        val transitions = mutableListOf<ActivityTransition>()

        // transition for when user starts walking
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        // transition for when user stops walking
        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        // register activity for transition updates
        val request = ActivityTransitionRequest(transitions)
        ActivityRecognition.getClient(getApplication())
            .requestActivityTransitionUpdates(request, myPendingIntent)
            .addOnSuccessListener {
                Log.d("HomeViewModel", "Activity transition updates registered")
            }
            .addOnFailureListener {e: Exception ->
                Log.e("HomeViewModel", "Activity transition updates failed",e)
            }
    }
}