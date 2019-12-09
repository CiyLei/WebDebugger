package com.dj.app.webdebugger.library.mars

import android.app.Service
import android.os.Build
import android.os.VibrationEffect
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.proto.AppProto
import com.tencent.mars.app.AppLogic
import com.tencent.mars.sdt.SdtLogic
import com.tencent.mars.stn.StnLogic
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import android.os.Vibrator
import com.dj.app.webdebugger.library.utils.DeviceUtil


/**
 * Create by ChenLei on 2019/12/7
 * Describe: Mars 接口回调
 */
internal class MarsStub(val marsServer: MarsServer) : StnLogic.ICallBack, SdtLogic.ICallBack,
    AppLogic.ICallBack {

    companion object {
        const val CLIENT_VERSION = 200
        const val SEND_DEVICE_APP_CMDID = 10006
        const val SEND_SHOCK_CMDID = 10007
        val TASK_ID_TO_DATA = ConcurrentHashMap<Int, ByteArray>()
    }

    /**
     * 后台的推送监听
     */
    override fun onPush(cmdid: Int, data: ByteArray?) {
        when (cmdid) {
            SEND_SHOCK_CMDID -> {
                // 振动
                val vibrator = WebDebugger.context!!.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(1000)
                }
            }
        }
    }

    /**
     * 发送任务编码
     */
    override fun req2Buf(
        taskID: Int,
        userContext: Any?,
        reqBuffer: ByteArrayOutputStream?,
        errCode: IntArray?,
        channelSelect: Int,
        host: String?
    ): Boolean {
        TASK_ID_TO_DATA[taskID]?.let {
            reqBuffer?.write(it)
            return true
        }
        return false
    }

    /**
     * 回复包变编码
     */
    override fun buf2Resp(
        taskID: Int,
        userContext: Any?,
        respBuffer: ByteArray?,
        errCode: IntArray?,
        channelSelect: Int
    ): Int {
        return StnLogic.RESP_FAIL_HANDLE_NORMAL
    }

    /**
     * 任务结束
     */
    override fun onTaskEnd(taskID: Int, userContext: Any?, errType: Int, errCode: Int): Int {
        return 0
    }

    override fun onNewDns(host: String?): Array<String>? {
        return null
    }

    override fun reportConnectInfo(status: Int, longlinkstatus: Int) {
    }

    /**
     * 流量情况回调
     * @param send
     * @param recv
     */
    override fun trafficData(send: Int, recv: Int) {
    }

    /**
     * Task 的各项指标值的回调
     * @param reportString
     */
    override fun reportTaskProfile(taskString: String?) {
    }

    override fun makesureAuthed(host: String?): Boolean {
        return true
    }

    override fun requestDoSync() {
    }

    /**
     * 长链接连接成功时的首次验证回调
     */
    override fun getLongLinkIdentifyCheckBuffer(
        identifyReqBuf: ByteArrayOutputStream?,
        hashCodeBuffer: ByteArrayOutputStream?,
        reqRespCmdID: IntArray
    ): Int {
        // 每次连接都要送发送设备信息
        val infoBuild = AppProto.Info.newBuilder()
            .setDeviceCode(marsServer.deviceCode)
            .setDeviceName(Build.MODEL)
            .setDeviceType(1)
            .setAddress(DeviceUtil.getLocalInetAddress()?.hostAddress ?: "")
            .setPort(WebDebugger.httpPort)
        if (WebDebugger.appAlias?.isNotEmpty() == true) {
            infoBuild.applicationName = WebDebugger.appAlias
        } else {
            infoBuild.applicationName = WebDebugger.context!!.packageName
        }
        identifyReqBuf?.write(infoBuild.build().toByteArray())
        reqRespCmdID[0] = SEND_DEVICE_APP_CMDID
        return StnLogic.ECHECK_NOW
    }

    /**
     * 长链接验证回复包
     */
    override fun onLongLinkIdentifyResp(buffer: ByteArray?, hashCodeBuffer: ByteArray?): Boolean {
        return true
    }

    override fun isLogoned(): Boolean {
        return false
    }

    override fun requestNetCheckShortLinkHosts(): Array<String> {
        return arrayOf("")
    }

    /**
     * SDT 检测结果的回调
     */
    override fun reportSignalDetectResults(resultsJson: String?) {
    }

    override fun getAppFilePath(): String {
        return WebDebugger.context!!.filesDir.apply {
            if (!exists()) {
                createNewFile()
            }
        }.absolutePath
    }

    /**
     * 这个用户名不知道有什么用，应该是做日志的时候用的
     * 如果不给这个用户会降低stn的频率，所以随便给个
     */
    override fun getAccountInfo(): AppLogic.AccountInfo {
        return AppLogic.AccountInfo(
            Random(System.currentTimeMillis() / 1000).nextInt().toLong(), "WebDebugger"
        )
    }

    override fun getClientVersion(): Int {
        return CLIENT_VERSION
    }

    override fun getDeviceType(): AppLogic.DeviceInfo {
        return AppLogic.DeviceInfo(
            android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL,
            "android-" + android.os.Build.VERSION.SDK_INT
        )
    }

}