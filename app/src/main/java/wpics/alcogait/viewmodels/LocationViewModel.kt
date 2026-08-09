package wpics.alcogait.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
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
     * Searches for places based on a query and updates the UI state
     *
     * @param searchQuery the query to search for
     * @param latitude the latitude to search near
     * @param longitude the longitude to search near
     * /* TODO -> determine if search area is really needed */
     */
    fun findPlaces(searchQuery: String, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // fields to include in the response for each returned place
            val placeFields = listOf(Place.Field.NAME)

            // search area
            val center = LatLng(latitude.toDouble(), longitude.toDouble())
            val circle = CircularBounds.newInstance(center, 1000.0)

            // request
            val searchByTextRequest = SearchByTextRequest.builder(searchQuery, placeFields)
                .setMaxResultCount(5)
                .setLocationRestriction(circle)
                .build()

            // perform search
            placesClient.searchByText(searchByTextRequest)
                .addOnSuccessListener { response ->
                    val placesList = response.places

                    if (placesList.isNotEmpty()) {
                        _uiState.update {
                            it.copy(placesSearchList = placesList, isLoading = false)
                        }
                    } else {
                        _uiState.update {
                            it.copy(errorMessage = "No places found", isLoading = false)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    _uiState.update {
                        it.copy(errorMessage = e.message, isLoading = false)
                    }
                }
        }
    }
}