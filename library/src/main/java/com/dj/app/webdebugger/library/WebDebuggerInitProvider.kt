package com.dj.app.webdebugger.library

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Create by ChenLei on 2019/10/30
 * Describe: 框架初始化入口
 */

internal class WebDebuggerInitProvider : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun onCreate(): Boolean {
        val httpPort = context!!.getString(R.string.HTTP_PORT).toInt()
        val webSocketPort = context!!.getString(R.string.WEB_SOCKET_PORT).toInt()
        val resourcePort = context!!.getString(R.string.RESOURCE_PORT).toInt()
        WebDebugger.start(context!!.applicationContext, httpPort, webSocketPort, resourcePort)
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null

}