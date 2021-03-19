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
package com.dj.app.webdebugger.library.websocket.server.logcat

import android.content.ContentValues.TAG
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 读取日志的线程
 */

internal class LogcatReader : Thread() {

    private var logcatProcess: Process? = null
    private var logcatReader: BufferedReader? = null
    var onLogcatListener: OnLogcatListener? = null

    override fun run() {
        try {
            openLogcatProcess()
            openLogcatReader()
            while (!currentThread().isInterrupted) {
                val logLine: String?
                try {
                    logLine = logcatReader?.readLine()
                } catch (ie: IOException) {
                    break
                }
                if (logLine!= null) {
                    onLogcatListener?.onLine(logLine)
                }
            }
        } finally {
            closeLogcatProcess()
            closeLogcatReader()
        }
    }

    fun cancel() {
        interrupt()
        closeLogcatProcess()
    }

    private fun openLogcatProcess() {
        synchronized(this) {
            if (logcatProcess == null) {
                try {
                    logcatProcess = Runtime.getRuntime().exec(arrayOf("logcat", "-v", "threadtime"))
                } catch (e: IOException) {
                    Log.w(TAG, "Can not execute logcat - " + e.message)
                }

            }
        }
    }

    private fun closeLogcatProcess() {
        synchronized(this) {
            if (logcatProcess != null) {
                logcatProcess!!.destroy()
                logcatProcess = null
            }
        }
    }

    private fun openLogcatReader() {
        synchronized(this) {
            if (logcatProcess != null) {
                if (logcatReader == null) {
                    logcatReader = BufferedReader(InputStreamReader(logcatProcess?.inputStream))
                }
            }
        }
    }

    private fun closeLogcatReader() {
        try {
            if (logcatReader != null) {
                logcatReader!!.close()
                logcatReader = null
            }
        } catch (ignore: IOException) {
        }
    }

    interface OnLogcatListener {
        fun onLine(line: String)
    }
}