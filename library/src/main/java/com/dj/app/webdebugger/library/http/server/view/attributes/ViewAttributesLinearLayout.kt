package com.dj.app.webdebugger.library.http.server.view.attributes

import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dj.app.webdebugger.library.ViewAttributes
import com.dj.app.webdebugger.library.ViewSelectAttributes

/**
 * Create by ChenLei on 2020/12/1
 * Describe: 查看、修改LinearLayout的属性
 */
internal abstract class ViewAttributesLinearLayout<T : LinearLayout> : ViewAttributes<T>() {

    override fun match(view: View): Boolean = view is LinearLayout

    class Orientation : ViewSelectAttributes<LinearLayout>() {
        companion object {
            val attributesMap = linkedMapOf(
                LinearLayout.VERTICAL to "Vertical",
                LinearLayout.HORIZONTAL to "Horizontal"
            )
        }

        override fun attribute(view: LinearLayout): String = "orientation"

        override fun selectOptions(view: LinearLayout): List<String> = attributesMap.values.toList()

        override fun setValue(view: LinearLayout, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.orientation = it.key
                    return
                }
            }
        }

        override fun description(view: LinearLayout): String = "对其方式"

        override fun getValue(view: LinearLayout): String = attributesMap[view.orientation] ?: ""

        override fun match(view: View): Boolean = view is LinearLayout
    }

    class Gravity : ViewSelectAttributes<LinearLayout>() {
        companion object {
            val attributesMap = linkedMapOf(
                (android.view.Gravity.START or android.view.Gravity.TOP) to "START_TOP",
                (android.view.Gravity.TOP or android.view.Gravity.CENTER) to "TOP",
                (android.view.Gravity.END or android.view.Gravity.TOP) to "END_TOP",
                android.view.Gravity.START or android.view.Gravity.CENTER to "START",
                android.view.Gravity.CENTER to "CENTER",
                android.view.Gravity.END or android.view.Gravity.CENTER to "END",
                (android.view.Gravity.START or android.view.Gravity.BOTTOM) to "START_BOTTOM",
                (android.view.Gravity.CENTER or android.view.Gravity.BOTTOM) to "BOTTOM",
                (android.view.Gravity.END or android.view.Gravity.BOTTOM) to "END_BOTTOM"
            )
        }

        override fun attribute(view: LinearLayout): String = "gravity"

        override fun description(view: LinearLayout): String = "内部对其方式"

        override fun selectOptions(view: LinearLayout): List<String> = attributesMap.values.toList()

        @RequiresApi(Build.VERSION_CODES.N)
        override fun getValue(view: LinearLayout): String = attributesMap[view.gravity] ?: ""

        override fun setValue(view: LinearLayout, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.gravity = it.key
                    return
                }
            }
        }

        override fun match(view: View): Boolean =
            view is LinearLayout && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }
}