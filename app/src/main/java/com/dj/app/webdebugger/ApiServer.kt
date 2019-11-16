package com.dj.app.webdebugger

import com.dj.app.webdebugger.library.annotation.ApiDescription
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 测试Retrofit Api
 */
interface ApiServer {
    @ApiDescription("测试")
    @GET("x/web-interface/search/default")
    fun test(): Call<ResponseBody>

    @GET("test2/tttttttt.do")
    fun test2(): Call<TestResponse>

    @ApiDescription("测试3")
    @GET("test3/t33333.do")
    fun test3(): Call<Map<String, TestResponse>>

    @ApiDescription("测试4")
    @GET("test3/t444444.do")
    fun test4(@Query("aq") aq: String): Call<List<List<TestResponse>>>
}