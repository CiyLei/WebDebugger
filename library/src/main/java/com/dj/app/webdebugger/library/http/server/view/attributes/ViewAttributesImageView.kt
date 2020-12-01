package com.dj.app.webdebugger.library.http.server.view.attributes

import android.view.View
import android.widget.ImageView
import com.dj.app.webdebugger.library.ViewAttributes
import com.dj.app.webdebugger.library.ViewSelectAttributes

/**
 * Create by ChenLei on 2020/12/1
 * Describe: 查看、修改ImageView的属性
 */
internal abstract class ViewAttributesImageView<T : ImageView> : ViewAttributes<T>() {

    override fun match(view: View): Boolean = view is ImageView

    class ScaleType : ViewSelectAttributes<ImageView>() {
        companion object {
            val attributesMap = linkedMapOf(
                ImageView.ScaleType.MATRIX to "MATRIX",
                ImageView.ScaleType.FIT_XY to "FIT_XY",
                ImageView.ScaleType.FIT_START to "FIT_START",
                ImageView.ScaleType.FIT_CENTER to "FIT_CENTER",
                ImageView.ScaleType.FIT_END to "FIT_END",
                ImageView.ScaleType.CENTER to "CENTER",
                ImageView.ScaleType.CENTER_CROP to "CENTER_CROP",
                ImageView.ScaleType.CENTER_INSIDE to "CENTER_INSIDE"
            )
        }

        override fun attribute(view: ImageView): String = "scaleType"

        override fun selectOptions(view: ImageView): List<String> = attributesMap.values.toList()

        override fun setValue(view: ImageView, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.scaleType = it.key
                    return
                }
            }
        }

        override fun description(view: ImageView): String = "图片显示方式"

        override fun getValue(view: ImageView): String = attributesMap[view.scaleType] ?: ""

        override fun match(view: View): Boolean = view is ImageView
    }
}