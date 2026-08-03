package wpics.alcogait

import android.app.Application
import androidx.room3.Room
import wpics.alcogait.data.AppDatabase

class AlcoGaitApp : Application() {
    lateinit var container: AppContainer
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "alcogait_db"
        ).build()

        container = AppContainer(this, database)
    }
}