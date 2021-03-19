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
package com.dj.app.webdebugger.library.http.resource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.util.Log
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.utils.FileUtil
import fi.iki.elonen.SimpleWebServer
import java.io.File


/**
 * Create by ChenLei on 2019/10/31
 * Describe: 静态资源服务器
 */

internal class ResourceDebugger(port: Int, rootResource: File) :
    SimpleWebServer("0.0.0.0", port, rootResource, true, "*") {

    companion object {
        var isStart = false

        @JvmStatic
        fun create(context: Context, port: Int): ResourceDebugger? {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cacheFile = FileUtil.getCachePath(context)
                if (!cacheFile.exists()) {
                    cacheFile.mkdirs()
                }
                isStart = true
                return ResourceDebugger(port, cacheFile)
            } else {
                if (WebDebugger.isDebug) {
                    Log.e("ResourceDebugger", "无写入权限，下载服务无法启动")
                }
            }
            return null
        }
    }
}