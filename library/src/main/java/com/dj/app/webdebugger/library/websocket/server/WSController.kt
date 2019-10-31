package com.dj.app.webdebugger.library.websocket.server

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.lang.Exception
import java.util.concurrent.Executors

/**
 * Create by ChenLei on 2019/10/31
 * Describe: WebSocket 基本控制类
 */

internal abstract class WSController(handle: NanoHTTPD.IHTTPSession) : NanoWSD.WebSocket(handle) {

    companion object {
        val threadPool = Executors.newCachedThreadPool()
    }

    var context: Context? = null

    override fun send(payload: String?) {
        threadPool.execute {
            try {
                super.send(payload)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}