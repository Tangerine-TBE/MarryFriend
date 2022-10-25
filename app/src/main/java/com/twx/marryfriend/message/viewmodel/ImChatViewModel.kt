package com.twx.marryfriend.message.viewmodel

import android.util.Base64
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.OurRelationship
import com.twx.marryfriend.constant.Contents
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.dLog
import com.xyzz.myutils.show.iLog
import org.json.JSONArray
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

    suspend fun getOurRelationship(guest_uid:String)=suspendCoroutine<OurRelationship>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/ourRelationship"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==200){
                    coroutine.resume(Gson().fromJson(jsonObject.getJSONObject("data").toString(),
                        OurRelationship::class.java))
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

    /**
     * 上传聊天记录，未实现
     */
    suspend fun putMessage(guest_uid:String)=suspendCoroutine<OurRelationship>{ coroutine->
        val url="${Contents.USER_URL}/marryfriend/TrendsNotice/ourRelationship"
        val map= mapOf(
            "host_uid" to (UserInfo.getUserId()?:return@suspendCoroutine coroutine.resumeWithException(Exception("未登录"))),
            "guest_uid" to guest_uid.toString()
        )
        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                val jsonObject=JSONObject(response)
                val code=jsonObject.getInt("code")
                if (code==200){
                    coroutine.resume(Gson().fromJson(jsonObject.getJSONObject("data").toString(),
                        OurRelationship::class.java))
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

    suspend fun getSensitiveWords()= suspendCoroutine<List<String>?> { continuation ->
        val url="${Contents.USER_URL}/marryfriend/GetParameter/minganWenzi"
        val cache=SPUtil.instance.getString(url,null)
        if (cache!=null){
            dLog("敏感词缓存")
            continuation.resume(jsonToList(cache))
            return@suspendCoroutine
        }
        NetworkUtil.sendPost(url, emptyMap(),{
            try {
                continuation.resume(jsonToList(it))
                SPUtil.instance.putString(url,it)
            }catch (e:Exception){
                continuation.resumeWithException(IllegalArgumentException("转换失败"))
            }
        },{msg ->
            continuation.resumeWithException(IllegalArgumentException(msg))
        })
    }

    private fun jsonToList(response:String):List<String>{
        val str=try {
            val jsonObject=JSONObject(response)
            jsonObject.getJSONObject("data").getString("array_string")
        }catch (e:Exception){
            null
        }
        val result=ArrayList<String>()
        val r=JSONArray(String(Base64.decode(str, 0)))
        for (i in 0 until r.length()){
            result.add(r.getString(i))
        }
        return result
    }
}