package wpics.alcogait.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class WalkSensorRecorder(context: Context) : SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    // Latest low-pass-filtered readings from each sensor, null until first reading arrives
    private var accelVal: FloatArray? = null
    private var gyroVal: FloatArray? = null
    private var magVal: FloatArray? = null

    private var isRecording = false
    private var currentWalk: Walk? = null

    /** Called for every recorded row */
    var onAccelerometerSample: ((Array<String>) -> Unit)? = null
    var onGyroscopeSample: ((Array<String>) -> Unit)? = null
    var onCompassSample: ((Array<String>) -> Unit)? = null

    // Smoothing factor for the exponential low-pass filter applied to raw sensor data
    companion object { private const val ALPHA = 0.15f }

    /** Subscribes this recorder to accelerometer/gyroscope/magnetometer updates */
    fun registerListeners() {
        sensorManager.registerListener(this, accelerometer, CommonCode.DELAY_IN_MILLISECONDS * 1000)
        sensorManager.registerListener(this, gyroscope, CommonCode.DELAY_IN_MILLISECONDS * 1000)
        sensorManager.registerListener(this, magnetometer, CommonCode.DELAY_IN_MILLISECONDS * 1000)
    }

    /** Unsubscribes from all sensor updates (call when recording stops / activity pauses) */
    fun unregisterListeners() {
        sensorManager.unregisterListener(this)
    }

    /** Begins a new recording for the given walk */
    fun startRecording(walk: Walk) {
        currentWalk = walk
        isRecording = true
        registerListeners()
    }

    /** Ends the current recording */
    fun stopRecording() {
        isRecording = false
        unregisterListeners()
        currentWalk = null
    }

    /**
     * Simple exponential low-pass filter used to smooth noisy raw sensor
     * readings: output = output + ALPHA * (input - output).
     *
     * On the very first call (no previous output yet) the raw input is
     * returned as-is so we have something to filter against next time.
     */
    private fun lowPass(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input

        for (i in input.indices) {
            output[i] += ALPHA * (input[i] - output[i])
        }

        return output
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
        // No-op: accuracy changes aren't currently handled.
    }

    /**
     * Fired on every new reading from any registered sensor. Filters and stores the data
     * derives device orientation ("compass") data when accelerometer and magnetometer data
     * are available.
     *
     * @param sensorEvent the new sensor reading
     */
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        // values returned by this sensor cannot be trusted
        if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return

        if (!isRecording) return

        val sensorName = sensorEvent.sensor.name

        when (sensorEvent.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // filter raw value
                accelVal = lowPass(sensorEvent.values.clone(), accelVal)
                // add to current walk
                val data = CommonCode.generatePrintableSensorData(sensorName, accelVal!!, sensorEvent.accuracy)
                currentWalk?.addPhoneAccelerometerData(data)
                onAccelerometerSample?.invoke(data)
            }

            Sensor.TYPE_GYROSCOPE -> {
                // filter raw value
                gyroVal = lowPass(sensorEvent.values.clone(), gyroVal)
                // add to current walk
                val data = CommonCode.generatePrintableSensorData(sensorName, gyroVal!!, sensorEvent.accuracy)
                currentWalk?.addPhoneGyroscopeData(data)
                onGyroscopeSample?.invoke(data)
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                magVal = lowPass(sensorEvent.values.clone(), magVal)
            }
        }

        // Once we have both accel and mag data we can compute device orientation
        if (accelVal != null && magVal != null) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            val success = SensorManager.getRotationMatrix(r, i, accelVal, magVal)
            if (success) {
                val compassVal = FloatArray(3)
                SensorManager.getOrientation(r, compassVal)
                val data = CommonCode.generatePrintableSensorData("Compass", compassVal, sensorEvent.accuracy)
                currentWalk?.addCompassData(data)
                onCompassSample?.invoke(data)
            }
        }

    }

}