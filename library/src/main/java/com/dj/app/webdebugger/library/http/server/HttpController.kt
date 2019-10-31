package com.dj.app.webdebugger.library.http.server

import android.content.Context
import com.dj.app.webdebugger.library.BaseResponseBean
import com.dj.app.webdebugger.library.ResponseConstant
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe: Http 基本控制类
 */

open class HttpController {

    var context: Context? = null

    fun success(data: Any, message: String = ""): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse(
            Gson().toJson(
                BaseResponseBean(
                    200,
                    true,
                    data,
                    message
                )
            )
        )
    }

    fun fail(code: Int, data: Any?, message: String = ""): NanoHTTPD.Response {
        return NanoHTTPD.newFixedLengthResponse(
            Gson().toJson(
                BaseResponseBean(
                    code,
                    false,
                    data,
                    message
                )
            )
        )
    }

    fun fail(responseCon: ResponseConstant, data: Any? = null): NanoHTTPD.Response {
        return fail(responseCon.code, data, responseCon.message)
    }
}
