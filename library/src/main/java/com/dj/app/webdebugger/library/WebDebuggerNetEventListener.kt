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
import okhttp3.*
import okhttp3.EventListener
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create by ChenLei on 2020/9/22
 * Describe: 网络请求监听器
 */
class WebDebuggerNetEventListener : EventListener() {

    override fun connectFailed(
        call: okhttp3.Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        super.connectFailed(call, inetSocketAddress, proxy, protocol, ioe)
        CallManager.instance.get(call).connectFailedTime = System.currentTimeMillis()
    }

    override fun responseHeadersStart(call: okhttp3.Call) {
        super.responseHeadersStart(call)
        CallManager.instance.get(call).responseHeadersStartTime = System.currentTimeMillis()
    }

    override fun connectionAcquired(call: okhttp3.Call, connection: Connection) {
        super.connectionAcquired(call, connection)
        CallManager.instance.get(call).connectionAcquiredTime = System.currentTimeMillis()
    }

    override fun connectionReleased(call: okhttp3.Call, connection: Connection) {
        super.connectionReleased(call, connection)
        CallManager.instance.get(call).connectionReleasedTime = System.currentTimeMillis()
    }

    override fun callEnd(call: okhttp3.Call) {
        super.callEnd(call)
        val netInfo = CallManager.instance.get(call)
        netInfo.callEndTime = System.currentTimeMillis()
        // WebDebuggerNetEventListener的callend和Interceptor到底哪个先触发居然不确定，所以都判断一下
        // 如果已经走完了Interceptor，写入了相应结果还是没有发送数据的话，就发送数据
        if (netInfo.code != 0 && !netInfo.isSent) {
            netInfo.isSent = true
            sendNetInfo(call, netInfo)
        }
    }

    override fun requestHeadersStart(call: okhttp3.Call) {
        super.requestHeadersStart(call)
        CallManager.instance.get(call).requestHeadersStartTime = System.currentTimeMillis()
    }

    override fun requestBodyEnd(call: okhttp3.Call, byteCount: Long) {
        super.requestBodyEnd(call, byteCount)
        CallManager.instance.get(call).requestBodyEndTime = System.currentTimeMillis()
    }

    override fun requestBodyStart(call: okhttp3.Call) {
        super.requestBodyStart(call)
        CallManager.instance.get(call).requestBodyStartTime = System.currentTimeMillis()
    }

    override fun callFailed(call: okhttp3.Call, ioe: IOException) {
        super.callFailed(call, ioe)
        val netInfo = CallManager.instance.get(call)
        netInfo.callFailedTime = System.currentTimeMillis()
        netInfo.callFailError = ioe.toString()
        // 读取详细信息
        try {
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            ioe.printStackTrace(pw)
            netInfo.callFailErrorDetail = sw.toString()
            sw.close()
            pw.close()
        } catch (e: Exception) {
            netInfo.callFailError = e.toString()
        }
        netInfo.isSent = true
        sendNetInfo(call, netInfo)
    }

    override fun connectEnd(
        call: okhttp3.Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol)
        CallManager.instance.get(call).connectEndTime = System.currentTimeMillis()
    }

    override fun responseBodyStart(call: okhttp3.Call) {
        super.responseBodyStart(call)
        CallManager.instance.get(call).responseBodyStartTime = System.currentTimeMillis()
    }

    override fun secureConnectStart(call: okhttp3.Call) {
        super.secureConnectStart(call)
        CallManager.instance.get(call).secureConnectStartTime = System.currentTimeMillis()
    }

    override fun dnsEnd(
        call: okhttp3.Call,
        domainName: String,
        inetAddressList: MutableList<InetAddress>
    ) {
        super.dnsEnd(call, domainName, inetAddressList)
        CallManager.instance.get(call).dnsEndTime = System.currentTimeMillis()
    }

    override fun connectStart(
        call: okhttp3.Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy
    ) {
        super.connectStart(call, inetSocketAddress, proxy)
        CallManager.instance.get(call).connectStartTime = System.currentTimeMillis()
    }

    override fun requestHeadersEnd(call: okhttp3.Call, request: Request) {
        super.requestHeadersEnd(call, request)
        CallManager.instance.get(call).requestHeadersEndTime = System.currentTimeMillis()
    }

    override fun responseHeadersEnd(call: okhttp3.Call, response: okhttp3.Response) {
        super.responseHeadersEnd(call, response)
        CallManager.instance.get(call).responseHeadersEndTime = System.currentTimeMillis()
    }

    override fun callStart(call: okhttp3.Call) {
        super.callStart(call)
        CallManager.instance.get(call).callStartTime = System.currentTimeMillis()
    }

    override fun responseBodyEnd(call: okhttp3.Call, byteCount: Long) {
        super.responseBodyEnd(call, byteCount)
        CallManager.instance.get(call).responseBodyEndTime = System.currentTimeMillis()
    }

    override fun dnsStart(call: okhttp3.Call, domainName: String) {
        super.dnsStart(call, domainName)
        CallManager.instance.get(call).dnsStartTime = System.currentTimeMillis()
    }

    override fun secureConnectEnd(call: okhttp3.Call, handshake: Handshake?) {
        super.secureConnectEnd(call, handshake)
        CallManager.instance.get(call).secureConnectEndTime = System.currentTimeMillis()
    }

    companion object {
        /**
         * 发送请求信息
         */
        internal fun sendNetInfo(call: Call, netInfo: NetInfoBean) {
            val request = call.request()
            // 补充请求信息
            if (netInfo.url.isBlank()) netInfo.url = request.url().toString()
            if (netInfo.method.isBlank()) netInfo.method = request.method()
            if (netInfo.requestDataTime.isBlank()) netInfo.requestDataTime =
                SimpleDateFormat.getDateTimeInstance().format(Date())
            if (netInfo.requestTime == 0L) netInfo.requestTime = System.currentTimeMillis()
            if (netInfo.requestHeaders.isEmpty()) netInfo.requestHeaders =
                WebDebuggerInterceptor.map2map(request.headers().toMultimap())
            if (netInfo.requestBody.isBlank()) netInfo.requestBody =
                WebDebuggerInterceptor.requestToString(request)
            // 发送请求详情
            WebDebugger.netObservable.notifyObservers(netInfo)
            if (WebDebugger.context != null) {
                // 持久化请求记录
                WebDebugger.dataBase.netHistoryDao().addNetHistory(netInfo)
            }
        }
    }
}