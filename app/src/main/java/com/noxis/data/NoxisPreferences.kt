package com.noxis.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "noxis_prefs")

class NoxisPreferences(private val context: Context) {
    companion object {
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_PASSWORD_HASH = stringPreferencesKey("password_hash")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK_THEME] ?: false }
    val username: Flow<String> = context.dataStore.data.map { it[KEY_USERNAME] ?: "User" }
    val passwordHash: Flow<String> = context.dataStore.data.map { it[KEY_PASSWORD_HASH] ?: "" }

    suspend fun setDarkTheme(value: Boolean) = context.dataStore.edit { it[KEY_DARK_THEME] = value }
    suspend fun setUsername(value: String) = context.dataStore.edit { it[KEY_USERNAME] = value }
    suspend fun setPasswordHash(value: String) = context.dataStore.edit { it[KEY_PASSWORD_HASH] = value }

    fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        return digest.digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
