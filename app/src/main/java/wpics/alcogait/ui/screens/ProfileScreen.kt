package wpics.alcogait.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wpics.alcogait.R
import wpics.alcogait.ui.components.ProfileIcon
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightGray
import wpics.alcogait.ui.theme.LightPink
import wpics.alcogait.viewmodels.HomeUiState
import wpics.alcogait.viewmodels.LocationUiState
import wpics.alcogait.viewmodels.LocationViewModel

@Composable
fun ProfileScreen(
    padding: PaddingValues,
    homeUiState: HomeUiState,
    locationViewModel: LocationViewModel
) {
    var numTipsy by remember { mutableIntStateOf(0) }
    var numDrunk by remember { mutableIntStateOf(0) }
    var numWasted by remember { mutableIntStateOf(0) }

    LaunchedEffect(homeUiState.currentUser?.userId) {
        val userId = homeUiState.currentUser?.userId ?: return@LaunchedEffect

        numTipsy = locationViewModel.getNumDrinksByDrunkState(userId, "TIPSY")
        numDrunk = locationViewModel.getNumDrinksByDrunkState(userId, "DRUNK")
        numWasted = locationViewModel.getNumDrinksByDrunkState(userId, "WASTED")
        Log.d("Profile Screen", "Tipsy: $numTipsy, Drunk: $numDrunk, Wasted: $numWasted")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(padding)
    ) {
        // first name header
        homeUiState.currentUser?.firstName?.let {
            Text(
                text = it,
                color = LightPink,
                fontSize = 40.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }

        // background
        GrayBackground()

        // profile icon
        Box(
            modifier = Modifier
                .clickable(onClick = { /* TODO -> allow user to edit profile pic */})
                .align(Alignment.TopCenter)
                .offset(y = 90.dp)
        ) {
            ProfileIcon(circleSize = 120, iconSize = 90)

            // edit icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(LightGray, CircleShape)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit_icon),
                    contentDescription = "Profile Icon",
                    tint = DarkBlue,
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.BottomCenter)
                .background(LightGray, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionHeader("Drinking Statistics")

            Box(modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(LightGray, RoundedCornerShape(10.dp))
                        .border(width = 2.dp, color = DarkBlue, shape = RoundedCornerShape(10.dp))
                        .align(Alignment.Center)
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGray, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            DrinkingStats("Tipsy", DarkBlue, numTipsy)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkBlue)
                                .padding(10.dp)
                        ) {
                            DrinkingStats("Drunk", LightGray, numDrunk)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(LightGray, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            DrinkingStats("Wasted", DarkBlue, numWasted)
                        }
                    }
                }
            }

            SectionHeader("Rideshare")

            SectionHeader("Contact Information")

            SectionHeader("Notifications")
        }
    }
}

@Composable
fun DrinkingStats(
    drunkState: String,
    textColor: Color,
    numTimes: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = drunkState,
            color = textColor,
            fontSize = 20.sp
        )

        Text(
            text = "${numTimes}x",
            color = textColor,
            fontSize = 20.sp
        )

    }
}

@Composable
fun SectionHeader(
    title: String
) {
    Text(
        text = title,
        color = DarkBlue,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    )
}

@Composable
fun GrayBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .background(LightGray)
                .align(Alignment.BottomCenter)
        ) {
            Canvas(modifier = Modifier
                .size(450.dp, 150.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-150).dp)
            ) {
                drawArc(
                    color = LightGray,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    size = Size(size.width, size.height * 2)
                )
            }
        }
    }
}