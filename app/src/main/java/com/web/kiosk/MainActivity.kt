package com.web.kiosk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import com.web.kiosk.app.FullScreenHelper
import com.web.kiosk.app.IdleBrightnessController
import com.web.kiosk.app.NotificationPermissionHelper
import com.web.kiosk.app.StayOnTopServiceStarter
import com.web.kiosk.app.TapUnlockHandler
import com.web.kiosk.components.MainScreen
import com.web.kiosk.components.TouchKioskInputOverlay
import com.web.kiosk.components.TvKioskInputOverlay
import com.web.kiosk.data.KioskSettingsFactory
import com.web.kiosk.ui.theme.ScreenliteWebKioskTheme
import com.web.kiosk.ui.theme.isTvDevice

class MainActivity : ComponentActivity() {
    private lateinit var unlockHandler: TapUnlockHandler
    lateinit var idleController: IdleBrightnessController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 隐藏状态栏和导航栏（基于 YF-356x 广播接口）
        hideSystemBars()

        FullScreenHelper.enableImmersiveMode(this.window)
        StayOnTopServiceStarter.ensureRunning(this)

        unlockHandler = TapUnlockHandler {
            openSettings()
        }

        val settings = KioskSettingsFactory.get(this)
        idleController = IdleBrightnessController(this, settings)

        setContent {
            ScreenliteWebKioskTheme {
                AppContent(unlockHandler, this)
            }
        }
    }

    // 发送广播隐藏系统栏
    private fun hideSystemBars() {
        // 隐藏导航栏（底部）- value: 0=隐藏, 1=显示
        sendBroadcast(Intent("com.android.yf_set_navigation_bar").apply {
            putExtra("value", 0)
        })

        // 隐藏状态栏（顶部）- value: 0=隐藏, 1=显示
        sendBroadcast(Intent("com.android.yf_set_status_bar").apply {
            putExtra("value", 0)
        })

        Log.d("MainActivity", "System bars hidden via YF broadcast")
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        idleController.onUserInteraction()
        return super.dispatchTouchEvent(ev)
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    override fun onPause() {
        super.onPause()
        idleController.stop()
    }

    override fun onResume() {
        super.onResume()
        idleController.start()

        // 可选：onResume 时再次确保隐藏（防止系统恢复）
        hideSystemBars()
    }
}

@Composable
fun AppContent(unlockHandler: TapUnlockHandler, activity: Activity) {
    val context = LocalContext.current
    val idleController = remember { (activity as MainActivity).idleController }
    val isIdleMode by idleController.isIdleMode.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("MainActivity", "Notification permission granted: $isGranted")
    }

    LaunchedEffect(isIdleMode) {
        Log.d("IdleDebug", "Compose: isIdleMode state changed to: $isIdleMode")
    }

    LaunchedEffect(Unit) {
        if (!NotificationPermissionHelper.hasPermission(context)) {
            NotificationPermissionHelper.requestPermission(permissionLauncher)
        }
    }

    val isTv = isTvDevice()

    Box(Modifier.fillMaxSize().background(Color.White)) {
        MainScreen(activity = activity, modifier = Modifier.fillMaxSize())

        if(isTv) {
            TvKioskInputOverlay(onTap = {
                idleController.onUserInteraction()
                unlockHandler.registerTap()
            })
        } else {
            TouchKioskInputOverlay(
                onTap = { unlockHandler.registerTap() },
                modifier = Modifier.align(Alignment.BottomStart),
            )
        }

        val idleFocusRequester = remember { FocusRequester() }

        if (isIdleMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable {
                        idleController.onUserInteraction()
                    }
                    .focusable()
                    .focusRequester(idleFocusRequester)
                    .onKeyEvent {
                        if (it.key == Key.DirectionCenter &&
                            it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                            idleController.onUserInteraction()
                            true
                        } else {
                            false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {}
        }
    }
}