package wpics.alcogait.data

import android.content.Context

class AppContainer(
    context: Context,
    database: AppDatabase
) {
    val walkRepository = WalkRepository(database.userDao(), database.drinksDao())
}