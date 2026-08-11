package wpics.alcogait.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.delay
import wpics.alcogait.R
import wpics.alcogait.Routes
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightGray
import wpics.alcogait.ui.theme.LightPink
import wpics.alcogait.viewmodels.LocationUiState
import wpics.alcogait.viewmodels.LocationViewModel

@Composable
fun TopBar(
    onMenuClick: () -> Unit = {},
    isLocationScreen: Boolean = false,
    isLogin: Boolean = false,
    navController: NavHostController? = null,
    route: String = "",
    locationUiState: LocationUiState? = null,
    locationViewModel: LocationViewModel? = null
) {
    val backgroundColor = if (isLocationScreen) Color.Transparent else DarkBlue

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        if (isLocationScreen) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp, end = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(space = 30.dp)
            ) {
                MenuButtonIcon(
                    onMenuClick = onMenuClick,
                    barColors = DarkBlue
                )

                SearchBar(
                    predictionsList = locationUiState!!.placePredictions,
                    onQueryChanged = { query ->
                        val lat = locationUiState.latitude
                        val long = locationUiState.longitude
                        Log.d("SearchBar", "Query=$query, lat=$lat, long=$long")
                        if (lat != null && long != null) {
                            locationViewModel!!.findPlacePredictions(query, lat, long)
                        }
                    },
                    onPredictionSelected = { prediction ->
                        locationViewModel?.selectPlace(prediction.placeId)
                    }
                )

                HelpButton()
            }
        } else if (isLogin) {
            LoginSignupHeader(navController, route)
        } else {
            MenuButton(
                onMenuClick = onMenuClick,
                barColors = LightPink
            )
        }
    }
}

@Composable
fun LoginSignupHeader(
    navController: NavHostController?,
    route: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBlue),
        horizontalArrangement = Arrangement
            .spacedBy(space = 10.dp, alignment = Alignment.CenterHorizontally)
    ) {
        // login screen
        Button(
            onClick = {
                navController?.navigate(Routes.Login.route)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (route == "Login") LightPink else DarkBlue,
                contentColor = if (route == "Login") DarkBlue else LightPink
            ),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(2.dp, LightPink)
        ) {
            Text(text = "Login")
        }

        // signup screen
        Button(
            onClick = {
                navController?.navigate(Routes.Signup.route)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (route == "Signup") LightPink else DarkBlue,
                contentColor = if (route == "Signup") DarkBlue else LightPink
            ),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(2.dp, LightPink)
        ) {
            Text(text = "Sign Up")
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
        size = 40,
        contentPadding = PaddingValues(10.dp)
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
            .width(25.dp),
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
fun SearchBar(
    predictionsList: List<AutocompletePrediction>,
    onQueryChanged: (String) -> Unit,
    onPredictionSelected: (AutocompletePrediction) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var popupClicked by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val density = LocalDensity.current

    // live searching
    LaunchedEffect(searchQuery) {
        delay(300)
        onQueryChanged(searchQuery)
    }

    Box {
        Row(
            modifier = Modifier
                .size(width = 250.dp, height = 40.dp)
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
                .padding(start = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = "Search Icon",
                modifier = Modifier
                    .size(30.dp)
            )

            BasicTextField(
                value = searchQuery,
                onValueChange = { newText ->
                    searchQuery = newText
                    popupClicked = false
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = 20.sp, color = DarkBlue),
                cursorBrush = SolidColor(DarkBlue),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        popupClicked = false
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Find location",
                                fontSize = 16.sp,
                                color = LightGray
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // autocomplete search predictions drop down
        if (predictionsList.isNotEmpty() && !popupClicked) {
            Popup(
                alignment = Alignment.TopStart,
                offset = with(density) {
                    IntOffset(x = 10, y = 44.dp.roundToPx())
                },
                properties = PopupProperties(focusable = false)
            ) {
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    predictionsList.forEach { prediction ->
                        Text(
                            text = prediction.getFullText(null).toString(),
                            color = DarkBlue,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClick = {
                                        searchQuery = prediction.getPrimaryText(null).toString()
                                        onPredictionSelected(prediction)
                                        keyboardController?.hide()
                                        popupClicked = true
                                    }
                                )
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HelpButton() {
    ButtonTwo(
        onClick = { /* TODO */ },
        size = 40,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.question_mark_icon),
            contentDescription = "Question Mark Icon",
            modifier = Modifier.size(30.dp)
        )
    }
}

