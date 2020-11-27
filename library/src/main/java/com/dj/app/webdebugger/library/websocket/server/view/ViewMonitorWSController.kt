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