package com.twx.marryfriend.friend

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.wLog
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FriendInfoViewModel:ViewModel() {
    suspend fun loadUserInfo(guest_uid: Int)= suspendCoroutine<RecommendBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/viewSomebody"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val dataString=jsonObject.getJSONObject("data").toString()
                val info=Gson().fromJson(dataString, RecommendBean::class.java)
                coroutine.resume(info)
            }catch (e:Exception){
                eLog(e.stackTraceToString())
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun follow(guest_uid: Int)= suspendCoroutine<Unit>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/UserCenter/plusFocusOther"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                if (jsonObject.getInt("code")==200){
                    coroutine.resume(Unit)
                }else{
                    coroutine.resumeWithException(Exception(jsonObject.getString("msg")))
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun unFollow(guest_uid: Int)= suspendCoroutine<Unit>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/UserCenter/cancelFocusOther"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                if (jsonObject.getInt("code")==200){
                    coroutine.resume(Unit)
                }else{
                    coroutine.resumeWithException(Exception(jsonObject.getString("msg")))
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}