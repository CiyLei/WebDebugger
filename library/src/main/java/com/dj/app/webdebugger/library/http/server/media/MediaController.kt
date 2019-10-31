package com.dj.app.webdebugger.library.http.server.media

import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 媒体控制器
 */
@Controller("/media")
internal class MediaController : HttpController() {

    @GetMapping("/screenCapture")
    fun screenCapture(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return success("123.jpg")
    }
}