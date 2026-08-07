package wpics.alcogait.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import wpics.alcogait.R
import wpics.alcogait.Routes
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightPink
import wpics.alcogait.viewmodels.HomeUiState
import wpics.alcogait.viewmodels.HomeViewModel

@Composable
fun MenuBar(
    displayChecked: Boolean,
    onDisplayCheckedChange: (Boolean) -> Unit,
    musicChecked: Boolean,
    onMusicCheckedChange: (Boolean) -> Unit,
    navController: NavController,
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    onCloseMenu: () -> Unit
){
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(color = Color.Black)
            .fillMaxSize()
            .padding(start = 10.dp, top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // profile header
        ProfileHeader(
            onClick = {
                navController.navigate(Routes.Profile.route)
                onCloseMenu()
            },
            uiState = uiState
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            color = LightPink,
            thickness = 1.dp
        )

        // navigate to home screen
        NavigateItem(
            destination = "Home",
            icon = R.drawable.home_icon,
            onClick = {
                navController.navigate(Routes.Home.route)
                onCloseMenu()
            }
        )

        // navigate to location screen
        NavigateItem(
            destination = "Location",
            icon = R.drawable.location_icon,
            onClick = {
                navController.navigate(Routes.Location.route)
                onCloseMenu()
            }
        )

        // navigate to time screen
        NavigateItem(
            destination = "Time",
            icon = R.drawable.time_icon,
            onClick = {
                navController.navigate(Routes.Time.route)
                onCloseMenu()
            }
        )

        // navigate to rideshare screen
        NavigateItem(
            destination = "Rideshare",
            icon = R.drawable.rideshare_icon,
            onClick = {
                navController.navigate(Routes.Rideshare.route)
                onCloseMenu()
            }
        )

        // toggle display character
        Toggle(
            checked = displayChecked,
            onCheckedChange = onDisplayCheckedChange,
            text = "Display Character"
        )

        // toggle music
        Toggle(
            checked = musicChecked,
            onCheckedChange = onMusicCheckedChange,
            text = "Music"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        // alcogait logo(?)
        AlcoGaitLogo()

        // logout button
        LogoutButton(navController, viewModel, onCloseMenu)

    }
}

@Composable
fun LogoutButton(
    navController: NavController,
    viewModel: HomeViewModel,
    onCloseMenu: () -> Unit
) {
    Button(
        onClick = {
            viewModel.logoutUser()
            navController.navigate(Routes.Login.route)
            onCloseMenu()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.Red
        ),
        contentPadding = PaddingValues(start = 5.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
    ) {
        Text(
            text = "Logout",
            fontSize = 20.sp
        )
    }
}

@Composable
fun NavigateItem(
    destination: String,
    icon: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = { onClick() },
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = LightPink
        )
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "$destination Icon",
                tint = LightPink,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = destination,
                color = LightPink,
                fontSize = 25.sp
            )
        }
    }
}

@Composable
fun ProfileHeader(
    onClick: () -> Unit,
    uiState: HomeUiState
) {
    Button(
        onClick = { onClick() },
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = LightPink
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            // profile icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color = DarkBlue, shape = CircleShape)
            ){
                Icon(
                    painter = painterResource(id = R.drawable.profile_mask),
                    contentDescription = "Profile Icon",
                    tint = LightPink,
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }

            // profile name
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                val text = if (uiState.currentUser?.firstName != null) uiState.currentUser.firstName else "Login"
                Text(
                    text = text,
                    color = LightPink,
                    fontSize = 25.sp
                )

                Text(
                    text = "View Profile",
                    color = LightPink,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )

        Text(
            text = if (checked) "$text: On" else "$text: Off",
            color = LightPink,
            fontSize = 22.sp
        )
    }

}

@Composable
fun AlcoGaitLogo() {
    Row(
        modifier = Modifier
            .padding(start = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color = LightPink, shape = CircleShape)
        )

        Text(
            text = "AlcoGait",
            color = LightPink,
            fontSize = 25.sp,
        )
    }
}