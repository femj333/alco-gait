package wpics.alcogait.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import wpics.alcogait.Routes
import wpics.alcogait.data.User
import wpics.alcogait.ui.components.LoginSignupHeader
import wpics.alcogait.viewmodels.HomeUiState
import wpics.alcogait.viewmodels.HomeViewModel

@Composable
fun SignupScreen(
    viewModel: HomeViewModel,
    navController: NavHostController,
    uiState: HomeUiState,
    padding: PaddingValues
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.currentUser) {
        if (uiState.currentUser != null) {
            navController.navigate(Routes.Home.route)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = firstName,
                onValueChange = { newText -> firstName = newText },
                label = { Text("Enter your first name") }
            )

            TextField(
                value = lastName,
                onValueChange = { newText -> lastName = newText },
                label = { Text("Enter your last name") }
            )

            TextField(
                value = email,
                onValueChange = { newText -> email = newText },
                label = { Text("Enter your email") }
            )

            TextField(
                value = phoneNumber,
                onValueChange = { newText -> phoneNumber = newText },
                label = { Text("Enter your phone number") }
            )

            TextField(
                value = username,
                onValueChange = { newText -> username = newText },
                label = { Text("Enter your username") }
            )

            TextField(
                value = password,
                onValueChange = { newText -> password = newText },
                label = { Text("Enter your password") }
            )

            Button(
                onClick = {
                    val passwordChars = password.toCharArray()

                    viewModel.registerUser(
                        username,
                        passwordChars,
                        firstName,
                        lastName,
                        email,
                        phoneNumber
                    )

                    // clear password
                    passwordChars.fill('\u0000')
                    password = ""
                }
            ) {
                Text("Sign Up")
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = Color.Red
                )
            }
        }
    }
}