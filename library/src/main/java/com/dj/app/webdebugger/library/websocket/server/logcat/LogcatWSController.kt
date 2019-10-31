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
class LogcatWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle) {

    private var logcat: LogcatReader? = null

    override fun onOpen() {
        if (logcat == null) {
            logcat = LogcatReader()
            logcat!!.onLogcatListener = object : LogcatReader.OnLogcatListener {
                override fun onLine(line: String) {
                    try {
                        send(line)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        close(NanoWSD.WebSocketFrame.CloseCode.NormalClosure, "", false)
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