package com.dj.app.webdebugger.library.common

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Create by ChenLei on 2019/11/1
 * Describe: 网络请求数据
 */
@Entity(tableName = "net_history")
internal data class NetInfoBean(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    /**
     * 请求url
     */
    var url: String = "",

    /**
     * 请求的方式
     */
    var method: String = "",

    /**
     * 开始请求的时间
     */
    var requestDataTime: String = "",

    /**
     * 请求总耗时
     */
    var timeCost: Long = 0L,

    /**
     * 请求头信息
     */
    var requestHeaders: HashMap<String, ArrayList<String>> = HashMap(),

    /**
     * 响应头信息
     */
    var responseHeaders: HashMap<String, ArrayList<String>> = HashMap(),

    /**
     * 响应code
     */
    var code: Int = 0,

    /**
     * 请求内容
     */
    var requestBody: String = "",

    /**
     * 相应内容
     */
    var responseBody: String = "",

    /**
     * 请求开始时间（对应callEndTime）
     */
    var callStartTime: Long = 0L,

    /**
     * dns开始时间（对应dnsEndTime）
     */
    var dnsStartTime: Long = 0L,

    /**
     * dns结束时间（对应dnsStartTime）
     */
    var dnsEndTime: Long = 0L,

    /**
     * 连接开始时间（对应connectEndTime）
     */
    var connectStartTime: Long = 0L,

    /**
     * https认证开始时间（对应secureConnectEndTime）
     */
    var secureConnectStartTime: Long = 0L,

    /**
     * https认证结束时间（对应secureConnectStartTime）
     */
    var secureConnectEndTime: Long = 0L,

    /**
     * 连接失败时间（对应connectStartTime，如果失败了，直接走callFailedTime）
     */
    var connectFailedTime: Long = 0L,

    /**
     * 连接结束时间（对应connectStartTime）
     */
    var connectEndTime: Long = 0L,

    /**
     * 获得一个长连接的时间（对应connectionReleasedTime）
     */
    var connectionAcquiredTime: Long = 0L,

    /**
     * 写入请求头Header开始时间
     */
    var requestHeadersStartTime: Long = 0L,

    /**
     * 写入请求头Header结束时间
     */
    var requestHeadersEndTime: Long = 0L,

    /**
     * 写入请求头Body开始时间
     */
    var requestBodyStartTime: Long = 0L,

    /**
     * 写入请求头Body结束时间
     */
    var requestBodyEndTime: Long = 0L,

    /**
     * 读取响应头Header开始时间
     */
    var responseHeadersStartTime: Long = 0L,

    /**
     * 读取响应头Header结束时间
     */
    var responseHeadersEndTime: Long = 0L,

    /**
     * 读取响应头Body开始时间
     */
    var responseBodyStartTime: Long = 0L,

    /**
     * 读取响应头Body结束时间
     */
    var responseBodyEndTime: Long = 0L,

    /**
     * 释放一个长连接的时间（对应connectionAcquiredTime）
     */
    var connectionReleasedTime: Long = 0L,

    /**
     * 请求失败的时间（对应connectFailedTime）
     */
    var callFailedTime: Long = 0L,

    /**
     * 请求结束的时间（对应callStartTime）
     */
    var callEndTime: Long = 0L,

    /**
     * 请求异常
     */
    var callFailError: String = ""
)
