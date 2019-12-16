package com.dj.app.webdebugger.library.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import com.dj.app.webdebugger.library.db.DBConverters

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 网络请求数据
 */
@Entity(tableName = "net_history")
@TypeConverters(value = [DBConverters::class])
internal data class NetInfoBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val url: String,
    val method: String,
    val requestDataTime: String,
    val timeCost: Long,
    val requestHeaders: Map<String, List<String>>,
    val responseHeaders: Map<String, List<String>>,
    val code: Int,
    val requestBody: String,
    val responseBody: String
)