package com.dj.app.webdebugger.library.websocket

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD

/**
 * Create by ChenLei on 2019/10/31
 * Describe: WebSocket 匹配
 */
interface IWebSocketMatch {

    /**
     * 是否有匹配的WebSocket
     */
    fun matchWebSocket(uri: String): Boolean

    /**
     * 返回匹配的WebSocket
     */
    fun openMatchWebSocket(handshake: NanoHTTPD.IHTTPSession): NanoWSD.WebSocket?
}