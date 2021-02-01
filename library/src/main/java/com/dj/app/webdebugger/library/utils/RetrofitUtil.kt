package com.dj.app.webdebugger.library.utils

import com.dj.app.webdebugger.library.annotation.ApiDescription
import com.dj.app.webdebugger.library.common.ApiInfo
import com.dj.app.webdebugger.library.utils.ClazzUtils.getField
import okhttp3.HttpUrl
import retrofit2.Retrofit
import retrofit2.http.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * Create by ChenLei on 2019/10/31
 * Describe: Retrofit 工具类
 */
internal object RetrofitUtil {

    // apiService 缓存
    var serviceMethodCache: MutableMap<Method, ApiInfo> = ConcurrentHashMap()

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
            for (entry in serviceMethodCache.entries) {
                val serviceMethod = entry.value
                val requestFactory = getField(
                    serviceMethod,
                    "requestFactory"
                )?.get(serviceMethod)
                if (requestFactory != null) {
                    val baseUrl = getField(requestFactory, "baseUrl")
                    if (baseUrl != null) {
                        baseUrl.set(requestFactory, HttpUrl.get(newUrl))
                        modifyCount++
                    }
                }
            }
        }
        return modifyCount == (serviceMethodCache?.size ?: 0)
    }

    /**
     * 分析 apiService
     */
    fun analysisApiService(retrofit: Retrofit, services: List<Class<*>>) {
        for (service in services) {
            for (method in service.declaredMethods) {
                loadServiceMethod(retrofit, method)
            }
        }
    }

    private fun loadServiceMethod(retrofit: Retrofit, method: Method) {
        if (!serviceMethodCache.containsKey(method)) {
            val apiInfo = parseAnnotations(retrofit, method)
            if (apiInfo != null) {
                serviceMethodCache[method] = apiInfo
            }
        }
    }

    private fun parseAnnotations(retrofit: Retrofit, method: Method): ApiInfo? {
        val annotations = method.annotations
        val apiInfo = parseMethodAnnotation(method, annotations)
        val parameterTypes = method.genericParameterTypes
        if (apiInfo != null) {
            for (i in parameterTypes.indices) {
                parseParameter(apiInfo, parameterTypes[i], method.parameterAnnotations[i])
            }
            val adapterType = method.genericReturnType
            try {
                val callAdapter = retrofit.callAdapter(adapterType, annotations)
                apiInfo.returnType = callAdapter.responseType()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        return apiInfo
    }

    private fun parseMethodAnnotation(method: Method, annotations: Array<Annotation>): ApiInfo? {
        var url = ""
        var methodDesc = ""
        var description = ""
        for (annotation in annotations) {
            if (annotation is DELETE && annotation.value.isNotBlank()) {
                url = annotation.value.trim()
                methodDesc = "DELETE"
            }
            if (annotation is GET && annotation.value.isNotBlank()) {
                url = annotation.value.trim()
                methodDesc = "GET"
            }
            if (annotation is POST && annotation.value.isNotBlank()) {
                url = annotation.value.trim()
                methodDesc = "POST"
            }
            if (annotation is PUT && annotation.value.isNotBlank()) {
                url = annotation.value.trim()
                methodDesc = "PUT"
            }
            if (annotation is ApiDescription && annotation.value.isNotBlank()) {
                description = annotation.value.trim()
            }
        }
        if (url.isNotBlank() || methodDesc.isNotBlank() || description.isNotBlank()) {
            return ApiInfo("${method.hashCode()}", url, methodDesc, description)
        }
        return null
    }

    private fun parseParameter(
        apiInfo: ApiInfo,
        parameterType: Type,
        annotations: Array<Annotation>
    ) {
        var typeName = parameterType.toString()
        if (parameterType is Class<*>) {
            typeName = parameterType.name
        }
        for (annotation in annotations) {
            when (annotation) {
                is Query -> {
                    if (annotation.value.isNotBlank()) {
                        apiInfo.requestBody[annotation.value] = typeName
                    }
                }
                is Field -> {
                    if (annotation.value.isNotBlank()) {
                        apiInfo.requestBody[annotation.value] = typeName
                    }
                }
                is Part -> {
                    if (annotation.value.isNotBlank()) {
                        apiInfo.requestBody[annotation.value] = typeName
                    }
                }
                else -> {
                    apiInfo.requestBody["不支持查看的类型 @${annotation.annotationClass.java.name}"] =
                        typeName
                }
            }
        }
    }
}