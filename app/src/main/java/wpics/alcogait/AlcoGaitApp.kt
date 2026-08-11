package wpics.alcogait

import android.app.Application
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wpics.alcogait.data.AppContainer
import wpics.alcogait.data.AppDatabase
import wpics.alcogait.data.User

class AlcoGaitApp : Application() {
    lateinit var container: AppContainer
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

         //var isNewDatabase = false

        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(applicationContext, BuildConfig.PLACES_API_KEY)
        }

        database = Room.databaseBuilder(this, AppDatabase::class.java, "alcogait_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .addCallback(object : RoomDatabase.Callback() {
                override suspend fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)
                    // isNewDatabase = true
            }
        }).build()

        container = AppContainer(this, database)

        /* insert dummy users into database
        if (isNewDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                container.walkRepository.insertUser(
                    User(
                        userId = 0,
                        firstName = "Fem",
                        lastName = "Jansen",
                        email = "fejansen@wpi.edu",
                        phoneNumber = "860-543-4206",

                    )
                )
                container.walkRepository.insertUser(
                    User(
                        userId = 1,
                        firstName = "Coco",
                        lastName = "Puff",
                        email = "cocopuff1@wpi.edu",
                        phoneNumber = "123-456-7890"
                    )
                )
            }
        }

         */
    }
}