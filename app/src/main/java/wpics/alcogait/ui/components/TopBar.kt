package wpics.alcogait.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightBlue
import wpics.alcogait.ui.theme.LightPink

@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .background(color = DarkBlue)
            .fillMaxWidth()
    ) {
        Button(
            onClick = { /*TODO -> open menu bar */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue
            ),
            modifier = Modifier
        ) {
            MenuBar()
        }
    }
}

@Composable
fun MenuBar() {
    Column(
        modifier = Modifier
            .background(color = DarkBlue)
            .fillMaxWidth(0.08f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ){
        HorizontalDivider(
            thickness = 2.dp,
            color = LightPink
        )

        HorizontalDivider(
            thickness = 2.dp,
            color = LightPink
        )

        HorizontalDivider(
            thickness = 2.dp,
            color = LightPink
        )
    }
}