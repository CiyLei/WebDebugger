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
    fun getCachePath(context: Context): File {
        val cacheName = "WebDebuggerCache"
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable()
        ) {
            return File(context.externalCacheDir.path + File.separator + cacheName);
        } else {
            return File(context.cacheDir.path + File.separator + cacheName);
        }
    }
}