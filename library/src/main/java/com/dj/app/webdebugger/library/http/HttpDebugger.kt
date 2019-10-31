package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe: Http 服务器
 */

class HttpDebugger(port: Int) : NanoHTTPD(port) {

    val httpMatchs = ArrayList<IHttpRouterMatch>()

    override fun serve(session: IHTTPSession): Response {
        httpMatchs.forEach {
            if (it.matchRouter(session.uri, session.method)) {
                val response = it.handle(session)
                if (response != null)
                    return response
            }
        }
        return newFixedLengthResponse("Error")
    }
}