package com.web.kiosk.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class SharedPreferencesKioskSettings(context: Context) : KioskSettings {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("kiosk_settings", Context.MODE_PRIVATE)
    private val keyIdleTimeout = "idle_timeout"
    private val keyIdleBrightness = "idle_brightness"
    private val keyActiveBrightness = "active_brightness"
    private val keyVolume = "volume"
    private val keyShowSystemSettingsButton = "show_system_settings_button"

    override fun getCheckInterval(): Flow<Long> = callbackFlow {
        val key = "check_interval"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(prefs.getLong(key, 10_000L))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getLong(key, 10_000L))

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setCheckInterval(interval: Long) {
        prefs.edit { putLong("check_interval", interval) }
    }

    override fun getStartUrl(): Flow<String> = callbackFlow {
        val key = "start_url"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                trySend(prefs.getString(key, "https://www.bing.com") ?: "https://www.bing.com")
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getString(key, "https://www.bing.com") ?: "https://www.bing.com")

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setStartUrl(url: String) {
        prefs.edit { putString("start_url", url) }
    }

    override fun getRotation(): Flow<Rotation> = callbackFlow {
        val key = "rotation"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                val degrees = prefs.getInt(key, Rotation.ROTATION_0.degrees)
                trySend(getRotationFromDegrees(degrees))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        val degrees = prefs.getInt(key, Rotation.ROTATION_0.degrees)
        trySend(getRotationFromDegrees(degrees))

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setRotation(rotation: Rotation) {
        prefs.edit { putInt("rotation", rotation.degrees) }
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

    override fun getIdleTimeout(): Flow<Long> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == keyIdleTimeout) trySend(prefs.getLong(keyIdleTimeout, 0L))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getLong(keyIdleTimeout, 0L))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setIdleTimeout(timeout: Long) {
        prefs.edit { putLong(keyIdleTimeout, timeout) }
    }

    override fun getIdleBrightness(): Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == keyIdleBrightness) trySend(prefs.getInt(keyIdleBrightness, 0))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getInt(keyIdleBrightness, 0))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setIdleBrightness(brightness: Int) {
        prefs.edit { putInt(keyIdleBrightness, brightness.coerceIn(0, 100)) }
    }

    override fun getActiveBrightness(): Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == keyActiveBrightness) trySend(prefs.getInt(keyActiveBrightness, 100))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getInt(keyActiveBrightness, 100))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setActiveBrightness(brightness: Int) {
        prefs.edit { putInt(keyActiveBrightness, brightness.coerceIn(0, 100)) }
    }

    override fun getUserAgentType(): Flow<UserAgentType> = callbackFlow {
        val key = "user_agent_type"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                val value = prefs.getString(key, UserAgentType.DESKTOP.value) ?: UserAgentType.DESKTOP.value
                trySend(UserAgentType.valueOf(value.uppercase()))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        val value = prefs.getString(key, UserAgentType.DESKTOP.value) ?: UserAgentType.DESKTOP.value
        trySend(UserAgentType.valueOf(value.uppercase()))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    override suspend fun setUserAgentType(type: UserAgentType) {
        prefs.edit { putString("user_agent_type", type.value) }
    }
    
    override fun getVolume(): Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == keyVolume) trySend(prefs.getInt(keyVolume, 50))
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getInt(keyVolume, 50))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
    
    override suspend fun setVolume(volume: Int) {
        prefs.edit { putInt(keyVolume, volume.coerceIn(0, 100)) }
    }
    
    override fun getPassword(): Flow<String> = callbackFlow {
        val key = "admin_password"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                val value = prefs.getString(key, "") ?: ""
                trySend(value)
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        val value = prefs.getString(key, "") ?: ""
        trySend(value)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
    
    override suspend fun setPassword(password: String) {
        prefs.edit { putString("admin_password", password) }
    }
    
    override suspend fun verifyPassword(password: String): Boolean {
        val storedPassword = prefs.getString("admin_password", "") ?: ""
        return storedPassword.isEmpty() || storedPassword == password
    }
    
    override fun hasPassword(): Flow<Boolean> = callbackFlow {
        val key = "admin_password"
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == key) {
                val value = prefs.getString(key, "") ?: ""
                trySend(value.isNotEmpty())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        val value = prefs.getString(key, "") ?: ""
        trySend(value.isNotEmpty())
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
    
    fun isShowSystemSettingsButton(): Flow<Boolean> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
            if (changedKey == keyShowSystemSettingsButton) {
                trySend(prefs.getBoolean(keyShowSystemSettingsButton, true))
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        trySend(prefs.getBoolean(keyShowSystemSettingsButton, true))
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()
    
    suspend fun setShowSystemSettingsButton(show: Boolean) {
        prefs.edit { putBoolean(keyShowSystemSettingsButton, show) }
    }
}
