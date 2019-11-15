package com.dj.app.webdebugger.library.utils

import android.content.Context

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.util.ArrayList
import java.util.Enumeration
import java.util.jar.JarEntry
import java.util.jar.JarFile

import dalvik.system.DexFile
import java.lang.reflect.Field

/**
 * ClazzUtils
 *
 * @author ZENG.XIAO.YAN
 * @version .
 */

internal object ClazzUtils {
    /**
     * 获取某个包下面所有的类
     *
     * @param context
     * @param packageName
     * @return
     */
    fun getClassName(context: Context, packageName: String): List<String> {
        val classNameList = ArrayList<String>()
        try {
            val df = DexFile(context.packageCodePath)//通过DexFile查找当前的APK中可执行文件
            val enumeration = df.entries()//获取df中的元素  这里包含了所有可执行的类名 该类名包含了包名+类名的方式
            while (enumeration.hasMoreElements()) {//遍历
                val className = enumeration.nextElement() as String
                if (className.contains(packageName)) {//在当前所有可执行的类里面查找包含有该包名的所有类
                    classNameList.add(className)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return classNameList
    }


    /**
     * 反射获取字段
     */
    fun getField(target: Any?, field: String, targetIsClass: Boolean = false, count: Int = 0): Field? {
        if (target == null) {
            return null
        }
        var f: Field? = null
        try {
            if (targetIsClass) {
                f = (target as Class<*>).getDeclaredField(field)
            } else {
                f = target::class.java.getDeclaredField(field)
            }
        } catch (e: Exception) {
            if (count < 3) {
                f = getField(target::class.java.superclass, field, true, count + 1)
            }
        }
        f?.isAccessible = true
        return f
    }
}

