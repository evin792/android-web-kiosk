package com.web.kiosk.util

import android.content.Context
import com.youngfeel.yf_rk356x_api.YF_RK356x_API_Manager

class YfApi(context: Context) {

    private val manager = YF_RK356x_API_Manager(context)

    // --- 系统信息 ---
    fun yfgetAPIVersion(): String = manager.yfgetAPIVersion() // 获取 API 版本号
    fun yfgetAndroidDeviceModel(): String = manager.yfgetAndroidDeviceModel() // 获取设备型号
    fun yfgetAndroidVersion(): String = manager.yfgetAndroidVersion() // 获取 Android 版本号
    fun yfgetSerialNumber(): String = manager.yfgetSerialNumber() // 获取机器 SN 串码
    fun yfgetKernelVersion(): String = manager.yfgetKernelVersion() // 获取内核版本号
    fun yfgetFirmwareVersion(): String = manager.yfgetFirmwareVersion() // 获取固件版本号
    fun yfgetBuildDate(): String = manager.yfgetBuildDate() // 获取系统编译日期
    fun yfgetRAMSize(): String = manager.yfgetRAMSize() // 获取运行内存大小
    fun yfgetInternalStorageMemory(): String = manager.yfgetInternalStorageMemory() // 获取内部存储总大小
    fun yfgetAvailableInternalMemorySize(): String = manager.yfgetAvailableInternalMemorySize() // 获取内部可用存储空间

    // --- 电源与控制 ---
    fun yfsetOnOffTime(timeonArray: IntArray, timeoffArray: IntArray, enable: Boolean) =
        manager.yfsetOnOffTime(timeonArray, timeoffArray, enable) // 设置定时开关机
    fun yfShutDown() = manager.yfShutDown() // 关机
    fun yfReboot() = manager.yfReboot() // 重启
    fun systemShell(command: String): Boolean = manager.systemShell(command) // 执行 adb 命令
    fun yfslientinstallapk(apkpath: String) = manager.yfslientinstallapk(apkpath) // 静默安装 APK
    fun yfsetHumanSensor(time: Int) = manager.yfsetHumanSensor(time) // 设置人体感应时间 (秒)

    // --- 显示与屏幕 ---
    fun yfSetLCDOn() = manager.yfSetLCDOn() // 打开屏背光
    fun yfSetLCDOff() = manager.yfSetLCDOff() // 关闭屏背光
    fun yfsetRotation(degree: String) = manager.yfsetRotation(degree) // 设置屏幕旋转 ("0"/"90"/"180"/"270")
    fun yfgetScreenWidth(): Int = manager.yfgetScreenWidth() // 获取屏幕宽度
    fun yfgetScreenHeight(): Int = manager.yfgetScreenHeight() // 获取屏幕高度
    fun yfsetNavigationBarVisibility(enable: Boolean) = manager.yfsetNavigationBarVisibility(enable) // 设置导航栏显示/隐藏
    fun yfsetStatusBarDisplay(enable: Boolean) = manager.yfsetStatusBarDisplay(enable) // 设置状态栏显示/隐藏 (占位)
    fun yfTakeScreenshot(path: String, name: String) = manager.yfTakeScreenshot(path, name) // 截图

    // --- 网络与通信 ---
    fun yfgetEthMacAddress(): String = manager.yfgetEthMacAddress() // 获取以太网 MAC 地址
    fun yfgetIpAddress(): String = manager.yfgetIpAddress() // 获取以太网 IP 地址
    fun yfsetEthIPAddress(ip: String, mask: String, gateway: String, dns: String): Boolean =
        manager.yfsetEthIPAddress(ip, mask, gateway, dns) // 设置以太网静态 IP
    fun yfsetEthonoff(enable: Boolean): Int = manager.yfsetEthonoff(enable) // 设置以太网开关
    fun yfgetCurrentNetType(): String = manager.yfgetCurrentNetType() // 获取当前网络类型
    fun yfgetUartPath(uartnum: String): String = manager.yfgetUartPath(uartnum) // 获取串口路径

    // --- 存储路径 ---
    fun yfgetInternalSDPath(): String = manager.yfgetInternalSDPath() // 获取内置 SD 卡路径
    fun yfgetSDPath(): String = manager.yfgetSDPath() // 获取外置 SD (TF) 卡路径
    fun yfgetUSBPath(): String = manager.yfgetUSBPath() // 获取 U 盘路径
}