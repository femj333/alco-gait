package wpics.alcogait.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        Column() {
            Text(text = "Location screen")

            if (uiState.drinksList != null) {
                uiState.drinksList.forEach { drink ->
                    Text("User ID: ${drink.userId}")
                    Text("Location: ${drink.latitude}, ${drink.longitude}")
                    Text("Timestamp: ${drink.timestamp}")
                    Text("Drunk State: ${drink.drunkState}")
                    Text("---------------------------------")
                }
            } else {
                Text("No drinks have been recorded for this user yet, start recording on the home screen to log a drinking occasion")
            }
        }

    }
}