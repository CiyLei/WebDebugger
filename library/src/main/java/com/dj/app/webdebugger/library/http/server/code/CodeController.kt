package com.dj.app.webdebugger.library.http.server.code

import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.PostMapping
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2020/8/6
 * Describe: 代码控制器
 */
@Controller("/code")
internal class CodeController : HttpController() {

    /**
     * 动态执行代码
     */
    @PostMapping("/execute")
    fun handleExecute(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val postParamt = getPostParamt(session)
        // 执行的代码
        val code = (postParamt?.get("code") as? String) ?: ""
        // 需要导入的包
        val import = (postParamt?.get("import") as? String) ?: ""
        // 是否运行在主线程
        val runOnMainThread = (postParamt?.get("runOnMainThread") as? Boolean) ?: false
        if (context != null) {
            val execute = DynamicExecute.newInstance(context!!, import, code)
            val result = execute.execute(runOnMainThread)
            if (result) {
                return success(execute.outContent)
            }
        }
        return fail(ResponseConstant.EXECUTE_CODE_FAIL)
    }
}