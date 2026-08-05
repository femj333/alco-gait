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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import wpics.alcogait.Routes
import wpics.alcogait.data.User
import wpics.alcogait.viewmodels.HomeViewModel

@Composable
fun LoginScreen(
    padding: PaddingValues,
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().padding(padding)
    ) {
        Column {
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

            Button(
                onClick = {
                    viewModel.insertUser(
                        User(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phoneNumber = phoneNumber
                        )
                    )

                    navController.navigate(Routes.Home.route)
                }
            ){
                Text("Sign Up")
            }
        }
    }
}