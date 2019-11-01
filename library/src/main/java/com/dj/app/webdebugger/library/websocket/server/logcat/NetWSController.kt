package com.dj.app.webdebugger.library.websocket.server.logcat

import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.common.NetInfoBean
import com.dj.app.webdebugger.library.websocket.server.WSController
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException
import java.util.*

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 网络请求WebSocket
 */
@Controller("/logcat/net")
internal class NetWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle), Observer {

    override fun update(o: Observable?, arg: Any?) {
        if (arg != null && arg is NetInfoBean) {
            sendOfJson(arg)
        }
    }

    override fun onOpen() {
        WebDebugger.netObservable.addObserver(this)
    }

    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean
    ) {
        WebDebugger.netObservable.deleteObserver(this)
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
    }

    override fun onException(exception: IOException?) {
    }

}