package com.dj.app.webdebugger.library.http.server.view.attributes

import android.view.View
import com.dj.app.webdebugger.library.ViewAttributes
import com.dj.app.webdebugger.library.ViewSelectAttributes
import com.dj.app.webdebugger.library.utils.ViewUtils

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 查看View的Id
 */
internal abstract class ViewAttributesView : ViewAttributes<View>() {

    override fun match(view: View): Boolean = true

    class ID : ViewAttributesView() {
        override fun attribute(view: View): String = "id"

        override fun description(view: View): String = "ID"

        override fun getValue(view: View): String = ViewUtils.toIdLabel(view)
    }

    class Visibility : ViewSelectAttributes<View>() {
        companion object {
            val attributesMap = linkedMapOf(
                View.VISIBLE to "VISIBLE",
                View.INVISIBLE to "INVISIBLE",
                View.GONE to "GONE"
            )
        }

        override fun attribute(view: View): String = "visibility"

        override fun selectOptions(view: View): List<String> = attributesMap.values.toList()

        override fun setValue(view: View, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.visibility = it.key
                    return
                }
            }
        }

        override fun description(view: View): String =
            "可见性（VISIBLE：可见；INVISIBLE：不可见，但是还占位置；GONE：消失，不占位置）"

        override fun getValue(view: View): String = attributesMap[view.visibility] ?: ""

        override fun match(view: View): Boolean = true
    }
}