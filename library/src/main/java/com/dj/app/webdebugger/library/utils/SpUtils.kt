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

import android.content.Context

/**
 * Create by ChenLei on 2021/7/6
 * Describe: sp工具类
 */
object SpUtils {
    /**
     * 保存数据
     */
    fun put(context: Context, key: String, value: String, fileName: String = "WebDebuggerConfig") {
        val sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        sp.edit().putString(key, value).apply()
    }

    /**
     * 读取数据
     */
    fun get(context: Context, key: String, fileName: String = "WebDebuggerConfig"): String? {
        val sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        return sp.getString(key, null)
    }

    /**
     * 清除数据
     */
    fun clear(context: Context, key: String, fileName: String = "WebDebuggerConfig") {
        val sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
        sp.edit().remove(key).apply()
    }
}