package wpics.alcogait.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface DrinksDao {
    @Query("SELECT * FROM drinks WHERE user_id = :userId")
    suspend fun getDrinksByUserId(userId: Long): List<Drinks>

    @Query("SELECT * FROM drinks WHERE user_id = :userId AND timestamp = :timestamp")
    suspend fun getDrinksByUserIdAndTimestamp(userId: Long, timestamp: String): Drinks?

    @Query("SELECT COUNT(*) FROM drinks WHERE user_id = :userId AND latitude = :latitude AND longitude = :longitude")
    suspend fun getNumDrinksAtLocation(userId: Long, latitude: Float, longitude: Float): Int

    @Query("SELECT timestamp, drunk_state FROM drinks WHERE user_id = :userId AND latitude = :latitude AND longitude = :longitude")
    suspend fun getTimeAndPlaceOfDrinksAtLocation(userId: Long, latitude: Float, longitude: Float): List<Pair<String, String?>>

    @Insert
    suspend fun insertDrink(drink: Drinks)
}