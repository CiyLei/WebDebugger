package com.dj.app.webdebugger.library.http.server.device

import android.Manifest
import android.content.Context
import android.os.Build
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context.TELEPHONY_SERVICE
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import android.view.WindowManager
import java.lang.Exception
import android.util.DisplayMetrics


/**
 * Create by ChenLei on 2019/11/1
 * Describe: 查看设备信息
 */
@Controller("/device")
internal class DeviceController : HttpController() {

    @GetMapping("/info")
    fun info(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val deviceInfoBean = DeviceInfoBean(
            WebDebugger.webSocketPort, arrayListOf(
                DeviceInfoBean.Group(
                    "基本信息", arrayListOf(
                        DeviceInfoBean.Info("品牌", Build.BRAND),
                        DeviceInfoBean.Info("型号", Build.MODEL),
                        DeviceInfoBean.Info("制造商", Build.MANUFACTURER),
                        DeviceInfoBean.Info("Android 版本", Build.VERSION.RELEASE),
                        DeviceInfoBean.Info("SDK", Build.VERSION.SDK_INT.toString()),
                        DeviceInfoBean.Info("IMEI", getIMEI())
                    )
                ),
                DeviceInfoBean.Group(
                    "详细信息", arrayListOf(
                        DeviceInfoBean.Info(
                            "出厂日期",
                            SimpleDateFormat.getDateTimeInstance().format(Date(Build.TIME))
                        ),
                        DeviceInfoBean.Info("主机地址", Build.HOST),
                        DeviceInfoBean.Info("产品", Build.PRODUCT),
                        DeviceInfoBean.Info("版本信息", Build.TYPE),
                        DeviceInfoBean.Info("标签", Build.TAGS),
                        DeviceInfoBean.Info("驱动", Build.DEVICE),
                        DeviceInfoBean.Info("基板", Build.BOARD),
                        DeviceInfoBean.Info("硬件", Build.HARDWARE),
//                        DeviceInfoBean.Info("设备标识", Build.FINGERPRINT),
                        DeviceInfoBean.Info("用户", Build.USER)
                    )
                )

            )
        )
        getWindowManager()?.let {
            val dm = DisplayMetrics()
            it.defaultDisplay.getMetrics(dm)
            deviceInfoBean.groups.add(
                DeviceInfoBean.Group(
                    "屏幕信息", arrayListOf(
                        DeviceInfoBean.Info("屏幕宽度", dm.widthPixels.toString()),
                        DeviceInfoBean.Info("屏幕高度", dm.heightPixels.toString()),
                        DeviceInfoBean.Info("屏幕密度", dm.density.toString()),
                        DeviceInfoBean.Info(
                            "屏幕宽度(dp)",
                            (dm.widthPixels / dm.density).toInt().toString()
                        ),
                        DeviceInfoBean.Info(
                            "屏幕高度(dp)",
                            (dm.heightPixels / dm.density).toInt().toString()
                        ),
                        DeviceInfoBean.Info("屏幕密度(dpi)", dm.densityDpi.toString())
                    )
                )
            )
        }
        return success(deviceInfoBean)
    }

    private fun getIMEI(): String {
        try {
            val tm = context!!.getSystemService(TELEPHONY_SERVICE) as TelephonyManager?
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return tm!!.deviceId
            }
        } catch (e: Exception) {
        }
        return "无权限，如需查看请在设置中开启"
    }

    private fun getWindowManager(): WindowManager? =
        context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}