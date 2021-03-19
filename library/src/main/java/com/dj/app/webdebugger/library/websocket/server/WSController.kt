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
package com.dj.app.webdebugger.library.websocket.server

import android.content.Context
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.lang.Exception
import java.util.concurrent.Executors

/**
 * Create by ChenLei on 2019/10/31
 * Describe: WebSocket 基本控制类
 */

internal abstract class WSController(handle: NanoHTTPD.IHTTPSession) : NanoWSD.WebSocket(handle) {

    companion object {
        val threadPool = Executors.newFixedThreadPool(5)
        val gson = Gson()
    }

    var context: Context? = null

    fun sendOfJson(data: Any) {
        threadPool.execute {
            try {
                send(gson.toJson(data))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}