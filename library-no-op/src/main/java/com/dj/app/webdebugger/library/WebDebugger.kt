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
package com.dj.app.webdebugger.library

import android.app.Application
import android.content.Context
import android.content.Intent
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import com.dj.app.webdebugger.library.websocket.IWebSocketMatch
import retrofit2.Retrofit


/**
 * Create by ChenLei on 2019/11/3
 * Describe: Web调试
 */
class WebDebugger {
    companion object {
        val httpMatchs = ArrayList<IHttpRouterMatch>()
        val webSocketMatchs = ArrayList<IWebSocketMatch>()

        // 所有的属性抽象
        val viewAttributesList = ArrayList<ViewAttributes<*>>()

        fun openDebug() {

        }

        @JvmStatic
        fun injectionRetrofit(
            retrofit: Retrofit,
            environment: Map<String, String>,
            vararg service: Class<*> = emptyArray()
        ) {

        }

        fun install(application: Application) {

        }

        fun serviceEnable(appAlias: String? = "") {
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {

        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        }
    }
}
