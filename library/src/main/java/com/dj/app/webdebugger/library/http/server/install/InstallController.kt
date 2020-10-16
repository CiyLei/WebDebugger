package com.dj.app.webdebugger.library.http.server.install

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.http.server.HttpController
import com.dj.app.webdebugger.library.utils.FileUtil
import fi.iki.elonen.NanoHTTPD
import okhttp3.HttpUrl
import java.io.File
import java.net.URI

/**
 * Create by ChenLei on 2020/10/16
 * Describe: 安装apk的控制类
 */
@Controller("/install")
internal class InstallController : HttpController() {

    // 下载服务
    private lateinit var mDownloadManager: DownloadManager

    /**
     * 监听下载的广播
     */
    private val mDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // 查询出所有下载完成的
            val query = DownloadManager.Query()
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)
            val cursor = mDownloadManager.query(query)
            // 移动到第一个，为刚下载完成的apk
            if (cursor.moveToFirst()) {
                val local =
                    cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                // 获取下载完成的目录
                val file = File(URI(Uri.parse(local).toString()))
                // 安装apk
                installApk(file)
            }
            cursor.close()
        }
    }

    override fun onStart() {
        super.onStart()
        // 获取下载服务
        (context?.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager)?.let {
            mDownloadManager = it
        }
        // 初始化的时候就监听下载的广播
        context!!.registerReceiver(
            mDownloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    @GetMapping("/installFromUrl")
    fun installFromUrl(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (!::mDownloadManager.isInitialized) {
            return fail(ResponseConstant.NOT_FOUND_DOWNLOAD_SERVICE)
        }
        val url = session.parameters["url"]?.firstOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        // 获取文件名称
        val fileName = HttpUrl.get(url).pathSegments().last() ?: url.hashCode().toString()
        // 构建下载请求
        val request = DownloadManager.Request(Uri.parse(url))
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(fileName)
        request.setDescription("WebDebugger下载安装服务正在从${url}下载${fileName}")
        request.setVisibleInDownloadsUi(true)
        // 设置下载路径
        val file = FileUtil.getDownLoadCacheFile(context!!).resolve(fileName)
        request.setDestinationUri(Uri.fromFile(file))
        // 开始异步下载
        mDownloadManager.enqueue(request)
        return success()
    }

    /**
     * 安装apk
     */
    private fun installApk(apkFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            val apkUri = FileProvider.getUriForFile(
                context!!,
                "com.dj.app.webdebugger.fileprovider",
                apkFile
            )
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        context!!.startActivity(intent)
    }
}