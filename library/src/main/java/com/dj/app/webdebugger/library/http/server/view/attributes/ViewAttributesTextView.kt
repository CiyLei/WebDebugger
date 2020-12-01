package com.dj.app.webdebugger.library.http.server.view.attributes

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.dj.app.webdebugger.library.ViewAttributes
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

    class Gravity : ViewAttributesTextView<TextView>() {

        override fun attribute(view: TextView): String = "gravity"

        override fun description(view: TextView): String =
            "内部对其方式（左：${android.view.Gravity.START} ，上${android.view.Gravity.TOP} ，" +
                    "右${android.view.Gravity.END} ，下${android.view.Gravity.BOTTOM} ，" +
                    "居中${android.view.Gravity.CENTER}）"

        override fun getValue(view: TextView): String = view.gravity.toString()

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.gravity = value.trim().toInt()
        }
    }

}