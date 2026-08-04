package wpics.alcogait.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import wpics.alcogait.data.Drinks
import wpics.alcogait.viewmodels.HomeUiState

@Composable
fun LocationScreen(
    padding: PaddingValues,
    uiState: HomeUiState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        /* TODO ->
            1. add scrollable google maps as background
            2. save previous drinking locations in database -> need drinking permissions
            3. access database to display drink icons with number of times drank at a certain location
            4. add pop up to display info about a location
        */
        if (uiState.drinksList != null) {
            DrinkLocationMap(uiState.drinksList)
        } else {
            Text("No drinks have been recorded for this user yet, start recording on the home screen to log a drinking occasion")
        }

    }
}

@Composable
fun DrinkLocationMap(
    drinksList: List<Drinks>
) {
    // initial camera positioning
    val firstDrinkLatLng = LatLng(drinksList.first().latitude.toDouble(), drinksList.first().longitude.toDouble())
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(firstDrinkLatLng, 15f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        drinksList.forEach { drink ->

            val drinkLatLng = LatLng(drink.latitude.toDouble(), drink.longitude.toDouble())
            val markerState = remember(drinkLatLng) { MarkerState(position = drinkLatLng) }

            Marker(
                state = markerState,
                title = "Drink Logged Here"
            )
        }
    }
}