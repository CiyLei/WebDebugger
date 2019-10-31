package com.dj.app.webdebugger.library.http.server.retrofit

import okhttp3.HttpUrl
import retrofit2.Retrofit
import java.lang.reflect.Field
import java.util.Map

/**
 * Create by ChenLei on 2019/10/31
 * Describe: Retrofit 工具类
 */
internal object RetrofitUtil {
    fun replaceRetrofitUrl(retrofit: Retrofit, newUrl: String): Boolean {
        return replaceBaseUrl(retrofit, newUrl) && replaceServiceMethod(retrofit, newUrl)
    }

    /**
     * 更换 Retrofit 的 baseUrl
     */
    private fun replaceBaseUrl(retrofit: Retrofit, newUrl: String): Boolean {
        if (newUrl == retrofit.baseUrl().toString()) {
            return true
        } else {
            val httpUrl = getField(retrofit, "baseUrl")
            if (httpUrl != null) {
                httpUrl.set(retrofit, HttpUrl.get(newUrl))
                return true
            }
        }
        return false
    }

    /**
     * 更换 HttpServiceMethod 的 RequestFactory 的 baseUrl
     */
    private fun replaceServiceMethod(retrofit: Retrofit, newUrl: String): Boolean {
        val serviceMethodCache =
            getField(retrofit, "serviceMethodCache")?.get(retrofit) as? Map<*, *>
        var modifyCount = 0
        if (serviceMethodCache != null) {
            for (entry in serviceMethodCache.entrySet()) {
                val serviceMethod = entry.value
                val requestFactory = getField(serviceMethod, "requestFactory")?.get(serviceMethod)
                if (requestFactory != null) {
                    val baseUrl = getField(requestFactory, "baseUrl")
                    if (baseUrl != null) {
                        baseUrl.set(requestFactory, HttpUrl.get(newUrl))
                        modifyCount++
                    }
                }
            }
        }
        if (modifyCount == serviceMethodCache?.size() ?: 0) {
            return true
        }
        return false
    }

    private fun getField(target: Any?, field: String, targetIsClass: Boolean = false): Field? {
        if (target == null) {
            return null
        }
        var f: Field? = null
        try {
            if (targetIsClass) {
                f = (target as Class<*>).getDeclaredField(field)
            } else {
                f = target::class.java.getDeclaredField(field)
            }
        } catch (e: Exception) {
            f = getField(target::class.java.superclass, field, true)
        }
        f?.isAccessible = true
        return f
    }
}