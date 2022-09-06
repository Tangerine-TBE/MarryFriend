package com.message

import com.google.gson.Gson

object ImUserInfoService {
    class ImUserInfo(val userId:String,val nickname:String?=null,val avatar:String?=null,val ext:Ext?=null){

    }
    data class Ext(val age:Int = 0,
                   val isRealName:Boolean = false,
                   val isVip:Boolean=false,
                   val isSuperVip:Boolean=false,
                   val city: String? = null,
                   val occupation: String? = null,
                   val education: String? = null,
                   val isMutualLike:Boolean = false,
                   val isFlower:Boolean = false)
    private val userInfoContainer by lazy {
        HashMap<String,ImUserInfo>()
    }

    fun setUserInfo(vararg userInfos:ImUserInfo){
        userInfos.forEach {
            userInfoContainer[it.userId] = it
        }
//        val avatarOptions = EaseAvatarOptions()
//        avatarOptions.avatarShape = 1
//        EaseIM.getInstance().setAvatarOptions(avatarOptions).userProvider = EaseUserProfileProvider { username ->
//            val user = EaseUser(username)
//            //设置用户昵称
//            user.nickname = getUserNickName(username)
//            //设置头像地址
//            user.avatar = getUserAvatar(username)
//            //最后返回构建的 EaseUser 对象
//            user
//        }
    }

    fun getUserNickName(id:String):String?{
        return userInfoContainer.get(id)?.nickname
    }

    fun getUserAvatar(id:String):String?{
        return userInfoContainer.get(id)?.avatar
    }

    private val gson by lazy {
        Gson()
    }
    fun getExt(id:String):String?{
        return userInfoContainer[id]?.ext?.let {
            gson.toJson(it)
        }
    }
}