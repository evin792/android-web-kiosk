package com.web.kiosk.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface KioskSettings {
    fun getCheckInterval(): Flow<Long>
    suspend fun setCheckInterval(interval: Long)
    fun getStartUrl(): Flow<String>
    suspend fun setStartUrl(url: String)
    fun getRotation(): Flow<Rotation>
    suspend fun setRotation(rotation: Rotation)
    fun getIdleTimeout(): Flow<Long>
    suspend fun setIdleTimeout(timeout: Long)
    fun getIdleBrightness(): Flow<Int>
    suspend fun setIdleBrightness(brightness: Int)
    fun getActiveBrightness(): Flow<Int>
    suspend fun setActiveBrightness(brightness: Int)
    fun getUserAgentType(): Flow<UserAgentType>
    suspend fun setUserAgentType(type: UserAgentType)
    
    // 音量管理
    fun getVolume(): Flow<Int>
    suspend fun setVolume(volume: Int)
    
    // USB 模式设置
    fun getUsbMode(): Flow<String>
    suspend fun setUsbMode(mode: String)
}

enum class UserAgentType(val value: String) {
    DESKTOP("desktop"),
    MOBILE("mobile")
}

object KioskConfig {
    /**
     * 是否显示"打开系统设置"按钮
     * true = 显示，false = 不显示
     */
    const val SHOW_SYSTEM_SETTINGS_BUTTON = false
}

private val Context.dataStore by preferencesDataStore(name = "kiosk_settings")

suspend fun Context.clearDataStoreData() {
    try {
        dataStore.edit { preferences ->
            @Suppress("UNCHECKED_CAST")
            val keys = preferences.asMap().keys.toList()
            keys.forEach { key ->
                try {
                    when (key) {
                        is Preferences.Key<*> -> preferences.remove(key as Preferences.Key<Any>)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("DataStoreClear", "Failed to remove key: ${key.name}", e)
                }
            }
        }
    } catch (e: Exception) {
        android.util.Log.e("DataStoreClear", "Failed to clear DataStore", e)
    }
}
