package com.dj.app.webdebugger

import android.app.Application
import com.dj.app.webdebugger.library.WebDebugger

/**
 * Create by ChenLei on 2019/10/31
 * Describe:
 */

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WebDebugger.initApplication(this)
    }
}