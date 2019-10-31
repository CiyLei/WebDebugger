package com.dj.app.webdebugger

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 测试Retrofit Api
 */
interface ApiServer {
    @GET("s?wd=123")
    fun test(): Call<ResponseBody>
}