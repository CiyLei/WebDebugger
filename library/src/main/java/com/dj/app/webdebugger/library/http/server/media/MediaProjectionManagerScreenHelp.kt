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
import java.io.File
import android.graphics.Bitmap
import android.media.Image
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import com.dj.app.webdebugger.library.utils.FileUtil
import com.dj.app.webdebugger.library.utils.ScreenUtil
import com.dj.app.webdebugger.library.utils.VideoUtils
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.ArrayList


/**
 * Create by ChenLei on 2019/10/31
 * Describe: MediaProjectionManager 帮助类
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
internal class MediaProjectionManagerScreenHelp(
    val mContext: Context,
    val mResultCode: Int,
    val mResultData: Intent
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
    // 是否在录制
    var isRecorder: Boolean = false
    // 录屏类
    private lateinit var mediaRecorder: MediaRecorder
    //  当前录制视频视频的路径
    private lateinit var currentVideoFilePath: String
    //  录制视频的集合
    private val mediaPathList: ArrayList<String> = ArrayList()
    // 虚拟屏幕,录屏或者截屏时创建的
    private var virtualDisplayScreenRecording: VirtualDisplay? = null
    // 是否暂停
    private var isPause: Boolean = false
    //  最后录屏完成之后的视频文件路径
    private lateinit var saveMediaPath: String

    init {
        createVirtualEnvironment()
    }

    private fun createVirtualEnvironment() {
        fileName = ScreenUtil.getScreenCaptureName()
        pathImage = FileUtil.getMediaCacheFile(mContext).absolutePath + File.separator + fileName
        val mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowWidth = mWindowManager.defaultDisplay.width
        windowHeight = mWindowManager.defaultDisplay.height
        this.metrics = DisplayMetrics()
        mWindowManager.defaultDisplay.getMetrics(metrics)
        mScreenDensity = metrics!!.densityDpi
        mImageReader =
            ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2) //ImageFormat.RGB_565
    }

    /**
     * 截屏
     */
    fun screenCapture(mListener: OnImageListener) {
        mImageReader!!.setOnImageAvailableListener({
            saveImage(it.acquireLatestImage(), mListener)
            mImageReader!!.setOnImageAvailableListener(null, Handler())
            stopVirtual()
            tearDownMediaProjection()
        }, Handler())
        setUpMediaProjection();
        virtualDisplay()
    }

    /**
     * 开始录屏
     */
    fun startScreenRecording() {
        if (!isRecorder) {
            setUpMediaProjection()
            startMedia()
            isRecorder = true
        }
    }

    /**
     * 暂停录制
     * <p>
     *  切换到后台就会触发暂停录制
     *  将
     * </p>
     */
    fun pauseRecorder() {
        if (isRecorder && !isPause) {
            isPause = true
            stopMedia()
        }
    }

    /**
     *   重启录屏
     */
    fun resumeRecorder() {
        if (isRecorder && isPause) {
            isRecorder = true
            isPause = false
            startMedia()
        }
    }

    /**
     * 停止录屏
     */
    fun stopScreenRecording() {
        if (isRecorder) {  //
            isRecorder = false
            isPause = false
            stopMedia()
            virtualDisplayScreenRecording?.release()    // 结束录屏之后将画布和录屏管理器设置为空
            mMediaProjection?.stop()
            // 合并此次录制的所有屏幕
            try {
                saveMediaPath =
                    FileUtil.getMediaCacheFile(mContext).absolutePath + File.separator + ScreenUtil.getScreenRecordingName()
                VideoUtils.appendMp4List(mediaPathList, saveMediaPath)
                // 删除合并前的视频
                mediaPathList.forEach {
                    val childFile = File(it)
                    if (childFile.exists()) {
                        childFile.delete()
                    }
                }
                mediaPathList.clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     *  录屏开始
     */
    private fun startMedia() {
        initRecorder()
        createScreenRecordingVirtualDisplay()
        mediaRecorder.start()
    }

    /**
     *  停止录屏
     */
    private fun stopMedia() {
        mediaRecorder.stop()
        mediaRecorder.reset()
    }

    /**
     *  初始化录屏参数
     */
    private fun initRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setOrientationHint(0)
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)  //  音频源
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)  //  视频来源
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //  视频输出格式

        currentVideoFilePath =
            FileUtil.getMediaCacheFile(mContext).absolutePath + File.separator + ScreenUtil.getScreenRecordingName()
        mediaRecorder.setOutputFile(currentVideoFilePath)  // 录制输出文件名
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder.setMaxDuration(1 * 60 * 1000)               // 设置最大时长5分钟
        mediaRecorder.setVideoEncodingBitRate(1 * 1024 * 1024)   //  设置视频文件的比特率,经过测试该属性对于视频大小影响最大
        setVideoSize(mediaRecorder);
//        mediaRecorder.setVideoSize(windowWidth, windowHeight)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setOnErrorListener(OnRecordErrorListener()) // 录制发生错误的监听
        mediaRecorder.setOnInfoListener(OnRecordInfoListener()) //
        try {
            mediaRecorder.prepare()
            mediaPathList.add(currentVideoFilePath) // 调用一次该方法就在此处将加入集合
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 因为某些手机的齐刘海会导致录屏失败，但是减去状态栏高度就可以了
     */
    private fun setVideoSize(mediaRecorder: MediaRecorder) {
        val identifier = mContext.resources.getIdentifier("status_bar_height", "dimen", "android");
        val height = mContext.resources.getDimensionPixelSize(identifier)
        mediaRecorder.setVideoSize(windowWidth, windowHeight - height)
    }

    /**
     * 创建虚拟屏幕以进行录屏
     */
    private fun createScreenRecordingVirtualDisplay() {
        try {
            virtualDisplayScreenRecording = mMediaProjection?.createVirtualDisplay(
                "MainScreen", windowWidth, windowHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.surface, null, null
            )
        } catch (e: Exception) {
            e.printStackTrace()
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

    fun saveImage(image: Image, mListener: OnImageListener) {
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

    inner class OnRecordErrorListener : MediaRecorder.OnErrorListener {
        override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
            // 发生错误，停止录制
        }
    }


    inner class OnRecordInfoListener : MediaRecorder.OnInfoListener {
        override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {
            when (what) {
                MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED
                -> {
                    // 录制达到最大时长
                    stopScreenRecording()
                }
            }
        }
    }

}