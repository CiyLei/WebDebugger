package com.dj.app.webdebugger.library.mars

import android.Manifest
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.common.WebDebuggerConstant
import com.dj.app.webdebugger.library.utils.DeviceUtil
import com.tencent.mars.BaseEvent
import com.tencent.mars.Mars
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import java.util.*

/**
 * Create by ChenLei on 2019/12/7
 * Describe: Mars 服务
 */
internal class MarsServer(val deviceCode: String, val servieHost: String, val servicePort: Int) {

    lateinit var marsStub: MarsStub

    companion object {

        // 申请次数（最多申请5次）
        private var mRequestCount = 0

        // 如果超过5次没有获取到设备号，则直接返回随机值
        private val mRandom = Random()

        var isStart = false

        @JvmStatic
        fun create(context: Context, servieHost: String, servicePort: Int): MarsServer? {
            val imei = DeviceUtil.getIMEI(context)
            if (imei == null && (mRequestCount++) < 5) {
                if (WebDebugger.topActivity != null) {
                    ActivityCompat.requestPermissions(
                        WebDebugger.topActivity!!, arrayOf(
                            Manifest.permission.READ_PHONE_STATE
                        ), WebDebuggerConstant.PERMISSION_PHONE_STATE_RESOURECE
                    )
                }
            } else {
                return MarsServer(
                    imei ?: "随机值(${mRandom.nextInt(100000)})",
                    servieHost,
                    servicePort
                )
            }
            return null
        }
    }

    /**
     * 启动mars服务
     */
    fun start() {
        isStart = true

        marsStub = MarsStub(this)
        Mars.loadDefaultMarsLibrary()

        // set callback
        AppLogic.setCallBack(marsStub)
        StnLogic.setCallBack(marsStub)
        SdtLogic.setCallBack(marsStub)

        // Initialize the Mars PlatformComm
        Mars.init(WebDebugger.context!!, Handler(Looper.getMainLooper()))

        StnLogic.setLonglinkSvrAddr(servieHost, intArrayOf(servicePort))
        StnLogic.setClientVersion(MarsStub.CLIENT_VERSION)
        Mars.onCreate(true)

        BaseEvent.onForeground(true)
        StnLogic.makesureLongLinkConnected()
    }
}
