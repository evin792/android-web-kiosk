package com.web.kiosk.components

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.web.kiosk.data.KioskSettingsFactory
import com.web.kiosk.data.UserAgentType

private const val TAG = "MainScreen"

@Composable
fun MainScreen(activity: Activity, modifier: Modifier) {
    val context = LocalContext.current
    var url by remember { mutableStateOf("about:blank") }
    var userAgentKey by remember { mutableIntStateOf(0) }
    var currentUserAgentType by remember { mutableStateOf(UserAgentType.DESKTOP) }
    val kioskSettings = remember { KioskSettingsFactory.get(context) }
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var pendingOpenSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kioskSettings.getStartUrl().collect { newUrl ->
            Log.d(TAG, "URL changed: $newUrl")
            url = newUrl
        }
    }

    LaunchedEffect(Unit) {
        kioskSettings.getUserAgentType().collect { newType ->
            Log.d(TAG, "UserAgentType collected: $newType")
            currentUserAgentType = newType
            userAgentKey++
            Log.d(TAG, "UserAgentType changed, key incremented to: $userAgentKey, current type: $currentUserAgentType")
        }
    }
    
    fun openSettings() {
        pendingOpenSettings = true
        showPasswordDialog = true
    }

    Log.d(TAG, "MainScreen recomposition: url=$url, userAgentKey=$userAgentKey, currentUserAgentType=$currentUserAgentType")

    key(url, userAgentKey) {
        Log.d(TAG, "WebViewComponent key triggered: url=$url, userAgentKey=$userAgentKey")
        WebViewComponent(url = url, activity = activity, modifier)
    }
    
    if (showPasswordDialog) {
        PasswordVerificationDialog(
            kioskSettings = kioskSettings,
            onDismiss = { showPasswordDialog = false },
            onSuccess = {
                showPasswordDialog = false
                if (pendingOpenSettings) {
                    activity.startActivity(android.content.Intent(activity, com.web.kiosk.SettingsActivity::class.java))
                    pendingOpenSettings = false
                }
            }
        )
    }
}