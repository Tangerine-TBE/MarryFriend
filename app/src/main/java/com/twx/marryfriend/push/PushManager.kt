package com.twx.marryfriend.push

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.twx.marryfriend.message.ImChatActivity
import com.vivo.push.model.UPSNotificationMessage
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import org.json.JSONObject

object PushManager {
    class NotificationMessage(val to:String,val from:String)
    private var currentNotificationMessage:NotificationMessage?=null
    //t	接收者 ID 。
    //f	发送者 ID 。
    //m	消息 ID 。
    //g	群组 ID ，当消息是群组消息时，这个值会被赋值。
    //e	用户自定义扩展。
    fun onNotificationMessageClicked(context:Context,t:String,f:String,m:String?=null,g:String?=null,e:String?=null){
        currentNotificationMessage=NotificationMessage(t,f)
        if(context is Activity){
            return
        }else{
            Handler(Looper.getMainLooper()).post {
                context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName))
            }
        }
    }

    fun onNotificationMessageClicked(context:Context){
        if(context is Activity){
            return
        }else{
            Handler(Looper.getMainLooper()).post {
                context.startActivity(context.packageManager.getLaunchIntentForPackage(context.packageName))
            }
        }
    }

    fun getNotificationMsg():NotificationMessage?{
        return currentNotificationMessage
    }

    fun handlerNotificationMsg() {
        currentNotificationMessage=null
    }
}