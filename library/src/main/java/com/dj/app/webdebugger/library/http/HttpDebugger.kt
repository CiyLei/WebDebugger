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
package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.Exception


/**
 * Create by ChenLei on 2019/10/30
 * Describe: Http 服务器
 */

internal class HttpDebugger(port: Int) : NanoHTTPD(port) {

    val httpMatchs = ArrayList<IHttpRouterMatch>()

    override fun serve(session: IHTTPSession): Response {
        httpMatchs.forEach {
            if (it.matchRouter(session.uri, session.method)) {
                try {
                    val response = it.handle(session)
                    if (response != null)
                        return response.apply {
                            // 指定允许其他域名访问
                            addHeader("Access-Control-Allow-Origin", "*")
                            // 是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回
                            addHeader("Access-Control-Allow-Credentials", "true")
                            // 允许的请求类型
                            addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,POST")
                            // 允许的请求头字段
                            addHeader(
                                "Access-Control-Allow-Headers",
                                "x-requested-with,content-type"
                            )
                            // 预检结果缓存时间
                            addHeader("Access-Control-Max-Age", "1800")
                        }
                } catch (e: Throwable) {
//                    e.printStackTrace()
                    val baos = ByteArrayOutputStream()
                    e.printStackTrace(PrintStream(baos))
                    baos.close()
                    return newFixedLengthResponse(baos.toString())
                }
            }
        }
        return newFixedLengthResponse("Error")
    }
}