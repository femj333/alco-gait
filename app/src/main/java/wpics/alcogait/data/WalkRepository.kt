package wpics.alcogait.data

class WalkRepository(
    private val userDao: UserDao,
    private val drinksDao: DrinksDao
) {
    suspend fun getUserById(userId: Int): User = userDao.getById(userId)

    suspend fun findUserByName(firstName: String, lastName: String): User =
        userDao.findByName(firstName, lastName)

    suspend fun insertUser(user: User) = userDao.insertUser(user)

    suspend fun getDrinksByUserId(userId: Int): List<Drinks> = drinksDao.getDrinksByUserId(userId)

    suspend fun getDrinksByUserIdAndTimestamp(userId: Int, timestamp: String): Drinks? =
        drinksDao.getDrinksByUserIdAndTimestamp(userId, timestamp)

    suspend fun logDrink(drink: Drinks) = drinksDao.insertDrink(drink)
}