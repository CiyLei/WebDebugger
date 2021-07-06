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
package com.dj.app.webdebugger

import android.app.Application
import com.dj.app.webdebugger.library.WebDebugger

/**
 * Create by ChenLei on 2019/10/31
 * Describe:
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            WebDebugger.routerNavigation["重新的404"] = "/404.html"
            WebDebugger.routerNavigation["测试"] = "/测试.html"
            WebDebugger.openDebug()
            WebDebugger.serviceEnable("测试WebDebugger")
            WebDebugger.install(this)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}