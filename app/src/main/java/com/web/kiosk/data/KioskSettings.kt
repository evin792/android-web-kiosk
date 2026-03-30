package com.web.kiosk.data

import kotlinx.coroutines.flow.Flow

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
    
    // 看门狗设置
    fun getWatchdogEnabled(): Flow<Boolean>
    suspend fun setWatchdogEnabled(enabled: Boolean)
    fun getWatchdogFeedInterval(): Flow<Long>
    suspend fun setWatchdogFeedInterval(interval: Long)
    
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