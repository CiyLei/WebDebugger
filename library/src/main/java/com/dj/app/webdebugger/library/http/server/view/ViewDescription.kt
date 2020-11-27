package com.dj.app.webdebugger.library.http.server.view

/**
 * Create by ChenLei on 2020/11/27
 * Describe: View描述
 */
internal data class ViewDescription(
    /**
     * View唯一标识
     */
    val id: String,
    /**
     * View描述
     */
    val label: String,
    /**
     * 子View
     */
    val children: List<ViewDescription> = emptyList()
)