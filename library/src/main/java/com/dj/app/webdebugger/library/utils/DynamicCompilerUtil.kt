package com.dj.app.webdebugger.library.utils

import android.content.Context
import android.util.Log
import com.android.dx.command.dexer.Main
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.http.server.code.TaskExecutor
import com.googlecode.dex2jar.tools.Jar2Dex
import dalvik.system.PathClassLoader
import org.codehaus.janino.SimpleCompiler
import org.codehaus.janino.util.ClassFile
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

/**
 * Create by ChenLei on 2020/8/6
 * Describe: 动态编译工具类
 */
internal object DynamicCompilerUtil {

    private const val TAG = "DynamicCompilerUtil"

    /**
     * 编译java文件
     * android里面用不了javax的一些类，无法使用JavaCompiler
     * 此框架是独立于javax的编译器
     * @param javaSource java源代码
     * @param classLoader 类加载器
     */
    fun compile(javaSource: String, classLoader: ClassLoader): ByteArray? {
        try {
            return SimpleCompiler().apply {
                setParentClassLoader(classLoader)
                cook(javaSource)
            }.classFiles.firstOrNull()?.toByteArray()
        } catch (e: Exception) {
            if (WebDebugger.isDebug) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Class文件转Jar
     * @param context 上下文
     * @param packageName 包名
     * @param className 类名
     * @param classData class文件数据
     * @return jar文件存放路径
     */
    fun class2Jar(
        context: Context,
        packageName: String,
        className: String,
        classData: ByteArray
    ): String? {
        // jar保存路径
        val jarPath =
            "${FileUtil.getTaskCacheFile(context).absolutePath}${File.separator}$className.jar"
        val jarOutputStream = JarOutputStream(FileOutputStream(jarPath))
        try {
            // class在jar中的路径
            val classPathForJar = packageName.replace(".", "/") + "/$className.class"
            jarOutputStream.putNextEntry(JarEntry(classPathForJar))
            // 写入class数据
            jarOutputStream.write(classData)
            return jarPath
        } catch (e: Exception) {
            if (WebDebugger.isDebug) {
                e.printStackTrace()
            }
        } finally {
            jarOutputStream.close()
        }
        return null
    }

    /**
     * jar文件转dex文件
     * @param jarPath jar文件存放路径
     * @return dex文件存放路径
     */
    fun jar2Dex(jarPath: String): String? {
        val jarFile = File(jarPath)
        // 获取jar的文件名称
        val jarName = jarFile.name.split(".").firstOrNull() ?: false
        // 同级目录下的dex保存路径
        val dexPath = "${jarFile.parent}${File.separator}$jarName.dex"
        // 构造命令行参数
        val arguments = Main.Arguments()
        // 参考Jar2Dex的命令参数，在同级目录下生成dex
        arguments.parse(
            arrayOf(
                "--no-strict",
                "--output=$dexPath",
                jarPath
            )
        )
        // 执行
        val result = Main.run(arguments)
        if (result == 0) {
            return dexPath
        } else {
            if (WebDebugger.isDebug) {
                Log.e(TAG, "jar2Dex: Jar转Dex失败，返回码：$result")
            }
        }
        return null
    }
}