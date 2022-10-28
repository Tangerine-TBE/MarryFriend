package com.twx.marryfriend.recommend

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.ImUserInfoHelper
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.dynamic.TrendSaloonList
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.bean.one_hello.OneClickHelloBean
import com.twx.marryfriend.bean.one_hello.OneClickHelloItemBean
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

class RecommendViewModel():ViewModel() {
    private var preDisLike=0
    private var countDownTimer:CountDownTimer?=null
    val countDownTimerLiveData=MutableLiveData<String>()
    private val numberFormat by lazy {
        NumberFormat.getInstance().also {
            it.minimumIntegerDigits=2
        }
    }

    suspend fun loadRecommendUserId()=suspendCoroutine<List<Int>>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/commendList"
        val map= mutableMapOf<String,String>(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "user_sex" to UserInfo.getOriginalUserSex().toString())

        LocationUtils.getLocation()?.provinceCode.also {
            map["province_code"] = it?.toString()?:"0"
        }
        LocationUtils.getLocation()?.cityCode.also {
            map["city_code"] = it?.toString()?:"0"
        }
        /**
         * {"code":200,"msg":"success","data":{"5":5}}
         */
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val idList=ArrayList<Int>()
                val jsonObject=JSONObject(response)
                try {
                    val data=jsonObject.getJSONObject("data")
                    data.keys().forEach {
                        idList.add(data.getInt(it))
                    }
                }catch (e:Exception){
                    jsonObject.getJSONArray("data").also {
                        for (i in 0 until it.length()){
                            idList.add(it.getInt(i))
                        }
                    }
                }

