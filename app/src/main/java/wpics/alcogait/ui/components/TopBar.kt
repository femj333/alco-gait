package wpics.alcogait.ui.components

import androidx.compose.animation.AnimatedContent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wpics.alcogait.R
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightBlue
import wpics.alcogait.ui.theme.LightGray
import wpics.alcogait.ui.theme.LightPink

@Composable
fun TopBar(
    onMenuClick: () -> Unit,
    isLocationScreen: Boolean
) {
    Box(
        modifier = Modifier
            .background(color = DarkBlue)
            .fillMaxWidth()
    ) {
        if (isLocationScreen) {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MenuButtonIcon(
                    onMenuClick = onMenuClick,
                    barColors = DarkBlue
                )

                SearchBar()

                HelpButton()
            }
        } else {
            MenuButton(
                onMenuClick = onMenuClick,
                barColors = LightPink
            )
        }
    }
}

@Composable
fun MenuButton(
    onMenuClick: () -> Unit,
    barColors: Color
) {
    Button(
        onClick = { onMenuClick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkBlue
        ),
        modifier = Modifier
    ) {
        MenuBars(barColors = barColors)
    }
}

@Composable
fun MenuButtonIcon(
    onMenuClick: () -> Unit,
    barColors: Color
) {
    ButtonTwo(
        onClick = { onMenuClick() },
        size = 20
    ) {
        MenuBars(barColors = barColors)
    }
}

@Composable
fun MenuBars(
    barColors: Color
) {
    Column(
        modifier = Modifier
            .background(color = DarkBlue)
            .fillMaxWidth(0.08f),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ){
        repeat(3) {
            HorizontalDivider(
                thickness = 2.dp,
                color = barColors
            )
        }
    }
}

@Composable
fun SearchBar() {
    Row(
        modifier = Modifier
            .size(width = 200.dp, height = 50.dp)
            .dropShadow(
                shape = RoundedCornerShape(20.dp),
                shadow = Shadow(
                    radius = 4.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = DpOffset(x = 0.dp, y = 2.dp)
                )
            )
            .dropShadow(
                shape = RoundedCornerShape(20.dp),
                shadow = Shadow(
                    radius = 13.dp,
                    spread = (-3).dp,
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = DpOffset(x = 0.dp, y = 7.dp)
                )
            )
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = Color.White)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.search_icon),
            contentDescription = "Search Icon",
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = "Find location",
            color = LightGray,
            fontSize = 25.sp
        )
    }
}

@Composable
fun HelpButton() {
    ButtonTwo(
        onClick = { /* TODO */ },
        size = 20
    ) {
        Icon(
            painter = painterResource(id = R.drawable.question_mark_icon),
            contentDescription = "Question Mark Icon",
            modifier = Modifier.size(20.dp)
        )
    }
}

