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

import com.dj.app.webdebugger.library.utils.ClazzUtils.getField
import com.dj.app.webdebugger.library.utils.MockUtil
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.internal.ObjectConstructor
import com.google.gson.internal.bind.CollectionTypeAdapterFactory
import com.google.gson.internal.bind.MapTypeAdapterFactory
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
import com.google.gson.internal.bind.TypeAdapters
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Create by ChenLei on 2019/11/15
 * Describe: Retrofit api 模型
 */
internal data class ApiInfo(
    val methodCode: String,
    val url: String = "",
    val method: String = "",
    val description: String = "",
    val requestBody: HashMap<String, String> = HashMap(),
    var returnType: Type? = null
) {

    // 等待处理的 TypeAdapter
    private val toBeAnalyzedTypeAdapter = LinkedList<TypeAdapter<*>>()

    // 已经处理的 Type
    private val alreadyAnalyzedTypeAdapter = ArrayList<TypeAdapter<*>>()

    fun toMap(): Map<String, Any> {
        val typeAdapter = Gson().getAdapter(TypeToken.get(returnType))
        val map = HashMap<String, Any>()
        map["methodCode"] = methodCode
        map["url"] = url
        map["method"] = method
        map["description"] = description
        map["requestBody"] = requestBody
        map["returnType"] = analysisReturnType(typeAdapter)
        map["detailedReturnType"] = analysisDetailedType(typeAdapter)
        map["isMock"] = MockUtil.mockMap.containsKey(methodCode)
        map["mock"] = MockUtil.mockMap[methodCode] ?: ""
        return map
    }

    private fun analysisReturnType(adapter: TypeAdapter<*>): Any {
        alreadyAnalyzedTypeAdapter.clear()
        val type = handleReturnType(adapter)
        if (type is HashMap<*, *> || type is ArrayList<*>) {
            return type
        } else if (type is MapTypeAdapterCarrier) {
            return "Map<${type.keyType}, ${type.valueType}>"
        }
        return emptyMap<String, String>()
    }

    /**
     * 分析返回类型
     */
    private fun handleReturnType(adapter: TypeAdapter<*>): Any? {
        alreadyAnalyzedTypeAdapter.add(adapter)
        // enclosingClass 内部类获取所在的类
        when {
            adapter::class.java.enclosingClass == ReflectiveTypeAdapterFactory::class.java -> {
                // 普通的类
                val paramMap = HashMap<String, Any>()
                val fields = getField(adapter, "boundFields")?.get(adapter) as? Map<String, Any>
                for (field in fields?.entries ?: emptySet()) {
                    val fieldName = field.key
                    val typeAdapterFactory = field.value
                    // fieldType是匿名内部类引用外部变量的值，所以有val$的前缀
                    val fieldType = getField(
                        typeAdapterFactory,
                        "val\$fieldType"
                    )?.get(typeAdapterFactory) as? TypeToken<*>
                    if (fieldType != null) {
                        // 判断是否是基本类型
                        if (isBaseType(fieldType.rawType)) {
                            paramMap[fieldName] = fieldType.toString()
                        } else {
                            // 非基本类型
                            val typeAdapter = getField(
                                typeAdapterFactory,
                                "val\$typeAdapter"
                            )?.get(typeAdapterFactory) as? TypeAdapter<*>
                            // 如果还有字段的话
                            if (typeAdapter != null && typeAdapter::class.java.enclosingClass != TypeAdapters::class.java) {
                                // 如果此字段已经处理过的话，直接显示名称
                                if (alreadyAnalyzedTypeAdapter.contains(adapter)) {
                                    paramMap[fieldName] = fieldType.toString()
                                } else {
                                    // 继续分析字段
                                    val type = handleReturnType(typeAdapter)
                                    if (type != null) {
                                        if (type is MapTypeAdapterCarrier) {
                                            paramMap[fieldName] =
                                                "Map<${type.keyType}, ${type.valueType}>"
                                        } else {
                                            paramMap[fieldName] = type
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return paramMap
            }
            adapter::class.java.enclosingClass == CollectionTypeAdapterFactory::class.java -> {
                // 数组类型
                val elementTypeAdapter = getField(adapter, "elementTypeAdapter")?.get(adapter)
                val paramList = ArrayList<Any>()
                if (elementTypeAdapter != null) {
                    val delegate = getField(
                        elementTypeAdapter,
                        "delegate"
                    )?.get(elementTypeAdapter) as? TypeAdapter<*>
                    if (delegate != null) {
                        handleReturnType(delegate)?.let {
                            paramList.add(it)
                        }
                    }
                }
                return paramList
            }
            adapter::class.java.enclosingClass == MapTypeAdapterFactory::class.java -> {
                // Map类型
                val keyTypeAdapter = getField(adapter, "keyTypeAdapter")?.get(adapter)
                val keyType = getField(keyTypeAdapter, "type")?.get(keyTypeAdapter)
                val valueTypeAdapter = getField(adapter, "valueTypeAdapter")?.get(adapter)
                val valueType = getField(valueTypeAdapter, "type")?.get(valueTypeAdapter)
                return MapTypeAdapterCarrier(keyType.toString(), valueType.toString())
            }
            adapter::class.java.simpleName == "FutureTypeAdapter" -> {
                // 内部类
                val delegate = getField(adapter, "delegate")?.get(adapter) as TypeAdapter<*>
                return handleReturnType(delegate)
            }
            else -> return null
        }
    }

    /**
     * 分析详细类型
     */
    private fun analysisDetailedType(adapter: TypeAdapter<*>): List<DetailedTypeInfo> {
        beginAnalysisDetailedType(adapter)
        var p = toBeAnalyzedTypeAdapter.poll()
        val results = ArrayList<DetailedTypeInfo>()
        while (p != null) {
            handleDetailedType(p)?.let {
                results.add(it)
            }
            p = toBeAnalyzedTypeAdapter.poll()
        }
        // 过滤基本类型
        return results.filter { !it.fileName.startsWith("java.util.") }
    }

    private fun beginAnalysisDetailedType(adapter: TypeAdapter<*>) {
        toBeAnalyzedTypeAdapter.clear()
        alreadyAnalyzedTypeAdapter.clear()
        addAnalysisDetailedType(adapter)
    }

    private fun handleDetailedType(adapter: TypeAdapter<*>): DetailedTypeInfo? {
        if (alreadyAnalyzedTypeAdapter.contains(adapter)) {
            // 分析过了不在分析了
            return null
        }
        alreadyAnalyzedTypeAdapter.add(adapter)
        when {
            adapter::class.java.enclosingClass == ReflectiveTypeAdapterFactory::class.java -> {
                // 普通的类
                val fields = getField(adapter, "boundFields")?.get(adapter) as? Map<String, Any>
                val constructor = getField(adapter, "constructor")?.get(adapter)
                val type = getField(constructor, "val\$type")?.get(constructor)
                var detailedTypeInfo = DetailedTypeInfo((type ?: "不支持此类型").toString())
                // 不知道为什么我们的BaseResponse是这个类型的
                if (constructor is ObjectConstructor<*>) {
                    val c2 = getField(constructor, "val\$constructor")?.get(constructor)
                    val c2Name = getField(c2, "declaringClass")?.get(c2)?.toString()
                    if (c2Name?.isNotBlank() == true) {
                        detailedTypeInfo = DetailedTypeInfo(c2Name)
                    }
                }
                for (field in fields?.entries ?: emptySet()) {
                    val fieldName = field.key
                    val typeAdapterFactory = field.value
                    // fieldType是匿名内部类引用外部变量的值，所以有val$的前缀
                    val fieldType = getField(
                        typeAdapterFactory,
                        "val\$fieldType"
                    )?.get(typeAdapterFactory) as? TypeToken<*>
                    if (fieldType != null) {
                        // 记录类型
                        detailedTypeInfo.parameterMap[fieldName] = fieldType.toString()
                        // 如果是非基本类型，继续分析
                        if (!isBaseType(fieldType.rawType)) {
                            val typeAdapter = getField(
                                typeAdapterFactory,
                                "val\$typeAdapter"
                            )?.get(typeAdapterFactory) as? TypeAdapter<*>
                            if (typeAdapter != null) {
                                addAnalysisDetailedType(typeAdapter)
                            }
                        }
                    }
                }
                return detailedTypeInfo
            }
            adapter::class.java.enclosingClass == CollectionTypeAdapterFactory::class.java -> {
                // 取出泛型，继续分析
                val elementTypeAdapter = getField(adapter, "elementTypeAdapter")?.get(adapter)
                val delegate = getField(
                    elementTypeAdapter,
                    "delegate"
                )?.get(elementTypeAdapter) as TypeAdapter<*>
                addAnalysisDetailedType(delegate)
                val type = getField(elementTypeAdapter, "type")?.get(elementTypeAdapter)
                return DetailedTypeInfo("java.util.List<${type.toString()}>")
            }
            adapter::class.java.enclosingClass == MapTypeAdapterFactory::class.java -> {
                // 取出泛型，继续分析
                val keyTypeAdapter = getField(adapter, "keyTypeAdapter")?.get(adapter)
                val keyDelegate =
                    getField(keyTypeAdapter, "delegate")?.get(keyTypeAdapter) as TypeAdapter<*>
                val keyType = getField(keyTypeAdapter, "type")?.get(keyTypeAdapter)

                val valueTypeAdapter = getField(adapter, "valueTypeAdapter")?.get(adapter)
                val valueDelegate =
                    getField(valueTypeAdapter, "delegate")?.get(valueTypeAdapter) as TypeAdapter<*>
                val valueType = getField(valueTypeAdapter, "type")?.get(valueTypeAdapter)

                addAnalysisDetailedType(keyDelegate)
                addAnalysisDetailedType(valueDelegate)
                return DetailedTypeInfo("java.util.Map<$keyType, $valueType>")
            }
            adapter::class.java.simpleName == "FutureTypeAdapter" -> {
                // 内部类
                val delegate = getField(adapter, "delegate")?.get(adapter) as TypeAdapter<*>
                addAnalysisDetailedType(delegate)
            }
        }
        return null
    }

    /**
     * 添加分析
     */
    private fun addAnalysisDetailedType(type: TypeAdapter<*>) {
        toBeAnalyzedTypeAdapter.offer(type)
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

    class MapTypeAdapterCarrier(val keyType: String, val valueType: String)
    class DetailedTypeInfo(
        val fileName: String,
        val parameterMap: HashMap<String, String> = HashMap()
    )
}
