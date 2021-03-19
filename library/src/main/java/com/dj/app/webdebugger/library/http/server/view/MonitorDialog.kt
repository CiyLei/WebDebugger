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
package com.dj.app.webdebugger.library.http.server.view

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dj.app.webdebugger.library.R
import com.dj.app.webdebugger.library.utils.ViewUtils

/**
 * Create by ChenLei on 2020/11/26
 * Describe: 监控Dialog
 */
internal class MonitorDialog : DialogFragment() {

    companion object {
        private const val KEY_HASH_CODE = "KEY_HASH_CODE"

        fun newInstance(hashCode: Int = 0): MonitorDialog {
            return MonitorDialog().apply {
                arguments = Bundle().apply {
                    putInt(KEY_HASH_CODE, hashCode)
                }
            }
        }
    }

    lateinit var monitorView: MonitorView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!, R.style.webdebugger_transparent_dialog).apply {
            monitorView = MonitorView(context)
            // 如果有默认的选择View
            val hashCode = arguments?.getInt(KEY_HASH_CODE)
            if (MonitorView.topView != null && hashCode != null && hashCode != 0) {
                ViewUtils.findView(MonitorView.topView!!, hashCode)?.let {
                    monitorView.refresh(it)
                }
            }
            setContentView(monitorView)
            // 设置无边框
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                window?.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            } else {
                window?.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                )
                window?.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                )
            }
        }
    }

    override fun onDestroy() {
        // 释放顶部view的引用，以防内存泄露
        MonitorView.topView = null
        super.onDestroy()
    }

}