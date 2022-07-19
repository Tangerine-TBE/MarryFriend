package com.twx.marryfriend.ilove

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.ilike.ILikeBean
import com.twx.marryfriend.bean.ilike.ILikeData
import com.twx.marryfriend.bean.ilike.ILikeItemBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.iLog
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LikeViewModel:ViewModel() {
    private val superLikeChangeListener by lazy {
        ArrayList<(ILikeItemBean)->Unit>()
    }

    fun onSuperLikeChange(itemBean: ILikeItemBean){
        superLikeChangeListener.forEach {
            it.invoke(itemBean)
        }
    }

    fun addSuperLikeChangeListener(listener:(ILikeItemBean)->Unit){
        superLikeChangeListener.add(listener)
    }

    suspend fun superLike(guest_uid: Int)=suspendCoroutine<Unit>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/unconcernToLike"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject= JSONObject(response)
                if (jsonObject.getInt("code")==200) {
                    coroutine.resume(Unit)
                }else{
                    coroutine.resumeWithException(Exception(response))
                }
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

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