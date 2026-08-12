package wpics.alcogait

import android.Manifest
import android.location.Location
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices
import wpics.alcogait.data.WalkRepository
import wpics.alcogait.ui.theme.AlcoGaitTheme
import wpics.alcogait.ui.screens.MainScreen

class MainActivity : ComponentActivity() {
    // request location permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationPermissionsGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (!locationPermissionsGranted) {
            // permission denied
            Log.d("MainActivity", "Location permission denied")
        } else {
            Log.d("MainActivity", "Location permission granted")
        }

        val activityRecognitionPermissionsGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true
        if (!activityRecognitionPermissionsGranted) {
            // permission denied
            Log.d("MainActivity", "Activity recognition permission denied")
        } else {
            Log.d("MainActivity", "Activity recognition permission granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val mediaPlayer = MediaPlayer.create(applicationContext, R.raw.jazz_music)

        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        ))

        setContent {
            AlcoGaitTheme {
                MainScreen(mediaPlayer = mediaPlayer)
            }
        }
    }
}
