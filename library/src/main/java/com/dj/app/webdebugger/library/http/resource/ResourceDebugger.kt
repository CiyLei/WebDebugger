package com.dj.app.webdebugger.library.http.resource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.dj.app.webdebugger.library.utils.FileUtil
import fi.iki.elonen.SimpleWebServer
import java.io.File


/**
 * Create by ChenLei on 2019/10/31
 * Describe: 静态资源服务器
 */

internal class ResourceDebugger(port: Int, rootResource: File) :
    SimpleWebServer("0.0.0.0", port, rootResource, true) {

    companion object {
        @JvmStatic
        fun create(context: Context, port: Int): ResourceDebugger? {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cacheFile = FileUtil.getCachePath(context)
                if (!cacheFile.exists()) {
                    cacheFile.mkdirs()
                }
                return ResourceDebugger(port, cacheFile)
            }
            Toast.makeText(context, "WebDebugger资源服务器开启失败，请在设置中开启写入文件的权限并重启app", Toast.LENGTH_LONG)
                .show()
            return null
        }
    }
}