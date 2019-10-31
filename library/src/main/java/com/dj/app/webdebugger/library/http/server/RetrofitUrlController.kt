package com.dj.app.webdebugger.library.http.server

import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 编辑Retrofit Url 的控制类
 */
@Controller("/retrofit")
class RetrofitUrlController : HttpController() {

    @GetMapping("/edit")
    fun handleEditRetrofitUrl(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return success(true)
    }
}
