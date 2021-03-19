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