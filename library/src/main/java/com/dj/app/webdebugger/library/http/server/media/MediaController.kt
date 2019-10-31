package com.dj.app.webdebugger.library.http.server.media

import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD
import android.content.Context.MEDIA_PROJECTION_SERVICE
import android.media.projection.MediaProjectionManager
import android.os.Build
import com.dj.app.webdebugger.library.ResponseConstant

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 媒体控制器
 */
@Controller("/media")
internal class MediaController : HttpController() {

    @GetMapping("/screenCapture")
    fun screenCapture(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val mMediaProjectionManager =
                context?.getSystemService(MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager

        }
        return fail(ResponseConstant.FAIL_VERSION)
    }
}