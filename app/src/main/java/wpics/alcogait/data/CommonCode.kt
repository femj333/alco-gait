package wpics.alcogait.data

import java.text.SimpleDateFormat
import java.util.Locale

object CommonCode {
    const val DELAY_IN_MILLISECONDS = 5

    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS", Locale.US)

    /**
     * Converts a sensor event to the CSV-ready row shared by the phone and wearable
     *
     * @param sensorName Name of the sensor
     * @param values Array of sensor values
     * @param accuracy Accuracy of the sensor
     *
     * @return Array of strings representing the sensor data in the order:
     *      1. Sensor name
     *      2. Sensor values
     *      3. Sensor accuracy
     *      4. Timestamp
     */
    fun generatePrintableSensorData(
        sensorName: String,
        values: FloatArray,
        accuracy: Int
    ): Array<String> {
        val result = arrayOfNulls<String>(values.size + 3)
        var i = 0

        // store sensor name
        result[i++] = sensorName

        // store sensor values
        for (v in values) result[i++] = v.toString()

        // store accuracy values
        result[i++] = accuracy.toString()

        // store timestamp
        result[i] = simpleDateFormat.format(System.currentTimeMillis())

        @Suppress("UNCHECKED_CAST")
        return result as Array<String>
    }
}