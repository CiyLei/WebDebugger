package com.dj.app.webdebugger

import com.dj.app.webdebugger.library.annotation.ApiDescription
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 测试Retrofit Api
 */
interface ApiServer {
    @ApiDescription("测试")
    @POST("x/web-interface/search/default")
    fun test(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("test2/tttttttt.do")
    fun test2(): Call<TestResponse<Float>>

    @ApiDescription("测试3")
    @GET("test3/t33333.do")
    fun test3(): Call<Map<String, TestResponse<Double>>>

    @ApiDescription("测试4")
    @GET("test3/t444444.do")
    fun test4(@Query("aq") aq: String): Call<List<List<TestResponse<String>>>>

    @ApiDescription("测试5")
    @GET("test3/555555.do")
    fun test5(@Query("5555aq") aq: String): Call<TestResponse.B>
}