package wpics.alcogait.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import wpics.alcogait.Routes
import wpics.alcogait.ui.components.ButtonOne
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightPink
import wpics.alcogait.viewmodels.HomeUiState
import wpics.alcogait.viewmodels.HomeViewModel

@Composable
fun LoginSignupScaffold(
    navController: NavHostController,
    uiState: HomeUiState,
    padding: PaddingValues,
    onClick: () -> Unit,
    buttonText: String,
    textFields: @Composable () -> Unit
){
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState.currentUser) {
        if (uiState.currentUser != null) {
            navController.navigate(Routes.Home.route)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(DarkBlue)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            textFields()

            // login/signup button
            ButtonOne(
                onClick = {
                    keyboardController?.hide()
                    onClick()
                },
                width = 100,
                height = 50,
                contentAlignment = Alignment.Center,
                roundedCornerSize = 20,
                offset = (-2)
            ) {
                Text(
                    text = buttonText,
                    color = DarkBlue
                )
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