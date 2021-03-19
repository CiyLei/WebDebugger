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
package com.dj.app.webdebugger

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Create by ChenLei on 2020/11/26
 * Describe:
 */
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        btnDialogTest.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("测试标题")
                setMessage("测试内容")
                setNegativeButton("确认") { _, _ ->

                }
            }.create().show()
        }
        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
                return object : RecyclerView.ViewHolder(
                    LayoutInflater.from(this@TestActivity)
                        .inflate(R.layout.item_test, p0, false)
                ) {}
            }

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
                p0.itemView.findViewById<TextView>(R.id.tv).text = p1.toString()
            }

            override fun getItemCount(): Int = 30

        }
    }
}