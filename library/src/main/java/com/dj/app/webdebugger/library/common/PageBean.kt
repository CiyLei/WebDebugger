package com.dj.app.webdebugger.library.common

/**
 * Create by ChenLei on 2019/12/16
 * Describe: 分页数据
 */
internal data class PageBean<T>(val total: Long, val list: T)