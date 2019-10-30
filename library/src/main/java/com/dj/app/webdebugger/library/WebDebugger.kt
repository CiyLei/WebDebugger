package com.dj.app.webdebugger.library

import android.content.Context
import com.dj.app.webdebugger.library.http.AssetsRouterMatch
import com.dj.app.webdebugger.library.http.AutoRouterMatch
import com.dj.app.webdebugger.library.http.HttpDebugger
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

class WebDebugger {
    companion object {
        val matchRegister = ArrayList<IHttpRouterMatch>()

        @JvmStatic
        fun startHttpServer(port: Int, context: Context) {
            val httpDebugger = HttpDebugger(port)
            httpDebugger.matchRegister.addAll(matchRegister)
            // 添加自带的模块
            httpDebugger.matchRegister.add(AutoRouterMatch(context))
            httpDebugger.matchRegister.add(AssetsRouterMatch(context))
            httpDebugger.start(NanoHTTPD.SOCKET_READ_TIMEOUT)
        }
    }
}