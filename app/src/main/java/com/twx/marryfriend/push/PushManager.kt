package com.twx.marryfriend.push

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
    //t	接收者 ID 。
    //f	发送者 ID 。
    //m	消息 ID 。
    //g	群组 ID ，当消息是群组消息时，这个值会被赋值。
    //e	用户自定义扩展。

    fun onNotificationMessageClicked(context: Context,extras:Bundle?){
        extras?:return
        iLog(extras.toString(),"推送，启动页传数据")
        val t = extras.getString("t")
        val f = extras.getString("f")
//            val m = extras.getString("m")
//            val g = extras.getString("g")
//            val e = extras["e"]
        //handle
    }

    fun onNotificationMessageClicked(context: Context, extStr:String?){
        extStr?:return
        iLog("来自小米的推送点击数据")
        try {
            val extras = JSONObject(extStr)
            val t = extras.getString("t")
            val f = extras.getString("f")
//            val m = extras.getString("m")
//            val g = extras.getString("g")
//            val e = extras["e"]

            Handler(Looper.getMainLooper()).post {
                context.startActivity(ImChatActivity.getIntent(context,f).also {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }catch (e:Exception){
            wLog(e.stackTraceToString())
        }
    }

    fun onNotificationMessageClicked(context: Context,message: UPSNotificationMessage?){
        val map: Map<String, String> = message?.params?:return
        iLog("来自vivo的推送点击数据")
        if (!map.isEmpty()) {
            val t = map["t"]
            val f = map["f"]
            val m = map["m"]
            val g = map["g"]
            val e: Any? = map["e"]
        }
    }
}