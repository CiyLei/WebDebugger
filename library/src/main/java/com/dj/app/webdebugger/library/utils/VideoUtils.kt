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
package com.dj.app.webdebugger.library.utils


import com.coremedia.iso.boxes.Container
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.util.ArrayList
import java.util.LinkedList

/**
 * Create by ChenLei on 2019/11/1
 * Describe:
 */

internal object VideoUtils {


    /**
     * 对Mp4文件集合进行追加合并(按照顺序一个一个拼接起来)
     *
     * @param mp4PathList [输入]Mp4文件路径的集合(支持m4a)(不支持wav)
     * @param outPutPath  [输出]结果文件全部名称包含后缀(比如.mp4)
     * @throws IOException 格式不支持等情况抛出异常
     */
    @Throws(IOException::class)
    fun appendMp4List(mp4PathList: List<String>, outPutPath: String) {
        val mp4MovieList = ArrayList<Movie>()// Movie对象集合[输入]
        for (mp4Path in mp4PathList) {// 将每个文件路径都构建成一个Movie对象
            mp4MovieList.add(MovieCreator.build(mp4Path))
        }
        val audioTracks = LinkedList<Track>()// 音频通道集合
        val videoTracks = LinkedList<Track>()// 视频通道集合
        for (mp4Movie in mp4MovieList) {// 对Movie对象集合进行循环
            for (inMovieTrack in mp4Movie.tracks) {
                if ("soun" == inMovieTrack.handler) {// 从Movie对象中取出音频通道
                    audioTracks.add(inMovieTrack)
                }
                if ("vide" == inMovieTrack.handler) {// 从Movie对象中取出视频通道
                    videoTracks.add(inMovieTrack)
                }
            }
        }
        val resultMovie = Movie()// 结果Movie对象[输出]
        if (!audioTracks.isEmpty()) {// 将所有音频通道追加合并
            resultMovie.addTrack(AppendTrack(*audioTracks.toTypedArray()))
        }
        if (!videoTracks.isEmpty()) {// 将所有视频通道追加合并
            resultMovie.addTrack(AppendTrack(*videoTracks.toTypedArray()))
        }
        val outContainer = DefaultMp4Builder().build(resultMovie)// 将结果Movie对象封装进容器
        val fileChannel = RandomAccessFile(String.format(outPutPath), "rw").channel
        outContainer.writeContainer(fileChannel)// 将容器内容写入磁盘
    }
}
