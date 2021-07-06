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
package com.dj.app.webdebugger.library.http.server.device

import android.content.Context
import android.os.Build
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD
import java.text.SimpleDateFormat
import java.util.*
import android.view.WindowManager
import android.util.DisplayMetrics
import com.dj.app.webdebugger.library.R
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.utils.DeviceUtil
import com.dj.app.webdebugger.library.utils.FileUtil
import java.io.File
import kotlin.collections.ArrayList


/**
 * Create by ChenLei on 2019/11/1
 * Describe: 查看设备信息
 */
@Controller("/device")
internal class DeviceController : HttpController() {

    @GetMapping("/getAdbNeedInfo")
    fun getScreenInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        getWindowManager()?.let {
            val dm = DisplayMetrics()
            it.defaultDisplay.getMetrics(dm)
            val widthPixels = dm.widthPixels
            val heightPixels = dm.heightPixels
            return success(
                AdbNeedInfoBean(
                    widthPixels,
                    heightPixels,
                    FileUtil.getMediaCacheFile(context!!).absolutePath + File.separator
                )
            )
        }
        return fail(ResponseConstant.GET_DEVICE_SCREEN_FAILED)
    }

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
                        DeviceInfoBean.Info(
                            "IMEI",
                            DeviceUtil.getIMEI(context!!) ?: "无权限，如需查看请在设置中开启"
                        )
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
            ),
            WebDebugger.context!!.getString(R.string.PORT_NUMBER).toInt(),
            ArrayList(WebDebugger.routerNavigation.map {
                DeviceInfoBean.Navigation(
                    it.key,
                    it.value
                )
            })
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

    private fun getWindowManager(): WindowManager? =
        context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
}