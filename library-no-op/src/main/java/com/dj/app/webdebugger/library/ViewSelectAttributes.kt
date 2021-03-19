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
package com.dj.app.webdebugger.library

import android.view.View

/**
 * Create by ChenLei on 2020/11/30
 * Describe: 修改值为选项的属性抽象
 */
abstract class ViewSelectAttributes<T : View> : ViewAttributes<T>() {

    override fun isEdit(view: T): Boolean = true

    override fun inputType(view: T): Int = INPUT_TYPE_SELECT

    abstract override fun selectOptions(view: T): List<String>

    abstract override fun setValue(view: T, value: String)
}