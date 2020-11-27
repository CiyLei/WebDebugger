package com.dj.app.webdebugger.library.common

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 返回体返回常量
 */

internal enum class ResponseConstant(val code: Int, val message: String) {
    NO_RETROFIT(101, "未设置Retrofit实例"),
    FAIL_BASE_URL(102, "无法获取Retrofit的BaseUrl"),
    FAIL_EDIT_URL(103, "修改RetrofitUrl失败"),
    RECORDING_SCREENONLY_SUPPORTS_ANDROID5(104, "录屏只支持Android5.0以上"),
    MEDIA_CACHE_ACQUISITION_FAILED(105, "媒体缓存获取失败"),
    GET_DEVICE_SCREEN_FAILED(106, "获取屏幕信息失败"),
    GET_API_LIST_FAILED(107, "获取API清单失败"),
    EXECUTE_CODE_FAIL(108, "执行代码失败"),
    MUST_PARAMETER_URL(109, "缺失必要的参数"),
    NOT_FOUND_DOWNLOAD_SERVICE(110, "无法找到下载服务"),
    FAIL_UPLOAD(111, "上传文件错误"),
    EMPTY_TOP_VIEW(112, "找不到顶部的View"),
}