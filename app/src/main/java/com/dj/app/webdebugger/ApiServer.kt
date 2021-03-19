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
package com.dj.app.webdebugger

import com.dj.app.webdebugger.library.annotation.ApiDescription
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 测试Retrofit Api
 */
interface ApiServer {
    @ApiDescription("测试")
    @POST("x/web-interface/search/default")
    @Headers("123:456")
    fun test(@Body requestBody: RequestBody): Call<ResponseBody>

    @GET("test2/tttttttt.do")
    fun test2(): Call<TestResponse<Float>>

    @ApiDescription("测试3")
    @GET("test3/t33333.do")
    fun test3(): Call<Map<String, TestResponse<Double>>>

    @ApiDescription("测试4")
    @POST("test3/t444444.do")
    fun test4(@Query("aq") aq: String): Call<List<List<TestResponse<String>>>>

    @ApiDescription("测试5")
    @GET("test3/555555.do")
    fun test5(@Query("5555aq") aq: String): Call<TestResponse.B>

    @ApiDescription("测试6")
    @GET("x/web-interface/search/default")
    fun test6(): Call<BiliBiliRes>
}