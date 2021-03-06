/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dj.app.webdebugger.library.common

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


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
     * 开始请求的时间
     */
    var requestTime: Long = 0L,

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
    var callFailError: String = "",

    /**
     * 请求详细异常
     */
    var callFailErrorDetail: String = "",

    /**
     * 是否发送过
     */
    @Ignore
    var isSent: Boolean = false
)
