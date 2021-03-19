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
