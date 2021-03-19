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
package com.dj.app.webdebugger.library.utils

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Invocation

/**
 * Create by ChenLei on 2021/2/1
 * Describe: mock工具类
 */
internal object MockUtil {

    // mock列表
    internal val mockMap = HashMap<String, String>()

    /**
     * 添加mock
     */
    fun addMock(methodCode: String, responseContent: String) {
        if (responseContent.isBlank()) {
            mockMap.remove(methodCode)
        } else {
            mockMap[methodCode] = responseContent
        }
    }

    /**
     * mock响应体
     */
    fun mock(request: Request, response: Response): Response {
        val invocation = request.tag(Invocation::class.java) ?: return response
        if (mockMap.containsKey(invocation.method().hashCode().toString())) {
            val content = mockMap[invocation.method().hashCode().toString()] ?: return response
            var body = ResponseBody.create(MediaType.get("application/json"), content)
            if (response.body() != null && response.body()!!.contentType() != null) {
                // 不降原来的response关掉，影响EventListener的回调
                response.close()
                val contentType = response.body()!!.contentType()!!
                body = ResponseBody.create(contentType, content)
            }
            return response.newBuilder().body(body).build()
        }
        return response
    }
}