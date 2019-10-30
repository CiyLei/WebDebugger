package com.dj.app.webdebugger.library.http

import android.content.Context
import android.content.res.AssetManager
import com.dj.app.webdebugger.library.utils.StreamUtil
import fi.iki.elonen.NanoHTTPD
import java.io.File


/**
 * Create by ChenLei on 2019/10/30
 * Describe: assets Assets 资源目录路由匹配
 */

class AssetsRouterMatch(context: Context) : IHttpRouterMatch {
    companion object {
        val HTTP_ROOT_PATH = "webdebug"
        val HTTP_INDEX_PATH = HTTP_ROOT_PATH + File.separator + "index.html"
        val HTTP_404_PATH = HTTP_ROOT_PATH + File.separator + "404.html"
    }

    var assetManager: AssetManager = context.assets
    var assetsPaths = ArrayList<String>()

    init {
        loadAssetsPaths(assetManager, HTTP_ROOT_PATH)
        assetsPaths = ArrayList(assetsPaths.map {
            if (it.startsWith(HTTP_ROOT_PATH)) {
                it.substring(HTTP_ROOT_PATH.length)
            } else {
                it
            }
        })
    }

    private fun loadAssetsPaths(assetManager: AssetManager, path: String) {
        val list = assetManager.list(path)
        list.forEach {
            val childPath = path + File.separator + it
            val childCount = assetManager.list(childPath).size
            if (childCount == 0) {
                assetsPaths.add(childPath)
            } else {
                loadAssetsPaths(assetManager, childPath)
            }
        }
    }

    override fun matchRouter(uri: String, method: NanoHTTPD.Method): Boolean {
        return true
    }

    override fun handle(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (assetsPaths.contains(session.uri)) {
            return NanoHTTPD.newFixedLengthResponse(getAssetsString(HTTP_ROOT_PATH + session.uri))
        } else {
            // 无uri的时候，展示 index.html
            if (session.uri == "/") {
                return NanoHTTPD.newFixedLengthResponse(getAssetsString(HTTP_INDEX_PATH))
            }
            return NanoHTTPD.newFixedLengthResponse(getAssetsString(HTTP_404_PATH))
        }
    }

    private fun getAssetsString(path: String): String {
        return StreamUtil.inputStreamToString(assetManager.open(path))
    }

}
