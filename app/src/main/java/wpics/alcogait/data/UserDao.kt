package wpics.alcogait.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getById(userId: Long): User

    @Query(
        """
            SELECT * FROM users
            WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1
            """
    )
    suspend fun findByName(first: String, last: String): User

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun usernameExists(username: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long
}