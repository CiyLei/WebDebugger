package com.dj.app.webdebugger.library

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 返回体返回常量
 */

internal enum class ResponseConstant(val code: Int, val message: String) {
    NO_RETROFIT(101,"未设置Retrofit实例"),
    FAIL_BASE_URL(102,"无法获取Retrofit的BaseUrl"),
    FAIL_EDIT_URL(103,"修改RetrofitUrl失败"),
    RECORDING_SCREENONLY_SUPPORTS_ANDROID5(104,"录屏只支持Android5.0以上"),
    MEDIA_CACHE_ACQUISITION_FAILED(105,"媒体缓存获取失败"),
}