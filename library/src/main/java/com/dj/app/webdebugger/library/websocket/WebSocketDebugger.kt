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
package com.dj.app.webdebugger.library.websocket

import android.content.Context
import fi.iki.elonen.NanoWSD
import java.io.IOException
import java.lang.Exception

/**
 * Create by ChenLei on 2019/10/31
 * Describe: WebSocket 服务器
 */

internal class WebSocketDebugger(port: Int) : NanoWSD(port) {

    val webSocketMatchs = ArrayList<IWebSocketMatch>()

    override fun openWebSocket(handshake: IHTTPSession): WebSocket {
        webSocketMatchs.forEach {
            if (it.matchWebSocket(handshake.uri)) {
                try {
                    val webSocket = it.openMatchWebSocket(handshake)
                    if (webSocket != null)
                        return webSocket
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return NonWebSocket(handshake)
    }

    class NonWebSocket(handshake: IHTTPSession) : WebSocket(handshake) {
        override fun onOpen() {
        }

        override fun onClose(
            code: WebSocketFrame.CloseCode?,
            reason: String?,
            initiatedByRemote: Boolean
        ) {
        }

        override fun onPong(pong: WebSocketFrame?) {
        }

        override fun onMessage(message: WebSocketFrame?) {
        }

        override fun onException(exception: IOException?) {
        }
    }
}