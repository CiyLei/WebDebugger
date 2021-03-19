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
package com.dj.app.webdebugger.library.http.server.media

import android.os.Build
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.utils.FileUtil
import com.dj.app.webdebugger.library.utils.ScreenUtil
import com.dj.app.webdebugger.library.websocket.server.media.MediaListBean

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 媒体控制器
 */
@Controller("/media")
internal class MediaController : HttpController() {

    /**
     * 截屏
     */
    @GetMapping("/screenCapture")
    fun screenCapture(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        ScreenUtil.screenCapture(context!!)
        return success()
    }

    /**
     * 查看媒体缓存列表信息
     */
    @GetMapping("/list")
    fun list(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val cachePath = FileUtil.getMediaCacheFile(context!!)
        if (cachePath.isDirectory) {
            return success(
                MediaListBean(
                    WebDebugger.resourcePort,
                    cachePath.listFiles().map { FileUtil.getMediaCachePath() + it.name }.toList().sorted()
                )
            )
        }
        return fail(ResponseConstant.MEDIA_CACHE_ACQUISITION_FAILED)
    }

    /**
     * 清除媒体缓存
     */
    @GetMapping("/clean")
    fun clean(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val cachePath = FileUtil.getMediaCacheFile(context!!)
        if (cachePath.exists()) {
            cachePath.listFiles().forEach {
                it.delete()
            }
        }
        return success()
    }

    /**
     * 开始录屏
     */
    @GetMapping("/startScreenRecording")
    fun startScreenRecording(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ScreenUtil.startScreenRecording(context!!)
            return success()
        }
        return fail(ResponseConstant.RECORDING_SCREENONLY_SUPPORTS_ANDROID5)
    }

    /**
     * 结束录屏
     */
    @GetMapping("/stopScreenRecording")
    fun stopScreenRecording(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            ScreenUtil.stopScreenRecording()
            return success()
        }
        return fail(ResponseConstant.RECORDING_SCREENONLY_SUPPORTS_ANDROID5)
    }
}