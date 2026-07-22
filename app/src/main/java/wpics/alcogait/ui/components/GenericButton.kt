package wpics.alcogait.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import wpics.alcogait.R
import wpics.alcogait.ui.theme.LightGray
import kotlin.math.round

@Composable
fun GenericButton(
    onClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    buttonColor: Color = LightGray,
    roundedCornerSize: Int = 50,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 170.dp, height = 40.dp)
            .clickable( onClick = onClick)
            .dropShadow(
                shape = RoundedCornerShape(roundedCornerSize.dp),
                shadow = Shadow(
                    radius = 4.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = DpOffset(x = 0.dp, y = 2.dp)
                )
            )
            .dropShadow(
                shape = RoundedCornerShape(roundedCornerSize.dp),
                shadow = Shadow(
                    radius = 13.dp,
                    spread = (-3).dp,
                    color = Color.Black.copy(alpha = 0.3f),
                    offset = DpOffset(x = 0.dp, y = 7.dp)
                )
            )
            .clip(RoundedCornerShape(roundedCornerSize.dp))
            .background(buttonColor, RoundedCornerShape(roundedCornerSize.dp))
            .innerShadow(
                shape = RoundedCornerShape(roundedCornerSize.dp),
                shadow = Shadow(
                    radius = 0.dp,
                    color = Color.Black.copy(alpha = 0.2f),
                    offset = DpOffset(x = 0.dp, (-3).dp)
                )
            )
            .padding(contentPadding),
        contentAlignment = Alignment.TopCenter
    ){
        content()
    }
}