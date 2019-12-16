package com.dj.app.webdebugger.library.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Create by ChenLei on 2019/12/16
 * Describe: ROOM 数据转化器
 */
internal class DBConverters {

    companion object {
        @JvmStatic
        @TypeConverter
        fun mapToString(map: Map<String, List<String>>): String {
            return Gson().toJson(map)
        }

        @JvmStatic
        @TypeConverter
        fun stringToMap(str: String): Map<String, List<String>> {
            return Gson().fromJson(str, object : TypeToken<Map<String, List<String>>>() {}.type)
        }
    }
}