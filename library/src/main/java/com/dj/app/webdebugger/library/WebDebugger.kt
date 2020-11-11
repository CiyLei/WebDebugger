package com.dj.app.webdebugger.library

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import androidx.room.Room
import com.dj.app.webdebugger.library.common.ScreenRecordingPrompt
import com.dj.app.webdebugger.library.common.WebDebuggerConstant
import com.dj.app.webdebugger.library.common.WebDebuggerConstant.REQUEST_SCREEN_CAPTURE
import com.dj.app.webdebugger.library.common.WebDebuggerConstant.REQUEST_SCREEN_RECORDING
import com.dj.app.webdebugger.library.common.WebDebuggerConstant.SCREEN_CAPTURE_FAILED
import com.dj.app.webdebugger.library.common.WebDebuggerConstant.SCREEN_RECORDING_FAILED
import com.dj.app.webdebugger.library.db.WebDebuggerDataBase
import com.dj.app.webdebugger.library.http.AssetsRouterMatch
import com.dj.app.webdebugger.library.http.AutoRouterMatch
import com.dj.app.webdebugger.library.http.HttpDebugger
import com.dj.app.webdebugger.library.http.IHttpRouterMatch
import com.dj.app.webdebugger.library.http.resource.ResourceDebugger
import com.dj.app.webdebugger.library.http.server.media.MediaProjectionManagerScreenHelp
import com.dj.app.webdebugger.library.http.server.view.attributes.ViewAttributesImageView
import com.dj.app.webdebugger.library.http.server.view.attributes.ViewAttributesLinearLayout
import com.dj.app.webdebugger.library.http.server.view.attributes.ViewAttributesTextView
import com.dj.app.webdebugger.library.http.server.view.attributes.ViewAttributesView
import com.dj.app.webdebugger.library.mars.MarsServer
import com.dj.app.webdebugger.library.websocket.AutoWebSocketMatch
import com.dj.app.webdebugger.library.websocket.IWebSocketMatch
import com.dj.app.webdebugger.library.websocket.WebSocketDebugger
import com.smarx.notchlib.NotchScreenManager
import com.tencent.mars.BaseEvent
import fi.iki.elonen.NanoHTTPD
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Create by ChenLei on 2019/10/30
 * Describe: Web调试
 */
