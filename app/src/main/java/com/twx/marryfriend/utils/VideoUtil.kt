package com.twx.marryfriend.utils

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.util.Log

/**
 * @author: Administrator
 * @date: 2022/9/6
 */
object VideoUtil {

    fun getLocalVideoDuration(filePath: String?): Int {
        var duration = 0
        duration = try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(filePath)
            mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                .toInt() / 1000 //除以 1000 返回是秒
            //时长(毫秒)
            //            String duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            //            //宽
            //            String width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            //            //高
            //            String height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        } catch (e: Exception) {
            e.printStackTrace()
            return duration
        }
        return duration
    }


    fun getLocalVideoWidth(filePath: String?): Int {
        var width = 0
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(filePath)
            width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()!!
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return width
        }
        return width
    }


    fun getLocalVideoHeight(filePath: String?): Int {
        var height = 0
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(filePath)
            height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()!!
            //            //高
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return height
        }
        return height
    }

    private fun getDuration(filePath: String): String? {
        val player = MediaPlayer()
        try {
            player.setDataSource(filePath) //filePath为文件的路径
            player.prepare()
        } catch (e: java.lang.Exception) {
            Log.d("guo", "getDuration: $e")
        }
        val duration: Double = player.duration.toDouble()//获取媒体文件时长
        Log.d("guo", "getDuration: $duration")
        player.release() //记得释放资源
        return (duration / 1000).toString()
    }


}