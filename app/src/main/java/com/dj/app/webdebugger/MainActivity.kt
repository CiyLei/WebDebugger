package com.dj.app.webdebugger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.WebDebuggerInterceptor
import com.dj.app.webdebugger.library.WebDebuggerNetEventListener
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    val okHttpClient =
        OkHttpClient.Builder().addInterceptor(WebDebuggerInterceptor()).eventListener(
            WebDebuggerNetEventListener()
        ).build()
    val retrofit =
        Retrofit.Builder().baseUrl("https://api.bilibili.com").client(okHttpClient).build()
    val apiServer = retrofit.create(ApiServer::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = HashMap<String, String>()
        map["百度"] = "http://www.baidu.com"
        map["搜狗"] = "https://www.sogou.com"
        map["哔哩哔哩"] = "https://api.bilibili.com"
        WebDebugger.injectionRetrofit(retrofit, map, ApiServer::class.java)
        btnTest.setOnClickListener {
            Log.v("v", "v")
            Log.d("d", "d")
            Log.i("i", "i")
            Log.w("w", "w")
            Log.e("e", "e")
            Log.wtf("wtf", "wtf")
            apiServer.test(
                RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    List(100) { it }.joinToString()
                )
            ).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    tvContent.text = t.message
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    tvContent.text = "code:${response.code()} ${response.body()?.string()}"
                }
            })
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
