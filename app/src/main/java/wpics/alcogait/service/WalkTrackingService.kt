package wpics.alcogait.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.IBinder
import androidx.annotation.RequiresPermission
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wpics.alcogait.AlcoGaitApp
import wpics.alcogait.data.Drinks
import wpics.alcogait.data.GaitAnalyzer
import wpics.alcogait.data.LocationHelper
import wpics.alcogait.data.Walk
import wpics.alcogait.data.WalkCSVWriter
import wpics.alcogait.data.WalkSensorRecorder
import wpics.alcogait.notifications.NotificationHelper
import wpics.alcogait.repository.RecordingStateRepository
import java.io.File
import java.time.Instant

class WalkTrackingService : Service() {
    companion object {
        const val ACTION_START = "wpics.alcogait.action.START_TRACKING"
        const val ACTION_STOP = "wpics.alcogait.action.STOP_TRACKING"
        const val EXTRA_USER_ID = "userId"

        // 20 second recordings
        private const val WINDOW_DURATION_MS = 20_000L

        // stopwatch tick delay
        private const val STOPWATCH_TICK_MS = 100L
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var trackingJob: Job? = null

    private lateinit var recorder: WalkSensorRecorder
    private lateinit var locationHelper: LocationHelper
    private val analyzer = GaitAnalyzer()

    private var currentWalk: Walk? = null
    private var currentUserId: Long = 0L
    private var recordingLocation: Location? = null
    private var recordingStartTime: Long = 0L
    private var completedWindows = 0

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        recorder = WalkSensorRecorder(application)
        locationHelper = LocationHelper(application)

        recorder.onAccelerometerSample = { data ->
            val x = data[1].toFloatOrNull()
            val y = data[2].toFloatOrNull()
            val z = data[3].toFloatOrNull()
            if (x != null && y != null && z != null) analyzer.addSample(x, y, z)
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> startTracking(intent.getLongExtra(EXTRA_USER_ID, 0L))
            ACTION_STOP -> stopTracking()
        }
        return START_NOT_STICKY
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun startTracking(userId: Long) {
        ServiceCompat.startForeground(
            this,
            NotificationHelper.SERVICE_NOTIFICATION_ID,
            NotificationHelper.buildServiceNotification(this),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
        )

        currentUserId = userId
        completedWindows = 0
        recordingStartTime = System.currentTimeMillis()

        val walk = Walk()
        currentWalk = walk
        recorder.startRecording(walk)

        RecordingStateRepository.setRecording(true)

        trackingJob?.cancel()
        trackingJob = serviceScope.launch {
            // fetch location once, don't block start
            launch {
                recordingLocation = locationHelper.getCurrentLocation()
            }

            // stopwatch
            launch {
                while (isActive) {
                    RecordingStateRepository.setElapsed(System.currentTimeMillis() - recordingStartTime)
                    delay(STOPWATCH_TICK_MS)
                }
            }

            // analysis windows
            while (isActive) {
                delay(WINDOW_DURATION_MS)
                val newState = analyzer.computeDrunkStateAndReset()
                completedWindows++

                if (newState != null) {
                    val changed = RecordingStateRepository.drunkState.value.label != newState.label
                    RecordingStateRepository.setDrunkState(newState)
                    if (changed) {
                        NotificationHelper.notifyDrunkStateChanged(this@WalkTrackingService, newState)
                    }
                }
            }
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
        recorder.stopRecording()

        val walk = currentWalk
        val enoughData = completedWindows > 0

        RecordingStateRepository.reset()

        if (walk != null && enoughData) {
            persistWalk(walk)
        }

        currentWalk = null
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun persistWalk(walk: Walk) {
        val timestamp = Instant.ofEpochMilli(recordingStartTime).toString()
        val userId = currentUserId
        val location = recordingLocation
        val drunkState = RecordingStateRepository.drunkState.value

        val walkRepository = (application as AlcoGaitApp).container.walkRepository

        serviceScope.launch {
            val rootFolder = File(
                getExternalFilesDir(null),
                "AlcoGait/session_${System.currentTimeMillis()}"
            ).apply { mkdirs() }

            val success = withContext(Dispatchers.IO) {
                WalkCSVWriter.write(walk, rootFolder.absolutePath)
            }

            if (success) {
                walkRepository.logDrink(
                    Drinks(
                        userId = userId,
                        latitude = location?.latitude?.toFloat() ?: 0f,
                        longitude = location?.longitude?.toFloat() ?: 0f,
                        timestamp = timestamp,
                        drunkState = drunkState.label
                    )
                )
            }
        }
    }

    override fun onDestroy() {
        recorder.unregisterListeners()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}