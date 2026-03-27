package com.web.kiosk.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

enum class Rotation(val degrees: Int) {
    ROTATION_0(0),
    ROTATION_90(90),
    ROTATION_180(180),
    ROTATION_270(270);
}

private val Context.dataStore by preferencesDataStore(name = "kiosk_settings")

class DataStoreKioskSettings(private val context: Context) : KioskSettings {
    private val keyCheckInterval = longPreferencesKey("check_interval")
    private val keyStartUrl = stringPreferencesKey("start_url")
    private val keyRotation = intPreferencesKey("rotation")
    private val keyIdleTimeout = longPreferencesKey("idle_timeout")
    private val keyIdleBrightness = intPreferencesKey("idle_brightness")
    private val keyActiveBrightness = intPreferencesKey("active_brightness")
    private val keyUserAgentType = stringPreferencesKey("user_agent_type")
    private val keyPassword = stringPreferencesKey("admin_password")

    override fun getCheckInterval(): Flow<Long> {
        return context.dataStore.data.map { prefs ->
            prefs[keyCheckInterval] ?: 10_000L
        }
    }

    override suspend fun setCheckInterval(interval: Long) {
        context.dataStore.edit { prefs ->
            prefs[keyCheckInterval] = interval
        }
    }

    override fun getStartUrl(): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[keyStartUrl] ?: "https://www.bing.com"
        }
    }

    override suspend fun setStartUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[keyStartUrl] = url
        }
    }

    override fun getRotation(): Flow<Rotation> {
        return context.dataStore.data.map { prefs ->
            val degrees: Int = try {
                prefs[keyRotation] ?: Rotation.ROTATION_0.degrees
            } catch (_: ClassCastException) {
                val legacyKey = longPreferencesKey("rotation")
                (prefs[legacyKey] ?: Rotation.ROTATION_0.degrees.toLong()).toInt()
            }
            getRotationFromDegrees(degrees)
        }
    }

    private fun getRotationFromDegrees(degrees: Int): Rotation {
        return when (degrees) {
            Rotation.ROTATION_0.degrees -> Rotation.ROTATION_0
            Rotation.ROTATION_90.degrees -> Rotation.ROTATION_90
            Rotation.ROTATION_180.degrees -> Rotation.ROTATION_180
            Rotation.ROTATION_270.degrees -> Rotation.ROTATION_270
            else -> Rotation.ROTATION_0
        }
    }

    override suspend fun setRotation(rotation: Rotation) {
        context.dataStore.edit { prefs ->
            prefs[keyRotation] = rotation.degrees
        }
    }

    override fun getIdleTimeout(): Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[keyIdleTimeout] ?: 0L
    }

    override suspend fun setIdleTimeout(timeout: Long) {
        context.dataStore.edit { prefs -> prefs[keyIdleTimeout] = timeout }
    }

    override fun getIdleBrightness(): Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[keyIdleBrightness] ?: 0
    }

    override suspend fun setIdleBrightness(brightness: Int) {
        context.dataStore.edit { prefs -> prefs[keyIdleBrightness] = brightness.coerceIn(0, 100) }
    }

    override fun getActiveBrightness(): Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[keyActiveBrightness] ?: 100
    }

    override suspend fun setActiveBrightness(brightness: Int) {
        context.dataStore.edit { prefs -> prefs[keyActiveBrightness] = brightness.coerceIn(0, 100) }
    }

    override fun getUserAgentType(): Flow<UserAgentType> = context.dataStore.data.map { prefs ->
        val value = prefs[keyUserAgentType] ?: UserAgentType.DESKTOP.value
        UserAgentType.valueOf(value.uppercase())
    }

    override suspend fun setUserAgentType(type: UserAgentType) {
        context.dataStore.edit { prefs -> prefs[keyUserAgentType] = type.value }
    }

    override fun getPassword(): Flow<String> = context.dataStore.data.map { prefs ->
        prefs[keyPassword] ?: ""
    }

    override suspend fun setPassword(password: String) {
        context.dataStore.edit { prefs -> prefs[keyPassword] = password }
    }

    override suspend fun verifyPassword(password: String): Boolean {
        val storedPassword = context.dataStore.data.first()[keyPassword] ?: ""
        return storedPassword.isEmpty() || storedPassword == password
    }

    override fun hasPassword(): Flow<Boolean> = context.dataStore.data.map { prefs ->
        !prefs[keyPassword].isNullOrEmpty()
    }
}