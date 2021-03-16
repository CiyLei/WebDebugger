package com.dj.app.webdebugger.library.http.server.view.attributes

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dj.app.webdebugger.library.ViewAttributes
import com.dj.app.webdebugger.library.ViewSelectAttributes
import com.dj.app.webdebugger.library.utils.DisplayUtil

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 查看、修改TextView的属性
 */
internal abstract class ViewAttributesTextView<T : TextView> : ViewAttributes<T>() {

    override fun match(view: View): Boolean = view is TextView

    class Text : ViewAttributesTextView<TextView>() {
        override fun attribute(view: TextView): String = "text"

        override fun description(view: TextView): String = "文本内容"

        override fun getValue(view: TextView): String = view.text.toString()

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.text = value
        }
    }

    class TextColor : ViewAttributesTextView<TextView>() {
        override fun attribute(view: TextView): String = "textColor"

        override fun description(view: TextView): String = "文本内容颜色"

        override fun getValue(view: TextView): String {
            return "#${Integer.toHexString(view.textColors.defaultColor)}"
        }

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.setTextColor(Color.parseColor(value))
        }
    }

    class TextSize : ViewAttributesTextView<TextView>() {

        override fun attribute(view: TextView): String = "textSize"

        override fun description(view: TextView): String = "字体大小（sp）"

        override fun getValue(view: TextView): String =
            DisplayUtil.px2sp(view.context, view.textSize).toString()

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.textSize = value.trim().toFloat()
        }
    }

    class Typeface : ViewSelectAttributes<TextView>() {
        companion object {
            val attributesMap = linkedMapOf(
                android.graphics.Typeface.NORMAL to "NORMAL",
                android.graphics.Typeface.BOLD to "BOLD",
                android.graphics.Typeface.ITALIC to "ITALIC",
                android.graphics.Typeface.BOLD_ITALIC to "BOLD_ITALIC"
            )
        }

        override fun attribute(view: TextView): String = "typeface"

        override fun selectOptions(view: TextView): List<String> = attributesMap.values.toList()

        override fun setValue(view: TextView, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.typeface = android.graphics.Typeface.defaultFromStyle(it.key)
                    return
                }
            }
        }

        override fun description(view: TextView): String = "字体样式"

        override fun getValue(view: TextView): String = attributesMap[view.typeface.style] ?: ""

        override fun match(view: View): Boolean = view is TextView
    }

    class Hint : ViewAttributesTextView<TextView>() {

        override fun attribute(view: TextView): String = "hint"

        override fun description(view: TextView): String = "提示文字"

        override fun getValue(view: TextView): String = view.hint?.toString() ?: ""

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.hint = value
        }
    }

    class HintTextColor : ViewAttributesTextView<TextView>() {

        override fun attribute(view: TextView): String = "hintTextColor"

        override fun description(view: TextView): String = "提示文字颜色"

        override fun getValue(view: TextView): String {
            return "#${Integer.toHexString(view.hintTextColors.defaultColor)}"
        }

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.setHintTextColor(Color.parseColor(value))
        }
    }

    class Gravity : ViewSelectAttributes<TextView>() {
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

        override fun attribute(view: TextView): String = "gravity"

        override fun description(view: TextView): String = "内部对其方式"

        override fun selectOptions(view: TextView): List<String> = attributesMap.values.toList()

        override fun getValue(view: TextView): String = attributesMap[view.gravity] ?: ""

        override fun setValue(view: TextView, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.gravity = it.key
                    return
                }
            }
        }

        override fun match(view: View): Boolean = view is TextView
    }

}