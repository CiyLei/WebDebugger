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
        ScreenUtil.saveScreenCapture(context!!)
        return success(true)
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
                    context!!.getString(R.string.RESOURCE_PORT).toInt(),
                    cachePath.listFiles().map { FileUtil.getMediaCachePath() + it.name }.toList()
                )
            )
        }
        return fail(ResponseConstant.FAILED_ACQUISITION)
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
        return success(true)
    }
}