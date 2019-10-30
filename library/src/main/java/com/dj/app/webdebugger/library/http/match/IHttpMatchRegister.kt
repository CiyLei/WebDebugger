package com.dj.app.webdebugger.library.http.match

import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

interface IHttpMatchRegister {
    /**
     * 注册匹配的路径
     */
    fun registerMatchPath(): List<String>

    /**
     * 处理匹配的请求
     */
    fun handle(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response
}