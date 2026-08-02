package wpics.alcogait.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import wpics.alcogait.Routes
import wpics.alcogait.ui.components.MenuBar
import wpics.alcogait.ui.components.TopBar

@Composable
fun MainScreen() {
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
                navController = navController
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
                    if (currentRoute == "Location") { /* might have to change to use Routes, check after back on wifi */
                        TopBar(
                            onMenuClick = { menuBarVisible = !menuBarVisible },
                            isLocationScreen = true
                        )
                    } else {
                        TopBar(
                            onMenuClick = { menuBarVisible = !menuBarVisible },
                            isLocationScreen = false
                        )
                    }
                }
            ) { padding ->
                NavHost(navController = navController, startDestination = Routes.Home.route) {
                    composable(Routes.Home.route) {
                        HomeScreen(displayCharacter = displayCharacter, padding = padding)
                    }

                    composable(Routes.Time.route) {
                        TimeScreen(padding)
                    }

                    composable(Routes.Profile.route) {
                        ProfileScreen(padding)
                    }

                    composable(Routes.Location.route) {
                        LocationScreen(padding)
                    }

                    composale(Routes.Rideshare.route) {
                        RideshareScreen(padding)
                    }
                }
            }
        }
    }
}