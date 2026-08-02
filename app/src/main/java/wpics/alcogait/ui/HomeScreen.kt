package wpics.alcogait.ui

import android.hardware.lights.Light
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getDrawable
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import wpics.alcogait.R
import wpics.alcogait.ui.components.GenericButton
import wpics.alcogait.ui.components.TopBar
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightBlue
import wpics.alcogait.ui.theme.LightPink
import wpics.alcogait.viewmodels.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import wpics.alcogait.ui.components.MenuBar
import java.util.Locale
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import wpics.alcogait.ui.components.Circle
import wpics.alcogait.ui.theme.AlcoGaitTheme
import wpics.alcogait.viewmodels.HomeUiState

fun formatElapsed(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    displayCharacter: Boolean,
    padding: PaddingValues
) {
    val uiState by viewModel.uiState.collectAsState()

    if (displayCharacter) {
        CharacterHomeScreen(uiState, viewModel, padding)
    } else {
        NoCharacterHomeScreen(uiState, viewModel, padding)
    }

    // alert when recording is too short
    if (uiState.showMinRecordingAlert) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMinRecordingAlert() },
            title = { Text("Recording Too Short") },
            text = { Text("You must record for at least 20 seconds before stopping") },
            confirmButton =  {
                TextButton(
                    onClick = { viewModel.dismissMinRecordingAlert() }
                )
                { Text("Ok") }
            }
        )
    }
}

