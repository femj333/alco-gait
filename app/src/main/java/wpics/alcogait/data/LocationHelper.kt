package wpics.alcogait.data

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationHelper(context: Context) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val cts = CancellationTokenSource()
        cont.invokeOnCancellation { cts.cancel() }

        try {
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location -> cont.resume(location, null) }
                .addOnFailureListener { cont.resume(null, null) }
        } catch (e: SecurityException) {
            cont.resume(null, null)
        }
    }
}