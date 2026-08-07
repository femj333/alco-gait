package wpics.alcogait.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Utility for securely hashing and verifying passwords using PBKDF2WithHmacSHA256.
 *
 * Passwords are never stored or compared in plaintext. Each password gets a unique
 * random salt, and the hash uses a high iteration count to slow down brute-force attacks.
 */
object PasswordHasher {
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH_BITS = 256
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val SALT_LENGTH_BYTES = 16

    /**
     * Generates a new random salt, encoded as a Base64 string for storage.
     */
    fun generateSalt(): String {
        val salt = ByteArray(SALT_LENGTH_BYTES)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    /**
     * Hashes [password] with the given Base64-encoded [salt].
     * Caller is responsible for clearing [password] after use.
     */
    fun hash(password: CharArray, salt: String): String {
        val saltBytes = Base64.decode(salt, Base64.NO_WRAP)
        val spec = PBEKeySpec(password, saltBytes, ITERATIONS, KEY_LENGTH_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hashBytes = factory.generateSecret(spec).encoded
        spec.clearPassword()
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }

    /**
     * Verifies [password] against a previously stored [expectedHash] and [salt].
     * Uses a constant-time comparison to avoid leaking timing information.
     */
    fun verify(password: CharArray, salt: String, expectedHash: String): Boolean {
        val computedHash = hash(password, salt)
        return constantTimeEquals(computedHash, expectedHash)
    }

    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
}