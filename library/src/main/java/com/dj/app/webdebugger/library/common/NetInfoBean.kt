package com.dj.app.webdebugger.library.common

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 网络请求数据
 */

internal data class NetInfoBean(
    val url: String,
    val method: String,
    val requestDataTime: String,
    val timeCost: Long,
    val requestHeaders: Map<String, List<String>>,
    val responseHeaders: Map<String, List<String>>,
    val code: Int,
    val requestBody: String,
    val responseBody: String
)