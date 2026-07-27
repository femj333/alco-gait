package wpics.alcogait.data

import android.util.Log
import kotlin.math.sqrt

class GaitAnalyzer {
    private val magnitudes = mutableListOf<Float>()

    /** Add magnitude of one sample to the window */
    fun addSample(x: Float, y: Float, z: Float) {
        magnitudes.add(sqrt(x * x + y * y + z * z))
    }

    /** Returns the current drunk state label */
    fun computeLabelAndReset(): String? {
        // no info
        if (magnitudes.isEmpty()) return null

        // compute variance of magnitude over window -> in place of ML model
        val mean = magnitudes.average()
        val variance = magnitudes.sumOf { (it - mean) * (it - mean) } / magnitudes.size
        val label =  when {
            variance < 0.5 -> "SOBER"
            variance < 2.0 -> "TIPSY"
            variance < 3.5 -> "DRUNK"
            else -> "WASTED"
        }

        magnitudes.clear()
        return label
    }
}