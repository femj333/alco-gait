package wpics.alcogait.viewmodels

import android.app.Application
import android.location.Location
import android.net.Uri
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
import com.google.android.libraries.places.api.net.FetchResolvedPhotoUriRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import wpics.alcogait.BuildConfig
import com.google.android.libraries.places.api.model.AuthorAttributions
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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
     * Gets the address or display name from the given coordinates and updates the UI state
     *
     * @param latitude the latitude of the coordinates
     * @param longitude the longitude of the coordinates
     */
    fun getAddressOrNameFromCoordinates(latitude: Float, longitude: Float) {
        viewModelScope.launch {
            /*
            _uiState.update {
                it.copy(
                    locationPopUpLoading = true,
                    errorMessage = null
                )
            }

             */

            try {
                val response = RetrofitInstance.geocodingService.reverseGeocode(
                    latlng = "$latitude,$longitude",
                    apiKey = BuildConfig.GEOCODING_API_KEY
                )
                Log.d("Geocoding", "Status=${response.status}, results=${response.results}")

                // store address and place id of response
                val result = response.results.firstOrNull()
                if (result != null) {
                    _uiState.update {
                        it.copy(
                            address = result.formatted_address,
                            selectedMarkerPlaceId = result.place_id
                        )
                    }

                    // get place name
                    val placeFields = listOf(Place.Field.NAME)
                    val request = FetchPlaceRequest.builder(result.place_id, placeFields).build()
                    placesClient.fetchPlace(request)
                        .addOnSuccessListener { response ->
                            _uiState.update {
                                it.copy(
                                    displayName = response.place.name,
                                    /* locationPopUpLoading = false */
                                )
                            }
                        }
                        .addOnFailureListener { e ->
                            _uiState.update {
                                it.copy(
                                    errorMessage = e.message,
                                    /* locationPopUpLoading = false */
                                )
                            }
                        }
                } else {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No address found",
                            /* locationPopUpLoading = false */
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = e.message,
                        /* locationPopUpLoading = false */
                    )
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
                it.copy(
                    numDrinksAtLocation =
                        hashMapOf(Pair(latitude, longitude) to numDrinks)
                )
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
     * Gets the date and drunk state of all logged drinking for a user at a location and updates the UI state
     *
     * @param userId the ID of the user
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     */
    fun getDateAndDrunkStateOfDrinksAtLocation(userId: Long, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    /* locationPopUpLoading = true, */
                    errorMessage = null
                )
            }

            val dateAndStateOfDrinks =
                walkRepository.getDateAndDrunkStateOfDrinksAtLocation(userId, latitude, longitude)
            _uiState.update {
                it.copy(
                    dateAndStateOfDrinks = dateAndStateOfDrinks,
                    /* locationPopUpLoading = false */
                )
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

    /**
     * Gets the image for a place and updates the UI state
     *
     * @param placeId the id of the place to get the image for
     */
    fun getPlaceImage(placeId: String) {
        _uiState.update {
            it.copy(
                /* locationPopUpLoading = true, */
                errorMessage = null
            )
        }

        // fields to include in response
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)

        // get place object
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                // get photo metadata
                val metadata = response.place.photoMetadatas
                if (metadata == null || metadata.isEmpty()) {
                    Log.w("LocationViewModel", "No photo metadata")
                    _uiState.update {
                        it.copy(
                            placePhotoUri = null,
                            placePhotoAttributions = null,
                            placePhotoAuthorAttributions = null,
                            /* locationPopUpLoading = false */
                        )
                    }
                    return@addOnSuccessListener
                }
                val photoMetadata = metadata[0]

                // get attribution text and author attributions
                val attributions = photoMetadata.attributions
                val authorAttributions = photoMetadata.authorAttributions

                // create request
                val request = FetchResolvedPhotoUriRequest.builder(photoMetadata)
                    .setMaxWidth(500)
                    .setMaxHeight(300)
                    .build()

                placesClient.fetchResolvedPhotoUri(request)
                    .addOnSuccessListener { response ->
                        _uiState.update {
                            it.copy(
                                placePhotoUri = response.uri,
                                placePhotoAttributions = attributions,
                                placePhotoAuthorAttributions = authorAttributions,
                                /* locationPopUpLoading = false */
                            )
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("LocationViewModel", "Place not found", e)
                _uiState.update {
                    it.copy(
                        /* locationPopUpLoading = false, */
                        errorMessage = e.message
                    )
                }
            }
    }

    /**
     * Loads the location popup for a user at a location and updates the UI state
     *
     * @param userId the ID of the user
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     */
    fun loadLocationPopup(userId: Long, latitude: Float, longitude: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    locationPopUpLoading = true,
                    errorMessage = null,
                    address = null,
                    displayName = null,
                    dateAndStateOfDrinks = null,
                    selectedMarkerPlaceId = null,
                    placePhotoUri = null,
                    placePhotoAttributions = null,
                    placePhotoAuthorAttributions = null
                )
            }

            try {
                val popupData = loadLocationPopupData(userId, latitude, longitude)
                _uiState.update {
                    it.copy(
                        dateAndStateOfDrinks = popupData.dateAndStateOfDrinks,
                        address = popupData.address,
                        displayName = popupData.displayName,
                        selectedMarkerPlaceId = popupData.placeId,
                        placePhotoUri = popupData.placePhotoUri,
                        placePhotoAttributions = popupData.placePhotoAttributions,
                        placePhotoAuthorAttributions = popupData.placePhotoAuthorAttributions,
                        locationPopUpLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("LocationViewModel", "Error loading location popup", e)
                _uiState.update {
                    it.copy(
                        errorMessage = e.message,
                        locationPopUpLoading = false
                    )
                }
            }
        }
    }

    private data class LocationPopupData(
        val dateAndStateOfDrinks: List<Pair<String, String?>>,
        val address: String?,
        val displayName: String?,
        val placeId: String?,
        val placePhotoUri: Uri?,
        val placePhotoAttributions: String?,
        val placePhotoAuthorAttributions: AuthorAttributions?
    )

    /**
     * Loads the location popup data for a user at a location, waiting for all data to be loaded
     *
     * @param userId the ID of the user
     * @param latitude the latitude of the location
     * @param longitude the longitude of the location
     */
    private suspend fun loadLocationPopupData(
        userId: Long,
        latitude: Float,
        longitude: Float
    ): LocationPopupData = coroutineScope {
        // wait for all data to be loaded
        val drinksDeferred = async {
            walkRepository.getDateAndDrunkStateOfDrinksAtLocation(userId, latitude, longitude)
        }
        val geocodeDeferred = async {
            RetrofitInstance.geocodingService.reverseGeocode(
                latlng = "$latitude,$longitude",
                apiKey = BuildConfig.GEOCODING_API_KEY
            )
        }

        val dateAndStateOfDrinks = drinksDeferred.await()
        val geocodeResponse = geocodeDeferred.await()
        Log.d("Geocoding", "Status=${geocodeResponse.status}, results=${geocodeResponse.results}")

        val result = geocodeResponse.results.firstOrNull()
        val address = result?.formatted_address
        val placeId = result?.place_id
        val displayName = placeId?.let { fetchPlaceName(it) }
        val placePhoto = placeId?.let { fetchPlacePhoto(it) }

        LocationPopupData(
            dateAndStateOfDrinks = dateAndStateOfDrinks,
            address = address,
            displayName = displayName,
            placeId = placeId,
            placePhotoUri = placePhoto?.uri,
            placePhotoAttributions = placePhoto?.attributions,
            placePhotoAuthorAttributions = placePhoto?.authorAttributions
        )
    }

    /**
     * Gets the name of a place from its ID
     *
     * @param placeId the ID of the place
     */
    private suspend fun fetchPlaceName(placeId: String): String? = try {
        val placeFields = listOf(Place.Field.NAME)

        suspendCancellableCoroutine { continuation ->
            val request = FetchPlaceRequest
                .builder(placeId, placeFields)
                .build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    continuation.resume(response.place.name)
                }
                .addOnFailureListener { error ->
                    continuation.resumeWithException(error)
                }
        }
    } catch (e: Exception) {
        Log.w("LocationViewModel", "Unable to fetch place name", e)
        null
    }

    private data class PlacePhotoResult(
        val uri: Uri?,
        val attributions: String?,
        val authorAttributions: AuthorAttributions?
    )

    /**
     * Gets the photo of a place from its ID
     *
     * @param placeId the ID of the place
     */
    private suspend fun fetchPlacePhoto(placeId: String): PlacePhotoResult? = try {
        val placeFields = listOf(Place.Field.PHOTO_METADATAS)
        val placeResponse = suspendCancellableCoroutine { continuation ->
            val request = FetchPlaceRequest
                .builder(placeId, placeFields)
                .build()
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    continuation.resume(response)
                }
                .addOnFailureListener { error ->
                    continuation.resumeWithException(error)
                }
        }

        val photoMetadata = placeResponse.place.photoMetadatas?.firstOrNull()
            ?: return PlacePhotoResult(null, null, null)

        val photoResponse = suspendCancellableCoroutine { continuation ->
            val request = FetchResolvedPhotoUriRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(300)
                .build()
            placesClient.fetchResolvedPhotoUri(request)
                .addOnSuccessListener { response ->
                    continuation.resume(response)
                }
                .addOnFailureListener { error ->
                    continuation.resumeWithException(error)
                }
        }

        PlacePhotoResult(
            uri = photoResponse.uri,
            attributions = photoMetadata.attributions,
            authorAttributions = photoMetadata.authorAttributions
        )
    } catch (e: Exception) {
        Log.w("LocationViewModel", "Unable to fetch place photo", e)
        null
    }
}