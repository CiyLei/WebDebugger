package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/11/3
 * Describe:
 */
abstract class SingleRouterMatch(val uri: String) : IHttpRouterMatch {
    override fun matchRouter(uri: String, method: NanoHTTPD.Method): Boolean = this.uri == uri
}