/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dj.app.webdebugger.library.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import android.telephony.TelephonyManager
import com.dj.app.webdebugger.library.WebDebugger
import java.lang.Exception
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * Create by ChenLei on 2019/12/7
 * Describe: 获取设备信息工具类
 */
internal object DeviceUtil {

    fun getDeviceCode(context: Context): String {
        var result = ""
        try {
            result =
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)
        } catch (e: Exception) {
            if (WebDebugger.isDebug) {
                e.printStackTrace()
            }
        }
        if (result.isBlank()) {
            result = UUID.randomUUID().toString().replace("-", "")
        }
        return result
    }

    fun getIMEI(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (tm?.deviceId?.isNotEmpty() == true) {
                    return tm.deviceId
                }
            }
        } catch (e: Exception) {
            if (WebDebugger.isDebug) {
                e.printStackTrace()
            }
        }
        return null
    }

    // 获取ip地址
    fun getLocalInetAddress(): InetAddress? {
        var ip: InetAddress? = null
        try {
            //列举
            val en_netInterface = NetworkInterface.getNetworkInterfaces()
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                val ni = en_netInterface.nextElement() as NetworkInterface//得到下一个元素
                val en_ip = ni.inetAddresses//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement()
                    if (!ip!!.isLoopbackAddress && ip.hostAddress.indexOf(":") == -1)
                        break
                    else
                        ip = null
                }
                if (ip != null) {
                    break
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ip
    }
}
