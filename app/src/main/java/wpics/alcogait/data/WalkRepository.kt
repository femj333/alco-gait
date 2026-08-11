package wpics.alcogait.data

import wpics.alcogait.security.PasswordHasher

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


    /**
     * Registers a new user with the given information.
     *
     * @return the created [User], or null if the username is already taken
     */
    suspend fun register(
        username: String,
        password: CharArray,
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String
    ): User? {
        // username exists
        if (userDao.usernameExists(username)) {
            // clear password
            password.fill('\u0000')
            return null
        }

        // salt and hash password
        val salt = PasswordHasher.generateSalt()
        val passwordHash = PasswordHasher.hash(password, salt)
        // wipe plaintext password from mem
        password.fill('\u0000')

        // insert user into database
        val user = User(
            username = username,
            passwordHash = passwordHash,
            salt = salt,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phoneNumber = phoneNumber
        )
        userDao.insertUser(user)
        return user
    }

    /**
     * Verifies login credentials for a user
     *
     * @return the matching [User] if credentials are valid, otherwise null
     */
    suspend fun login(username: String, password: CharArray): User? {
        val user = userDao.getUserByUsername(username)
        val isValid = user != null && PasswordHasher.verify(password, user.salt, user.passwordHash)
        password.fill('\u0000')
        return if (isValid) user else null
    }
}