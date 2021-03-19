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
package com.dj.app.webdebugger.library.websocket.server.view

import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.websocket.server.WSController
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException
import java.util.*

/**
 * Create by ChenLei on 2020/11/27
 * Describe: View监控选择的WebSocket
 */
@Controller("/view/monitor")
internal class ViewMonitorWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle),
    Observer {

    // 上一个发送的code
    private var mPreHashCode = 0

    override fun update(o: Observable?, arg: Any?) {
        if (arg is Int) {
            if (arg != mPreHashCode) {
                sendOfJson(arg)
                mPreHashCode = arg
            }
        }
    }

    override fun onOpen() {
        WebDebugger.viewMonitorObservable.addObserver(this)
    }

    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean
    ) {
        WebDebugger.viewMonitorObservable.deleteObserver(this)
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
    }

    override fun onException(exception: IOException?) {
    }
}