package wpics.alcogait.data

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    private val LAST_USER_ID = stringPreferencesKey("last_user_id")

    private val LAST_CHARACTER_DISPLAY = booleanPreferencesKey("last_character_display")

    val lastUserId: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[LAST_USER_ID] }

    val lastCharacterDisplay: Flow<Boolean?> = context.dataStore.data
        .map { prefs -> prefs[LAST_CHARACTER_DISPLAY] }

    suspend fun saveLastUserId(userId: String) {
        Log.d("UserPreferences", "Saving last user id: $userId")
        context.dataStore.edit { prefs -> prefs[LAST_USER_ID] = userId }
    }

    suspend fun saveLastCharacterDisplay(display: Boolean) {
        context.dataStore.edit { prefs -> prefs[LAST_CHARACTER_DISPLAY] = display }
    }

    suspend fun clearLastUserId() {
        context.dataStore.edit { prefs -> prefs.remove(LAST_USER_ID) }
    }

}