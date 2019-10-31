package com.dj.app.webdebugger.library

import android.content.Context
import com.dj.app.webdebugger.library.http.AssetsRouterMatch
import com.dj.app.webdebugger.library.http.AutoRouterMatch
import com.dj.app.webdebugger.library.http.HttpDebugger
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import com.dj.app.webdebugger.library.http.resource.ResourceDebugger
import com.dj.app.webdebugger.library.websocket.AutoWebSocketMatch
import com.dj.app.webdebugger.library.websocket.IWebSocketMatch
import com.dj.app.webdebugger.library.websocket.WebSocketDebugger
import fi.iki.elonen.NanoHTTPD
import retrofit2.Retrofit

/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

class WebDebugger {
    companion object {
        val httpMatchs = ArrayList<IHttpRouterMatch>()
        val webSocketMatchs = ArrayList<IWebSocketMatch>()
        internal var retrofit: Retrofit? = null
        internal val environment = HashMap<String, String>()

        /**
         * 框架启动入口
         * @httpPort Http服务器端口
         * @webSocketPort WebSocket服务器端口
         * @ResourcePort 资源服务器端口
         */
        @JvmStatic
        fun start(context: Context, httpPort: Int, webSocketPort: Int, resourcePort: Int) {
            startHttpServer(httpPort, context)
            startWebSocketServer(webSocketPort, context)
            reloadResourceServer(context, resourcePort)
        }

        @JvmStatic
        internal fun startHttpServer(port: Int, context: Context) {
            val httpDebugger = HttpDebugger(port)
            httpDebugger.httpMatchs.addAll(httpMatchs)
            // 添加自带的模块
            httpDebugger.httpMatchs.add(AutoRouterMatch(context))
            httpDebugger.httpMatchs.add(AssetsRouterMatch(context))
            httpDebugger.start(NanoHTTPD.SOCKET_READ_TIMEOUT)
        }

        @JvmStatic
        internal fun startWebSocketServer(port: Int, context: Context) {
            val webSocketDebugger = WebSocketDebugger(port)
            webSocketDebugger.webSocketMatchs.addAll(webSocketMatchs)
            // 添加自带的模块
            webSocketDebugger.webSocketMatchs.add(AutoWebSocketMatch(context))
            webSocketDebugger.start(0)
        }

        /**
         * @environment: 设置预设的环境（key：环境名称，value：环境url）
         */
        @JvmStatic
        fun injectionRetrofit(retrofit: Retrofit, environment: Map<String, String>) {
            this.retrofit = retrofit
            this.environment.clear()
            this.environment.putAll(environment)
        }

        /**
         * 加载资源服务器
         * 因为需要写文件的权限才能开启成功，所以开放这个方法，可以由外部获得权限后调用开启
         */
        @JvmStatic
        fun reloadResourceServer(context: Context, port: Int) {
            ResourceDebugger.create(context, port)?.start(0)
        }
    }
}