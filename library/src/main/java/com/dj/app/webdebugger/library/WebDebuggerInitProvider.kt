package com.dj.app.webdebugger.library

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.dj.app.webdebugger.library.http.HttpDebugger
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/10/30
 * Describe: 框架初始化入口
 */

class WebDebuggerInitProvider : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun onCreate(): Boolean {
        val port = context?.getString(R.string.PORT_NUMBER)?.toInt() ?: 8080
        HttpDebugger(port).start(NanoHTTPD.SOCKET_READ_TIMEOUT)
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