class WebDebugger {
    companion object {
        internal var httpDebugger: HttpDebugger? = null
        internal var webSocketDebugger: WebSocketDebugger? = null
        internal var resourceDebugger: ResourceDebugger? = null
        val httpMatchs = ArrayList<IHttpRouterMatch>()
        val webSocketMatchs = ArrayList<IWebSocketMatch>()
        internal var context: Context? = null
        internal var httpPort: Int = 8080
        internal var webSocketPort: Int = 8081
        internal var resourcePort: Int = 8082
        internal var retrofit: Retrofit? = null
        internal val environment = HashMap<String, String>()
        internal var apiService: Class<*>? = null
        internal var topActivity: Activity? = null

        // 媒体变化被观察者
        internal val mediaObservable = WebDebuggerObservable()

        // 录屏的实例
        internal var screenRecordingHelp: MediaProjectionManagerScreenHelp? = null

        // 网络调试被观察者
        internal val netObservable = WebDebuggerObservable()

        // 网络调试被观察者
        internal val viewMonitorObservable = WebDebuggerObservable()

        // 录像显示的红点
        internal var screenRecordingPrompt: ScreenRecordingPrompt? = null

        // 是否开启连接后台服务
        internal var serviceEnable = false

        // 应用的别名
        internal var appAlias: String? = null
        internal var serviceHost: String = ""
        internal var servicePort: Int = 8085

        // 是否Debug 决定是否打印日志
        internal var isDebug = false;

        // 数据库对象
        internal val dataBase: WebDebuggerDataBase by lazy {
            // allowMainThreadQueries 允许主线程查询
            // fallbackToDestructiveMigration 设置迁移数据库如果发生错误，将会重新创建数据库，而不是发生崩溃
            Room.databaseBuilder(context!!, WebDebuggerDataBase::class.java, "db_webdebugger.db")
                .addMigrations(WebDebuggerDataBase.MIGRATION_1_TO_2).allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        }

        // 刘海屏的刘海高度（录屏的时候需要用到）
        internal var notchHeight: Int? = null

        // 所有的属性抽象
        val viewAttributesList = ArrayList<ViewAttributes<*>>().apply {

            add(ViewAttributesLinearLayout.Orientation())

            add(ViewAttributesImageView.ScaleType())

            add(ViewAttributesTextView.Text())
            add(ViewAttributesTextView.TextColor())
            add(ViewAttributesTextView.TextSize())
            add(ViewAttributesTextView.Typeface())
            add(ViewAttributesTextView.Hint())
            add(ViewAttributesTextView.HintTextColor())
            add(ViewAttributesTextView.Gravity())

            add(ViewAttributesView.ID())
            add(ViewAttributesView.WidthHeight())
            add(ViewAttributesView.Background())
            add(ViewAttributesView.Visibility())
            add(ViewAttributesView.Enabled())
            add(ViewAttributesView.Margin())
            add(ViewAttributesView.Padding())
//            add(ViewAttributesView.PaddingStartEnd())
            add(ViewAttributesView.Scroll())
            add(ViewAttributesView.XY())
            add(ViewAttributesView.Alpha())
            add(ViewAttributesView.Rotation())
            add(ViewAttributesView.RotationXY())
            add(ViewAttributesView.ScaleXY())
            add(ViewAttributesView.TranslationXY())
        }

        fun openDebug() {
            isDebug = true
        }

        /**
         * 框架启动入口
         * @httpPort Http服务器端口
         * @webSocketPort WebSocket服务器端口
         * @ResourcePort 资源服务器端口
         */
        @JvmStatic
        internal fun start(
            context: Context,
            httpPort: Int,
            webSocketPort: Int,
            resourcePort: Int,
            serviceHost: String,
            servicePort: Int
        ) {
            this.context = context
            this.httpPort = httpPort
            this.webSocketPort = webSocketPort
            this.resourcePort = resourcePort
            this.serviceHost = serviceHost
            this.servicePort = servicePort
            startHttpServer(httpPort, context)
            startWebSocketServer(webSocketPort, context)
            checkPermissions()
        }

        @JvmStatic
        internal fun startHttpServer(port: Int, context: Context) {
            httpDebugger = HttpDebugger(port)
            httpDebugger?.httpMatchs?.addAll(httpMatchs)
            // 添加自带的模块
            httpDebugger?.httpMatchs?.add(AutoRouterMatch(context))
            httpDebugger?.httpMatchs?.add(AssetsRouterMatch(context))
            httpDebugger?.start(NanoHTTPD.SOCKET_READ_TIMEOUT)
        }

        @JvmStatic
        internal fun startWebSocketServer(port: Int, context: Context) {
            webSocketDebugger = WebSocketDebugger(port)
            webSocketDebugger?.webSocketMatchs?.addAll(webSocketMatchs)
            // 添加自带的模块
            webSocketDebugger?.webSocketMatchs?.add(AutoWebSocketMatch(context))
            webSocketDebugger?.start(0)
        }

        /**
         * @environment: 设置预设的环境（key：环境名称，value：环境url）
         */
        @JvmStatic
        fun injectionRetrofit(
            retrofit: Retrofit,
            environment: Map<String, String>,
            service: Class<*>? = null
        ) {
            this.retrofit = retrofit
            this.environment.clear()
            this.environment.putAll(environment)
            this.apiService = service
        }

        /**
         * 检查必要权限
         */
        @JvmStatic
        internal fun checkPermissions() {
            val activity = topActivity ?: return
            val requestPermission = ArrayList<String>()
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_PHONE_STATE
                ) != PERMISSION_GRANTED
            ) {
                requestPermission.add(Manifest.permission.READ_PHONE_STATE)
            }
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PERMISSION_GRANTED
            ) {
                requestPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (requestPermission.isEmpty()) {
                // 所有必要的权限已同意
                reloadResourceServer()
                startMarsServer()
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    requestPermission.toTypedArray(),
                    WebDebuggerConstant.REQUEST_PERMISSION_CODE
                )
            }
        }

        /**
         * 加载资源服务器
         * 因为需要写文件的权限才能开启成功，所以开放这个方法，可以由外部获得权限后调用开启
         */
        @JvmStatic
        internal fun reloadResourceServer() {
            if (ResourceDebugger.isStart) return
            resourceDebugger = ResourceDebugger.create(this.context!!, this.resourcePort)
            resourceDebugger?.start(0)
        }

        /**
         * 开启Mars服务
         */
        @JvmStatic
        internal fun startMarsServer() {
            if (MarsServer.isStart) return
            MarsServer.create(this.context!!, this.serviceHost, this.servicePort)?.start()
        }

        /**
         * 在Application中初始化（主要的目的是为了获取顶层的Activity）
         */
        fun install(application: Application) {
            try {
                val httpPort = application.getString(R.string.HTTP_PORT).toInt()
                val webSocketPort = application.getString(R.string.WEB_SOCKET_PORT).toInt()
                val resourcePort = application.getString(R.string.RESOURCE_PORT).toInt()
                val serviceHost = application.getString(R.string.SERVICEHOST).toString()
                val servicePort = application.getString(R.string.SERVICEPORT).toInt()
                start(application, httpPort, webSocketPort, resourcePort, serviceHost, servicePort)
                screenRecordingPrompt = ScreenRecordingPrompt(application)

                application.registerActivityLifecycleCallbacks(object :
                    Application.ActivityLifecycleCallbacks {
                    override fun onActivityPaused(activity: Activity) {
                        try {
                            if (MarsServer.isStart) {
                                BaseEvent.onForeground(false)
                            }
                        } catch (e: Throwable) {
                            if (isDebug) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onActivityResumed(activity: Activity) {
                        try {
                            if (MarsServer.isStart) {
                                BaseEvent.onForeground(true)
                            }
                        } catch (e: Throwable) {
                            if (isDebug) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onActivityStarted(activity: Activity) {
                        try {
                            topActivity = activity
                            checkPermissions()
                        } catch (e: Throwable) {
                            if (isDebug) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                    }

                    override fun onActivitySaveInstanceState(
                        activity: Activity,
                        outState: Bundle
                    ) {
                    }

                    override fun onActivityStopped(activity: Activity) {
                    }

                    override fun onActivityCreated(
                        activity: Activity,
                        savedInstanceState: Bundle?
                    ) {
                        if (notchHeight == null) {
                            // 获取刘海屏的刘海高度
                            NotchScreenManager.getInstance().getNotchInfo(
                                activity
                            ) { notchScreenInfo ->
                                notchHeight =
                                    if (notchScreenInfo?.hasNotch == true && (notchScreenInfo.notchRects?.size
                                            ?: 0) > 0
                                    ) {
                                        notchScreenInfo.notchRects[0].height()
                                    } else {
                                        0
                                    }
                            }
                        }
                    }
                })
            } catch (e: Throwable) {
                if (isDebug) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * 启动连接后台服务
         */
        fun serviceEnable(appAlias: String? = "") {
            this.serviceEnable = true
            this.appAlias = appAlias
        }

        /**
         * 需要监听申请权限的返回值
         */
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            try {
                when (requestCode) {
                    // 开启资源服务器
                    WebDebuggerConstant.REQUEST_PERMISSION_CODE -> {
                        reloadResourceServer()
                        startMarsServer()
                    }
                }
            } catch (e: Throwable) {
                if (isDebug) {
                    e.printStackTrace()
                }
            }
        }

        /**
         * 截屏和录屏需要用到
         */
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            try {
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
                            Toast.makeText(context, SCREEN_RECORDING_FAILED, Toast.LENGTH_LONG)
                                .show()
                        } else if (resultCode == Activity.RESULT_OK) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (topActivity != null && !Settings.canDrawOverlays(topActivity)) {
                                        // 没有显示录像红点的权限
                                        val intent =
                                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        topActivity?.startActivityForResult(intent, 1)
                                        Toast.makeText(
                                            topActivity,
                                            "没有录屏提示的权限，必须开启且重新开始录屏",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return
                                    }
                                }
                                // 显示录像红点
                                screenRecordingPrompt?.show()
                                // 开始录屏
                                if (screenRecordingHelp == null) {
                                    screenRecordingHelp =
                                        MediaProjectionManagerScreenHelp(
                                            context!!,
                                            resultCode,
                                            data
                                        )
                                    screenRecordingHelp!!.startScreenRecording()
                                }
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                if (isDebug) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal class WebDebuggerObservable : Observable() {
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