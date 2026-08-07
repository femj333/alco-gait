package wpics.alcogait.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import wpics.alcogait.Routes
import wpics.alcogait.ui.theme.DarkBlue

@Composable
fun LoginSignupHeader(
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBlue)
    ) {
        // login screen
        Button(
            onClick = {
                navController.navigate(Routes.Login.route)
            }
        ) {
            Text(text = "Login")
        }

        // signup screen
        Button(
            onClick = {
                navController.navigate(Routes.Signup.route)
            }
        ) {
            Text(text = "Sign Up")
        }
    }
}