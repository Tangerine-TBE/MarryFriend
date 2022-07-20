package com.twx.marryfriend.friend

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.RecommendBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FriendInfoViewModel:ViewModel() {
    suspend fun loadUserInfo(guest_uid: Int)= suspendCoroutine<RecommendBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/viewSomebody"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val dataString=jsonObject.getJSONObject("data").toString()
                val info=Gson().fromJson(dataString,RecommendBean::class.java)
                coroutine.resume(info)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}