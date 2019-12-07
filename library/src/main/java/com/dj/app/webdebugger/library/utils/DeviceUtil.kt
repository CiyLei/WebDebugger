package com.dj.app.webdebugger.library.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.telephony.TelephonyManager
import java.lang.Exception


/**
 * Create by ChenLei on 2019/12/7
 * Describe: 获取设备信息工具类
 */
internal object DeviceUtil {

    fun getIMEI(context: Context): String? {
        try {
            val tm = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return tm!!.deviceId
            }
        } catch (e: Exception) {
        }
        return null
    }
}
