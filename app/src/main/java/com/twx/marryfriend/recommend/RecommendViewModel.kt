package com.twx.marryfriend.recommend

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.RecommendBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class RecommendViewModel():ViewModel() {
    private var preDisLike=0

    suspend fun loadRecommendUserId()=suspendCoroutine<List<Int>>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/commendList"
        val map= mapOf(
            "user_id" to UserInfo.getUserId(),
            "user_sex" to UserInfo.getUserSex().toString())
        /**
         * {"code":200,"msg":"success","data":[{"5":5}]}
         */
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject= JSONObject(response).getJSONArray("data").getJSONObject(0)
                val idList=ArrayList<Int>()
                jsonObject.keys().forEach {
                    idList.add(it.toInt())
                }
                coroutine.resume(idList)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun loadRecommendUserInfo(idArray: List<Int>)=suspendCoroutine<List<RecommendBean>>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachFive"
        val map= mapOf(
            "user_id" to UserInfo.getUserId(),
            "id_array" to JSONArray(idArray).toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val recommendData=ArrayList<RecommendBean>()
                val responseData=JSONObject(response)
                try {
                    //{"code":200,"msg":"success","data":[[]]}
                    if (responseData.getJSONArray("data").getJSONArray(0).length()==0){
                        coroutine.resume(recommendData)
                        return@sendPostSecret
                    }
                }catch (e:Exception){

                }

                val jsonObject=responseData.getJSONObject("data")
                val gson=Gson()
                jsonObject.keys().forEach {
                    gson.fromJson(jsonObject.getJSONObject(it).toString(),RecommendBean::class.java).apply {
                        recommendData.add(this)
                    }
                }
                coroutine.resume(recommendData)
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun disLike(guest_uid: Int)=suspendCoroutine<Unit>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachOneCommend"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString(),
            "previous_uid" to preDisLike.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                if (jsonObject.getInt("code")==200) {
                    preDisLike=guest_uid
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

    suspend fun like(guest_uid: Int)=suspendCoroutine<Boolean>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/plusPutongXihuanOther"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                if (jsonObject.getInt("code")==200) {
                    coroutine.resume(jsonObject.getJSONArray("data").getInt(0)==2)
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

    suspend fun superLike(guest_uid: Int)=suspendCoroutine<Unit>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/plusChaojiXihuanOther"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
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
}