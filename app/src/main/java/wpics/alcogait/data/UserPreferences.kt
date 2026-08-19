package wpics.alcogait.data

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    private val LAST_USER_ID = stringPreferencesKey("last_user_id")

    val lastUserId: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[LAST_USER_ID] }

    suspend fun saveLastUserId(userId: String) {
        Log.d("UserPreferences", "Saving last user id: $userId")
        context.dataStore.edit { prefs -> prefs[LAST_USER_ID] = userId }
    }

    suspend fun clearLastUserId() {
        context.dataStore.edit { prefs -> prefs.remove(LAST_USER_ID) }
    }

}