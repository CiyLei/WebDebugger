package com.dj.app.webdebugger.library.http.resource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.utils.FileUtil
import com.dj.app.webdebugger.library.WebDebuggerConstant.PERMISSION_START_RESOURECE
import fi.iki.elonen.SimpleWebServer
import java.io.File


/**
 * Create by ChenLei on 2019/10/31
 * Describe: 静态资源服务器
 */

internal class ResourceDebugger(port: Int, rootResource: File) :
    SimpleWebServer("0.0.0.0", port, rootResource, true) {

    companion object {
        var isStart = false

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
                isStart = true
                return ResourceDebugger(port, cacheFile)
            } else {
                if (WebDebugger.topActivity != null) {
                    ActivityCompat.requestPermissions(WebDebugger.topActivity!!, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), PERMISSION_START_RESOURECE)
                }
            }
            return null
        }
    }
}