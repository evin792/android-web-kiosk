package com.web.kiosk.app

import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.web.kiosk.util.YfBroadcast

object FullScreenHelper {
    
    fun enableImmersiveMode(context: Context, window: Window) {
        yfHideSystemBars(context)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableImmersiveModeApi30(window)
        } else {
            @Suppress("DEPRECATION")
            enableImmersiveModeLegacy(window)
        }
    }
    
    private fun yfHideSystemBars(context: Context) {
        YfBroadcast.yfSetStatusBarVisible(context, false)
        YfBroadcast.yfSetNavigationBarVisible(context, false)
    }
    
    private fun enableImmersiveModeApi30(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    
    @Suppress("DEPRECATION")
    private fun enableImmersiveModeLegacy(window: Window) {
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        decorView.systemUiVisibility = uiOptions

        decorView.setOnSystemUiVisibilityChangeListener(null)
        
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                decorView.systemUiVisibility = uiOptions
            }
        }
    }
    
    fun disableImmersiveMode(context: Context, window: Window) {
        yfShowSystemBars(context)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            disableImmersiveModeApi30(window)
        } else {
            @Suppress("DEPRECATION")
            disableImmersiveModeLegacy(window)
        }
    }
    
    private fun yfShowSystemBars(context: Context) {
        YfBroadcast.yfSetStatusBarVisible(context, true)
        YfBroadcast.yfSetNavigationBarVisible(context, true)
    }
    
    private fun disableImmersiveModeApi30(window: Window) {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.show(WindowInsetsCompat.Type.systemBars())
    }
    
    @Suppress("DEPRECATION")
    private fun disableImmersiveModeLegacy(window: Window) {
        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener(null)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}