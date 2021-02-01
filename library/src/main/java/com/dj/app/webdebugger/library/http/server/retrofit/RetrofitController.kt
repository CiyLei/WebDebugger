package com.dj.app.webdebugger.library.http.server.retrofit

import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.annotation.PostMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import com.dj.app.webdebugger.library.utils.MockUtil
import com.dj.app.webdebugger.library.utils.RetrofitUtil
import fi.iki.elonen.NanoHTTPD
import okhttp3.HttpUrl
import java.lang.reflect.Field


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 编辑Retrofit Url 的控制类
 */
@Controller("/retrofit")
internal class RetrofitController : HttpController() {

    /**
     * 编辑Retrofit的Url
     * @newUrl post参数 新地址
     */
    @PostMapping("/edit")
    fun handleEditUrl(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return if (WebDebugger.retrofit == null) {
            fail(ResponseConstant.NO_RETROFIT)
        } else {
            val param = getPostParamt(session)
            var newUrl = param?.get("newUrl") as? String
            if (newUrl?.isNotEmpty() == true) {
                if (RetrofitUtil.replaceRetrofitUrl(WebDebugger.retrofit!!, newUrl)) {
                    success()
                } else {
                    fail(ResponseConstant.FAIL_EDIT_URL)
                }
            } else {
                fail(201, null, "缺少newUrl参数")
            }
        }
    }

    /**
     * 查看Retrofit的当前url和预配置的地址
     */
    @GetMapping("/info")
    fun handleInfo(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (WebDebugger.retrofit != null) {
            val baseUrl =
                getField(WebDebugger.retrofit!!, "baseUrl").get(WebDebugger.retrofit) as? HttpUrl
            if (baseUrl != null) {
                val environmentList = ArrayList<RetrofitEnvironmentBean>()
                for (entry: Map.Entry<String, String> in WebDebugger.environment) {
                    environmentList.add(RetrofitEnvironmentBean(entry.key, entry.value))
                }
                return success(RetrofitInfoBean(baseUrl.toString(), environmentList))
            } else {
                return fail(ResponseConstant.FAIL_BASE_URL)
            }
        }
        return fail(ResponseConstant.NO_RETROFIT)
    }

    private fun getField(target: Any, field: String): Field {
        val f = target::class.java.getDeclaredField(field)
        f.isAccessible = true
        return f
    }

    /**
     * 查看接口清单
     */
    @GetMapping("/apiList")
    fun handleApiList(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (WebDebugger.retrofit != null && WebDebugger.apiServices != null) {
            synchronized(RetrofitUtil::class.java) {
                RetrofitUtil.serviceMethodCache.clear()
                RetrofitUtil.analysisApiService(
                    WebDebugger.retrofit!!,
                    WebDebugger.apiServices!!.toList()
                )
                return success(RetrofitUtil.serviceMethodCache.values.map { it.toMap() })
            }
        }
        return fail(ResponseConstant.GET_API_LIST_FAILED)
    }

    /**
     * 添加mock
     */
    @PostMapping("/addMock")
    fun addMock(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParameter = getPostParamt(session)
        val methodCode = postParameter?.get("methodCode")?.toString() ?: ""
        val responseContent = postParameter?.get("responseContent")?.toString() ?: ""
        if (methodCode.isNotBlank()) {
            MockUtil.addMock(methodCode, responseContent)
            return success()
        }
        return fail(ResponseConstant.MUST_PARAMETER_URL)
    }
}
