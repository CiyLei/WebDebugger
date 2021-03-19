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
package com.dj.app.webdebugger.library.websocket.server.device

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.view.Choreographer
import androidx.annotation.RequiresApi
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.websocket.server.WSController
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 查看内存、FPS信息的WebSocket
 */
@Controller("/device")
internal class DeviceWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle) {

    private var fpsThread: FPSThread? = null
    private var am: ActivityManager? = null
    private var interval = 1000L
    private var fps: Double = 0.toDouble()

    private val handler = Handler(Looper.getMainLooper())

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isOpen) {
                val systemMemInfo = ActivityManager.MemoryInfo()
                am?.getMemoryInfo(systemMemInfo)
                val processMemInfo = am?.getProcessMemoryInfo(intArrayOf(Process.myPid()))?.get(0)
                // 内存总大小（单位KB）
                var totalMem = 0L
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    totalMem = systemMemInfo.totalMem / 1024
                }
                // 返回应用占用内存大小（单位KB）
                var totalPrivateDirty = processMemInfo?.totalPrivateDirty ?: 0
                // 返回应用实际占用内存大小（单位KB）
                var totalPss = processMemInfo?.totalPss ?: 0
                sendOfJson(DeviceMemoryFpsBean(totalMem, totalPrivateDirty, totalPss, fps))
                handler.postDelayed(this, interval)
            }
        }
    }

    override fun onOpen() {
        if (fpsThread == null) {
            fpsThread = FPSThread()
            fpsThread!!.start()
        }
        am = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        handler.post(runnable)
    }

    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean
    ) {
        handler.removeCallbacks(runnable)
        fpsThread?.quit()
        fpsThread = null
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {

    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {

    }

    override fun onException(exception: IOException?) {
        handler.removeCallbacks(runnable)
        fpsThread?.quit()
        fpsThread = null
    }

    inner class FPSThread : Thread() {

        private var choreographer: Choreographer? = null
        private var startFrameTimeMillis: Long = 0
        private var numFramesRendered: Int = 0
        private var looper: Looper? = null

        override fun run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Looper.prepare()
                looper = Looper.myLooper()
                choreographer = Choreographer.getInstance()
                choreographer!!.postFrameCallback(frameCallback)
                Looper.loop()
            }
        }

        private val frameCallback: Choreographer.FrameCallback =
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            object : Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    val currentFrameTimeMillis = TimeUnit.NANOSECONDS.toMillis(frameTimeNanos)
                    if (startFrameTimeMillis > 0) {
                        val duration = currentFrameTimeMillis - startFrameTimeMillis
                        numFramesRendered++

                        if (duration > interval) {
                            fps = (numFramesRendered * 1000f / duration).toDouble()
                            startFrameTimeMillis = currentFrameTimeMillis
                            numFramesRendered = 0
                        }
                    } else {
                        startFrameTimeMillis = currentFrameTimeMillis
                    }
                    choreographer?.postFrameCallback(this)
                }
            }

        fun quit() {
            looper?.quit()
        }
    }

}