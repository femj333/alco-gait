package wpics.alcogait.ui.components


import android.hardware.lights.Light
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import wpics.alcogait.R
import wpics.alcogait.ui.theme.DarkBlue
import wpics.alcogait.ui.theme.LightBlue
import wpics.alcogait.ui.theme.LightPink

@Composable
fun MenuBar(){
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(color = Color.Black)
            .fillMaxSize()
            .padding(start = 10.dp, top = 30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // profile icon
            ProfileIcon()

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Text(
                    text = "Fem",
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

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            color = LightPink,
            thickness = 1.dp
        )

        // navigate to home screen
        NavigateItem("Home", R.drawable.home_icon)

        // navigate to location screen
        NavigateItem("Location", R.drawable.location_icon)

        // navigate to time screen
        NavigateItem("Time", R.drawable.time_icon)

        // navigate to rideshare screen
        NavigateItem("Rideshare", R.drawable.rideshare_icon)

        // toggle display character
        DisplayCharacterToggle()

        // alcogait logo(?)
        AlcoGaitLogo()
    }
}

@Composable
fun NavigateItem(
    destination: String,
    icon: Int
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

@Composable
fun ProfileIcon() {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(color = DarkBlue, shape = CircleShape)
    ){
        Icon(
            painter = painterResource(id = R.drawable.profile_mask),
            contentDescription = "Profile Icon",
            tint = LightPink,
            modifier = Modifier.size(50.dp).align(Alignment.Center)
        )
    }
}

@Composable
fun DisplayCharacterToggle() {
    var checked by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
        )

        Text(
            text = "Display Character: On",
            color = LightPink,
            fontSize = 22.sp
        )
    }
}

@Composable
fun AlcoGaitLogo() {
    Box(modifier = Modifier
        .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
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
}