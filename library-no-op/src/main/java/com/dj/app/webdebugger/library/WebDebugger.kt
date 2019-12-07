package com.dj.app.webdebugger.library

import android.app.Application
import android.content.Context
import android.content.Intent
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import com.dj.app.webdebugger.library.websocket.IWebSocketMatch
import retrofit2.Retrofit


/**
 * Create by ChenLei on 2019/11/3
 * Describe: Web调试
 */
class WebDebugger {
    companion object {
        val httpMatchs = ArrayList<IHttpRouterMatch>()
        val webSocketMatchs = ArrayList<IWebSocketMatch>()

        @JvmStatic
        fun injectionRetrofit(retrofit: Retrofit, environment: Map<String, String>, service: Class<*>? = null) {

        }

        fun install(application: Application) {

        }

        fun serviceEnable(appAlias: String? = "") {
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {

        }

        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        }
    }
}
