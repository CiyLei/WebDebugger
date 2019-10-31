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