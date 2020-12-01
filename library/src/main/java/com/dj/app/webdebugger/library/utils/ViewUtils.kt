package com.dj.app.webdebugger.library.utils

import android.content.Context
import android.content.res.Resources
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.dj.app.webdebugger.library.R
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.http.server.view.ViewController
import com.dj.app.webdebugger.library.http.server.view.ViewDescription
import com.dj.app.webdebugger.library.http.server.view.attributes.AttributesBean
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * Create by ChenLei on 2020/11/30
 * Describe: View工具类
 */
internal object ViewUtils {

    // 缓存code和View的关系
    private val mCacheView = WeakHashMap<Int, View>()

    /**
     * 返回View唯一识别码
     */
    fun toCode(view: View): Int = view.hashCode()

    /**
     * 返回id的说明
     */
    fun toIdLabel(view: View): String {
        val sb = StringBuilder()
        try {
            val pkgName: String = when (view.id and -0x1000000) {
                0x7f000000 -> "app"
                0x01000000 -> "android"
                else -> view.resources.getResourcePackageName(view.id)
            }
            val typeName: String = view.resources.getResourceTypeName(view.id)
            val entryName: String = view.resources.getResourceEntryName(view.id)
            sb.append(pkgName)
            sb.append(":")
            sb.append(typeName)
            sb.append("/")
            sb.append(entryName)
        } catch (e: Resources.NotFoundException) {
            if (WebDebugger.isDebug) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    /**
     * 返回View的说明
     */
    fun toLabel(view: View): String {
        val sb = StringBuilder()
        sb.append(view.javaClass.simpleName).append(' ')
        sb.append('{')
        sb.append(view.width).append('-').append(view.height).append(' ')
        sb.append(view.left).append(',').append(view.top).append('-').append(view.right).append(',')
            .append(view.bottom).append(' ')
        sb.append(toIdLabel(view))
        sb.append('}')
        return sb.toString()
    }

    /**
     * 获取最顶层的View
     */
    fun getTopView(): View? {
        // 顶部是不是监控Dialog
//        val topIsMonitor =
//            (WebDebugger.topActivity as? FragmentActivity)?.supportFragmentManager?.findFragmentByTag(
//                ViewController.MONITOR_DIALOG_TAG
//            ) != null
        var topView = WebDebugger.topActivity?.window?.decorView
        // 获取顶层window的view
        try {
            val wm =
                WebDebugger.topActivity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val mGlobal = wm.javaClass.getDeclaredField("mGlobal").apply {
                isAccessible = true
            }.get(wm)
            val mRoots = mGlobal.javaClass.getDeclaredField("mRoots").apply {
                isAccessible = true
            }.get(mGlobal)
            val viewList = (mRoots as? ArrayList<*>)?.map {
                it?.javaClass?.getDeclaredField("mView")?.apply {
                    isAccessible = true
                }?.get(it) as? ViewGroup
            } ?: ArrayList<ViewGroup>()
            // 寻找最后一个不包含MonitorView的根布局
            var lastView: View? = null
            for (i in (viewList.size - 1)..0 step -1) {
                if (viewList[i]?.findViewById<View>(R.id.webdedebugger_monitorView_id) == null) {
                    lastView = viewList[i]
                    break
                }
            }
            topView = lastView ?: topView
        } catch (e: Exception) {
            if (WebDebugger.isDebug) {
                e.printStackTrace()
            }
        }
        return topView
    }

    /**
     * 从View中找到hashCode一致的View
     */
    fun findView(view: View, code: Int): View? {
        // 先从缓存中读取
        var result = mCacheView[code]
        if (result != null) {
            return result
        } else {
            // 从View中读取，读取成功，进行缓存，在返回
            if (toCode(view) == code) {
                mCacheView[code] = view
                result = view
            } else {
                if (view is ViewGroup) {
                    // 递归从子布局中找
                    for (i in 0 until view.childCount) {
                        result = findView(view.getChildAt(i), code)
                    }
                }
            }
            // 缓存
            if (result != null) mCacheView[code] = result
        }
        return result
    }

    /**
     * 将View转换为ViewDescription
     */
    fun view2ViewDescription(view: View): ViewDescription {
        if (view is ViewGroup) {
            val childrenViews = ArrayList<ViewDescription>()
            for (i in 0 until view.childCount) {
                childrenViews.add(view2ViewDescription(view.getChildAt(i)))
            }
            return ViewDescription(
                toCode(view).toString(),
                toLabel(view),
                childrenViews
            )
        }
        return ViewDescription(toCode(view).toString(), toLabel(view))
    }

    /**
     * 读取所有的属性
     */
    fun readAllAttributes(view: View): Map<String, ArrayList<AttributesBean>> {
        val result = LinkedHashMap<String, ArrayList<AttributesBean>>()
        WebDebugger.viewAttributesList.forEach { attributes ->
            if (attributes.match(view)) {
                val name = ClazzUtils.getGenericType(attributes)
                var list = result[name]
                if (list == null) {
                    list = ArrayList()
                    result[name] = list
                }
                list.add(
                    AttributesBean(
                        attributes.attribute(view),
                        attributes.getValue(view),
                        attributes.description(view),
                        attributes.isEdit(view),
                        attributes.inputType(view),
                        attributes.selectOptions(view)
                    )
                )
            }
        }
        return result
    }
}