package com.dj.app.webdebugger.library

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.dj.app.webdebugger.library.WebDebuggerConstant.PERMISSION_START_RESOURECE
import com.dj.app.webdebugger.library.WebDebuggerConstant.REQUEST_SCREEN_CAPTURE
import com.dj.app.webdebugger.library.WebDebuggerConstant.REQUEST_SCREEN_RECORDING
import com.dj.app.webdebugger.library.WebDebuggerConstant.RESOURCE_SERVER_FAILED_TO_OPEN
import com.dj.app.webdebugger.library.WebDebuggerConstant.SCREEN_CAPTURE_FAILED
import com.dj.app.webdebugger.library.WebDebuggerConstant.SCREEN_RECORDING_FAILED
import com.dj.app.webdebugger.library.http.AssetsRouterMatch
import com.dj.app.webdebugger.library.http.AutoRouterMatch
import com.dj.app.webdebugger.library.http.HttpDebugger
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import com.dj.app.webdebugger.library.http.resource.ResourceDebugger
import com.dj.app.webdebugger.library.http.server.media.MediaProjectionManagerScreenHelp
import com.dj.app.webdebugger.library.websocket.AutoWebSocketMatch
import com.dj.app.webdebugger.library.websocket.IWebSocketMatch
import com.dj.app.webdebugger.library.websocket.WebSocketDebugger
import fi.iki.elonen.NanoHTTPD
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

class WebDebugger {
    companion object {
        val httpMatchs = ArrayList<IHttpRouterMatch>()
        val webSocketMatchs = ArrayList<IWebSocketMatch>()
        internal var context: Context? = null
        internal var httpPort: Int = 8080
        internal var webSocketPort: Int = 8081
        internal var resourcePort: Int = 8082
        internal var retrofit: Retrofit? = null
        internal val environment = HashMap<String, String>()
        internal var topActivity: Activity? = null
        // 媒体变化被观察者
        internal val mediaObservable = MediaObservable()
        // 录屏的实例
        internal var screenRecordingHelp: MediaProjectionManagerScreenHelp? = null

        /**
         * 框架启动入口
         * @httpPort Http服务器端口
         * @webSocketPort WebSocket服务器端口
         * @ResourcePort 资源服务器端口
         */
        @JvmStatic
        fun start(context: Context, httpPort: Int, webSocketPort: Int, resourcePort: Int) {
            this.context = context
            this.httpPort = httpPort
            this.webSocketPort = webSocketPort
            this.resourcePort = resourcePort
            startHttpServer(httpPort, context)
            startWebSocketServer(webSocketPort, context)
            reloadResourceServer()
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
        internal fun reloadResourceServer() {
            ResourceDebugger.create(this.context!!, this.resourcePort!!)?.start(0)
        }

        /**
         * 在Application中初始化（主要的目的是为了获取顶层的Activity）
         */
        fun initApplication(application: Application) {
            application.registerActivityLifecycleCallbacks(object :
                Application.ActivityLifecycleCallbacks {
                override fun onActivityPaused(activity: Activity?) {
                }

                override fun onActivityResumed(activity: Activity?) {
                }

                override fun onActivityStarted(activity: Activity?) {
                    topActivity = activity
                    if (!ResourceDebugger.isStart) {
                        reloadResourceServer()
                    }
                }

                override fun onActivityDestroyed(activity: Activity?) {
                }

                override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                }

                override fun onActivityStopped(activity: Activity?) {
                }

                override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                }
            })
        }

        /**
         * 需要监听申请权限的返回值
         */
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            when (requestCode) {
                // 开启资源服务器
                PERMISSION_START_RESOURECE -> {
                    if (grantResults[0] == PERMISSION_GRANTED) {
                        reloadResourceServer()
                    } else {
                        Toast.makeText(context, RESOURCE_SERVER_FAILED_TO_OPEN, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        /**
         * 截屏和录屏需要用到
         */
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            when (requestCode) {
                // 请求截屏
                REQUEST_SCREEN_CAPTURE -> {
                    if (data == null) {
                        // 申请失败
                        Toast.makeText(context, SCREEN_CAPTURE_FAILED, Toast.LENGTH_LONG).show()
                    } else if (resultCode == Activity.RESULT_OK) {
                        // 开始截屏
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            // 以防太快导致申请权限的弹框还在
                            Handler().postDelayed({
                                val screenCapture =
                                    MediaProjectionManagerScreenHelp(
                                        context!!,
                                        resultCode,
                                        data
                                    )
                                screenCapture.screenCapture(
                                    object :
                                        MediaProjectionManagerScreenHelp.OnImageListener {
                                        override fun onImagePath(fileName: String) {
                                            mediaObservable.notifyObservers()
                                        }
                                    })
                            }, 300)
                        }
                    }
                }
                // 请求录屏
                REQUEST_SCREEN_RECORDING -> {
                    if (data == null) {
                        // 申请失败
                        Toast.makeText(context, SCREEN_RECORDING_FAILED, Toast.LENGTH_LONG).show()
                    } else if (resultCode == Activity.RESULT_OK) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            // 开始录屏
                            if (screenRecordingHelp == null) {
                                screenRecordingHelp =
                                    MediaProjectionManagerScreenHelp(context!!, resultCode, data)
                                screenRecordingHelp!!.startScreenRecording()
                            }
                        }
                    }
                }
            }
        }
    }

    internal class MediaObservable : Observable() {
        override fun notifyObservers() {
            setChanged()
            super.notifyObservers()
        }

        override fun notifyObservers(arg: Any?) {
            setChanged()
            super.notifyObservers(arg)
        }
    }
}