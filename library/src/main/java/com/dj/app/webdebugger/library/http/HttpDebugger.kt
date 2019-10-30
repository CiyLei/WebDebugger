package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

class HttpDebugger(port: Int) : NanoHTTPD(port) {

    val matchRegister = ArrayList<IHttpRouterMatch>()

    override fun serve(session: IHTTPSession): Response {
        matchRegister.forEach {
            if (it.matchRouter(session.uri, session.method)) {
                val response = it.handle(session)
                if (response != null)
                    return response
            }
        }
        return newFixedLengthResponse("Error")
    }
}