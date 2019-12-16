package com.dj.app.webdebugger.library.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.dj.app.webdebugger.library.common.NetInfoBean

/**
 * Create by ChenLei on 2019/12/16
 * Describe:
 */
@Database(entities = [NetInfoBean::class], version = 1)
internal abstract class WebDebuggerDataBase : RoomDatabase() {
    abstract fun netHistoryDao(): NetHistoryDao
}