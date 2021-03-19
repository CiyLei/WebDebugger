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
package com.dj.app.webdebugger.library.http.server.view.attributes

import com.dj.app.webdebugger.library.ViewAttributes

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 属性模型抽象
 */
internal data class AttributesBean(
    /**
     * 属性名称
     */
    val attributes: String,
    /**
     * 属性的值
     */
    val value: String = "",
    /**
     * 属性的说明
     */
    val description: String = "",
    /**
     * 属性是否可以编辑
     */
    val isEdit: Boolean = false,
    /**
     * 属性的编辑输入类型
     */
    val inputType: Int = ViewAttributes.INPUT_TYPE_INPUT,
    /**
     * 属性的编辑可选择项
     */
    val selectOptions: List<String> = emptyList(),
    /**
     * 属性类型
     */
    val type: Int = TYPE_NONE
) {
    companion object {
        // 代表是个普通的属性
        const val TYPE_NONE = 0

        // 代表是个标签
        const val TYPE_LABEL = 1
    }
}