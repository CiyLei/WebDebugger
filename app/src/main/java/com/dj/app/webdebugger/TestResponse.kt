package com.dj.app.webdebugger


/**
 * Create by ChenLei on 2019/11/15
 * Describe:
 */
data class TestResponse<T>(val t: T, val a: String, val b: B) {
    data class B(val c: Int, val map: Map<Boolean, Double>, val v: B)
}
