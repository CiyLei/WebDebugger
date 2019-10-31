package com.dj.app.webdebugger.library.websocket.server

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD

/**
 * Create by ChenLei on 2019/10/31
 * Describe: WebSocket 基本控制类
 */

abstract class WSController(handle: NanoHTTPD.IHTTPSession) : NanoWSD.WebSocket(handle) {

    var context: Context? = null

}