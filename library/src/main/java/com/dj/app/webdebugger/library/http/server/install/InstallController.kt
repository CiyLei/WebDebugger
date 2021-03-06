/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dj.app.webdebugger.library.http.server.install

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.annotation.PostMapping
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.http.server.HttpController
import com.dj.app.webdebugger.library.utils.FileUtil
import fi.iki.elonen.NanoFileUpload
import fi.iki.elonen.NanoHTTPD
import okhttp3.HttpUrl
import org.apache.commons.fileupload.disk.DiskFileItem
import org.apache.commons.fileupload.disk.DiskFileItemFactory
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

    // 上传服务
    private lateinit var mUploader: NanoFileUpload

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
        // 初始化文件上传服务
        mUploader = NanoFileUpload(
            DiskFileItemFactory(0, FileUtil.getDownLoadCacheFile(context!!))
        )
    }

    /**
     * 根据url下载安装apk
     */
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
     * 上传apk进行安装
     */
    @PostMapping("/installFromUpload")
    fun installFromUpload(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val fileMap = mUploader.parseParameterMap(session)
        val apkFileItem = fileMap.values.first().first() as? DiskFileItem
            ?: return fail(ResponseConstant.FAIL_UPLOAD)
        // 安装apk
        installApk(apkFileItem.storeLocation)
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
                getPackName() + ".webdebugger.fileprovider",
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

    /**
     * 获取包名
     */
    private fun getPackName(): String {
        return context?.packageName ?: ""
    }
}