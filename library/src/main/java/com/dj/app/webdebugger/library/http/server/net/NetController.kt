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