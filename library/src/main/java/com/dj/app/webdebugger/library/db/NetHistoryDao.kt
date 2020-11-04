package com.dj.app.webdebugger.library.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
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