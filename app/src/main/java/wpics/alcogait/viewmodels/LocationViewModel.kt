package wpics.alcogait.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import wpics.alcogait.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wpics.alcogait.AlcoGaitApp

import wpics.alcogait.data.RetrofitInstance
import wpics.alcogait.data.User
import wpics.alcogait.data.WalkRepository

class LocationViewModel(
    application: Application,
    private val walkRepository: WalkRepository
) : AndroidViewModel(application) {
    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AlcoGaitApp)
                LocationViewModel(
                    application = application,
                    walkRepository = application.container.walkRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun getAddressFromCoordinates(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = RetrofitInstance.geocodingService.reverseGeocode(
                    latlng = "$latitude,$longitude",
                    apiKey = BuildConfig.GEOCODING_API_KEY
                )
                Log.d("Geocoding", "Status=${response.status}, results=${response.results}")
                val address = response.results.firstOrNull()?.formatted_address
                if (address != null) {
                    _uiState.update {
                        it.copy(address = address, isLoading = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = "No address found", isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message, isLoading = false)
                }
            }
        }
    }

    fun getNumDrinksAtLocation(userId: Long, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val numDrinks = walkRepository.getNumDrinksAtLocation(userId, latitude, longitude)
            _uiState.update {
                it.copy(numDrinksAtLocation = hashMapOf(Pair(latitude, longitude) to numDrinks))
            }
        }
    }

    fun getTimeAndPlaceOfDrinksAtLocation(userId: Long, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val timeAndPlaceOfDrinks =
                walkRepository.getTimeAndPlaceOfDrinksAtLocation(userId, latitude, longitude)
            _uiState.update {
                it.copy(timeAndPlaceOfDrinks = timeAndPlaceOfDrinks)
            }
        }
    }
}