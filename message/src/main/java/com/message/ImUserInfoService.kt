package com.message

import com.google.gson.Gson
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import com.xyzz.myutils.textTimeToTimeInMillis

object ImUserInfoService {
    class ImUserInfo(val userId:String,val nickname:String?=null,val avatar:String?=null,val ext:Ext?=null){

    }
    data class Ext constructor(val age:Int = 0,
                   val isRealName:Boolean = false,
                   val isVip:Boolean=false,
                   val isSuperVip:Boolean=false,
                   val city: String? = null,
                   val occupation: String? = null,
                   val education: String? = null,
                   val isMutualLike:Boolean = false,
                   val isFlower:Boolean = false){
        var blacklist_permanent=0//系统是否永久拉黑	0否，1是
        var blacklist_close_time:String?=null//系统拉黑过期时间
        var blacklist_status=0

        /**
         * 该账号是否被系统封
         */
        fun isSystemBlacklist():Boolean{
            return blacklist_status==1
        }
    }
    private val userInfoContainer by lazy {
        HashMap<String,ImUserInfo>()
    }

    fun setUserInfo(vararg userInfos:ImUserInfo){
        userInfos.forEach {
            userInfoContainer[it.userId] = it

            // 设置所有用户属性。
            val userInfo = EMUserInfo()
            userInfo.userId = it.userId
            userInfo.nickname = it.nickname
            userInfo.avatarUrl = it.avatar
//            userInfo.birth = "2000.10.10"
//            userInfo.signature = "hello world"
//            userInfo.phoneNumber = "13333333333"
//            userInfo.email = "123456@qq.com"
//            userInfo.gender = 1
            userInfo.ext=getExtStr(it.userId)
            EMClient.getInstance().userInfoManager()
                .updateOwnInfo(userInfo, object : EMValueCallBack<String?> {
                    override fun onError(error: Int, errorMsg: String) {

                    }
                    override fun onSuccess(value: String?) {

                    }
                })
        }
    }

    fun getUserNickName(id:String):String?{
        return userInfoContainer.get(id)?.nickname
    }

    fun getUserAvatar(id:String):String?{
        return userInfoContainer.get(id)?.avatar
    }

    fun getUser(id:String):ImUserInfo?{
        return userInfoContainer.get(id)
    }

    private val gson by lazy {
        Gson()
    }
    fun getExtStr(id:String):String?{
        return userInfoContainer[id]?.ext?.let {
            gson.toJson(it)
        }
    }

    fun getExt(id:String):Ext?{
        return userInfoContainer[id]?.ext
    }
}