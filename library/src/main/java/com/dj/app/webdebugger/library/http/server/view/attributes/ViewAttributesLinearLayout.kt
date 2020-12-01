package com.dj.app.webdebugger.library.http.server.view.attributes

import android.view.View
import android.widget.LinearLayout
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
}