package com.dj.app.webdebugger.library.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import com.dj.app.webdebugger.library.WebDebugger
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.support.annotation.RequiresApi
import com.dj.app.webdebugger.library.WebDebuggerConstant.REQUEST_SCREENCAPTURE
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by ChenLei on 2019/10/31
 * Describe: 屏幕工具类
 */
internal object ScreenUtil {

    /**
     * 保存截屏
     */
    fun saveScreenCapture(context: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上使用MediaProjectionManager
            saveMediaProjectionManagerScreenCapture()
        } else {
            // 5.0一下尝试截取顶层Activity
            if (WebDebugger.topActivity != null) {
                saveActivityScreenCapture(WebDebugger.topActivity!!)
            }
        }
    }

    /**
     * 保持某个Activity的截屏
     */
    private fun saveActivityScreenCapture(activity: Activity) {
        val dView = activity.window.decorView
        dView.isDrawingCacheEnabled = true
        dView.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(dView.drawingCache)
        if (bitmap != null) {
            try {
                // 获取内置SD卡路径
                val sdCardPath = FileUtil.getCachePath(activity)
                // 图片文件路径
                val fileName = getScreenCaptureName()
                val filePath = sdCardPath + fileName
                val file = File(filePath)
                val os = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                os.flush()
                os.close()
            } catch (e: Exception) {
            }
            WebDebugger.mediaObservable.notifyObservers()
        }
    }

    /**
     * 使用MediaProjectionManager截屏
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun saveMediaProjectionManagerScreenCapture() {
        if (WebDebugger.topActivity != null) {
            val mediaProjectionManager =
                WebDebugger.topActivity!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            WebDebugger.topActivity!!.startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_SCREENCAPTURE
            )
        }
    }

    /**
     * 获取截屏名称
     */
    fun getScreenCaptureName(): String =
        "${SimpleDateFormat.getDateTimeInstance().format(Date())} 截屏.png"
}