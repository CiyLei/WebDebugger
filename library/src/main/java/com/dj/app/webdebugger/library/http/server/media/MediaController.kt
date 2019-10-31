package com.dj.app.webdebugger.library.http.server.media

import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD
import com.dj.app.webdebugger.library.R
import com.dj.app.webdebugger.library.ResponseConstant
import com.dj.app.webdebugger.library.utils.FileUtil
import com.dj.app.webdebugger.library.utils.ScreenUtil
import com.dj.app.webdebugger.library.websocket.server.media.MediaListBean
import java.io.File

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 媒体控制器
 */
@Controller("/media")
internal class MediaController : HttpController() {

    @GetMapping("/screenCapture")
    fun screenCapture(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        ScreenUtil.saveScreenCapture(context!!)
        return success(true)
    }

    @GetMapping("/list")
    fun list(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val cachePath = File(FileUtil.getCachePath(context!!))
        if (cachePath.isDirectory) {
            return success(
                MediaListBean(
                    context!!.getString(R.string.RESOURCE_PORT).toInt(),
                    cachePath.listFiles().map { it.name }.toList()
                )
            )
        }
        return fail(ResponseConstant.FAILED_ACQUISITION)
    }
}