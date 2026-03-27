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
    
    // 密码管理
    fun getPassword(): Flow<String>
    suspend fun setPassword(password: String)
    suspend fun verifyPassword(password: String): Boolean
    fun hasPassword(): Flow<Boolean>
}

enum class UserAgentType(val value: String) {
    DESKTOP("desktop"),
    MOBILE("mobile")

}