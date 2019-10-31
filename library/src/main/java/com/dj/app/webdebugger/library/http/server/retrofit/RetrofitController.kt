package com.dj.app.webdebugger.library.http.server.retrofit

import com.dj.app.webdebugger.library.ResponseConstant
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.annotation.PostMapping
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD
import okhttp3.HttpUrl
import java.lang.reflect.Field


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 编辑Retrofit Url 的控制类
 */
@Controller("/retrofit")
internal class RetrofitController : HttpController() {

    @PostMapping("/edit")
    fun handleEditUrl(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return if (WebDebugger.retrofit == null) {
            fail(ResponseConstant.NO_RETROFIT)
        } else {
            session.parseBody(HashMap<String,String>())
            val newUrl = session.parameters["newUrl"]
            if (newUrl?.isNotEmpty() == true) {
                if (RetrofitUtil.replaceRetrofitUrl(WebDebugger.retrofit!!, newUrl[0])) {
                    success(true)
                } else {
                    fail(ResponseConstant.FAIL_EDIT_URL)
                }
            } else {
                fail(201, "缺少newUrl参数")
            }
        }
    }

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
}
