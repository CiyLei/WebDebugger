package com.dj.app.webdebugger.library.annotation

/**
 * Create by ChenLei on 2019/10/31
 * Describe: Get请求注解
 */
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
internal annotation class GetMapping(val value: String)