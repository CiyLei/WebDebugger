package com.dj.app.webdebugger.library.common

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 常量
 */

internal object WebDebuggerConstant {
    // 开启资源服务器的请求码
    val PERMISSION_START_RESOURECE = 43801
    val RESOURCE_SERVER_FAILED_TO_OPEN = "资源服务器开启失败，请在设置中开启写入文件的权限，再重启App"
    // 发起截屏的request
    val REQUEST_SCREEN_CAPTURE = 43802
    val SCREEN_CAPTURE_FAILED = "截屏权限申请失败"
    // 发起录屏的request
    val REQUEST_SCREEN_RECORDING = 43803
    val SCREEN_RECORDING_FAILED = "录屏权限申请失败"
    // 获取手机设备码的请求码
    val PERMISSION_PHONE_STATE_RESOURECE = 43804
    val RESOURCE_PHONE_STATE_FAILED_TO_OPEN = "获取手机设备码失败，无法开启连接服务"
}