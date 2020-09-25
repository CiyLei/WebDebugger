package com.dj.app.webdebugger.library.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Create by ChenLei on 2019/12/16
 * Describe: ROOM 数据转化器
 */
internal class WebDebuggerDBConverters {

    companion object {
        @JvmStatic
        @TypeConverter
        fun mapToString(map: HashMap<String, ArrayList<String>>): String {
            return Gson().toJson(map)
        }

        @JvmStatic
        @TypeConverter
        fun stringToMap(str: String): HashMap<String, ArrayList<String>> {
            return Gson().fromJson(str, object : TypeToken<HashMap<String, ArrayList<String>>>() {}.type)
        }
    }
}