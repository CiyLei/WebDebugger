package com.dj.app.webdebugger.library.websocket.server.media

import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.WebDebugger.Companion.mediaObservable
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.utils.FileUtil
import com.dj.app.webdebugger.library.websocket.server.WSController
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoWSD
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 监听添加媒体缓存文件的WebSocket
 */
@Controller("/media/add")
internal class MediaWSController(handle: NanoHTTPD.IHTTPSession) : WSController(handle), Observer {

    private val fileCacheList = ArrayList<String>()

    override fun update(o: Observable?, arg: Any?) {
        val cachePath = FileUtil.getMediaCacheFile(context!!)
        if (cachePath.isDirectory) {
            val c =
                ArrayList<String>(cachePath.listFiles().map { FileUtil.getMediaCachePath() + it.name })
            // 发生添加的文件名称
            val addList = c.filter { !fileCacheList.contains(it) }
            sendOfJson(MediaListBean(WebDebugger.resourcePort, addList))
            fileCacheList.addAll(addList)
        }
    }

    override fun onOpen() {
        val cachePath = FileUtil.getMediaCacheFile(context!!)
        if (cachePath.isDirectory) {
            fileCacheList.addAll(cachePath.listFiles().map { FileUtil.getMediaCachePath() + it.name })
            sendOfJson(MediaListBean(WebDebugger.resourcePort, fileCacheList))
        }
        mediaObservable.addObserver(this)
    }

    override fun onClose(
        code: NanoWSD.WebSocketFrame.CloseCode?,
        reason: String?,
        initiatedByRemote: Boolean
    ) {
        mediaObservable.deleteObserver(this)
    }

    override fun onPong(pong: NanoWSD.WebSocketFrame?) {
    }

    override fun onMessage(message: NanoWSD.WebSocketFrame?) {
    }

    override fun onException(exception: IOException?) {
//        mediaObservable.deleteObserver(this)
    }

}