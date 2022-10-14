package com.twx.marryfriend.likeme

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.likeme.LikeMeBean
import com.twx.marryfriend.bean.likeme.LikeMeData
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.iLog
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LiveViewModel():ViewModel() {
    private var page=1

    suspend fun refresh():LikeMeData?{
        page=1
        return loadLoveMe()
    }

    suspend fun getNextPage():LikeMeData?{
        page++
        return loadLoveMe()
    }

    suspend fun loadLoveMe()=suspendCoroutine<LikeMeData?>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/otherPutongXihuanMeList"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "page" to page.toString(),
            "size" to "10"
        )

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                iLog(response)
                val likeMeBean=Gson().fromJson(response,LikeMeBean::class.java)
                coroutine.resume(likeMeBean.data)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}