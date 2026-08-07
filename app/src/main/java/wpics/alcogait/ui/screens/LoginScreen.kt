package wpics.alcogait.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import wpics.alcogait.Routes
import wpics.alcogait.data.User
import wpics.alcogait.ui.components.LoginSignupHeader
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.viewmodels.HomeUiState
import wpics.alcogait.viewmodels.HomeViewModel

@Composable
fun LoginScreen(
    padding: PaddingValues,
    viewModel: HomeViewModel,
    navController: NavHostController,
    uiState: HomeUiState
) {
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

                    viewModel.loginUser(
                        username,
                        passwordChars
                    )

                    // clear password
                    passwordChars.fill('\u0000')
                    password = ""
                }
            ) {
                Text("Login")
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