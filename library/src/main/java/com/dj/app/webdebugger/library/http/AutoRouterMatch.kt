package com.dj.app.webdebugger.library.http

import android.content.Context
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.annotation.PostMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import com.dj.app.webdebugger.library.utils.ClazzUtils
import fi.iki.elonen.NanoHTTPD
import java.lang.reflect.Method


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 注解方式的路由匹配
 */
internal class AutoRouterMatch(context: Context, scanPackName: String = "") : IHttpRouterMatch {

    val controllers = HashMap<String, HttpController>()
    val getMethods = HashMap<String, ControllerMathod>()
    val postMethods = HashMap<String, ControllerMathod>()

    init {
        // 获取 server 包下面所有的类
        val controllerClassList = ArrayList(
            ClazzUtils.getClassName(
                context,
                if (scanPackName.isEmpty())
                    "${javaClass.`package`.name}.server"
                else
                    scanPackName
            )
        )
        controllerClassList.forEach {
            val controllerClass = context.classLoader.loadClass(it)
            // 遍历所有 Controller 注解过的类
            if (controllerClass.isAnnotationPresent(Controller::class.java) && HttpController::class.java.isAssignableFrom(
                    controllerClass
                )
            ) {
                val controllerUri = controllerClass.getAnnotation(Controller::class.java)?.value
                val controller = controllerClass.newInstance() as? HttpController
                if (controller != null && controllerUri != null) {
                    controller.context = context
                    controllers[controllerUri] = controller
                    // 遍历所有Controller中的方法，找出所有由GetMapping或者PostMapping注解过的方法
                    for (method in controller::class.java.declaredMethods) {
                        if (method.isAnnotationPresent(GetMapping::class.java)) {
                            val getUri = method.getAnnotation(GetMapping::class.java).value
                            method.isAccessible = true
                            getMethods[controllerUri + getUri] =
                                ControllerMathod(controller, method)
                        }
                        if (method.isAnnotationPresent(PostMapping::class.java)) {
                            val postUri = method.getAnnotation(PostMapping::class.java).value
                            method.isAccessible = true
                            postMethods[controllerUri + postUri] =
                                ControllerMathod(controller, method)
                        }
                    }
                }
            }
        }
    }

    override fun matchRouter(uri: String, method: NanoHTTPD.Method): Boolean {
        if (method == NanoHTTPD.Method.GET) {
            return getMethods.containsKey(uri)
        } else if (method == NanoHTTPD.Method.POST) {
            return postMethods.containsKey(uri)
        }
        return false
    }

    override fun handle(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response? {
        if (session.method == NanoHTTPD.Method.GET) {
            val controller = getMethods[session.uri]
            return controller?.method?.invoke(controller.controller, session) as? NanoHTTPD.Response
        }
        if (session.method == NanoHTTPD.Method.POST) {
            val controller = postMethods[session.uri]
            return controller?.method?.invoke(controller.controller, session) as? NanoHTTPD.Response
        }
        return null
    }

    class ControllerMathod(val controller: HttpController, val method: Method)

}