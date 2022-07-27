package com.twx.marryfriend.recommend

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.bean.one_hello.OneClickHelloBean
import com.twx.marryfriend.bean.one_hello.OneClickHelloItemBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
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
                val data=JSONObject(response).getJSONArray("data")
                if (data.toString().trim()=="[[]]"){
                    coroutine.resume(emptyList())
                }else{
                    val jsonObject= data.getJSONObject(0)
                    val idList=ArrayList<Int>()
                    jsonObject.keys().forEach {
                        idList.add(it.toInt())
                    }
                    coroutine.resume(idList)
                }
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun loadRecommendUserInfo(idArray: List<Int>)=suspendCoroutine<List<RecommendBean>>{ coroutine->
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
                eLog(e.stackTraceToString())
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
            "feeling" to "hate")

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

    suspend fun like(guest_uid: Int,mutualLikeAction:(()->Unit)?=null)=suspendCoroutine<String>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachOneCommend"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString(),
            "feeling" to "love")

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                if (jsonObject.getInt("code")==200) {
                    val code=jsonObject.getJSONArray("data").getInt(0)
                    if (code==2){
                        mutualLikeAction?.invoke()
                    }
                    iLog("返回的状态code:${code},2为相互喜欢")
                    coroutine.resume("喜欢成功")
                }else if(jsonObject.getString("code")=="444"){
                    coroutine.resume(jsonObject.getString("msg"))
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

    suspend fun otherLike(guest_uid: Int,mutualLikeAction:(()->Unit)?=null)=suspendCoroutine<String>{coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/plusPutongXihuanOther"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                if (jsonObject.getInt("code")==200) {
                    val code=jsonObject.getJSONArray("data").getInt(0)
                    if (code==2){
                        mutualLikeAction?.invoke()
                    }
                    iLog("返回的状态code:${code},2为相互喜欢")
                    coroutine.resume("喜欢成功")
                }else if(jsonObject.getString("code")=="444"){
                    coroutine.resume(jsonObject.getString("msg"))
                }else if(jsonObject.getInt("code")==484){
                    coroutine.resume(jsonObject.getString("msg"))
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

    suspend fun loadOneClickHelloUserInfo() =suspendCoroutine<OneClickHelloBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/everydayLuckList"
        val map= mapOf(
            "user_id" to UserInfo.getUserId(),
            "user_sex" to UserInfo.getUserSex().toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                iLog(response)
                coroutine.resume(Gson().fromJson(response,OneClickHelloBean::class.java))
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun sendHello(list: List<OneClickHelloItemBean>) =suspendCoroutine<Unit>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/oneClickHello"
        val map= mapOf(
            "host_uid" to UserInfo.getUserId(),
            "host_sex" to UserInfo.getUserSex().toString(),
            "uid_array" to list.map { it.user_id }.let { Gson().toJson(it) })

        NetworkUtil.sendPostSecret(url,map,{ response ->
            val jsonObject=JSONObject(response)
            try {
                iLog(response)
                if (jsonObject.getInt("code")==200){
                    coroutine.resume(Unit)
                }else{
                    coroutine.resumeWithException(Exception("${response}"))
                }
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }
}