package com.dj.app.webdebugger

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
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