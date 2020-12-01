package com.dj.app.webdebugger.library.http.server.view.attributes

import android.view.View
import android.widget.TextView
import com.dj.app.webdebugger.library.ViewAttributes

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 查看修改，TextView的Text
 */
internal abstract class ViewAttributesTextView : ViewAttributes<TextView>() {

    override fun match(view: View): Boolean = view is TextView

    class Text : ViewAttributesTextView() {
        override fun attribute(view: TextView): String = "text"

        override fun description(view: TextView): String = "文本内容"

        override fun getValue(view: TextView): String = view.text.toString()

        override fun isEdit(view: TextView): Boolean = true

        override fun setValue(view: TextView, value: String) {
            view.text = value
        }
    }
}