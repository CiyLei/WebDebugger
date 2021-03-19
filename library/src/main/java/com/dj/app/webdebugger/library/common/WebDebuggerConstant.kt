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
package com.dj.app.webdebugger.library.common

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 常量
 */

internal object WebDebuggerConstant {
    // 发起截屏的request
    val REQUEST_SCREEN_CAPTURE = 43802
    val SCREEN_CAPTURE_FAILED = "截屏权限申请失败"
    // 发起录屏的request
    val REQUEST_SCREEN_RECORDING = 43803
    val SCREEN_RECORDING_FAILED = "录屏权限申请失败"
    // 申请必要权限request
    val REQUEST_PERMISSION_CODE = 43805
}