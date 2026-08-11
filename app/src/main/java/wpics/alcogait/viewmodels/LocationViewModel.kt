package wpics.alcogait.viewmodels

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import wpics.alcogait.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import wpics.alcogait.AlcoGaitApp
import wpics.alcogait.data.LocationHelper

import wpics.alcogait.data.RetrofitInstance
import wpics.alcogait.data.User
import wpics.alcogait.data.WalkRepository
import java.util.Arrays

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

    private val placesClient = Places.createClient(getApplication())

    private var sessionToken = AutocompleteSessionToken.newInstance()

    private var locationHelper = LocationHelper(application)
    private var recordingLocation: Location? = null

    /**
     * Gets the address from the given coordinates and updates the UI state
     *
     * @param latitude the latitude of the coordinates
     * @param longitude the longitude of the coordinates
     */
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

    /**
     * Gets the number of times a user has logged drinking at a location and updates the UI state
     *
     * @param userId the ID of the user
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     */
    fun getNumDrinksAtLocation(userId: Long, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val numDrinks = walkRepository.getNumDrinksAtLocation(userId, latitude, longitude)
            _uiState.update {
                it.copy(numDrinksAtLocation = hashMapOf(Pair(latitude, longitude) to numDrinks))
            }
        }
    }

    fun fetchCurrentLocation() {
        viewModelScope.launch {
            recordingLocation = locationHelper.getCurrentLocation()
            _uiState.update {
                it.copy(
                    latitude = recordingLocation?.latitude?.toFloat(),
                    longitude = recordingLocation?.longitude?.toFloat()
                )
            }
        }
    }

    /**
     * Gets the time and place of all logged drinking for a user at a location and updates the UI state
     *
     * @param userId the ID of the user
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     */
    fun getTimeAndPlaceOfDrinksAtLocation(userId: Long, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            val timeAndPlaceOfDrinks =
                walkRepository.getTimeAndPlaceOfDrinksAtLocation(userId, latitude, longitude)
            _uiState.update {
                it.copy(timeAndPlaceOfDrinks = timeAndPlaceOfDrinks)
            }
        }
    }

    /**
     * Gets autocomplete predictions based on the query. Cheap enough to call on every keystroke
     *
     * @param searchQuery the query to search for
     * @param latitude the latitude to search near
     * @param longitude the longitude to search near
     */
    fun findPlacePredictions(searchQuery: String, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            if (searchQuery.isBlank()) {
                _uiState.update {
                    it.copy(
                        placePredictions = emptyList()
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            // search area bias
            val center = LatLng(latitude.toDouble(), longitude.toDouble())
            val bounds = LatLngBounds.builder()
                .include(LatLng(
                    center.latitude - 0.05,
                    center.longitude - 0.05
                ))
                .include(LatLng(
                    center.latitude + 0.05,
                    center.longitude + 0.05
                ))
                .build()

            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(searchQuery)
                .setLocationBias(RectangularBounds.newInstance(bounds))
                .setSessionToken(sessionToken)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    Log.d("Autocomplete", "Found ${response.autocompletePredictions.size} predictions")
                    _uiState.update {
                        it.copy(
                            placePredictions = response.autocompletePredictions,
                            isLoading = false
                        )
                    }
                    for (prediction in response.autocompletePredictions) {
                        Log.d("Autocomplete", "Prediction: ${prediction.getFullText(null)}")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Autocomplete", "Error finding predictions", e)
                    _uiState.update {
                        it.copy(
                            errorMessage = e.message,
                            isLoading = false,
                            placePredictions = emptyList()
                        )
                    }
                }
        }
    }

    /**
     * Selects a place from the list of autocomplete predictions
     *
     * @param placeId the ID of the place to select
     */
    fun selectPlace(placeId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            // fields to include in the response for each returned place
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )

            val request = FetchPlaceRequest.builder(placeId, placeFields)
                .setSessionToken(sessionToken)
                .build()

            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    _uiState.update {
                        it.copy(
                            selectedPlace = response.place,
                            isLoading = false
                        )
                    }
                    Log.d("LocationViewModel: selectPlace", "Selected place: ${response.place}")
                    // start a new session
                    sessionToken = AutocompleteSessionToken.newInstance()
                }
                .addOnFailureListener { e ->
                    _uiState.update {
                        it.copy(
                            errorMessage = e.message,
                            isLoading = false
                        )
                    }
                    Log.e("LocationViewModel: selectPlace", "Error selecting place", e)
                }
        }
    }
}