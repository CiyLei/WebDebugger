package com.dj.app.webdebugger.library


/**
 * Create by ChenLei on 2019/10/30
 * Describe: 基本返回类型
 */
internal data class BaseResponseBean<T>(
    var code: Int,
    var success: Boolean,
    var data: T,
    var message: String
)
