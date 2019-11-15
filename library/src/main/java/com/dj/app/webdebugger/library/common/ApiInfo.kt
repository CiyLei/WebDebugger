package com.dj.app.webdebugger.library.common

import com.dj.app.webdebugger.library.utils.RetrofitUtil.getField
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.internal.bind.CollectionTypeAdapterFactory
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/**
 * Create by ChenLei on 2019/11/15
 * Describe: Retrofit api 模型
 */
internal data class ApiInfo(
    val url: String,
    val method: String,
    val requestBody: HashMap<String, String> = HashMap(),
    var returnType: Type? = null
) {

    fun toMap(): Map<String, Any> {
        val map = HashMap<String, Any>()
        map["url"] = url
        map["method"] = method
        map["requestBody"] = requestBody
        map["returnType"] = addParameterizedType(Gson().getAdapter(TypeToken.get(returnType))) ?: Any()
        return map
    }

    private fun addParameterizedType(adapter: TypeAdapter<*>): Any? {
        // enclosingClass 内部类获取所在的类
        if (adapter::class.java.enclosingClass == ReflectiveTypeAdapterFactory::class.java) {
            // 普通的类
            val paramMap = HashMap<String, Any>()
            val fields = getField(adapter, "boundFields")?.get(adapter) as? Map<String, Any>
            for (field in fields?.entries ?: emptySet()) {
                val fieldName = field.key
                val typeAdapterFactory = field.value
                // fieldType是匿名内部类引用外部变量的值，所以有val$的前缀
                val fieldType = getField(typeAdapterFactory, "val\$fieldType")?.get(typeAdapterFactory) as? TypeToken<*>
                if (fieldType != null) {
                    // 判断是否是基本类型
                    if (isBaseType(fieldType.rawType)) {
                        paramMap[fieldName] = fieldType.rawType.simpleName
                    } else {
                        // 非基本类型
                        val typeAdapter = getField(typeAdapterFactory, "val\$typeAdapter")?.get(typeAdapterFactory) as? TypeAdapter<*>
                        if (typeAdapter != null && typeAdapter::class.java.enclosingClass != TypeAdapters::class.java) {
                            // 还有字段的话
                            addParameterizedType(typeAdapter)?.let {
                                paramMap[fieldName] = it
                            }
                        }
                    }
                }
            }
            return paramMap
        } else if (adapter::class.java.enclosingClass == CollectionTypeAdapterFactory::class.java) {
            // 数组类型
            val elementTypeAdapter = getField(adapter, "elementTypeAdapter")?.get(adapter)
            val paramList = ArrayList<Any>()
            if (elementTypeAdapter != null) {
                val delegate = getField(elementTypeAdapter, "delegate")?.get(elementTypeAdapter) as? TypeAdapter<*>
                if (delegate != null) {
                    addParameterizedType(delegate)?.let {
                        paramList.add(it)
                    }
                }
            }
            return paramList
        }
        return null
    }

    /**
     * 是否是基础类型
     */
    private fun isBaseType(mClass: Class<*>): Boolean {
        when {
            mClass.name == "java.lang.Integer" -> return true
            mClass.name == "java.lang.Byte" -> return true
            mClass.name == "java.lang.Long" -> return true
            mClass.name == "java.lang.Double" -> return true
            mClass.name == "java.lang.Float" -> return true
            mClass.name == "java.lang.Character" -> return true
            mClass.name == "java.lang.Short" -> return true
            mClass.name == "java.lang.Boolean" -> return true
            mClass.name == "java.lang.String" -> return true
            mClass == Integer::class.java -> return true
            mClass == Byte::class.java -> return true
            mClass == Long::class.java -> return true
            mClass == Double::class.java -> return true
            mClass == Float::class.java -> return true
            mClass == Character::class.java -> return true
            mClass == Short::class.java -> return true
            mClass == Boolean::class.java -> return true
            mClass == String::class.java -> return true
            mClass == Int::class.java -> return true
            else -> mClass.isPrimitive
        }
        return false
    }
}
