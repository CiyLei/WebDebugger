package com.dj.app.webdebugger.library.http.server.view

import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.ViewGroup
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.http.server.HttpController
import fi.iki.elonen.NanoHTTPD

/**
 * Create by ChenLei on 2020/11/26
 * Describe: 查看View信息控制器
 *
 * 灵感 [https://github.com/willowtreeapps/Hyperion-Android]
 */
@Controller("/view")
internal class ViewController : HttpController() {

    companion object {
        private const val MONITOR_DIALOG_TAG = "MONITOR_DIALOG_TAG"
    }

    @GetMapping("/installMonitorView")
    fun overlayView(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        (WebDebugger.topActivity as? FragmentActivity)?.let {
            if (it.supportFragmentManager.findFragmentByTag(MONITOR_DIALOG_TAG) == null) {
                // 提前获取顶部的View
                MonitorView.topView = MonitorView.obtainTopView()
                MonitorDialog.newInstance().show(it.supportFragmentManager, MONITOR_DIALOG_TAG)
            }
        }
        return success(true)
    }

    @GetMapping("/unInstallMonitorView")
    fun unOverlayView(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        ((WebDebugger.topActivity as? FragmentActivity)?.supportFragmentManager?.findFragmentByTag(
            MONITOR_DIALOG_TAG
        ) as? MonitorDialog)?.dismiss()
        return success(true)
    }

    @GetMapping("/viewTree")
    fun viewTree(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val topView = MonitorView.obtainTopView() ?: return fail(ResponseConstant.EMPTY_TOP_VIEW)
        return success(listOf(view2ViewDescription(topView)))
    }

    @GetMapping("/selectView")
    fun selectView(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val hashCode = session.parameters["hashCode"]?.firstOrNull()?.toIntOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        (WebDebugger.topActivity as? FragmentActivity)?.let {
            if (it.supportFragmentManager.findFragmentByTag(MONITOR_DIALOG_TAG) == null) {
                // 如果没有覆盖弹框的话
                MonitorView.topView = MonitorView.obtainTopView()
                MonitorDialog.newInstance(hashCode).show(it.supportFragmentManager, MONITOR_DIALOG_TAG)
            } else {
                // 如果有覆盖弹框的话
                MonitorView.topView?.let { topView ->
                    MonitorView.findView(topView, hashCode)?.let { v ->
                        ((WebDebugger.topActivity as? FragmentActivity)?.supportFragmentManager?.findFragmentByTag(
                            MONITOR_DIALOG_TAG
                        ) as? MonitorDialog)?.monitorView?.refresh(v)
                    }
                }
            }
        }
        return success()
    }

    /**
     * 将View转换为ViewDescription
     */
    private fun view2ViewDescription(view: View): ViewDescription {
        if (view is ViewGroup) {
            val childrenViews = ArrayList<ViewDescription>()
            for (i in 0 until view.childCount) {
                childrenViews.add(view2ViewDescription(view.getChildAt(i)))
            }
            return ViewDescription(view.hashCode().toString(), view.toString(), childrenViews)
        }
        return ViewDescription(view.hashCode().toString(), view.toString())
    }

}