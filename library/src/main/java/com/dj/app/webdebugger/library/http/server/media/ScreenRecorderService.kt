package com.dj.app.webdebugger.library.http.server.media

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.dj.app.webdebugger.library.WebDebugger
import java.util.*

/**
 * Create by ChenLei on 2020/11/11
 * 截屏、录制视频服务
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ScreenRecorderService : Service() {

    companion object {
        // 截屏动作
        const val ACTION_SCREENSHOT = "ACTION_SCREENSHOT"

        // 开始录屏动作
        const val ACTION_START_SCREEN_RECORDING = "ACTION_START_SCREEN_RECORDING"

        // 结束录屏动作
        const val ACTION_STOP_SCREEN_RECORDING = "ACTION_STOP_SCREEN_RECORDING"

        // 截屏的参数
        const val SCREENSHOT_KEY_CODE = "code"
        const val SCREENSHOT_KEY_DATA = "data"

        // 推送参数
        private const val CHANNEL_ID = "webdebugger_channel_id"
        private const val CHANNEL_NAME = "webdebugger_channel_name"
    }

    private val mRandom = Random()

    // 录像工具类
    private var mHelp: MediaProjectionManagerScreenHelp? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val code = intent?.getIntExtra(SCREENSHOT_KEY_CODE, -1) ?: return super.onStartCommand(
            intent,
            flags,
            startId
        )
        val data =
            intent.getParcelableExtra<Intent>(SCREENSHOT_KEY_DATA) ?: return super.onStartCommand(
                intent,
                flags,
                startId
            )
        when (intent.action) {
            ACTION_SCREENSHOT -> {
                // 截屏
                MediaProjectionManagerScreenHelp(this, code, data).screenCapture(object :
                    MediaProjectionManagerScreenHelp.OnImageListener {
                    override fun onImagePath(fileName: String) {
                        WebDebugger.mediaObservable.notifyObservers()
                    }
                })
            }
            ACTION_START_SCREEN_RECORDING -> {
                // 开始录像
                if (mHelp == null) {
                    mHelp = MediaProjectionManagerScreenHelp(this, code, data)
                }
                mHelp?.startScreenRecording()
            }
            ACTION_STOP_SCREEN_RECORDING -> {
                // 结束录像
                mHelp?.stopScreenRecording()
                mHelp = null
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 开启通知栏
     */
    private fun createNotificationChannel() {
        // android8.0 以上要创建一个通道
        var channel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true);//是否在桌面icon右上角展示小红点
            channel.lightColor = Color.RED;//小红点颜色
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
        }

        // 创建通知
        val builder: NotificationCompat.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(this, "webdebugger")
            } else {
                NotificationCompat.Builder(this)
            }
        builder.setContentTitle("截屏")
        builder.setContentText("Webdebugger截屏中")
        builder.setChannelId(CHANNEL_ID)
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        // 将builder对象转换为普通的notification
        val notification = builder.build()
        // 点击通知后通知消失
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // android8.0 以上，创建通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(channel!!)
        }
        startForeground(mRandom.nextInt(10000), notification)
    }

}