package wpics.alcogait.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import wpics.alcogait.R
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightGray
import wpics.alcogait.ui.theme.LightPink

@Composable
fun ProfileIcon(
    circleSize: Int = 60,
    iconSize: Int = 50
) {
    Box(
        modifier = Modifier
            .size(circleSize.dp)
            .background(color = DarkBlue, shape = CircleShape)
            .border(width = 2.dp, color = LightGray, shape = CircleShape)
    ){
        Icon(
            painter = painterResource(id = R.drawable.profile_mask),
            contentDescription = "Profile Icon",
            tint = LightPink,
            modifier = Modifier
                .size(iconSize.dp)
                .align(Alignment.Center)
        )
    }
}