package wpics.alcogait.data

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(entities = [User::class, Drinks::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun drinksDao(): DrinksDao
}