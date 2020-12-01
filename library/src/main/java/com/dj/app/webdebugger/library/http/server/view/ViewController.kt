package com.dj.app.webdebugger.library.http.server.view

import android.os.Handler
import android.os.Looper
import android.support.v4.app.FragmentActivity
import com.dj.app.webdebugger.library.WebDebugger
import com.dj.app.webdebugger.library.annotation.Controller
import com.dj.app.webdebugger.library.annotation.GetMapping
import com.dj.app.webdebugger.library.common.ResponseConstant
import com.dj.app.webdebugger.library.http.server.HttpController
import com.dj.app.webdebugger.library.http.server.view.attributes.AttributesBean
import com.dj.app.webdebugger.library.utils.ViewUtils
import fi.iki.elonen.NanoHTTPD
import java.lang.Exception

/**
 * Create by ChenLei on 2020/11/26
 * Describe: 查看View信息控制器
 *
 * 灵感 [https://github.com/willowtreeapps/Hyperion-Android]
 */
@Controller("/view")
internal class ViewController : HttpController() {

    private val mHandle = Handler(Looper.getMainLooper())

    companion object {
        const val MONITOR_DIALOG_TAG = "MONITOR_DIALOG_TAG"
    }

    /**
     * 安装查看View的模块
     */
    @GetMapping("/installMonitorView")
    fun overlayView(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        (WebDebugger.topActivity as? FragmentActivity)?.let {
            if (it.supportFragmentManager.findFragmentByTag(MONITOR_DIALOG_TAG) == null) {
                // 提前获取顶部的View
                MonitorView.topView = ViewUtils.getTopView()
                MonitorDialog.newInstance().show(it.supportFragmentManager, MONITOR_DIALOG_TAG)
            }
        }
        return success(true)
    }

    /**
     * 卸载查看View的模块
     */
    @GetMapping("/unInstallMonitorView")
    fun unOverlayView(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        ((WebDebugger.topActivity as? FragmentActivity)?.supportFragmentManager?.findFragmentByTag(
            MONITOR_DIALOG_TAG
        ) as? MonitorDialog)?.dismiss()
        return success(true)
    }

    /**
     * 返回当前View的树
     */
    @GetMapping("/viewTree")
    fun viewTree(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val topView = ViewUtils.getTopView() ?: return fail(ResponseConstant.EMPTY_TOP_VIEW)
        return success(listOf(ViewUtils.view2ViewDescription(topView)))
    }

    /**
     * 选中某个View
     */
    @GetMapping("/selectView")
    fun selectView(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val code = session.parameters["code"]?.firstOrNull()?.toIntOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        (WebDebugger.topActivity as? FragmentActivity)?.let {
            if (it.supportFragmentManager.findFragmentByTag(MONITOR_DIALOG_TAG) == null) {
                // 如果没有覆盖弹框的话
                MonitorView.topView = ViewUtils.getTopView()
                MonitorDialog.newInstance(code)
                    .show(it.supportFragmentManager, MONITOR_DIALOG_TAG)
            } else {
                // 如果有覆盖弹框的话
                MonitorView.topView?.let { topView ->
                    ViewUtils.findView(topView, code)?.let { v ->
                        ((WebDebugger.topActivity as? FragmentActivity)?.supportFragmentManager?.findFragmentByTag(
                            MONITOR_DIALOG_TAG
                        ) as? MonitorDialog)?.monitorView?.refresh(v)
                    }
                }
            }
        }
        return success()
    }

    @GetMapping("/getAttributes")
    fun getAttributes(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val code = session.parameters["code"]?.firstOrNull()?.toIntOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        MonitorView.topView?.let { topView ->
            ViewUtils.findView(topView, code)?.let { v ->
                val allAttributes = ViewUtils.readAllAttributes(v)
                val result = ArrayList<AttributesBean>()
                for (allAttribute in allAttributes) {
                    // 第一个为标签
                    result.add(AttributesBean(allAttribute.key, type = AttributesBean.TYPE_LABEL))
                    // 之后是属性列表
                    result.addAll(allAttribute.value)
                }
                return success(result)
            }
        }
        return success(emptyList<AttributesBean>())
    }

    /**
     * 设置属性
     */
    @GetMapping("/setAttributes")
    fun setAttributes(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val code = session.parameters["code"]?.firstOrNull()?.toIntOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        val attribute = session.parameters["attribute"]?.firstOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        val value = session.parameters["value"]?.firstOrNull()
            ?: return fail(ResponseConstant.MUST_PARAMETER_URL)
        MonitorView.topView?.let { topView ->
            ViewUtils.findView(topView, code)?.let { v ->
                WebDebugger.viewAttributesList.forEach {
                    if (it.match(v) && it.attribute(v) == attribute) {
                        mHandle.post {
                            try {
                                it.setValue(v, value)
                            } catch (e: Exception) {
                                if (WebDebugger.isDebug) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        return success()
                    }
                }
            }
        }
        return success(emptyList<AttributesBean>())
    }

}