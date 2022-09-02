package com.message

object ImUserInfoService {
    class ImUserInfo(val userId:String,val nickname:String?=null,val avatar:String?=null){
    }
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
}