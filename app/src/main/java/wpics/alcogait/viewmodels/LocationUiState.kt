package wpics.alcogait.viewmodels

import wpics.alcogait.data.Drinks
import wpics.alcogait.data.User

data class LocationUiState(
    val numDrinksAtLocation: HashMap<Pair<Float, Float>, Int>? = null,
    val timeAndPlaceOfDrinks: List<Pair<String, String?>>? = null,
    val address: String? = null,
    val latitude: Float? = null,
    val longitude: Float? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)