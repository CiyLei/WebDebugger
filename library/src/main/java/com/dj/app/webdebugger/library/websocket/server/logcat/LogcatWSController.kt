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
package com.dj.app.webdebugger.library.websocket.server.logcat

import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.websocket.server.WSController
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException
import java.lang.Exception

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 日志查看WebSocket
 */
@Controller("/logcat")
internal class LogcatWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle) {

    private var logcat: LogcatReader? = null

    override fun onOpen() {
        if (logcat == null) {
            logcat = LogcatReader()
            logcat!!.onLogcatListener = object : LogcatReader.OnLogcatListener {
                override fun onLine(line: String) {
                    if (isOpen) {
                        send(line)
                    } else {
                        logcat?.cancel()
                        logcat = null
                    }
                }
            }
            logcat!!.start()
        }
    }

    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean
    ) {
        logcat?.cancel()
        try {
            logcat?.join()
        } catch (ignore: InterruptedException) {
        }
        logcat = null
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
    }

    override fun onException(exception: IOException?) {
        logcat?.cancel()
        try {
            logcat?.join()
        } catch (ignore: InterruptedException) {
        }
        logcat = null
    }

}