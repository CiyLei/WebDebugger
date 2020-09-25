package com.dj.app.webdebugger.library.http.server.net

import android.util.LruCache
import com.dj.app.webdebugger.library.common.NetInfoBean
import okhttp3.Call

/**
 * Create by ChenLei on 2020/9/22
 * Describe: 请求管理类
 */
internal class CallManager private constructor() {
    companion object {
        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CallManager() }
    }

    /**
     * 缓存Call对象的地方，用于 [com.dj.app.webdebugger.library.WebDebuggerInterceptor] 和 [com.dj.app.webdebugger.library.WebDebuggerNetEventListener] 共享 Call 对象的地方
     * 最多缓存16个请求
     */
    private val mCallCache = LruCache<Call, NetInfoBean>(16)

    /**
     * 根据Call对象获取请求信息
     */
    @Synchronized
    fun get(call: Call): NetInfoBean {
        if (mCallCache[call] == null) {
            mCallCache.put(call, NetInfoBean())
        }
        return mCallCache[call]
    }
}