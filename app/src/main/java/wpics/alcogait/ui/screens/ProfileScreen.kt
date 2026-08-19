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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
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
    var enablePushNotifications by remember { mutableStateOf(true) }
    var enableEmailNotifications by remember { mutableStateOf(true) }

    LaunchedEffect(homeUiState.currentUser?.userId) {
        val userId = homeUiState.currentUser?.userId ?: return@LaunchedEffect

        numTipsy = locationViewModel.getNumDrinksByDrunkState(userId, "TIPSY")
        numDrunk = locationViewModel.getNumDrinksByDrunkState(userId, "DRUNK")
        numWasted = locationViewModel.getNumDrinksByDrunkState(userId, "WASTED")
        Log.d("Profile Screen", "Tipsy: $numTipsy, Drunk: $numDrunk, Wasted: $numWasted")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBlue)
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
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

            // background arc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
                    .background(LightGray)
                    .align(Alignment.BottomCenter)
            ) {
                Canvas(modifier = Modifier
                    .size(450.dp, height = 150.dp)

                    .offset(y = (-45).dp)
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

            // profile icon
            Box(
                modifier = Modifier
                    .clickable(onClick = { /* TODO -> allow user to edit profile pic */ })
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
        }

        // profile information section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    LightGray,

                )
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionHeader("Drinking Statistics")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .background(LightGray, RoundedCornerShape(10.dp))
                        .border(
                            width = 2.dp,
                            color = DarkBlue,
                            shape = RoundedCornerShape(10.dp)
                        )
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

            ColumnSection(
                height = 110,
                textContent1 = {
                    Text(
                        text = "Saved Locations",
                        color = LightPink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                textContent2 = {
                    Text(
                        text = "Add New Saved Location",
                        color = LightPink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )

            SectionHeader("Contact Information")

            ColumnSection(
                height = 130,
                textContent1 = {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 1.dp,
                            alignment = Alignment.CenterVertically
                        )
                    ) {
                        Text(
                            text = "Email",
                            color = LightPink,
                            fontSize = 15.sp
                        )

                        Text(
                            text = homeUiState.currentUser?.email ?: "Add email",
                            color = LightPink,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                textContent2 = {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 1.dp,
                            alignment = Alignment.CenterVertically
                        ),
                    ) {
                        Text(
                            text = "Phone Number",
                            color = LightPink,
                            fontSize = 15.sp
                        )

                        Text(
                            text = homeUiState.currentUser?.phoneNumber ?: "Add phone number",
                            color = LightPink,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

            )

            SectionHeader("Notifications")

            Toggle(
                text = "Push",
                checked = enablePushNotifications,
                onCheckedChange = { enablePushNotifications = it }
            )

            Toggle(
                text = "Email",
                checked = enableEmailNotifications,
                onCheckedChange = { enableEmailNotifications = it }
            )
        }
    }
}

@Composable
fun Toggle(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = LightPink,
                uncheckedThumbColor = Color.Black,
                uncheckedTrackColor = DarkBlue
            )
        )

        Text(
            text = text,
            color = LightPink,
            fontSize = 22.sp
        )
    }
}

@Composable
fun ColumnSection(
    height: Int,
    textContent1: @Composable () -> Unit,
    textContent2: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .background(DarkBlue, RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ClickableSection(
                    onClick = {/* TODO */},
                    textContent = { textContent1() },
                    modifier = Modifier.weight(5f)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.99f)
                        .weight(0.5f),
                    color = LightPink
                )

                ClickableSection(
                    onClick = {/* TODO */},
                    textContent = { textContent2() },
                    modifier = Modifier.weight(5f)
                )
            }
        }
    }
}

@Composable
fun ClickableSection(
    onClick: () -> Unit,
    textContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick() }),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        textContent()

        // arrow icon
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow Icon",
            tint = LightPink,
            modifier = Modifier.size(35.dp)
        )
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
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .background(Color.Yellow)
    ) {

    }
}