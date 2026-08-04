package wpics.alcogait.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LocationScreen(
    padding: PaddingValues
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
        Text(text = "Location screen")
    }
}