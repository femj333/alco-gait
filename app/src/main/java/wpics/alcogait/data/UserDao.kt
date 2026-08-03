package wpics.alcogait.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getById(userId: Int): User

    @Query(
        """
            SELECT * FROM users
            WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1
            """
    )
    suspend fun findByName(first: String, last: String): User

    @Insert
    suspend fun insertUser(user: User)
}