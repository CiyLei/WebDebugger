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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dj.app.webdebugger.library.WebDebugger
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import androidx.annotation.RequiresApi
import com.dj.app.webdebugger.library.common.WebDebuggerConstant.REQUEST_SCREEN_CAPTURE
import com.dj.app.webdebugger.library.common.WebDebuggerConstant.REQUEST_SCREEN_RECORDING
import com.dj.app.webdebugger.library.http.server.media.MediaProjectionManagerScreenHelp
import com.dj.app.webdebugger.library.http.server.media.ScreenRecorderService
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by ChenLei on 2019/10/31
 * Describe: 屏幕工具类
 */
internal object ScreenUtil {

    /**
     * 截屏
     */
    fun screenCapture(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上使用MediaProjectionManager
            requestMediaProjectionManagerScreenCapture()
        } else {
            // 5.0一下尝试截取顶层Activity
            if (WebDebugger.topActivity != null) {
                saveActivityScreenCapture(WebDebugger.topActivity!!)
            }
        }
    }

    /**
     * 保持某个Activity的截屏
     */
    private fun saveActivityScreenCapture(activity: Activity) {
        val dView = activity.window.decorView
        dView.isDrawingCacheEnabled = true
        dView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(dView.drawingCache)
        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                val sdCardPath = FileUtil.getMediaCacheFile(activity).absolutePath
                // 图片文件路径
                val fileName = getScreenCaptureName()
                val filePath = sdCardPath + File.separator + fileName
                val file = File(filePath)
                val os = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                os.flush()
                os.close()
            } catch (e: Exception) {
            }
            WebDebugger.mediaObservable.notifyObservers()
        }
    }

    /**
     * 使用MediaProjectionManager截屏
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun requestMediaProjectionManagerScreenCapture() {
        if (WebDebugger.topActivity != null) {
            val mediaProjectionManager =
                WebDebugger.topActivity!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            WebDebugger.topActivity!!.startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_SCREEN_CAPTURE
            )
        }
    }

    /**
     * 获取截屏文件名称
     */
    fun getScreenCaptureName(): String =
        "${SimpleDateFormat.getDateTimeInstance().format(Date())} 截屏.png"

    /**
     * 开始录屏
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startScreenRecording(context: Context) {
        if (WebDebugger.topActivity != null) {
            val mediaProjectionManager =
                WebDebugger.topActivity!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            WebDebugger.topActivity!!.startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_SCREEN_RECORDING
            )
        }
    }

    /**
     * 停止录屏
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun stopScreenRecording() {
        // 关闭小红点
        WebDebugger.screenRecordingPrompt?.hide()
        // 停止录像
        WebDebugger.screenService?.stopScreenRecording()
        // 通知媒体更新
        WebDebugger.mediaObservable.notifyObservers()
        WebDebugger.screenService = null
    }

    /**
     * 获取录屏文件名称
     */
    fun getScreenRecordingName(): String =
        "${SimpleDateFormat.getDateTimeInstance().format(Date())} 录屏.mp4"

    /**
     * 本地截屏、录屏服务
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getLocalScreenService(context: Context, code: Int, data: Intent) = object : ScreenService {

        val mHelp = MediaProjectionManagerScreenHelp(context, code, data)

        override fun screenshot() {
            mHelp.screenCapture(object : MediaProjectionManagerScreenHelp.OnImageListener {
                override fun onImagePath(fileName: String) {
                    WebDebugger.mediaObservable.notifyObservers()
                }
            })
        }

        override fun startScreenRecording() {
            mHelp.startScreenRecording()
        }

        override fun stopScreenRecording() {
            mHelp.stopScreenRecording()
        }

    }

    /**
     * 远程截屏、录屏服务
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRemoteScreenService(context: Context, code: Int, data: Intent) = object : ScreenService {

        val serviceIntent = Intent(context, ScreenRecorderService::class.java).apply {
            putExtra(ScreenRecorderService.SCREENSHOT_KEY_CODE, code)
            putExtra(ScreenRecorderService.SCREENSHOT_KEY_DATA, data)
        }

        override fun screenshot() {
            context.startForegroundService(serviceIntent.apply {
                action = ScreenRecorderService.ACTION_SCREENSHOT
            })
        }

        override fun startScreenRecording() {
            context.startForegroundService(serviceIntent.apply {
                action = ScreenRecorderService.ACTION_START_SCREEN_RECORDING
            })
        }

        override fun stopScreenRecording() {
            context.startForegroundService(serviceIntent.apply {
                action = ScreenRecorderService.ACTION_STOP_SCREEN_RECORDING
            })
        }

    }

    /**
     * 获取截屏、录屏服务
     */
    fun getScreenService(context: Context, code: Int, data: Intent): ScreenService? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                getRemoteScreenService(context, code, data)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                getLocalScreenService(context, code, data)
            }
            else -> {
                null
            }
        }
    }

    interface ScreenService {
        /**
         * 截屏
         */
        fun screenshot()

        /**
         * 开始录屏
         */
        fun startScreenRecording()

        /**
         * 结束录屏
         */
        fun stopScreenRecording()
    }
}