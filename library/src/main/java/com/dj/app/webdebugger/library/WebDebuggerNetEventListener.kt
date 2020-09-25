package com.dj.app.webdebugger.library

import com.dj.app.webdebugger.library.common.NetInfoBean
import com.dj.app.webdebugger.library.http.server.net.CallManager
import okhttp3.*
import okhttp3.EventListener
import java.io.IOException
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
        sendNetInfo(call, netInfo)
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

    /**
     * 发送请求信息
     */
    private fun sendNetInfo(call: Call, netInfo: NetInfoBean) {
        val request = call.request()
        // 补充请求信息
        if (netInfo.url.isBlank()) netInfo.url = request.url().toString()
        if (netInfo.method.isBlank()) netInfo.method = request.method()
        if (netInfo.requestDataTime.isBlank()) netInfo.requestDataTime =
            SimpleDateFormat.getDateTimeInstance().format(Date())
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