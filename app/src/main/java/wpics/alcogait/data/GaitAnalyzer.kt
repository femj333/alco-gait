package wpics.alcogait.data

import android.util.Log
import wpics.alcogait.R
import wpics.alcogait.models.DrunkState
import kotlin.math.sqrt

class GaitAnalyzer {
    private val magnitudes = mutableListOf<Float>()
    private val lock = Any()

    /** Add magnitude of one sample to the window */
    fun addSample(x: Float, y: Float, z: Float) {
        synchronized(lock) {
            magnitudes.add(sqrt(x * x + y * y + z * z))
        }
    }

    /** Returns the current drunk state label and image */
    fun computeDrunkStateAndReset(): DrunkState? {
        val snapshot: List<Float> = synchronized(lock) {
            // no info
            if (magnitudes.isEmpty()) return null
            val copy = magnitudes.toList()
            magnitudes.clear()
            copy
        }

        // compute variance of magnitudes-> in place of ML model
        val mean = magnitudes.average()
        val variance = magnitudes.sumOf { (it - mean) * (it - mean) } / magnitudes.size

        // arbitrary drunk state from variance
        val label =  when {
            variance < 0.5 -> "SOBER"
            variance < 2.0 -> "TIPSY"
            variance < 3.5 -> "DRUNK"
            else -> "WASTED"
        }

        val finePrint = when (label) {
            "SOBER" -> "Your BAC is between 0.0-0.02"
            "TIPSY" -> "Your BAC is between 0.02-0.08"
            "DRUNK" -> "Your BAC is between 0.08-0.1"
            else -> "Your BAC is above 0.1"
        }

        val image = when (label) {
            "SOBER" -> R.drawable.sober
            "TIPSY" -> R.drawable.tipsy
            "DRUNK" -> R.drawable.drunk
            else -> R.drawable.wasted
        }

        val noCharacterText = when (label) {
            "SOBER" -> "Your BAC is currently under the legal limit (0.08). It is safe to drive."
            "TIPSY" -> "If you want to drive, stop drinking now. Your BAC is approaching the legal limit (0.08)."
            "DRUNK" -> "Don't drive. Get a ride home with a sober friend or use a rideshare app."
            else -> "Do not drive. You are drunk. Use a rideshare app."
        }

        val noCharacterDrinks = when (label) {
            "SOBER" -> R.drawable.wine_glasses_sober
            "TIPSY" -> R.drawable.wine_glasses_tipsy
            "DRUNK" -> R.drawable.wine_glasses_drunk
            else -> R.drawable.wine_glasses_wasted
        }

        val fontSize = if (label == "WASTED") 50 else 60

        return DrunkState(label, finePrint, image, fontSize, noCharacterText, noCharacterDrinks)
    }
}