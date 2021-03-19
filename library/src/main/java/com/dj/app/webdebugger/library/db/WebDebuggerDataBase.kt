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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dj.app.webdebugger.library.common.NetInfoBean

/**
 * Create by ChenLei on 2019/12/16
 * Describe:
 */
@Database(entities = [NetInfoBean::class], version = 2)
@TypeConverters(value = [WebDebuggerDBConverters::class])
internal abstract class WebDebuggerDataBase : RoomDatabase() {
    companion object {
        /**
         * 版本1升2的sql
         * NetInfoBean添加网络请求的事件时间
         */
        @JvmStatic
        val MIGRATION_1_TO_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table net_history add column callStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column dnsStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column dnsEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column connectStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column secureConnectStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column secureConnectEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column connectFailedTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column connectEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column connectionAcquiredTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column requestHeadersStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column requestHeadersEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column requestBodyStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column requestBodyEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column responseHeadersStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column responseHeadersEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column responseBodyStartTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column responseBodyEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column connectionReleasedTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column callFailedTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column callEndTime INTEGER NOT NULL DEFAULT 0")
                database.execSQL("alter table net_history add column callFailError TEXT NOT NULL DEFAULT \"\"")
                database.execSQL("alter table net_history add column callFailErrorDetail TEXT NOT NULL DEFAULT \"\"")
                database.execSQL("alter table net_history add column requestTime INTEGER NOT NULL DEFAULT 0")
            }
        }

    }

    abstract fun netHistoryDao(): NetHistoryDao
}