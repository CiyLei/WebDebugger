package com.dj.app.webdebugger.library.annotation

/**
 * Create by ChenLei on 2019/10/31
 * Describe: 控制器注解
 */

@kotlin.annotation.Target(AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
internal annotation class Controller(val value: String)