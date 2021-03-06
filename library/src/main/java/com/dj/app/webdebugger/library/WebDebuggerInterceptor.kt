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
package com.dj.app.webdebugger.library

import com.dj.app.webdebugger.library.common.NetInfoBean
import com.dj.app.webdebugger.library.http.server.net.CallManager
import com.dj.app.webdebugger.library.utils.MockUtil
import okhttp3.*
import okio.Buffer
import retrofit2.Invocation
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 网络调试的OkHttp拦截器
 */
class WebDebuggerInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url().toString()
        val method = request.method()
        // 开始请求的时间
        val requestDataTime = SimpleDateFormat.getDateTimeInstance().format(Date())
        val requestHeaders = request.headers().toMultimap()
        val requestTime = System.currentTimeMillis()
        var response = chain.proceed(request)
        // 请求的时间用时（单位毫秒）
        val timeCost = System.currentTimeMillis() - requestTime
        // 模拟响应体
        response = MockUtil.mock(request, response)
        val code = response.code()
        val responseHeaders = response.headers().toMultimap()
        // 读取请求body数据
        val requestBodyString = requestToString(request)
        // 读取返回body数据
        val responseBodyStringBuild = StringBuilder()
        val contentType = response.body()?.contentType()?.toString()
        if (contentType == null || contentType.contains("text") || contentType.contains("json")) {
            responseBodyStringBuild.append(responseBodyToString(response.body()!!))
        } else {
            responseBodyStringBuild.append("不支持查看的类型：$contentType")
        }
        CallManager.instance.get(chain.call()).let {
            it.url = url
            it.method = method
            it.requestDataTime = requestDataTime
            it.requestTime = requestTime
            it.timeCost = timeCost
            it.requestHeaders = map2map(requestHeaders)
            it.responseHeaders = map2map(responseHeaders)
            it.code = code
            it.requestBody = requestBodyString
            it.responseBody = responseBodyStringBuild.toString()
            // WebDebuggerNetEventListener的callend和这个到底哪个先触发居然不确定，所以都判断一下
            // 如果结束的call还没发送数据的话，就发送数据
            // 如果没有初始化WebDebuggerNetEventListener(判断connectionAcquiredTime是否等于0) 而且还没发送数据的话，就发送数据
            if ((it.callEndTime != 0L || it.connectionAcquiredTime == 0L) && !it.isSent) {
                it.isSent = true
                WebDebuggerNetEventListener.sendNetInfo(chain.call(), it)
            }
        }
        return response
    }

    companion object {

        /**
         * 请求转字符串
         */
        internal fun requestToString(request: Request): String {
            val requestBodyStringBuild = StringBuilder()
            if (request.body() is MultipartBody) {
                (request.body() as MultipartBody).parts().forEach {
                    val contentType = it.body().contentType().toString()
                    if (contentType.contains("text") || contentType.contains("json")) {
                        requestBodyStringBuild.append(requestBodyToString(it.body())).append("\r\n")
                    } else {
                        requestBodyStringBuild.append("不支持查看的类型：$contentType").append("\r\n")
                    }
                }
            } else if (request.body() != null) {
                requestBodyStringBuild.append(requestBodyToString(request.body()!!))
            }
            return requestBodyStringBuild.toString()
        }

        /**
         * 请求体转字符串
         */
        internal fun requestBodyToString(requestBody: RequestBody): String {
            return try {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                var charset: Charset = Charset.forName("UTF-8")
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    val c = contentType.charset(charset)
                    if (c != null) {
                        charset = c
                    }
                }
                buffer.readString(charset)
            } catch (e: Exception) {
                e.message ?: ""
            }
        }

        /**
         * 响应体转字符串
         */
        internal fun responseBodyToString(responseBody: ResponseBody): String {
            return try {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE)
                val buffer = source.buffer()
                var charset: Charset = Charset.forName("UTF-8")
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    val c = contentType.charset(charset)
                    if (c != null) {
                        charset = c
                    }
                }
                return buffer.clone().readString(charset)
            } catch (e: Exception) {
                e.message ?: ""
            }
        }

        internal fun map2map(map: Map<String, List<String>>): HashMap<String, ArrayList<String>> {
            val m = HashMap<String, ArrayList<String>>()
            map.entries.forEach { e ->
                val list = ArrayList<String>()
                e.value.forEach { v ->
                    list.add(v)
                }
                m[e.key] = list
            }
            return m
        }
    }

}