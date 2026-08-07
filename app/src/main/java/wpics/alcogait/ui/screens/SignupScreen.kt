package wpics.alcogait.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import wpics.alcogait.Routes
import wpics.alcogait.data.User
import wpics.alcogait.ui.components.LoginSignupHeader
import wpics.alcogait.ui.components.LoginSignupTextField
import wpics.alcogait.ui.components.PasswordTextField
import wpics.alcogait.ui.theme.DarkBlue
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

    LoginSignupScaffold(
        navController = navController,
        uiState = uiState,
        padding = padding,
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
        },
        buttonText = "Sign Up",
    ) {
        LoginSignupTextField(
            value = firstName,
            onValueChange = { newText -> firstName = newText },
            label = "First name"
        )

        LoginSignupTextField(
            value = lastName,
            onValueChange = { newText -> lastName = newText },
            label = "Last name"
        )

        LoginSignupTextField(
            value = email,
            onValueChange = { newText -> email = newText },
            label = "Email"
        )

        LoginSignupTextField(
            value = phoneNumber,
            onValueChange = { newText -> phoneNumber = newText },
            label = "Phone number"
        )

        LoginSignupTextField(
            value = username,
            onValueChange = { newText -> username = newText },
            label = "Username"
        )

        PasswordTextField(
            value = password,
            onValueChange = { newText -> password = newText }
        )
    }
}