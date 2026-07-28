package wpics.alcogait.data

import android.util.Log
import wpics.alcogait.R
import wpics.alcogait.models.DrunkState
import kotlin.math.sqrt

class GaitAnalyzer {
    private val magnitudes = mutableListOf<Float>()

    /** Add magnitude of one sample to the window */
    fun addSample(x: Float, y: Float, z: Float) {
        magnitudes.add(sqrt(x * x + y * y + z * z))
    }

    /** Returns the current drunk state label and image */
    fun computeDrunkStateAndReset(): DrunkState? {
        // no info
        if (magnitudes.isEmpty()) return null

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

        val image = when (label) {
            "SOBER" -> R.drawable.sober
            "TIPSY" -> R.drawable.tipsy
            "DRUNK" -> R.drawable.drunk
            else -> R.drawable.wasted
        }

        val fontSize = if (label == "WASTED") 50 else 60

        val drunkState = DrunkState(label, image, fontSize)
        magnitudes.clear()
        return drunkState
    }
}