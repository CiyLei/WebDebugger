package com.dj.app.webdebugger

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 测试Retrofit Api
 */
interface ApiServer {
    @GET("s?wd=123")
    fun test(): Call<ResponseBody>

    @GET("test2/tttttttt.do")
    fun test2(): Call<TestResponse>

    @GET("test3/t33333.do")
    fun test3(): Call<List<TestResponse>>

    @GET("test3/t444444.do")
    fun test4(@Query("aq") aq: String): Call<List<List<TestResponse>>>
}