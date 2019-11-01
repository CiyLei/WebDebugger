package com.dj.app.webdebugger.library

import com.dj.app.webdebugger.library.common.NetInfoBean
import okhttp3.*
import okio.Buffer
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

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
        val t1 = System.currentTimeMillis()
        val response = chain.proceed(request)
        // 请求的时间用时（单位毫秒）
        val timeCost = System.currentTimeMillis() - t1
        val code = response.code()
        val responseHeaders = response.headers().toMultimap()
        // 读取请求body数据
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
        // 读取返回body数据
        val responseBodyStringBuild = StringBuilder()
        val contentType = response.body()?.contentType()?.toString()
        if (contentType == null || contentType.contains("text") || contentType.contains("json")) {
            responseBodyStringBuild.append(responseBodyToString(response.body()!!))
        } else {
            responseBodyStringBuild.append("不支持查看的类型：$contentType")
        }
        WebDebugger.netObservable.notifyObservers(
            NetInfoBean(
                url,
                method,
                requestDataTime,
                timeCost,
                requestHeaders,
                responseHeaders,
                code,
                requestBodyStringBuild.toString(),
                responseBodyStringBuild.toString()
            )
        )
        return response
    }

    private fun requestBodyToString(requestBody: RequestBody): String {
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

    private fun responseBodyToString(responseBody: ResponseBody): String {
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

}