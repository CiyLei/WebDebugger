package com.dj.app.webdebugger

/**
 * Create by ChenLei on 2021/2/1
 * Describe:
 */
data class BiliBiliRes(
    val code: Int,
    val `data`: Data,
    val message: String,
    val ttl: Int
)

data class Data(
    val goto_type: Int,
    val goto_value: String,
    val id: Long,
    val name: String,
    val seid: String,
    val show_name: String,
    val type: Int,
    val url: String
)