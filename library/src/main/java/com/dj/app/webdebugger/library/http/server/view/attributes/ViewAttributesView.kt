package com.dj.app.webdebugger.library.http.server.view.attributes

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import com.dj.app.webdebugger.library.ViewAttributes
import com.dj.app.webdebugger.library.ViewSelectAttributes
import com.dj.app.webdebugger.library.utils.ViewUtils

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 查看、修改View的属性
 */
internal abstract class ViewAttributesView<T : View> : ViewAttributes<T>() {

    override fun match(view: View): Boolean = true

    class ID : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "id"

        override fun description(view: View): String = "ID"

        override fun getValue(view: View): String = ViewUtils.toIdLabel(view)
    }

    class WidthHeight : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "width, height"

        override fun description(view: View): String = "宽高"

        override fun getValue(view: View): String = "${view.width}, ${view.height}"

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val split = value.split(",")
            view.layoutParams = view.layoutParams.apply {
                width = split[0].trim().toInt()
                height = split[1].trim().toInt()
            }
        }
    }

    class Background : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "background"

        override fun description(view: View): String = "背景颜色"

        override fun getValue(view: View): String {
            if (view.background is ColorDrawable) {
                return "#${Integer.toHexString((view.background as ColorDrawable).color)}"
            }
            return view.background?.toString() ?: "#ffffffff"
        }

        override fun isEdit(view: View): Boolean =
            view.background == null || view.background is ColorDrawable

        override fun setValue(view: View, value: String) {
            if (view.background == null || view.background is ColorDrawable) {
                view.setBackgroundColor(Color.parseColor(value))
            }
        }
    }

    class Visibility : ViewSelectAttributes<View>() {
        companion object {
            val attributesMap = linkedMapOf(
                View.VISIBLE to "Visible",
                View.INVISIBLE to "Invisible",
                View.GONE to "Gone"
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

    class Enabled : ViewSelectAttributes<View>() {
        companion object {
            val attributesMap = linkedMapOf(
                true to "True",
                false to "False"
            )
        }

        override fun attribute(view: View): String = "enabled"

        override fun selectOptions(view: View): List<String> = attributesMap.values.toList()

        override fun setValue(view: View, value: String) {
            attributesMap.forEach {
                if (it.value == value) {
                    view.isEnabled = it.key
                    return
                }
            }
        }

        override fun description(view: View): String = "可用"

        override fun getValue(view: View): String = attributesMap[view.isEnabled] ?: ""

        override fun match(view: View): Boolean = true
    }

    class Margin : ViewAttributesView<View>() {

        override fun match(view: View): Boolean {
            return view.layoutParams is ViewGroup.MarginLayoutParams
        }

        override fun attribute(view: View): String = "margin"

        override fun description(view: View): String = "外边距（左，上，右，下）"

        override fun getValue(view: View): String {
            val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return ""
            return "${lp.leftMargin}, ${lp.topMargin}, ${lp.rightMargin}, ${lp.bottomMargin}"
        }

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return
            val split = value.split(",")
            val ml = split[0].trim().toInt()
            val mt = split[1].trim().toInt()
            val mr = split[2].trim().toInt()
            val mb = split[3].trim().toInt()
            view.layoutParams = lp.apply {
                setMargins(ml, mt, mr, mb)
            }
        }
    }

    class Padding : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "padding"

        override fun description(view: View): String = "内边距（左，上，右，下）"

        override fun getValue(view: View): String =
            "${view.paddingLeft}, ${view.paddingTop}, ${view.paddingRight}, ${view.paddingBottom}"

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val split = value.split(",")
            val pl = split[0].trim().toInt()
            val pt = split[1].trim().toInt()
            val pr = split[2].trim().toInt()
            val pb = split[3].trim().toInt()
            view.setPadding(pl, pt, pr, pb)
        }
    }

    class Scroll : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "scroll"

        override fun description(view: View): String = "滚动坐标（x， y）"

        override fun getValue(view: View): String = "${view.scrollX}, ${view.scrollY}"
    }

//    class PaddingStartEnd : ViewAttributesView<View>() {
//        override fun match(view: View): Boolean =
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
//
//        override fun attribute(view: View): String = "padding(s, e)"
//
//        override fun description(view: View): String = "内边距（开始，结束）"
//
//        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//        override fun getValue(view: View): String = "${view.paddingStart}, ${view.paddingEnd}"
//
//        override fun isEdit(view: View): Boolean = true
//
//        override fun setValue(view: View, value: String) {
//            val split = value.split(",")
//            val ps = split[0].trim().toInt()
//            val pe = split[1].trim().toInt()
//            view.setPadding(ps, view.paddingTop, pe, view.paddingBottom)
//        }
//    }

    class XY : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "x, y"

        override fun description(view: View): String = "左上角的坐标"

        override fun getValue(view: View): String = "${view.x}, ${view.y}"

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val split = value.split(",")
            view.x = split[0].trim().toFloat()
            view.y = split[1].trim().toFloat()
        }
    }

    class Alpha : ViewAttributesView<View>() {
        override fun attribute(view: View): String = "alpha"

        override fun description(view: View): String = "透明度"

        override fun getValue(view: View): String = view.alpha.toString()

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            view.alpha = value.toFloat()
        }

    }

    class Rotation : ViewAttributesView<View>() {

        override fun attribute(view: View): String = "rotation"

        override fun description(view: View): String = "旋转"

        override fun getValue(view: View): String = view.rotation.toString()

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            view.rotation = value.toFloat()
        }
    }

    class RotationXY : ViewAttributesView<View>() {

        override fun attribute(view: View): String = "rotation(x, y)"

        override fun description(view: View): String = "沿X，Y轴旋转"

        override fun getValue(view: View): String = "${view.rotationX}, ${view.rotationY}"

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val split = value.split(",")
            view.rotationX = split[0].trim().toFloat()
            view.rotationY = split[1].trim().toFloat()
        }
    }

    class ScaleXY : ViewAttributesView<View>() {

        override fun attribute(view: View): String = "scale(x, y)"

        override fun description(view: View): String = "缩放"

        override fun getValue(view: View): String = "${view.scaleX}, ${view.scaleY}"

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val split = value.split(",")
            view.scaleX = split[0].trim().toFloat()
            view.scaleY = split[1].trim().toFloat()
        }
    }

    class TranslationXY : ViewAttributesView<View>() {

        override fun attribute(view: View): String = "translation(x, y)"

        override fun description(view: View): String = "偏移"

        override fun getValue(view: View): String = "${view.translationX}, ${view.translationY}"

        override fun isEdit(view: View): Boolean = true

        override fun setValue(view: View, value: String) {
            val split = value.split(",")
            view.translationX = split[0].trim().toFloat()
            view.translationY = split[1].trim().toFloat()
        }
    }
}