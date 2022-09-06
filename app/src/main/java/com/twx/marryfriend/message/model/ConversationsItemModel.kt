package com.twx.marryfriend.message.model

import com.message.conversations.ConversationType
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.R
import com.xyzz.myutils.display.DateDisplayManager

class ConversationsItemModel constructor(val conversationId:String, val conversationsType: ConversationType) {
    private val messageDataDisplay by lazy {
        DateDisplayManager.getMessageDataImpl()
    }
    var age:Int=0
        set(value) {
            field=value
            if (age<=0){
                ageShow=null
            }else {
                ageShow = age.toString() + "岁"
            }
        }

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

    var mutualAndFlower= R.mipmap.ic_message_mutual_like
        get() {
            return if (isMutualLike){
                R.mipmap.ic_message_mutual_like
            }else if (isFlower){
                R.mipmap.ic_message_flower
            }else{
                field
            }
        }


    var ageShow:String?=null
    var msgType= ConversationType.Chat
    var lastTime=0L
        set(value) {
            field=value
            messageDataDisplay.setTime(value)
            lastTimeShow=messageDataDisplay.toText()
        }
    var lastTimeShow:String?=null
    var lastMassage:String?=null
    var unReaderCount=0
}