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
package com.dj.app.webdebugger.library.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dj.app.webdebugger.library.common.NetInfoBean

/**
 * Create by ChenLei on 2019/12/16
 * Describe: 请求历史记录Dao层
 */
@Dao
internal interface NetHistoryDao {

    /**
     * 插入一条请求记录
     */
    @Insert
    fun addNetHistory(netInfoBean: NetInfoBean)

    /**
     * 获取所有的历史请求记录
     */
    @Query("select * from net_history order by requestTime desc limit (:page - 1)*:pageSize,:pageSize")
    fun getAllNetHistory(page: Int, pageSize: Int): List<NetInfoBean>

    /**
     * 获取历史记录格式
     */
    @Query("select count(*) from net_history")
    fun getAllNetHistoryCount(): Long
}