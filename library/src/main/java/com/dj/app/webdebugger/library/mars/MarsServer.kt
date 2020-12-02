package com.dj.app.webdebugger.library.mars

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
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

        var isStart = false

        @JvmStatic
        fun create(context: Context, serviceHost: String, servicePort: Int): MarsServer? {
            val activity = WebDebugger.topActivity ?: return null
            val permission =
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                val deviceCode =
                    DeviceUtil.getIMEI(context) ?: UUID.randomUUID().toString().replace("-", "")
                return MarsServer(deviceCode, serviceHost, servicePort)
            } else {
                if (WebDebugger.isDebug) {
                    Log.e("MarsServer", "无读取设备唯一识别码权限，设备中心服务无法启动")
                }
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
