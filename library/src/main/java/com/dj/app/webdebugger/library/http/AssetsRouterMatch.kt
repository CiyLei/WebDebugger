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
package com.dj.app.webdebugger.library.http

import android.content.Context
import android.content.res.AssetManager
import com.dj.app.webdebugger.library.utils.FileUtil
import fi.iki.elonen.NanoHTTPD
import java.io.File


/**
 * Create by ChenLei on 2019/10/30
 * Describe: assets Assets 资源目录路由匹配
 */

internal class AssetsRouterMatch(context: Context) : IHttpRouterMatch {
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
        list?.forEach {
            val childPath = path + File.separator + it
            val childCount = assetManager.list(childPath)?.size ?: 0
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
            for (entry in mimeTypeMap()) {
                if (session.uri.endsWith(entry.key)) {
                    return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        entry.value,
                        assetManager.open(HTTP_ROOT_PATH + session.uri),
                        -1
                    )
                }
            }
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
        return FileUtil.inputStreamToString(assetManager.open(path))
    }

    private fun mimeTypeMap(): Map<String, String> {
        val map = HashMap<String, String>()
        map[".css"] = "text/css; charset=utf-8"
        map[".ttf"] = "font/ttf;"
        map[".otf"] = "font/otf;"
        map[".woff"] = "font/woff;"
        map[".ico"] = "image/x-icon;"
        map[".webp"] = "image/webp; charset=UTF-8"
        map[".wasm"] = "application/octet-stream"
        return map
    }

}
