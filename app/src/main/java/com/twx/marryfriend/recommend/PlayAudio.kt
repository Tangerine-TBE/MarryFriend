package com.twx.marryfriend.recommend

import android.media.MediaPlayer
import android.net.Uri
import com.twx.marryfriend.base.BaseConstant

object PlayAudio {
    private val mediaPlayer by lazy { MediaPlayer() }
    private var currentPath:String?=null
    private var isPrepare=false
    private var currentOnCompletion:(()->Unit)?=null

    fun play(path: String,onPlay:()->Unit,onCompletion:()->Unit,onError:((String)->Unit)?=null){
        if (currentPath==path&&isPrepare){
            resume()
            onPlay.invoke()
            return
        }

        stop()
        mediaPlayer.setDataSource(path)

        mediaPlayer.prepareAsync()
        mediaPlayer.setOnErrorListener { mediaPlayer, i, i2 ->
            mediaPlayer?.reset()
            onError?.invoke("出错了")
            false
        }
        mediaPlayer.setOnPreparedListener { player ->
            currentOnCompletion=onCompletion
            isPrepare=true
            player.start()
            currentPath=path
            onPlay.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
        }
    }

    fun play(uri: Uri,onPlay:()->Unit,onCompletion:()->Unit,onError:((String)->Unit)?=null){
        val path=uri.toString()
        if (currentPath==path&&isPrepare){
            resume()
            onPlay.invoke()
            return
        }

        stop()
        mediaPlayer.setDataSource(BaseConstant.application,uri)

        mediaPlayer.prepareAsync()
        mediaPlayer.setOnErrorListener { mediaPlayer, i, i2 ->
            mediaPlayer?.reset()
            onError?.invoke("出错了")
            false
        }
        mediaPlayer.setOnPreparedListener { player ->
            currentOnCompletion=onCompletion
            isPrepare=true
            player.start()
            currentPath=path
            onPlay.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion.invoke()
        }
    }

    fun resume(){
        if (!isPlay()){
            mediaPlayer.start()
        }
    }

    fun pause(){
        if (isPlay()){
            mediaPlayer.pause()
        }
    }

    fun stop(){
        currentOnCompletion?.invoke()
        currentOnCompletion=null
        isPrepare=false
        currentPath=null
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    private fun release(){
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()
    }

    fun isPlay():Boolean{
        return mediaPlayer.isPlaying
    }
}