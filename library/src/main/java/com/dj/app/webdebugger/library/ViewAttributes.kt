package com.dj.app.webdebugger.library

import android.view.View
import com.dj.app.webdebugger.library.utils.DisplayUtil

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 读取、修改 View属性的抽象
 */
abstract class ViewAttributes<out T : View> {

    companion object {
        // 输入类型为：字符串输入
        const val INPUT_TYPE_INPUT = 0

        // 输入类型为：选择输入
        const val INPUT_TYPE_SELECT = 1
    }

    /**
     * 匹配View
     */
    abstract fun match(view: View): Boolean

    /**
     * 属性名称
     */
    abstract fun attribute(view: @UnsafeVariance T): String

    /**
     * 属性说明
     */
    abstract fun description(view: @UnsafeVariance T): String

    /**
     * 返回属性的值
     */
    abstract fun getValue(view: @UnsafeVariance T): String

    /**
     * 是否可以修改属性的值
     */
    open fun isEdit(view: @UnsafeVariance T): Boolean = false

    /**
     * 输入类型
     */
    open fun inputType(view: @UnsafeVariance T): Int = INPUT_TYPE_INPUT

    /**
     * 选择的选项
     */
    open fun selectOptions(view: @UnsafeVariance T): List<String> = emptyList()

    /**
     * 执行修改值
     */
    open fun setValue(view: @UnsafeVariance T, value: String) {

    }

    fun px2dip(view: View, value: Float): Int = DisplayUtil.px2dip(view.context, value)

    fun dip2px(view: View, value: Float): Int = DisplayUtil.dip2px(view.context, value)
}