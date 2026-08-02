package wpics.alcogait

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import wpics.alcogait.ui.HomeScreen
import wpics.alcogait.ui.LocationScreen
import wpics.alcogait.ui.ProfileScreen
import wpics.alcogait.ui.RideshareScreen
import wpics.alcogait.ui.TimeScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Home.route) {
        composable(Routes.Time.route) {
            TimeScreen(navController = navController)
        }
        composable(Routes.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Routes.Location.route) {
            LocationScreen(navController = navController)
        }
        composale(Routes.Rideshare.route) {
            RideshareScreen(navController = navController)
        }

    }
}