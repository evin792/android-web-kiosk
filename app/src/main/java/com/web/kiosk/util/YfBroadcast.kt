package com.web.kiosk.util

import android.content.Context
import android.content.Intent
import com.youngfeel.yf_rk356x_api.YF_RK356x_API_Manager

/**
 * YF-3566/3568 系统广播接口工具类
 * 基于文档：YF-3566/3568 广播接口介绍 v1.0.7.20221029
 *
 * 调用方式：YfBroadcast.yfReboot(context)
 */
object YfBroadcast {

    // ================= 1. 定时开关机 (文档 #1) =================
    fun yfSetTimerSwitch(context: Context, timeOn: IntArray, timeOff: IntArray, enable: Boolean) {
        Intent("com.android.yf_set_timer_swtich").apply {
            putExtra("timeon", timeOn)
            putExtra("timeoff", timeOff)
            putExtra("enable", enable)
            context.sendBroadcast(this)
        }
    }

    // ================= 2. 风扇控制 (文档 #2) =================
    fun yfFanOn(context: Context) = context.sendBroadcast(Intent("com.android.yf_fan_on"))
    fun yfFanOff(context: Context) = context.sendBroadcast(Intent("com.android.yf_fan_off"))

    // ================= 3. OTA 升级 (文档 #3) =================
    fun yfSystemUpdate(context: Context, updatePath: String, targetPackage: String = "android.rockchip.update.service") {
        Intent("yf.action.SYSTEM_UPDATE").apply {
            putExtra("path", updatePath)
            setPackage(targetPackage)
            context.sendBroadcast(this)
        }
    }

    // ================= 4. 软件看门狗 (文档 #4) =================
    fun yfWatchdogOn(context: Context) = context.sendBroadcast(Intent("com.android.yf_watchdog_on"))
    fun yfWatchdogOff(context: Context) = context.sendBroadcast(Intent("com.android.yf_watchdog_off"))
    fun yfWatchdogFeed(context: Context) = context.sendBroadcast(Intent("com.android.yf_watchdog_feed"))
    fun yfWatchdogSetTime(context: Context, seconds: Int) {
        Intent("com.android.yf_watchdog_feed_time").apply {
            putExtra("time", seconds)
            context.sendBroadcast(this)
        }
    }

    // ================= 5. 背光亮度 (文档 #5) =================
    fun yfSetBrightness(context: Context, value: Int) {
        Intent("com.android.yf_set_system_brightness").apply {
            putExtra("value", value.coerceIn(0, 255))
            context.sendBroadcast(this)
        }
    }

    // ================= 6. 背光开关 (文档 #6) =================
    fun yfBacklightOn(context: Context) = context.sendBroadcast(Intent("com.android.lcd_bl_on"))
    fun yfBacklightOff(context: Context) = context.sendBroadcast(Intent("com.android.lcd_bl_off"))

    // ================= 7. 静默安装/卸载 (文档 #7) =================
    fun yfSilentInstall(context: Context, apkPath: String, autoStart: Boolean = false) {
        Intent("com.android.yf_slient_install").apply {
            putExtra("path", apkPath)
            putExtra("isboot", autoStart)
            context.sendBroadcast(this)
        }
    }
    fun yfSilentUninstall(context: Context, packageName: String) {
        Intent("com.android.yf_slient_unstall").apply {
            putExtra("pageage", packageName)  // 文档拼写
            context.sendBroadcast(this)
        }
    }

    // ================= 8-9. 重启/关机 (文档 #8-9) =================
    fun yfReboot(context: Context) = context.sendBroadcast(Intent("com.android.yf_reboot"))
    fun yfShutdown(context: Context) = context.sendBroadcast(Intent("com.android.yf_shutdown"))

    // ================= 10. 延迟启动 App (文档 #10) =================
    fun yfDelayStartApp(context: Context, packageName: String, delaySeconds: Int) {
        Intent("com.android.yf_delay_startapp").apply {
            putExtra("pkgname", packageName)
            putExtra("delaytime", delaySeconds)
            context.sendBroadcast(this)
        }
    }

    // ================= 11-13. 时间/日期设置 (文档 #11-13) =================
    fun yfSetTime(context: Context, hour: Int, minute: Int, second: Int) {
        Intent("com.android.yf_settime").apply {
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("second", second)
            context.sendBroadcast(this)
        }
    }
    fun yfSetDate(context: Context, year: Int, month: Int, day: Int) {
        Intent("com.android.yf_setdate").apply {
            putExtra("year", year)
            putExtra("mon", month)
            putExtra("day", day)
            context.sendBroadcast(this)
        }
    }
    fun yfSetAutoTime(context: Context, enabled: Boolean) {
        Intent("com.android.setautotime").apply {
            putExtra("state", enabled)
            context.sendBroadcast(this)
        }
    }

    // ================= 14. 清理内存 (文档 #14) =================
    fun yfClearMemory(context: Context, showToast: Boolean = false) {
        Intent("com.android.yf_clear_memory").apply {
            putExtra("isShow", showToast)
            context.sendBroadcast(this)
        }
    }

