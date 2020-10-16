package com.dj.app.webdebugger.library.http.server

import android.content.Context
import com.dj.app.webdebugger.library.common.BaseResponseBean
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe: Http 基本控制类
 */

internal open class HttpController {

    var context: Context? = null

    /**
     * 在context注入之后调用
     */
    open fun onStart() {

    }

    fun success(data: Any? = null, message: String = ""): NanoHTTPD.Response {
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

    /**
     * 获取参数
     */
    fun getPostParamt(session: NanoHTTPD.IHTTPSession): Map<String, Any>? {
        val param = HashMap<String, String>()
        session.parseBody(param)
        if (param.containsKey("postData")) {
            val postData = param["postData"]
            return Gson().fromJson<Map<String, Any>>(postData, Map::class.java)
        }
        return null
    }
}
