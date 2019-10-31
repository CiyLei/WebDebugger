package com.dj.app.webdebugger.library.websocket.server

import com.dj.app.webdebugger.library.annotation.Controller
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 日志查看WebSocket
 */
@Controller("/logcat")
class LogcatWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle) {

    override fun onOpen() {

    }

    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean
    ) {
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
    }

    override fun onException(exception: IOException?) {
    }

}