    // ================= 15. 开机自启应用 (文档 #15) =================
    fun yfSetAutoBootApp(context: Context, packageName: String? = null) {
        Intent("com.android.yf_set_auto_bootapp").apply {
            packageName?.let { putExtra("package", it) }
            context.sendBroadcast(this)
        }
    }
    
    /**
     * 设置当前应用为开机自启动
     * @param context 上下文
     */
    fun yfSetAutoBootCurrentApp(context: Context) {
        yfSetAutoBootApp(context, context.packageName)
    }
    
    /**
     * 取消开机自启动
     * @param context 上下文
     */
    fun yfCancelAutoBootApp(context: Context) {
        yfSetAutoBootApp(context, null)
    }

    // ================= 16. HDMI 控制 (文档 #16) =================
    fun yfHdmiControl(context: Context, enabled: Boolean) {
        Intent("com.android.yf_hdmi_control").apply {
            putExtra("value", if (enabled) 1 else 0)
            context.sendBroadcast(this)
        }
    }

    // ================= 17. 设置默认 Launcher (文档 #17) =================
    fun yfSetDefaultLauncher(context: Context, packageName: String, className: String) {
        Intent("com.android.yf_set_defaultLauncher").apply {
            putExtra("pkgname", packageName)
            putExtra("classname", className)
            context.sendBroadcast(this)
        }
    }

    // ================= 18-19. 导航栏/状态栏 (文档 #18-19) =================
    fun yfSetNavigationBarVisible(context: Context, visible: Boolean) {
        Intent("com.android.yf_set_navigation_bar").apply {
            putExtra("value", if (visible) 1 else 0)
            context.sendBroadcast(this)
        }
    }
    fun yfSetStatusBarVisible(context: Context, visible: Boolean) {
        Intent("com.android.yf_set_status_bar").apply {
            putExtra("value", if (visible) 1 else 0)
            context.sendBroadcast(this)
        }
    }
    fun yfHideSystemBars(context: Context) {
        yfSetStatusBarVisible(context, false)
        yfSetNavigationBarVisible(context, false)
    }
    fun yfShowSystemBars(context: Context) {
        yfSetStatusBarVisible(context, true)
        yfSetNavigationBarVisible(context, true)
    }

    // ================= 20-24. WIFI 相关 (文档 #20-24) =================
    fun yfSetWifiSwitch(context: Context, enabled: Boolean) {
        Intent("com.android.yf_set_wifi_switch").apply {
            putExtra("enable", enabled.toString())  // ⚠️ 文档: String "true"/"false"
            context.sendBroadcast(this)
        }
    }
    fun yfConnectWifi(context: Context, ssid: String, password: String = "") {
        Intent("com.android.yf_set_link_wifi").apply {
            putExtra("name", ssid)
            putExtra("password", password)
            context.sendBroadcast(this)
        }
    }
    fun yfSetWifiApSwitch(context: Context, enabled: Boolean) {
        Intent("com.android.yf_set_wifi_ap_switch").apply {
            putExtra("enable", enabled.toString())
            context.sendBroadcast(this)
        }
    }
    fun yfSetWifiAp(context: Context, ssid: String, password: String? = null) {
        Intent("com.android.yf_set_wifi_ap").apply {
            putExtra("name", ssid)
            password?.let { putExtra("password", it) }
            context.sendBroadcast(this)
        }
    }
    fun yfSetWifiApFixedIp(context: Context, enabled: Boolean) {
        Intent("com.android.yf_set_wifi_ap_fixed_ip").apply {
            putExtra("enable", enabled.toString())
            context.sendBroadcast(this)
        }
    }

    // ================= 25. GPIO 控制 (文档 #25) =================
    fun yfSetGpio(context: Context, gpio: String, high: Boolean) {
        Intent("com.android.yf_set_gpio").apply {
            putExtra("gpio", gpio)
            putExtra("value", if (high) 1 else 0)
            context.sendBroadcast(this)
        }
    }
    fun yfFanControl(context: Context, on: Boolean) = yfSetGpio(context, "fan", on)
    fun yfUsbHostPower(context: Context, on: Boolean) = yfSetGpio(context, "usbhost", on)

    // ================= 26. USB 模式切换 (使用 systemShell) =================
    /**
     * 切换 USB 模式为 Host 或 OTG
     * @param context 上下文
     * @param mode 模式："host" 或 "otg"
     * @return 执行是否成功
     */
    fun yfSetUsbMode(context: Context, mode: String): Boolean {
        return try {
            val yfapi = YF_RK356x_API_Manager(context)
            val command = "echo $mode > /sys/devices/platform/fe8a0000.usb2-phy/otg_mode"
            yfapi.systemShell(command)
        } catch (e: Exception) {
            android.util.Log.e("YfBroadcast", "Failed to switch USB mode", e)
            false
        }
    }
}