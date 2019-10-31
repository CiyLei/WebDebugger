package com.dj.app.webdebugger.library.http.server.media

import android.media.projection.MediaProjection
import android.hardware.display.DisplayManager
import android.os.Build
import android.content.Context
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjectionManager
import android.content.Intent
import android.media.ImageReader
import android.support.annotation.RequiresApi
import android.util.DisplayMetrics
import android.view.WindowManager
import com.dj.app.webdebugger.library.utils.FileUtil
import java.io.File
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Handler
import com.dj.app.webdebugger.library.utils.ScreenUtil
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Create by ChenLei on 2019/10/31
 * Describe: MediaProjectionManager 截屏
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class MediaProjectionManagerScreenCapture(
    val mContext: Context,
    val mResultCode: Int,
    val mResultData: Intent,
    val mListener: OnImageListener
) {
    private var mMediaProjection: MediaProjection? = null
    private var mVirtualDisplay: VirtualDisplay? = null
    private var mMediaProjectionManager: MediaProjectionManager =
        mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    private var mImageReader: ImageReader? = null
    private var fileName: String? = null
    private var pathImage: String? = null
    private var windowWidth = 0
    private var windowHeight = 0
    private var metrics: DisplayMetrics? = null
    private var mScreenDensity = 0

    init {
        createVirtualEnvironment()
    }

    private fun createVirtualEnvironment() {
        fileName = ScreenUtil.getScreenCaptureName()
        pathImage = FileUtil.getCachePath(mContext) + File.separator + fileName
        val mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowWidth = mWindowManager.defaultDisplay.width
        windowHeight = mWindowManager.defaultDisplay.height
        this.metrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics!!.densityDpi
        mImageReader =
            ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2) //ImageFormat.RGB_565
        mImageReader!!.setOnImageAvailableListener({
            saveImage(it.acquireLatestImage())
            mImageReader!!.setOnImageAvailableListener(null, Handler())
            stopVirtual()
            tearDownMediaProjection()
        }, Handler())
    }

    fun screenCapture() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay()
        }
    }

    private fun setUpMediaProjection() {
        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData)
    }

    private fun virtualDisplay() {
        mVirtualDisplay = mMediaProjection?.createVirtualDisplay(
            "screen-mirror",
            windowWidth,
            windowHeight,
            mScreenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader!!.surface,
            null,
            null
        )
    }

    fun saveImage(image: Image) {
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        var bitmap: Bitmap? =
            Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap!!.copyPixelsFromBuffer(buffer)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
        image.close()
        if (bitmap != null) {
            try {
                val fileImage = File(pathImage)
                if (!fileImage.exists()) {
                    fileImage.createNewFile()
                }
                val out = FileOutputStream(fileImage)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                val media = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri = Uri.fromFile(fileImage)
                media.data = contentUri
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        mListener.onImagePath(fileName!!)
    }

    private fun tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection!!.stop()
            mMediaProjection = null
        }
    }

    private fun stopVirtual() {
        if (mVirtualDisplay == null) {
            return
        }
        mVirtualDisplay!!.release()
        mVirtualDisplay = null
    }

    interface OnImageListener {
        fun onImagePath(fileName: String)
    }
}