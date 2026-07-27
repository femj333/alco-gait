package wpics.alcogait.ui

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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopBar()
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // background layer
            Background(stateBackground = uiState.drunkStateImage)

            // drunk state sign
            /* TODO -> change state based on readings */
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 35.dp)
            ) {
                DrunkStateSign(stateText = uiState.drunkStateText)
            }

            Row(
                modifier = Modifier
                    .offset(x = 25.dp, y = 180.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ){
                // uber button
                GenericButton(
                    onClick = {/* TODO */},
                    contentPadding = PaddingValues(top = 8.dp)
                ) {
                    UberImage()
                }

                // lyft button
                GenericButton(
                    onClick = {/* TODO */},
                    contentPadding = PaddingValues(top = 4.dp)
                ) {
                    LyftImage()
                }
            }


            // start button
            Box(
                modifier = Modifier
                    .offset(x = 130.dp, y = 720.dp),
            ){
                GenericButton(
                    onClick = { if (uiState.isRecording) viewModel.onStopClicked() else viewModel.onStartClicked() },
                    buttonColor = LightBlue,
                    roundedCornerSize = 10
                ) {
                    Text(
                        text = if (uiState.isRecording) "STOP" else "START",
                        color = LightPink,
                        fontSize = 30.sp,
                    )
                }
            }

        }
    }

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
fun UberImage(){
    Image(
        painter = painterResource(id = R.drawable.uber_logo),
        contentDescription = "Uber logo",
        modifier = Modifier.size(width = 120.dp, height = 20.dp)
    )
}

@Composable
fun LyftImage(){
    Image(
        painter = painterResource(id = R.drawable.lyft_logo),
        contentDescription = "Lyft logo",
        modifier = Modifier.size(width = 150.dp, height = 30.dp)
    )
}

@Composable
fun DrunkStateSign(
    stateText: String
){
    Box(
        modifier = Modifier
            .size(width = 275.dp, height = 110.dp)
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
            verticalArrangement = Arrangement.spacedBy(11.dp),

            ){
            repeat(6) {
                Circle()
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 260.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),

            ){
            repeat(6) {
                Circle()
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),

            ){
            repeat(15) {
                Circle()
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-9).dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),

            ){
            repeat(15) {
                Circle()
            }
        }

        Text(
            text = stateText,
            color = LightPink,
            fontSize = 65.sp,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}


@Composable
fun Circle() {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(LightPink)
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