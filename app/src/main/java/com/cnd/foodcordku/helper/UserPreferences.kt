package com.cnd.foodcordku.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.*

class UserPreferences(context: Context) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences>

    init {
        dataStore = applicationContext.createDataStore(
            name = "app_preferences"
        )
    }

    val bookmark: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER]
        }

    suspend fun saveIdUser(idUser: String) {
        dataStore.edit { prefernces ->
            prefernces[KEY_USER] = idUser
        }
    }

    companion object {
        val KEY_USER = stringPreferencesKey("key_user")
    }
}