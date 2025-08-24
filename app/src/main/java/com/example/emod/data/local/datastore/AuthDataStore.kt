package com.example.emod.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthDataStore(private val context: Context) {

    companion object {
        private val KEY_JWT = stringPreferencesKey("jwt")
        private val KEY_DEVICE_CODE = stringPreferencesKey("device_code")
        private val KEY_PROFILE_COMPLETED = booleanPreferencesKey("profile_completed")

        // Name
        private val KEY_NAME = stringPreferencesKey("name")
    }

    val jwtFlow: Flow<String?> = context.dataStore.data.map { it[KEY_JWT] }
    val profileCompletedFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_PROFILE_COMPLETED] ?: false }
    val deviceCodeFlow: Flow<String?> = context.dataStore.data.map { it[KEY_DEVICE_CODE] }

    // name Flow
    val nameFlow: Flow<String?> = context.dataStore.data.map { it[KEY_NAME] }

    suspend fun save(jwt: String, deviceCode: String, profileCompleted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_JWT] = jwt
            prefs[KEY_DEVICE_CODE] = deviceCode
            prefs[KEY_PROFILE_COMPLETED] = profileCompleted
        }
    }

    suspend fun setProfileCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_PROFILE_COMPLETED] = completed
        }
    }

    suspend fun setName(name: String){
        context.dataStore.edit { prefs ->
            prefs[KEY_NAME] = name
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
