package com.twx.marryfriend.ilove

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.ilike.ILikeBean
import com.twx.marryfriend.bean.ilike.ILikeData
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.iLog
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LikeViewModel:ViewModel() {

    suspend fun loadLike(page:Int)= suspendCoroutine<ILikeData?> { coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/mePutongXihuanOtherList"
        val map= mapOf(
            "user_id" to UserInfo.getUserId()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                iLog(response)
                val ilikeBean=Gson().fromJson(response,ILikeBean::class.java)
                coroutine.resume(ilikeBean.data)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        }, mapOf("page" to page.toString(),"size" to "10"))
    }

    suspend fun loadSuperLike(page:Int)= suspendCoroutine<ILikeData?> { coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/meChaojiXihuanOtherList"
        val map= mapOf(
            "user_id" to UserInfo.getUserId()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                iLog(response)
                val ilikeBean=Gson().fromJson(response,ILikeBean::class.java)
                coroutine.resume(ilikeBean.data)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        }, mapOf("page" to page.toString(),"size" to "10"))
    }

    suspend fun loadDisLike(page:Int)= suspendCoroutine<ILikeData?> { coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/unconcernList"
        val map= mapOf(
            "user_id" to UserInfo.getUserId()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                iLog(response)
                val ilikeBean=Gson().fromJson(response,ILikeBean::class.java)
                coroutine.resume(ilikeBean.data)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        }, mapOf("page" to page.toString(),"size" to "10"))
    }
}