package wpics.alcogait.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import wpics.alcogait.R
import wpics.alcogait.ui.components.TopBar
import wpics.alcogait.ui.theme.DarkBlue

@Composable
fun HomeScreen() {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopBar()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ){
            Image(
                modifier = Modifier,
                painter = rememberDrawablePainter(
                    drawable = getDrawable(
                        LocalContext.current,
                        R.drawable.sober
                    )
                ),
                contentDescription = "Sober character animation",
                contentScale = ContentScale.FillWidth,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = DarkBlue)
            )

        }
    }
}