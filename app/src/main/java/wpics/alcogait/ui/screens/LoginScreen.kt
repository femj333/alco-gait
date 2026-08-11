package wpics.alcogait.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import wpics.alcogait.ui.components.LoginSignupTextField
import wpics.alcogait.ui.components.PasswordTextField
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

    LoginSignupScaffold(
        navController = navController,
        uiState = uiState,
        padding = padding,
        onClick = {
            val passwordChars = password.toCharArray()

            viewModel.loginUser(
                username,
                passwordChars
            )

            // clear password
            passwordChars.fill('\u0000')
            password = ""
        },
        buttonText = "Login"
    ) {
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