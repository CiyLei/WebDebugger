package com.dj.app.webdebugger.library.websocket

import android.content.Context
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.utils.ClazzUtils
import com.dj.app.webdebugger.library.websocket.server.WSController
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 注解方式的WebSocket匹配
 */

internal class AutoWebSocketMatch(val context: Context, scanPackName: String = "") : IWebSocketMatch {

    val controllers = HashMap<String, Class<WSController>>()

    init {
        // 获取 server 包下面所有的类
        val controllerClassList = ClazzUtils.getClassName(
            context,
            if (scanPackName.isEmpty())
                "${javaClass.`package`.name}.server"
            else
                scanPackName
        )
        controllerClassList.forEach {
            val controllerClass = context.classLoader.loadClass(it)
            // 遍历所有 Controller 注解过的类
            if (controllerClass.isAnnotationPresent(Controller::class.java) && WSController::class.java.isAssignableFrom(
                    controllerClass
                )
            ) {
                val controllerUri = controllerClass.getAnnotation(Controller::class.java)?.value
                if (controllerUri != null) {
                    controllers[controllerUri] = controllerClass as Class<WSController>
                }
            }
        }
    }

    override fun matchWebSocket(uri: String): Boolean = controllers.containsKey(uri)

    override fun openMatchWebSocket(handshake: NanoHTTPD.IHTTPSession): NanoWSD.WebSocket? {
        val constructor =
            controllers[handshake.uri]?.getConstructor(NanoHTTPD.IHTTPSession::class.java)
        if (constructor != null) {
            val wsController = constructor.newInstance(handshake)
            wsController.context = this.context
            return wsController
        }
        return null
    }

}