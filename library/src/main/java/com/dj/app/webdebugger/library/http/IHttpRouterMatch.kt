package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

interface IHttpRouterMatch {
    /**
     * 注册匹配的路径
     */
    fun matchRouter(uri: String, method: NanoHTTPD.Method): Boolean

    /**
     * 处理匹配的请求
     */
    fun handle(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response?
}