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
                val contentType = response.body()!!.contentType()!!
                body = ResponseBody.create(contentType, content)
            }
            return response.newBuilder().body(body).build()
        }
        return response
    }
}