@Composable
fun CharacterHomeScreen(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    padding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        // background layer
        Background(stateBackground = uiState.drunkState.image)

        // drunk state sign
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 30.dp)
        ) {
            CharacterDrunkStateSign(
                stateText = uiState.drunkState.label,
                fontSize = uiState.drunkState.fontSize
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 180.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            // uber button
            UberButton(
                buttonWidth = 170,
                buttonHeight = 40,
                logoWidth = 120,
                logoHeight = 20
            )

            // lyft button
            LyftButton(
                buttonWidth = 170,
                buttonHeight = 40,
                logoWidth = 150,
                logoHeight = 30
            )
        }

        // start button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-10).dp)
        ){
            StartButton(uiState, viewModel)
        }

        // stopwatch
        if(uiState.isRecording) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-50).dp)
            ) {
                Text (
                    text = formatElapsed(uiState.elapsedMillis),
                    color = LightPink,
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Composable
fun NoCharacterHomeScreen(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    padding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBlue)
            .padding(padding)
    ) {
        // drunk state sign
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 30.dp)
        ) {
            NoCharacterDrunkStateSign(
                stateText = "YOU ARE ${uiState.drunkState.label}",
                fontSize = 45
            )
        }

        // drink icons
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 160.dp)
        ) {
            Image(
                painter = painterResource(id = uiState.drunkState.noCharacterDrink),
                contentDescription = "Drink icon",
                modifier = Modifier.size(width = 230.dp, height = 150.dp)
            )
        }

        // warning text
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 330.dp)
                .fillMaxWidth(0.75f)
        ) {
            Text(
                text = uiState.drunkState.noCharacterText,
                color = LightPink,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
        }


        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 460.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            // uber button
            UberButton(
                buttonWidth = 220,
                buttonHeight = 60,
                logoWidth = 130,
                logoHeight = 35,
                roundedCornerSize = 20,
            )

            // lyft button
            LyftButton(
                buttonWidth = 220,
                buttonHeight = 60,
                logoWidth = 150,
                logoHeight = 47,
                roundedCornerSize = 20
            )
        }

        // start button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)
        ){
            StartButton(uiState, viewModel)
        }

        // stopwatch
        if(uiState.isRecording) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-65).dp)
            ) {
                Text (
                    text = formatElapsed(uiState.elapsedMillis),
                    color = LightPink,
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Composable
fun UberButton(
    buttonWidth: Int,
    buttonHeight: Int,
    logoWidth: Int,
    logoHeight: Int,
    roundedCornerSize: Int = 50
) {
    GenericButton(
        onClick = {/* TODO */},
        contentPadding = PaddingValues(top = 8.dp),
        roundedCornerSize = roundedCornerSize,
        width = buttonWidth,
        height = buttonHeight
    ) {
        Image(
            painter = painterResource(id = R.drawable.uber_logo),
            contentDescription = "Uber logo",
            modifier = Modifier.size(width = logoWidth.dp, height = logoHeight.dp)
        )
    }
}

@Composable
fun LyftButton(
    buttonWidth: Int,
    buttonHeight: Int,
    logoWidth: Int,
    logoHeight: Int,
    roundedCornerSize: Int = 50
) {
    GenericButton(
        onClick = {/* TODO */},
        contentPadding = PaddingValues(top = 4.dp),
        roundedCornerSize = roundedCornerSize,
        width = buttonWidth,
        height = buttonHeight
    ) {
        Image(
            painter = painterResource(id = R.drawable.lyft_logo),
            contentDescription = "Lyft logo",
            modifier = Modifier.size(width = logoWidth.dp, height = logoHeight.dp)
        )
    }
}

@Composable
fun StartButton(
    uiState: HomeUiState,
    viewModel: HomeViewModel
) {
    GenericButton(
        onClick = { if (uiState.isRecording) viewModel.onStopClicked() else viewModel.onStartClicked() },
        buttonColor = LightBlue,
        roundedCornerSize = 10,
        width = 170,
        height = 40
    ) {
        Text(
            text = if (uiState.isRecording) "STOP" else "START",
            color = LightPink,
            fontSize = 30.sp,
        )
    }
}

@Composable
fun CharacterDrunkStateSign(
    stateText: String,
    fontSize: Int
){
    Box(
    ) {
        // background to block out gif sign
        Box(modifier =
            Modifier
                .background(DarkBlue)
                .size(width = 300.dp, height = 150.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 275.dp, height = 125.dp)
                .dropShadow(
                    shape = RoundedCornerShape(10.dp),
                    shadow = Shadow(
                        radius = 1.dp,
                        spread = 2.dp,
                        color = Color(0x40000000),
                        offset = DpOffset(x = (-2).dp, 8.dp)
                    )
                )
                .clip(RoundedCornerShape(10.dp))
                .background(color = LightBlue)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 8.dp),
                verticalArrangement = Arrangement.spacedBy(9.5.dp),

                ){
                repeat(7) {
                    Circle()
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-8).dp),
                verticalArrangement = Arrangement.spacedBy(9.5.dp),

                ){
                repeat(7) {
                    Circle()
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(9.5.dp),

                ){
                repeat(15) {
                    Circle()
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-11).dp),
                horizontalArrangement = Arrangement.spacedBy(9.5.dp),

                ){
                repeat(15) {
                    Circle()
                }
            }

            Text(
                text = stateText,
                color = LightPink,
                fontSize = fontSize.sp,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun NoCharacterDrunkStateSign(
    stateText: String,
    fontSize: Int
) {
    Box(
        modifier = Modifier
            .size(width = 350.dp, height = 140.dp)
            .dropShadow(
                shape = RoundedCornerShape(10.dp),
                shadow = Shadow(
                    radius = 1.dp,
                    spread = 2.dp,
                    color = Color(0x40000000),
                    offset = DpOffset(x = (-2).dp, 8.dp)
                )
            )
            .clip(RoundedCornerShape(10.dp))
            .background(color = LightBlue)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),

            ){
            repeat(7) {
                Circle()
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-8).dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),

            ){
            repeat(7) {
                Circle()
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 11.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),

            ){
            repeat(15) {
                Circle()
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-11).dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),

            ){
            repeat(15) {
                Circle()
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Text(
                text = stateText,
                color = LightPink,
                textAlign = TextAlign.Center,
                fontSize = fontSize.sp,
                modifier = Modifier.width(250.dp),
                style = LocalTextStyle.current.merge(
                    TextStyle(lineHeight = 1.em)
                )
            )
        }
    }

}

@Composable
fun Circle() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(color = LightPink)
    )
}

@Composable
fun Background(stateBackground: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        Image(
            modifier = Modifier,
            painter = rememberDrawablePainter(
                drawable = getDrawable(
                    LocalContext.current,
                    stateBackground
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