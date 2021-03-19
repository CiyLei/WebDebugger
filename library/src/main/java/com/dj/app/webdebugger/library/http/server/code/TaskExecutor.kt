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
package com.dj.app.webdebugger.library.http.server.code

import android.app.Activity
import android.content.Context
import com.dj.app.webdebugger.library.WebDebugger
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Create by ChenLei on 2020/8/7
 * Describe: 任务处理器
 */
abstract class TaskExecutor : Runnable {

    /**
     * 记录打印输出的内容
     */
    private val mByteArrayOutputStream = ByteArrayOutputStream()

    /**
     * 源码中的System.out需要代替为此字段
     */
    @JvmField
    val out = PrintStream(mByteArrayOutputStream)

    override fun run() {
        execute()
    }

    abstract fun execute()

    /**
     * 获取输出的内容
     */
    fun getOutContent() = mByteArrayOutputStream.toString()

    fun getContext(): Context = WebDebugger.context!!

    /**
     * 获取顶上的Activity
     */
    fun getTopActivity(): Activity? = WebDebugger.topActivity

}