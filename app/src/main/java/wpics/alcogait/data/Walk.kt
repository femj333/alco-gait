package wpics.alcogait.data

import java.io.Serializable

/**
 * Represents a single recorded "walk" trial of a given [WalkType] at a given BAC level. Captures:
 *      - the phone's accelerometer
 *      - gyroscope
 *      - derived compass readings
 */
class Walk(
    private val walkNumber: Int,
    private val bac: Double,
    val walkType: WalkType
): Serializable {
    // Raw sensor readings collected during this walk
    private val phoneAccelerometerDataList = mutableListOf<Array<String>>()
    private val phoneGyroscopeDataList = mutableListOf<Array<String>>()
    private val compassDataList = mutableListOf<Array<String>>()

    private var watchSampleSize = 0

    /** Appends one row of phone accelerometer data (e.g. [name, x, y, z, accuracy, timestamp]) */
    fun addPhoneAccelerometerData(data: Array<String>) {
        this.phoneAccelerometerDataList.add(data)
    }

    /** Appends one row of phone gyroscope data */
    fun addPhoneGyroscopeData(data: Array<String>) {
        this.phoneGyroscopeDataList.add(data)
    }

    /** Appends one row of derived compass/orientation data */
    fun addCompassData(data: Array<String>) {
        this.compassDataList.add(data)
    }

    /**
     * Total number of samples recorded for this walk across all phone
     * sensors plus whatever sample count was reported by the watch.
     */
    fun getSampleSize(): Int =
         phoneAccelerometerDataList.size + phoneGyroscopeDataList.size + compassDataList.size + watchSampleSize

    fun getBAC() = bac
    fun getWalkNumber() = walkNumber

    /**
     * Builds this walk's data as a list of CSV rows:
     *      - BAC header line
     *      - each sensor's section (title row, column-header row, data rows), separated by blank rows
     */
    fun toCSVFormat(): List<Array<String>> {
        val space = arrayOf("")
        val result = mutableListOf<Array<String>>()

        result.add(arrayOf("BAC = $bac"))
        result.add(space)

        result.add(arrayOf("ACCELEROMETER DATA (PHONE)"))
        result.add(arrayOf("Sensor Name", "X", "Y", "Z", "Accuracy", "Timestamp"))
        result.addAll(phoneAccelerometerDataList)
        result.add(space)

        result.add(arrayOf("GYROSCOPE DATA (PHONE)"))
        result.add(arrayOf("Sensor Name", "X", "Y", "Z", "Accuracy", "Timestamp"))
        result.addAll(phoneGyroscopeDataList)
        result.add(space)

        result.add(arrayOf("COMPASS DATA (PHONE)"))
        result.add(arrayOf("Sensor Name", "X", "Y", "Z", "Accuracy", "Timestamp"))
        result.addAll(compassDataList)

        return result
    }
}