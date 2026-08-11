package wpics.alcogait.ui.screens

import android.Manifest
import android.R.attr.onClick
import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import wpics.alcogait.R
import wpics.alcogait.data.Drinks
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightPink
import wpics.alcogait.util.formatAsReadableDate
import wpics.alcogait.viewmodels.HomeUiState
import wpics.alcogait.viewmodels.HomeViewModel
import wpics.alcogait.viewmodels.LocationUiState
import wpics.alcogait.viewmodels.LocationViewModel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import wpics.alcogait.ui.theme.LightGray

@Composable
fun LocationScreen(
    padding: PaddingValues,
    locationUiState: LocationUiState,
    homeUiState: HomeUiState,
    locationViewModel: LocationViewModel,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState()

    // get information for each user when the screen is first loaded
    LaunchedEffect(homeUiState.currentUser) {
        // get the drinks list for the user from the database
        if (homeUiState.currentUser != null) {
            homeViewModel.getDrinksByUserId(homeUiState.currentUser.userId)
        }

        // check location permissions
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        // store the users coordinates
        if (hasPermission) {
            locationViewModel.fetchCurrentLocation()
        }
    }

    // center the camera on the selected search location
    LaunchedEffect(locationUiState.selectedPlace) {
        val selectedPlace = locationUiState.selectedPlace
        if (selectedPlace != null) {
            val latLng = selectedPlace.latLng
            Log.d("LocationScreen", "Selected place coordinates:$latLng")

            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            )
        }
    }

    // get the place's image each time a new marker is selected
    LaunchedEffect(locationUiState.selectedMarkerPlaceId) {
        locationUiState.selectedMarkerPlaceId?.let { placeId ->
            locationViewModel.getPlaceImage(placeId)
        }
    }


    val focusManager = LocalFocusManager.current

    if (homeUiState.drinksList != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            DrinkLocationMap(
                homeUiState.drinksList,
                locationUiState,
                homeUiState,
                locationViewModel,
                cameraPositionState
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue)
                .padding(padding)
        ) {
            Text(
                text = "No drinks have been recorded for this user yet. Start recording on the " +
                        "home screen to log a drinking occasion.",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 10.dp, end = 10.dp),
                textAlign = TextAlign.Center,
                color = LightPink,
                fontSize = 30.sp
            )
        }
    }
}

@Composable
fun DrinkLocationMap(
    drinksList: List<Drinks>,
    locationUiState: LocationUiState,
    homeUiState: HomeUiState,
    locationViewModel: LocationViewModel,
    cameraPositionState: CameraPositionState
) {
    var selectedDrink by remember { mutableStateOf<Drinks?>(null) }

    // initially center camera on most recently logged drink
    LaunchedEffect(Unit) {
        val drinks = homeUiState.drinksList
        if (drinks != null) {
            val recentDrinkLatLng = LatLng(
                drinks.last().latitude.toDouble(),
                drinks.last().longitude.toDouble()
            )
            cameraPositionState.position = CameraPosition.fromLatLngZoom(recentDrinkLatLng, 15f)
        }
    }

    // re-center camera whenever a marker is selected
    LaunchedEffect(selectedDrink) {
        if (selectedDrink != null) {
            val selectedDrinkLatLng = LatLng(
                selectedDrink!!.latitude.toDouble(),
                selectedDrink!!.longitude.toDouble()
            )
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(selectedDrinkLatLng, 15f)
            )
        }
    }

    Box(
        modifier = Modifier
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            drinksList.forEach { drink ->
                val userId = homeUiState.currentUser?.userId ?: 0
                val lat = drink.latitude
                val long = drink.longitude

                val drinkLatLng = LatLng(lat.toDouble(), long.toDouble())
                val markerState = remember(drinkLatLng) { MarkerState(position = drinkLatLng) }
                locationViewModel.getNumDrinksAtLocation(userId, lat, long)
                val numDrinksAtLocation = locationUiState.numDrinksAtLocation?.get(Pair(lat, long))

                Marker(
                    state = markerState,
                    title = numDrinksAtLocation.toString().let { "Drank $it times here"},
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.drink_pin_icon),
                    onClick = {
                        selectedDrink = drink
                        true
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = selectedDrink != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it }
            ) + fadeOut()
        ) {
            selectedDrink?.let { drink ->
                LocationPopUp(
                    locationViewModel = locationViewModel,
                    drink = drink,
                    locationUiState = locationUiState,
                    onDismiss = { selectedDrink = null }
                )
            }
        }
    }
}

@Composable
fun LocationPopUp(
    locationViewModel: LocationViewModel,
    drink: Drinks,
    locationUiState: LocationUiState,
    onDismiss: () -> Unit
) {
    LaunchedEffect(drink) {
        locationViewModel.getTimeAndPlaceOfDrinksAtLocation(
            drink.userId,
            drink.latitude,
            drink.longitude
        )
        locationViewModel.getAddressOrNameFromCoordinates(
            drink.latitude,
            drink.longitude
        )
    }

    val timeAndPlaceOfDrinks = locationUiState.timeAndPlaceOfDrinks
    val address = locationUiState.address
    val displayName = locationUiState.displayName

    Box(
        modifier = Modifier.size(width = 360.dp, height = 190.dp)
    ) {
        // popup body
        Box(
            modifier = Modifier
                .size(350.dp, height = 180.dp)
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
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // try for display name header first, fallback to address
                    if (displayName != null) {
                        Text(
                            text = displayName,
                            color = DarkBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else if (address != null) {
                        Text(
                            text = address.substringBefore(","),
                            color = DarkBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = LightGray.copy(alpha = 0.4f)
                    )

                    // location drinking history
                    Text(
                        text = "History",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        if (timeAndPlaceOfDrinks.isNullOrEmpty()) {
                            Text(
                                text = "No history yet",
                                color = LightGray,
                                fontSize = 13.sp
                            )
                        } else {
                            timeAndPlaceOfDrinks.takeLast(3).forEach { drink ->
                                val date = formatAsReadableDate(drink.first)
                                val drunkState =
                                    drink.second?.lowercase()?.replaceFirstChar { it.titlecase() }
                                Text(
                                    text = "$date: $drunkState",
                                    color = DarkBlue,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // image or placeholder
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightGray.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    val uri = locationUiState.placePhotoUri
                    if (uri != null) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Place photo",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = LightGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // close button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-10).dp, y = (-10).dp)
                .size(30.dp)
                .clickable(onClick = onDismiss)
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 4.dp,
                        spread = 0.dp,
                        color = Color.Black.copy(alpha = 0.4f),
                        offset = DpOffset(x = 0.dp, y = 2.dp)
                    )
                )
                .dropShadow(
                    shape = CircleShape,
                    shadow = Shadow(
                        radius = 13.dp,
                        spread = (-3).dp,
                        color = Color.Black.copy(alpha = 0.3f),
                        offset = DpOffset(x = 0.dp, y = 7.dp)
                    )
                )
                .clip(CircleShape)
                .background(Color.White, CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = LightGray,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Center)
            )
        }
    }
}