package wpics.alcogait

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import wpics.alcogait.ui.theme.AlcoGaitTheme
import wpics.alcogait.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlcoGaitTheme {
                MainScreen()
            }
        }
    }
}