package com.dj.app.webdebugger.library

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 常量
 */

internal object WebDebuggerConstant {
    // 开启资源服务器的请求码
    val PERMISSION_START_RESOURECE = 1001
    val RESOURCE_SERVER_FAILED_TO_OPEN = "资源服务器开启失败，请在设置中开启写入文件的权限，再重启App"
    // 发起截屏的request
    val REQUEST_SCREENCAPTURE = 1002
    val SCREEN_CAPTURE_FAILED = "申请失败，截屏失败"
}