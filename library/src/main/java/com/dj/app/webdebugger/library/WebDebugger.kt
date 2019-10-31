package com.dj.app.webdebugger.library

import android.content.Context
import com.dj.app.webdebugger.library.http.AssetsRouterMatch
import com.dj.app.webdebugger.library.http.AutoRouterMatch
import com.dj.app.webdebugger.library.http.HttpDebugger
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import com.dj.app.webdebugger.library.websocket.AutoWebSocketMatch
import com.dj.app.webdebugger.library.websocket.IWebSocketMatch
import com.dj.app.webdebugger.library.websocket.WebSocketDebugger
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

class WebDebugger {
    companion object {
        val httpMatchs = ArrayList<IHttpRouterMatch>()
        val webSocketMatchs = ArrayList<IWebSocketMatch>()

        @JvmStatic
        fun start(context: Context, httpPort: Int, webSocketPort: Int) {
            startHttpServer(httpPort, context)
            startWebSocketServer(webSocketPort, context)
        }

        @JvmStatic
        fun startHttpServer(port: Int, context: Context) {
            val httpDebugger = HttpDebugger(port)
            httpDebugger.httpMatchs.addAll(httpMatchs)
            // 添加自带的模块
            httpDebugger.httpMatchs.add(AutoRouterMatch(context))
            httpDebugger.httpMatchs.add(AssetsRouterMatch(context))
            httpDebugger.start(NanoHTTPD.SOCKET_READ_TIMEOUT)
        }

        @JvmStatic
        fun startWebSocketServer(port: Int, context: Context) {
            val webSocketDebugger = WebSocketDebugger(port)
            webSocketDebugger.webSocketMatchs.addAll(webSocketMatchs)
            // 添加自带的模块
            webSocketDebugger.webSocketMatchs.add(AutoWebSocketMatch(context))
            webSocketDebugger.start(0)
        }
    }
}