                coroutine.resume(idList)
            }catch (e:Exception){
                if (e.message=="Value [] at data of type org.json.JSONArray cannot be converted to JSONObject"){
                    coroutine.resume(emptyList())
                }else {
                    coroutine.resumeWithException(Exception("转换失败:${response}"))
                }
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun loadRecommendUserInfo(idArray: List<Int>)=suspendCoroutine<List<RecommendBean>>{ coroutine->
        if (idArray.isEmpty()){
            coroutine.resume(emptyList())
            return@suspendCoroutine
        }
        ImUserInfoHelper.addFriendInfo(idArray.map { it.toString() })
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachFive"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "id_array" to JSONArray(idArray).toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val recommendData=ArrayList<RecommendBean>()
                val responseData=JSONObject(response)
                try {
                    //{"code":200,"msg":"success","data":{}}
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

    suspend fun loadLaseDynamic()=suspendCoroutine<TrendSaloonList?>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/newestTrends"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录")))
        )

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val data=jsonObject.getJSONArray("data")
                if(data.length()<=0){
                    coroutine.resume(null)
                }else{
                    coroutine.resume(Gson().fromJson(data.getJSONObject(0).toString(),TrendSaloonList::class.java))
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    suspend fun disLikeHome(guest_uid: Int)=suspendCoroutine<RecommendCall>{ coroutine->
        if (BuildConfig.DEBUG&&UserInfo.isTestDv()){
            viewModelScope.launch {
                iLog("不喜欢,未请求接口")
                delay(700)
                coroutine.resume(RecommendCall())
            }
            return@suspendCoroutine
        }
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachOneCommend"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString(),
            "feeling" to "hate")

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==432){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_NOT_HAVE,jsonObject.getString("msg")))
                }else if (code==200) {
                    preDisLike=guest_uid
                    coroutine.resume(RecommendCall())
                }else{
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,jsonObject.getString("msg")))
                }
            }catch (e:Exception){
                coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,"转换失败,${response}"))
            }
        },{
            coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,it))
        })
    }

    suspend fun likeHome(
        guest_uid: Int,
        mutualLikeAction: (() -> Unit)? = null)=suspendCoroutine<RecommendCall>{ coroutine->
        if (BuildConfig.DEBUG&&UserInfo.isTestDv()){
            viewModelScope.launch {
                iLog("喜欢,未请求接口")
                delay(700)
                coroutine.resume(RecommendCall())
            }
            return@suspendCoroutine
        }
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachOneCommend"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString(),
            "feeling" to "love")

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==432){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_NOT_HAVE,jsonObject.getString("msg")))
                }else if (jsonObject.getInt("code")==200) {
                    val mCode=jsonObject.getJSONArray("data").getInt(0)
                    if (mCode==2){
                        mutualLikeAction?.invoke()
                    }
                    iLog("返回的状态code:${mCode},2为相互喜欢")
                    coroutine.resume(RecommendCall())
                }else if(jsonObject.getInt("code")==444){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,jsonObject.getString("msg")))
                }else{
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,response))
                }
            }catch (e:Exception){
                coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,response))
            }
        },{
            coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,it))
        })
    }

    suspend fun superLikeHome(guest_uid: Int,coinInsufficient:(()->Unit)?=null)=suspendCoroutine<RecommendCall>{coroutine->
        if (BuildConfig.DEBUG&&UserInfo.isTestDv()){
            viewModelScope.launch {
                iLog("超级喜欢,未请求接口")
                delay(700)
                coroutine.resume(RecommendCall())
            }
            return@suspendCoroutine
        }
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/eachOneCommend"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString(),
            "feeling" to "send")

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==432){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_NOT_HAVE,jsonObject.getString("msg")))
                }else if (code==200) {
                    coroutine.resume(RecommendCall())
                }else{
                    val tip=try {
                        jsonObject.getString("msg")
                    }catch (e:Exception){
                        response
                    }
                    if (code==444){
                        coinInsufficient?.invoke()
                    }
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,tip))
                }
            }catch (e:Exception){
                coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,response))
            }
        },{
            coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,it))
        })
    }

    suspend fun otherDisLike(guest_uid: Int)=suspendCoroutine<RecommendCall>{ coroutine->
        if (BuildConfig.DEBUG&&UserInfo.isTestDv()){
            viewModelScope.launch {
                iLog("不喜欢,未请求接口")
                delay(700)
                coroutine.resume(RecommendCall())
            }
            return@suspendCoroutine
        }
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/plusUnconcern"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==432){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_NOT_HAVE,jsonObject.getString("msg")))
                }else if (code==200) {
                    preDisLike=guest_uid
                    coroutine.resume(RecommendCall())
                }else{
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,jsonObject.getString("msg")))
                }
            }catch (e:Exception){
                coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,"转换失败,${response}"))
            }
        },{
            coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,it))
        })
    }

    suspend fun otherLike(guest_uid: Int, mutualLikeAction: (() -> Unit)? = null)=suspendCoroutine<RecommendCall>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/plusPutongXihuanOther"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==432){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_NOT_HAVE,jsonObject.getString("msg")))
                }else if (jsonObject.getInt("code")==200) {
                    val mCode=jsonObject.getJSONArray("data").getInt(0)
                    if (mCode==2){
                        mutualLikeAction?.invoke()
                    }
                    iLog("返回的状态code:${mCode},2为相互喜欢")
                    coroutine.resume(RecommendCall())
                }else if(jsonObject.getInt("code")==444){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,jsonObject.getString("msg")))
                }else{
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,response))
                }
            }catch (e:Exception){
                coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,response))
            }
        },{
            coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,it))
        })
    }

    suspend fun otherSuperLike(guest_uid: Int, coinInsufficient:(()->Unit)?=null)=suspendCoroutine<RecommendCall>{ coroutine->
        if (BuildConfig.DEBUG&&UserInfo.isTestDv()){
            viewModelScope.launch {
                iLog("超级喜欢,未请求接口")
                delay(700)
                coroutine.resume(RecommendCall())
            }
            return@suspendCoroutine
        }
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/plusChaojiXihuanOther"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString())

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==432){
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_NOT_HAVE,jsonObject.getString("msg")))
                }else if (code==200) {
                    coroutine.resume(RecommendCall())
                }else{
                    val tip=try {
                        jsonObject.getString("msg")
                    }catch (e:Exception){
                        response
                    }
                    if (code==444){
                        coinInsufficient?.invoke()
                    }
                    coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,tip))
                }
            }catch (e:Exception){
                coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,response))
            }
        },{
            coroutine.resume(RecommendCall(RecommendCall.RECOMMEND_OTHER,it))
        })
    }

    suspend fun loadOneClickHelloUserInfo() =suspendCoroutine<OneClickHelloBean>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/CommendSearch/everydayLuckList"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "user_sex" to UserInfo.getOriginalUserSex().toString())

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
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "host_sex" to UserInfo.getOriginalUserSex().toString(),
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

    suspend fun likeWoUnread(isRe:Boolean=false)=suspendCoroutine<Int>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/getLikeFavorites"
        val map= mutableMapOf<String,String>(
            "user_id" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
        "is_up" to if (isRe) "1" else "0"
        )

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                coroutine.resume(jsonObject.getJSONObject("data").getInt("newLikeNum"))
            }catch (e:Exception){
                coroutine.resumeWithException(Exception("转换失败:${response}"))
            }
        },{
            coroutine.resumeWithException(Exception(it))
        })
    }

    fun startCountDownTimer(finished:()->Unit):Boolean{
        val currentTime=System.currentTimeMillis()
        val cal=Calendar.getInstance().also {
            it.set(Calendar.HOUR_OF_DAY,12)
            it.set(Calendar.MINUTE,0)
            it.set(Calendar.SECOND,0)
            it.set(Calendar.MILLISECOND,0)
        }
        val millisInFunction=cal.timeInMillis-currentTime
        return if (millisInFunction<0){
            false
        }else{
            countDownTimer?.cancel()
            countDownTimer=object :CountDownTimer(millisInFunction,1000L){
                override fun onTick(millisUntilFinished: Long) {
                    val h=millisUntilFinished/1000L/60L/60L
                    val m=millisUntilFinished/1000L/60L%60L
                    val s=millisUntilFinished/1000L%60L
                    countDownTimerLiveData.value="${numberFormat.format(h)}:${numberFormat.format(m)}:${numberFormat.format(s)}"
                }

                override fun onFinish() {
                    finished.invoke()
                }
            }
            countDownTimer?.start()
            true
        }
    }
}