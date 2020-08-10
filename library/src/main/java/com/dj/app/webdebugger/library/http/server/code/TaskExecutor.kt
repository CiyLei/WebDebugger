package com.dj.app.webdebugger.library.http.server.code

import android.content.Context
import com.dj.app.webdebugger.library.WebDebugger
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Create by ChenLei on 2020/8/7
 * Describe: 任务处理器
 */
abstract class TaskExecutor : Runnable {

    /**
     * 记录打印输出的内容
     */
    private val mByteArrayOutputStream = ByteArrayOutputStream()

    /**
     * 源码中的System.out需要代替为此字段
     */
    @JvmField
    val out = PrintStream(mByteArrayOutputStream)

    override fun run() {
        execute()
    }

    abstract fun execute()

    /**
     * 获取输出的内容
     */
    fun getOutContent() = mByteArrayOutputStream.toString()

    fun getContext(): Context = WebDebugger.context!!
}