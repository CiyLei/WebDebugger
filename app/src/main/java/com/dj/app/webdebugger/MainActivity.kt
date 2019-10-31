package com.dj.app.webdebugger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dj.app.webdebugger.library.WebDebugger
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder().baseUrl("http://www.baidu.com").build()
    val apiServer = retrofit.create(ApiServer::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val map = HashMap<String, String>()
        map["百度"] = "http://www.baidu.com"
        map["搜狗"] = "https://www.sogou.com"
        WebDebugger.injectionRetrofit(retrofit, map)
        btnTest.setOnClickListener {
            apiServer.test().enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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
        Toast.makeText(this, "34543243545555", Toast.LENGTH_LONG).show()
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
