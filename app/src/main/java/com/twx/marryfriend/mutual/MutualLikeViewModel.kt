package com.twx.marryfriend.mutual

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.message.mutual.MutualLikeBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.iLog
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MutualLikeViewModel:ViewModel() {
    private val gson by lazy { Gson() }

    suspend fun getMutualLike()= suspendCoroutine<MutualLikeBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/likeEachOtherList"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject= JSONObject(response)
                coroutine.resume(gson.fromJson(jsonObject.getJSONObject("data").toString(), MutualLikeBean::class.java))
                iLog(response)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        }, mapOf(
            "page" to "1",
            "size" to "20"
        ))
    }
}