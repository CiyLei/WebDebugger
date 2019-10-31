package com.dj.app.webdebugger.library.utils

import android.content.Context
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 文件工具类
 */

internal object FileUtil {
    /**
     * 输入流转字符串
     */
    fun inputStreamToString(inputStream: InputStream): String {
        val br = BufferedReader(InputStreamReader(inputStream))
        val sb = StringBuffer()
        var line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        return sb.toString()
    }

    /**
     * 获取缓存目录
     */
    fun getCachePath(context: Context): String {
        val cacheName = "WebDebuggerCache"
        var path = ""
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            || !Environment.isExternalStorageRemovable()
        ) {
            path = context.externalCacheDir.path + File.separator + cacheName + File.separator;
        } else {
            path = context.cacheDir.path + File.separator + cacheName;
        }
        val pathFile = File(path)
        if (!pathFile.exists()) {
            pathFile.mkdirs()
        }
        return path
    }
}