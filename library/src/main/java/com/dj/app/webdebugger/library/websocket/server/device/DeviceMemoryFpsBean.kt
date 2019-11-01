package com.dj.app.webdebugger.library.websocket.server.device

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 内存和fsp模型
 */

internal data class DeviceMemoryFpsBean(
    val totalMem: Long,
    val totalPrivateDirty: Int,
    val totalPss: Int,
    val fps: Double
)