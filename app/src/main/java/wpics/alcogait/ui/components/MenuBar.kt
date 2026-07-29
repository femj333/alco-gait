package wpics.alcogait.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import wpics.alcogait.ui.theme.LightPink

@Composable
fun MenuBar(){
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        Text(
            text = "Home",
            color = LightPink,
        )
        Text(
            text = "Location",
            color = LightPink
        )
        Text(
            text = "Time",
            color = LightPink
        )
        Text(
            text = "Rideshare",
            color = LightPink
        )
        Text(
            text = "Display Character: On",
            color = LightPink
        )
    }

}