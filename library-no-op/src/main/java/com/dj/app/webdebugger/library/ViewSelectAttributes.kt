package com.dj.app.webdebugger.library

import android.view.View

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 修改值为选项的属性抽象
 */
abstract class ViewSelectAttributes<T : View> : ViewAttributes<T>() {

    override fun isEdit(view: T): Boolean = true

    override fun inputType(view: T): Int = INPUT_TYPE_SELECT

    abstract override fun selectOptions(view: T): List<String>

    abstract override fun setValue(view: T, value: String)
}