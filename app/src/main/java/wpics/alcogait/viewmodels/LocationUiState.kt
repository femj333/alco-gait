package wpics.alcogait.viewmodels

import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place

data class LocationUiState(
    val numDrinksAtLocation: HashMap<Pair<Float, Float>, Int>? = null,
    val timeAndPlaceOfDrinks: List<Pair<String, String?>>? = null,
    val placePredictions: List<AutocompletePrediction> = emptyList(),
    val selectedPlace: Place? = null,
    val address: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)