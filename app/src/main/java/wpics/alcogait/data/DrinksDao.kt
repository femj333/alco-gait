package wpics.alcogait.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface DrinksDao {
    @Query("SELECT * FROM drinks WHERE userId = :userId")
    suspend fun getDrinksByUserId(userId: Int): List<Drinks>

    @Query("SELECT * FROM drinks WHERE userId = :userId AND timestamp = :timestamp")
    suspend fun getDrinksByUserIdAndTimestamp(userId: Int, timestamp: String): Drinks?

    @Insert
    suspend fun insertDrink(drink: Drinks)
}