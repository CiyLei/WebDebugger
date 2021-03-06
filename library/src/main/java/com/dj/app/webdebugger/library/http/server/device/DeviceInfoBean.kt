/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dj.app.webdebugger.library.http.server.device

import java.security.acl.Group

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 设备信息模型
 */

internal data class DeviceInfoBean(
    val port: Int,
    val groups: ArrayList<Group>,
    val dbPort: Int,
    val routerNavigation: ArrayList<Navigation>
) {

    data class Group(val groupName: String, val infos: ArrayList<Info>)
    data class Info(val name: String, val value: String)
    data class Navigation(val name: String, val router: String)
}

