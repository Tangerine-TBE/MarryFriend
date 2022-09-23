package com.twx.marryfriend.message

import androidx.lifecycle.ViewModel
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.iLog
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ImChatViewModel:ViewModel() {

    suspend fun addBlockList(guest_uid:String)=suspendCoroutine<Unit>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/plusBlockSession"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==200){
                    coroutine.resume(Unit)
                }else{
                    coroutine.resumeWithException(Exception(jsonObject.getString("msg")))
                }
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun removeBlockList(guest_uid:String)=suspendCoroutine<Unit>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/deleteBlockSession"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==200){
                    coroutine.resume(Unit)
                }else{
                    coroutine.resumeWithException(Exception(jsonObject.getString("msg")))
                }
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}