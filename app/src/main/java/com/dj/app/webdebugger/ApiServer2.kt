package com.dj.app.webdebugger

import android.os.Handler
import com.dj.app.webdebugger.library.annotation.ApiDescription
import retrofit2.Call
import retrofit2.http.*

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 测试Retrofit Api
 */
interface ApiServer2 {
    @ApiDescription("测试ApiServer2")
    @GET("测试ApiServer2")
    fun test2(): Call<TestResponse<Handler>>
}