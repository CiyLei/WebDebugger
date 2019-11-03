package com.dj.app.webdebugger.library

import okhttp3.Interceptor
import okhttp3.Response


/**
 * Create by ChenLei on 2019/11/3
 * Describe:
 */
class WebDebuggerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}