package wpics.alcogait.data

class WalkRepository(
    private val userDao: UserDao,
    private val drinksDao: DrinksDao
) {
    suspend fun getUserById(userId: Long): User = userDao.getById(userId)

    suspend fun findUserByName(firstName: String, lastName: String): User =
        userDao.findByName(firstName, lastName)

    suspend fun insertUser(user: User): Long = userDao.insertUser(user)

    suspend fun getDrinksByUserId(userId: Long): List<Drinks> = drinksDao.getDrinksByUserId(userId)

    suspend fun getDrinksByUserIdAndTimestamp(userId: Long, timestamp: String): Drinks? =
        drinksDao.getDrinksByUserIdAndTimestamp(userId, timestamp)

    suspend fun logDrink(drink: Drinks) = drinksDao.insertDrink(drink)

    suspend fun getNumDrinksAtLocation(userId: Long, latitude: Float, longitude: Float): Int =
        drinksDao.getNumDrinksAtLocation(userId, latitude, longitude)

    suspend fun getTimeAndPlaceOfDrinksAtLocation(userId: Long, latitude: Float, longitude: Float): List<Pair<String, String?>> =
        drinksDao.getTimeAndPlaceOfDrinksAtLocation(userId, latitude, longitude)
}