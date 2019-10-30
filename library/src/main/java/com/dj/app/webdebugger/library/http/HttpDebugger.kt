package com.dj.app.webdebugger.library.http

import fi.iki.elonen.NanoHTTPD


/**
 * Create by ChenLei on 2019/10/30
 * Describe:
 */

class HttpDebugger(port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val method = session.method
        val uri = session.uri
        var msg = "<html><body><h1>Hello server</h1>\n"
        val parms = session.parms
        if (parms["username"] == null) {
            msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n"
        } else {
            msg += "<p>Hello, " + parms["username"] + "!</p>"
        }
        msg += "<p>method:$method   uri:$uri</p>"
        msg += "</body></html>\n"
        return newFixedLengthResponse(msg)
    }
}