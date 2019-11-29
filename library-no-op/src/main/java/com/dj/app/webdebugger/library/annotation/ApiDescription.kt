package com.dj.app.webdebugger.library.annotation


/**
 * Create by ChenLei on 2019/11/15
 * Describe: api 说明注解
 */
@kotlin.annotation.Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ApiDescription(val value: String)
