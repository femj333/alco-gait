package wpics.alcogait.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import wpics.alcogait.Routes
import wpics.alcogait.ui.components.MenuBar
import wpics.alcogait.ui.components.TopBar
import wpics.alcogait.viewmodels.HomeViewModel
import wpics.alcogait.viewmodels.LocationViewModel

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var menuBarVisible by remember { mutableStateOf(false) }
    var displayCharacter by remember { mutableStateOf(true) }
    var playMusic by remember { mutableStateOf(true) }
    val density = LocalDensity.current

    val menuWidth = 340.dp
    val contentOffsetX by animateDpAsState(
        targetValue = if (menuBarVisible) menuWidth else 0.dp,
        label = "contentOffset"
    )

    val homeUiState by homeViewModel.uiState.collectAsState()
    val locationUiState by locationViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // menu bar pullout
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(menuWidth)
        ) {
            MenuBar(
                displayChecked = displayCharacter,
                onDisplayCheckedChange = { displayCharacter = it },
                musicChecked = playMusic,
                onMusicCheckedChange = { playMusic = it },
                navController = navController,
                uiState = homeUiState,
                viewModel = homeViewModel,
                onCloseMenu = { menuBarVisible = false }
            )
        }

        // main home screen, pushes over when menu bar is open
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    translationX = with(density) { contentOffsetX.toPx() }
                )
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                topBar = {
                    if (currentRoute == "Location") {
                        TopBar(
                            onMenuClick = { menuBarVisible = !menuBarVisible },
                            isLocationScreen = true
                        )
                    } else if (currentRoute == "Login"){
                        TopBar(
                            isLogin = true,
                            navController = navController,
                            route = "Login"
                        )
                    } else if (currentRoute == "Signup") {
                        TopBar(
                            isLogin = true,
                            navController = navController,
                            route = "Signup"
                        )
                    } else {
                        TopBar(
                            onMenuClick = { menuBarVisible = !menuBarVisible }
                        )
                    }
                }
            ) { padding ->
                NavHost(navController = navController, startDestination = Routes.Login.route) {
                    composable(Routes.Home.route) {
                        HomeScreen(
                            displayCharacter = displayCharacter,
                            padding = padding,
                            uiState = homeUiState,
                            viewModel = homeViewModel
                        )
                    }

                    composable(Routes.Time.route) {
                        TimeScreen(padding)
                    }

                    composable(Routes.Profile.route) {
                        ProfileScreen(padding)
                    }

                    composable(Routes.Location.route) {
                        LocationScreen(padding, locationUiState, homeUiState, locationViewModel, homeViewModel)
                    }

                    composable(Routes.Rideshare.route) {
                        RideshareScreen(padding)
                    }

                    composable(Routes.Login.route) {
                        LoginScreen(padding, homeViewModel, navController, homeUiState)
                    }

                    composable(Routes.Signup.route) {
                        SignupScreen(homeViewModel, navController, homeUiState, padding)
                    }
                }
            }
        }
    }
}