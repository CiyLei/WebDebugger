package com.dj.app.webdebugger.library.http.server.device

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 设备信息模型
 */

internal data class DeviceInfoBean(val port: Int, val groups: ArrayList<Group>, val dbPort: Int) {

    data class Group(val groupName: String, val infos: ArrayList<Info>)
    data class Info(val name: String, val value: String)
}

