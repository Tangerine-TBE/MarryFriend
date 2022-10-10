package com.twx.marryfriend.message.model

class ConversationsItemModel(val conversationId: String) {
    var age:Int=0
    var userImage:String?=""
    var nickname:String?=null
    var isRealName=false
    var isVip=false
    var isSuperVip=false
    var location:String?=null
    var occupation:String?=null
    var education:String?=null
    var isMutualLike=false
    var isFlower=false

    var blacklist_permanent=0//系统是否永久拉黑	0否，1是
    var blacklist_close_time:String?=null//系统拉黑过期时间
    var blacklist_status=0
}