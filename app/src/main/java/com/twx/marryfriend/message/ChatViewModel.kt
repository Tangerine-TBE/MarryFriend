package com.twx.marryfriend.message

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyphenate.chat.EMMessageBody
import com.message.ImMessageManager
import com.message.chat.*
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.message.model.ChatItemModel
import com.twx.marryfriend.toUri
import com.xyzz.myutils.MyRecorderUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ChatViewModel:ViewModel() {
    companion object{
        private const val PAGE_SIZE=20
    }
    private var msgId:String?=null
    var page=1
    private val audioRecorder by lazy {
        MyRecorderUtil()
    }
    private val dir by lazy {
        File(BaseConstant.application.filesDir,"temp").also {
            it.mkdirs()
        }
    }
    private var currentFile:File?=null

    fun startRecorder(){
        val f=File(dir,System.currentTimeMillis().toString())
        currentFile=f
        audioRecorder.startRecording(f.absolutePath)
    }

    fun stopRecorder():Uri?{
        audioRecorder.stopRecording()
        val uri=currentFile?.toUri(BaseConstant.application)

        val result=if (getVoiceLength()<1){
            currentFile?.delete()
            null
        }else{
            uri
        }
        currentFile=null
        return result
    }

    fun getVoiceLength():Int{
        return ((audioRecorder.timeInterval+500)/1000).toInt()
    }


    suspend fun getHistoryMessage(username:String)= suspendCoroutine<List<ChatItemModel<Message<out EMMessageBody>>>>{continuation->
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ImMessageManager.getHistoryMessage(
                    username, PAGE_SIZE,
                    msgId
                ).also { list ->
                    msgId=list.lastOrNull()?.msgId
                    continuation.resume(list.map {
                        ChatItemModel(it)
                    })
                }
                page++
            }catch (e:Exception){
                continuation.resumeWithException(Exception("获取历史记录失败"))
            }

        }
    }
}