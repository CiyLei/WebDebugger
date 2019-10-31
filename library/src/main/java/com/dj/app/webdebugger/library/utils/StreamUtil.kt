package com.dj.app.webdebugger.library.utils

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 流工具类
 */

internal object StreamUtil {
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
}