package com.dj.app.webdebugger.library.http.server.net

import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.common.PageBean
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2019/12/16
 * Describe: 网络请求Controller
 */
@Controller("/net")
internal class NetController : HttpController() {

    @GetMapping("/getHistory")
    fun getHistory(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val page = session.parameters?.get("page")?.get(0) ?: "1"
        val pageSize = session.parameters?.get("pageSize")?.get(0) ?: "20"
        return success(
            PageBean(
                WebDebugger.dataBase.netHistoryDao().getAllNetHistoryCount(),
                WebDebugger.dataBase.netHistoryDao().getAllNetHistory(
                    page.toInt(),
                    pageSize.toInt()
                )
            )
        )
    }
}