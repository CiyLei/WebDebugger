package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.Exception


/**
 * Create by ChenLei on 2019/10/30
 * Describe: Http 服务器
 */

internal class HttpDebugger(port: Int) : NanoHTTPD(port) {

    val httpMatchs = ArrayList<IHttpRouterMatch>()

    override fun serve(session: IHTTPSession): Response {
        httpMatchs.forEach {
            if (it.matchRouter(session.uri, session.method)) {
                try {
                    val response = it.handle(session)
                    if (response != null)
                        return response
                } catch (e: Throwable) {
//                    e.printStackTrace()
                    val baos = ByteArrayOutputStream()
                    e.printStackTrace(PrintStream(baos))
                    baos.close()
                    return newFixedLengthResponse(baos.toString())
                }
            }
        }
        return newFixedLengthResponse("Error")
    }
}