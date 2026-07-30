package wpics.alcogait.ui.components

import androidx.compose.animation.AnimatedContent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightBlue
import wpics.alcogait.ui.theme.LightPink

@Composable
fun TopBar(onMenuClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = DarkBlue)
            .fillMaxWidth()
    ) {
        Button(
            onClick = { onMenuClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue
            ),
            modifier = Modifier
        ) {
            MenuButton()
        }
    }
}

@Composable
fun MenuButton() {
    Column(
        modifier = Modifier
            .background(color = DarkBlue)
            .fillMaxWidth(0.08f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ){
        repeat(3) {
            HorizontalDivider(
                thickness = 2.dp,
                color = LightPink
            )
        }
    }
}

