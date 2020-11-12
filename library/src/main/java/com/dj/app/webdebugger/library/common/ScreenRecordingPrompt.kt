package com.dj.app.webdebugger.library.common

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.WindowManager
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.utils.DisplayUtil
import java.lang.Exception

/**
 * Create by ChenLei on 2019/11/3
 * Describe: 录像的红点
 */
internal class ScreenRecordingPrompt(val context: Context) {
    val windowsManage =
        context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var pointView: FlashingPointView? = null

    fun show() {
        val dp10 = DisplayUtil.dip2px(context, 10f)
        pointView = FlashingPointView(context).apply {
            setPadding(dp10, dp10, dp10, dp10)
        }
        val params = WindowManager.LayoutParams()
        // 类型
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        // 设置flag
        val flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.flags = flags
        // 不设置这个弹出框的透明遮罩显示为黑色
        params.format = PixelFormat.TRANSLUCENT
        params.width = DisplayUtil.dip2px(context, 35f)
        params.height = DisplayUtil.dip2px(context, 35f)
        params.gravity = Gravity.END or Gravity.TOP
        handler.obtainMessage(1).apply {
            obj = params
        }.sendToTarget()
    }

    fun hide() {
        handler.obtainMessage(2).sendToTarget()
    }

    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            try {
                when (msg.what) {
                    1 -> {
                        windowsManage.addView(pointView, msg.obj as WindowManager.LayoutParams)
                    }
                    2 -> {
                        windowsManage.removeViewImmediate(pointView)
                    }
                }
            } catch (e: Exception) {
                if (WebDebugger.isDebug) {
                    e.printStackTrace()
                }
            }
        }
    }
}