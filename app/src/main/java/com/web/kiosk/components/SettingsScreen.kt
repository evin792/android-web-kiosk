package com.web.kiosk.components

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.web.kiosk.R
import com.web.kiosk.data.KioskConfig
import com.web.kiosk.data.KioskSettingsFactory
import com.web.kiosk.data.Rotation
import com.web.kiosk.data.UserAgentType
import com.web.kiosk.service.StayOnTopService
import com.web.kiosk.util.YfBroadcast
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextOverflow
import com.youngfeel.yf_rk356x_api.YF_RK356x_API_Manager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val kioskSettings = remember { KioskSettingsFactory.get(context) }

    var kioskUrl by remember { mutableStateOf("") }
    var checkIntervalSeconds by remember { mutableStateOf("") }
    var rotation by remember { mutableStateOf(Rotation.ROTATION_0) }
    var idleTimeout by remember { mutableStateOf("") }
    var idleBrightness by remember { mutableStateOf("") }
    var activeBrightness by remember { mutableStateOf("") }
    var userAgentType by remember { mutableStateOf(UserAgentType.DESKTOP) }

    var checkIntervalError by remember { mutableStateOf<String?>(null) }
    var idleTimeoutError by remember { mutableStateOf<String?>(null) }
    var idleBrightnessError by remember { mutableStateOf<String?>(null) }
    var activeBrightnessError by remember { mutableStateOf<String?>(null) }

    val tabs = listOf(
        stringResource(R.string.tab_general),
        stringResource(R.string.tab_display),
        stringResource(R.string.tab_brightness),
        stringResource(R.string.tab_system),
        stringResource(R.string.tab_info)
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "Unknown"
            versionName
        } catch (_: Exception) {
            "Unknown"
        }
    }

    LaunchedEffect(Unit) {
        kioskUrl = kioskSettings.getStartUrl().first()
        checkIntervalSeconds = (kioskSettings.getCheckInterval().first() / 1000).toString()
        rotation = kioskSettings.getRotation().first()
        idleTimeout = kioskSettings.getIdleTimeout().first().toString()
        idleBrightness = kioskSettings.getIdleBrightness().first().toString()
        activeBrightness = kioskSettings.getActiveBrightness().first().toString()
        userAgentType = kioskSettings.getUserAgentType().first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineLarge) },
                actions = {
                    Text(
                        text = "v$appVersion",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(selectedTabIndex = selectedTabIndex, edgePadding = 16.dp) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> GeneralSettingsTab(
                    kioskUrl = kioskUrl,
                    onKioskUrlChange = { kioskUrl = it },
                    checkIntervalSeconds = checkIntervalSeconds,
                    onCheckIntervalChange = { checkIntervalSeconds = it },
                    checkIntervalError = checkIntervalError,
                    onCheckIntervalErrorChange = { checkIntervalError = it },
                    userAgentType = userAgentType,
                    onUserAgentTypeChange = { userAgentType = it }
                )
                1 -> DisplaySettingsTab(
                    rotation = rotation,
                    onRotationChange = { rotation = it },
                    idleTimeout = idleTimeout,
                    onIdleTimeoutChange = { idleTimeout = it },
                    idleTimeoutError = idleTimeoutError,
                    onIdleTimeoutErrorChange = { idleTimeoutError = it }
                )
                2 -> BrightnessSettingsTab(
                    idleBrightness = idleBrightness,
                    activeBrightness = activeBrightness,
                    onIdleBrightnessChange = { idleBrightness = it },
                    onActiveBrightnessChange = { activeBrightness = it },
                    idleBrightnessError = idleBrightnessError,
                    activeBrightnessError = activeBrightnessError,
                    onIdleBrightnessErrorChange = { idleBrightnessError = it },
                    onActiveBrightnessErrorChange = { activeBrightnessError = it }
                )
                3 -> SystemSettingsTabContent(
                    context = context,
                    onReboot = { YfBroadcast.yfReboot(context) },
                    onOpenSettings = {
                        Intent(Settings.ACTION_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    }
                )
                4 -> InfoSettingsTab(
                    context = context
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End)
            ) {
                OutlinedButton(
                    onClick = { (context as? ComponentActivity)?.finish() },
                    modifier = Modifier
                        .widthIn(min = 120.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(
                        stringResource(R.string.button_cancel),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {
                        var hasError = false

                        val checkIntervalValue = checkIntervalSeconds.toLongOrNull()
                        if (checkIntervalSeconds.isBlank()) { checkIntervalError = "Required"; hasError = true }
                        else if (checkIntervalValue == null || checkIntervalValue !in 1..99999) { checkIntervalError = "Invalid"; hasError = true }

                        val idleTimeoutValue = idleTimeout.toLongOrNull()
                        if (idleTimeoutValue == null || idleTimeoutValue < 0) { idleTimeoutError = "Must be ≥ 0"; hasError = true }

                        val idleBrightnessValue = idleBrightness.toIntOrNull()
                        if (idleBrightnessValue == null || idleBrightnessValue !in 0..100) { idleBrightnessError = "0–100"; hasError = true }

                        val activeBrightnessValue = activeBrightness.toIntOrNull()
                        if (activeBrightnessValue == null || activeBrightnessValue !in 0..100) { activeBrightnessError = "0–100"; hasError = true }

                        if (hasError) return@Button

                        (context as? ComponentActivity)?.lifecycleScope?.launch {
                            kioskSettings.setCheckInterval(checkIntervalValue!! * 1000L)
                            kioskSettings.setStartUrl(kioskUrl)
                            kioskSettings.setRotation(rotation)
                            kioskSettings.setIdleTimeout(idleTimeoutValue!!)
                            kioskSettings.setIdleBrightness(idleBrightnessValue!!)
                            kioskSettings.setActiveBrightness(activeBrightnessValue!!)
                            kioskSettings.setUserAgentType(userAgentType)

                            StayOnTopService.restart(context)
                        }
                        (context as? ComponentActivity)?.finish()
                    },
                    modifier = Modifier
                        .widthIn(min = 120.dp)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium,
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(
                        stringResource(R.string.button_save),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun InfoSettingsTab(context: Context) {
    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "Unknown"
            versionName
        } catch (_: Exception) {
            "Unknown"
        }
    }

    val firmwareVersion = remember {
        try {
            android.os.Build.DISPLAY ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    val hardwareModel = remember {
        try {
            val manufacturer = android.os.Build.MANUFACTURER ?: ""
            val model = android.os.Build.MODEL ?: ""
            if (manufacturer.isNotEmpty() && model.isNotEmpty()) {
                "$manufacturer $model"
            } else {
                model.ifEmpty { "Unknown" }
            }
        } catch (_: Exception) {
            "Unknown"
        }
    }

    val deviceId = remember {
        try {
            val cpuInfo = java.io.File("/proc/cpuinfo").readText()
            val serialRegex = Regex("Serial\\s*:\\s*(.+)")
            val match = serialRegex.find(cpuInfo)
            match?.groupValues?.get(1)?.trim() ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    val yfApi = remember {
        try {
            YF_RK356x_API_Manager(context)
        } catch (_: Exception) {
            null
        }
    }

    val screenResolution = remember {
        try {
            val width = yfApi?.yfgetScreenWidth() ?: 0
            val height = yfApi?.yfgetScreenHeight() ?: 0
            if (width > 0 && height > 0) "${width}×${height}" else "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    val ramSize = remember {
        try {
            yfApi?.yfgetRAMSize() ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    val storageSize = remember {
        try {
            yfApi?.yfgetInternalStorageMemory() ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.info_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.app_version_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = appVersion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.firmware_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = firmwareVersion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.hardware_model_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hardwareModel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.device_id_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = deviceId,
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.resolution_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = screenResolution,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.ram_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = ramSize,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.storage_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = storageSize,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun SystemSettingsTabContent(
    context: Context,
    onReboot: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var showRebootConfirm by remember { mutableStateOf(false) }

    if (showRebootConfirm) {
        @Suppress("UNUSED_VALUE")
        AlertDialog(
            onDismissRequest = { showRebootConfirm = false },
            title = { Text(stringResource(R.string.confirm_reboot_title)) },
            text = { Text(stringResource(R.string.confirm_reboot_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRebootConfirm = false
                        onReboot()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.button_reboot_confirm), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRebootConfirm = false }) {
                    Text(stringResource(R.string.button_cancel_reboot))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp)
                .align(Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.button_wifi_settings),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Button(
                    onClick = {
                        val wifiIntent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(wifiIntent)
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "设置",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp)
                .align(Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.button_adjust_volume),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Button(
                    onClick = {
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        audioManager.adjustStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_SAME,
                            AudioManager.FLAG_SHOW_UI
                        )
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "调节",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (KioskConfig.SHOW_SYSTEM_SETTINGS_BUTTON) {
            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 300.dp)
                    .align(Alignment.CenterHorizontally),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.button_open_system_settings),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    Button(
                        onClick = onOpenSettings,
                        modifier = Modifier.height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "打开",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { showRebootConfirm = true },
            modifier = Modifier
                .widthIn(max = 300.dp)
                .height(40.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                stringResource(R.string.button_reboot),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.warning_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.warning_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun GeneralSettingsTab(
    kioskUrl: String,
    onKioskUrlChange: (String) -> Unit,
    checkIntervalSeconds: String,
    onCheckIntervalChange: (String) -> Unit,
    checkIntervalError: String?,
    onCheckIntervalErrorChange: (String?) -> Unit,
    userAgentType: UserAgentType,
    onUserAgentTypeChange: (UserAgentType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = kioskUrl,
            onValueChange = onKioskUrlChange,
            label = { Text(stringResource(R.string.settings_kiosk_url_label)) },
            placeholder = { Text(stringResource(R.string.settings_kiosk_url_placeholder)) },
            supportingText = { Text(stringResource(R.string.settings_kiosk_url_desc)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = checkIntervalSeconds,
            onValueChange = { newValue ->
                if (newValue.isBlank() || newValue.matches(Regex("\\d*"))) {
                    onCheckIntervalChange(newValue)
                    onCheckIntervalErrorChange(null)
                }
            },
            label = { Text(stringResource(R.string.settings_check_interval_label)) },
            placeholder = { Text(stringResource(R.string.settings_check_interval_placeholder)) },
            supportingText = { 
                Text(
                    checkIntervalError ?: stringResource(R.string.settings_check_interval_supporting),
                    color = if (checkIntervalError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = checkIntervalError != null,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (checkIntervalError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = if (checkIntervalError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.settings_user_agent_type_label),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.settings_user_agent_type_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = userAgentType == UserAgentType.DESKTOP,
                onClick = { onUserAgentTypeChange(UserAgentType.DESKTOP) },
                label = { Text("💻 " + stringResource(R.string.settings_user_agent_desktop)) },
                leadingIcon = if (userAgentType == UserAgentType.DESKTOP) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = if (userAgentType == UserAgentType.DESKTOP) {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = true,
                        borderColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false
                    )
                }
            )

            FilterChip(
                selected = userAgentType == UserAgentType.MOBILE,
                onClick = { onUserAgentTypeChange(UserAgentType.MOBILE) },
                label = { Text("📱 " + stringResource(R.string.settings_user_agent_mobile)) },
                leadingIcon = if (userAgentType == UserAgentType.MOBILE) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                border = if (userAgentType == UserAgentType.MOBILE) {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = true,
                        borderColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false
                    )
                }
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun DisplaySettingsTab(
    rotation: Rotation,
    onRotationChange: (Rotation) -> Unit,
    idleTimeout: String,
    onIdleTimeoutChange: (String) -> Unit,
    idleTimeoutError: String?,
    onIdleTimeoutErrorChange: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        RotationSelector(rotation = rotation, onRotationChange = onRotationChange)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = idleTimeout,
            onValueChange = {
                if (it.matches(Regex("\\d*"))) {
                    onIdleTimeoutChange(it)
                    onIdleTimeoutErrorChange(null)
                }
            },
            label = { Text(stringResource(R.string.settings_idle_timeout_label)) },
            placeholder = { Text(stringResource(R.string.seconds_before_dimming)) },
            supportingText = { 
                if (idleTimeoutError != null) {
                    Text(
                        idleTimeoutError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = idleTimeoutError != null,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (idleTimeoutError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = if (idleTimeoutError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun BrightnessSettingsTab(
    idleBrightness: String,
    activeBrightness: String,
    onIdleBrightnessChange: (String) -> Unit,
    onActiveBrightnessChange: (String) -> Unit,
    idleBrightnessError: String?,
    activeBrightnessError: String?,
    onIdleBrightnessErrorChange: (String?) -> Unit,
    onActiveBrightnessErrorChange: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = idleBrightness,
            onValueChange = {
                if (it.matches(Regex("\\d*"))) {
                    onIdleBrightnessChange(it)
                    onIdleBrightnessErrorChange(null)
                }
            },
            label = { Text(stringResource(R.string.settings_idle_brightness_label)) },
            placeholder = { Text("0–100") },
            supportingText = { 
                if (idleBrightnessError != null) {
                    Text(
                        idleBrightnessError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = idleBrightnessError != null,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (idleBrightnessError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = if (idleBrightnessError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = activeBrightness,
            onValueChange = {
                if (it.matches(Regex("\\d*"))) {
                    onActiveBrightnessChange(it)
                    onActiveBrightnessErrorChange(null)
                }
            },
            label = { Text(stringResource(R.string.settings_active_brightness_label)) },
            placeholder = { Text("0–100") },
            supportingText = { 
                if (activeBrightnessError != null) {
                    Text(
                        activeBrightnessError,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = activeBrightnessError != null,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (activeBrightnessError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = if (activeBrightnessError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(Modifier.height(24.dp))
    }
}
