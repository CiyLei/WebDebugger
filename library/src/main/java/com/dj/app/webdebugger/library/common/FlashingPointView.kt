package com.dj.app.webdebugger.library.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.min

/**
 * Create by ChenLei on 2020/11/13
 * Describe: 录像的红点
 */
class FlashingPointView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 闪烁标志
    private var mFlashFlag = true

    // 闪烁点的画笔
    private val mPointPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
        }
    }

    private val mTimer = Timer()

    // 定时任务
    private val mTimerTask = object : TimerTask() {
        override fun run() {
            Handler(Looper.getMainLooper()).post {
                invalidate()
                mFlashFlag = !mFlashFlag
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mTimer.schedule(mTimerTask, 0, 500)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mTimer.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPointPaint.color = if (mFlashFlag) Color.parseColor("#77ff0000") else Color.TRANSPARENT
        var radius = min(width, height) / 2.0f
        var cx = width / 2.0f
        var cy = height / 2.0f
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (paddingStart != 0) {
                cx += paddingStart / 2.0f
                radius -= paddingStart / 4.0f
            }
            if (paddingEnd != 0) {
                cx -= paddingEnd / 2.0f
                radius -= paddingEnd / 4.0f
            }
        } else {
            if (paddingLeft != 0) {
                cx += paddingLeft / 2.0f
                radius -= paddingLeft / 4.0f
            }
            if (paddingRight != 0) {
                cx -= paddingRight / 2.0f
                radius -= paddingRight / 4.0f
            }
        }
        if (paddingTop != 0) {
            cy += paddingTop / 2.0f
            radius -= paddingTop / 4.0f
        }
        if (paddingBottom != 0) {
            cy -= paddingBottom / 2.0f
            radius -= paddingBottom / 4.0f
        }
        canvas?.drawCircle(cx, cy, radius, mPointPaint)
    }
}