package com.dj.app.webdebugger.library.http.server

import android.content.Context
import com.dj.app.webdebugger.library.BaseResponseBean
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 封装了快捷返回Response的Controller
 */

open class ContextController {

    var context: Context? = null

    fun success(data: Any, message: String = ""): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse(Gson().toJson(BaseResponseBean(200, data, message)))
    }

    fun fail(code: Int, data: Any, message: String = ""): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse(
            Gson().toJson(
                BaseResponseBean(
                    code,
                    data,
                    message
                )
            )
        )
    }
}
