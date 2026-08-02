package wpics.alcogait

sealed class Routes(val route: String) {
    object Home : Routes("Home")
    object Location : Routes("Location")
    object Profile : Routes("Profile")
    object Rideshare : Routes("Rideshare")
    object Time : Routes("Time")
}