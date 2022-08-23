package com.xyzz.myutils

import android.media.MediaRecorder
import android.util.Log
import com.xyzz.myutils.MyRecorderUtil
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.lang.Exception
import kotlin.Throws

/**
 * 录音工具
 */
class MyRecorderUtil {
    private var mRecorder: MediaRecorder? = null
    private var startTime: Long = 0

    /**
     * 获取录音时长,单位毫秒
     */
    var timeInterval: Long = 0
        private set
    private var isRecording = false

    /**
     * 获取录音文件地址
     */
    var filePath: String? = null
        private set

    /**
     * 开始录音
     */
    fun startRecording(filePath: String) {
        this.filePath = filePath
        if (isRecording) {
            mRecorder!!.release()
            mRecorder = null
        }
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder!!.setOutputFile(this.filePath)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        startTime = System.currentTimeMillis()
        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
            isRecording = true
        } catch (e: Exception) {
            Log.e(TAG, "prepare() failed")
        }
    }

    /**
     * 停止录音
     */
    fun stopRecording(): String? {
        timeInterval = System.currentTimeMillis() - startTime
        try {
            if (timeInterval < 1000) {
                mRecorder!!.stop()
            }
            mRecorder!!.release()
            mRecorder = null
            isRecording = false
            val t = filePath
            filePath = null
            return t
        } catch (e: Exception) {
            Log.e(TAG, "release() failed")
        }
        return null
    }

    /**
     * 取消语音
     */
    @Synchronized
    fun cancelRecording() {
        if (mRecorder != null) {
            try {
                mRecorder!!.release()
                mRecorder = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val file = File(filePath)
            file.deleteOnExit()
        }
        isRecording = false
    }

    /**
     * 获取录音文件
     */
    val date: ByteArray?
        get() = if (filePath == null) null else try {
            readFile(File(filePath))
        } catch (e: IOException) {
            Log.e(TAG, "read file error$e")
            null
        }

    companion object {
        private const val TAG = "RecorderUtil"

        /**
         * 将文件转化为byte[]
         *
         * @param file 输入文件
         */
        @Throws(IOException::class)
        private fun readFile(file: File): ByteArray {
// Open file
            val f = RandomAccessFile(file, "r")
            return try {
// Get and check length
                val longlength = f.length()
                val length = longlength.toInt()
                if (length.toLong() != longlength) throw IOException("File size  = 2 GB")
                // Read file and return data
                val data = ByteArray(length)
                f.readFully(data)
                data
            } finally {
                f.close()
            }
        }
    }
}