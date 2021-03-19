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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.WebDebuggerInterceptor
import com.dj.app.webdebugger.library.WebDebuggerNetEventListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    val okHttpClient =
        OkHttpClient.Builder().addInterceptor(WebDebuggerInterceptor()).eventListener(
            WebDebuggerNetEventListener()
        ).build()
    val retrofit =
        Retrofit.Builder().baseUrl("https://api.bilibili.com")
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build()
    val apiServer = retrofit.create(ApiServer::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = HashMap<String, String>()
        map["百度"] = "http://www.baidu.com"
        map["搜狗"] = "https://www.sogou.com"
        map["哔哩哔哩"] = "https://api.bilibili.com"
        WebDebugger.injectionRetrofit(retrofit, map, ApiServer::class.java, ApiServer2::class.java)
        btnHttpTest.setOnClickListener {
            Log.v("v", "v")
            Log.d("d", "d")
            Log.i("i", "i")
            Log.w("w", "w")
            Log.e("e", "e")
            Log.wtf("wtf", "wtf")
//            apiServer.test(
//                RequestBody.create(
//                    MediaType.parse("application/json; charset=utf-8"),
//                    List(100) { it }.joinToString()
//                )
//            )
            apiServer.test6().enqueue(object : Callback<BiliBiliRes> {
                override fun onFailure(call: Call<BiliBiliRes>, t: Throwable) {
                    t.printStackTrace()
                    tvContent.text = t.message
                }

                override fun onResponse(
                    call: Call<BiliBiliRes>,
                    response: Response<BiliBiliRes>
                ) {
                    tvContent.text = "code:${response.code()} ${response.body()}"
                }
            })
        }
        btnStartTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        WebDebugger.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        WebDebugger.onActivityResult(requestCode, resultCode, data)
    }
}
