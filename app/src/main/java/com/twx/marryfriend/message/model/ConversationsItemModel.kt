package com.twx.marryfriend.message.model

import com.twx.marryfriend.BuildConfig

class ConversationsItemModel(val conversationId: String) {
    var age:Int=0
    var userImage:String?=""
    var nickname:String?=null
    var isRealName=false
    var isVip=false
        get() {
            if (BuildConfig.DEBUG){
                return true
            }else{
                return field
            }
        }
    var isSuperVip=false
    var location:String?=null
    var occupation:String?=null
    var education:String?=null
    var isMutualLike=false
    var isFlower=false
}