package wpics.alcogait.data

import kotlin.math.sqrt

class GaitAnalyzer(
    // rolling window of samples
    private val windowSize: Int = 200
) {
    private val magnitudes = ArrayDeque<Float>()

    /** Feed it the raw filtered X, Y, X strings already produced for the CSV row */
    fun ingest(values: Array<String>): String {
        // compute magnitude of acceleration vector
        val x = values[1].toFloatOrNull() ?: return currentLabel()
        val y = values[2].toFloatOrNull() ?: return currentLabel()
        val z = values[3].toFloatOrNull() ?: return currentLabel()
        val magnitude = sqrt(x * x + y * y + z * z)

        // push the new value
        magnitudes.addLast(magnitude)

        // remove oldest value once over capacity
        if (magnitudes.size > windowSize) magnitudes.removeFirst()

        return currentLabel()
    }

    /** Returns the current drunk state label */
    private fun currentLabel(): String {
        // too little info
        if (magnitudes.size < windowSize / 2) return "SOBER"

        // compute variance of magnitude over window -> in place of ML model
        val mean = magnitudes.average()
        val variance = magnitudes.sumOf { (it - mean) * (it - mean) } / magnitudes.size
        return when {
            variance < 0.5 -> "SOBER"
            variance < 1.0 -> "TISPY"
            variance < 1.5 -> "DRUNK"
            else -> "WASTED"
        }
    }
}