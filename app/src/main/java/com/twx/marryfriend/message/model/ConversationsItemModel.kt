package com.twx.marryfriend.message.model

import androidx.databinding.BaseObservable
import com.message.conversations.ConversationType
import com.twx.marryfriend.R
import com.xyzz.myutils.display.DateDisplayManager

class ConversationsItemModel constructor(val conversationId:String, val conversationsType: ConversationType) :BaseObservable(){
    private val messageDataDisplay by lazy {
        DateDisplayManager.getMessageDataImpl()
    }
    var lastTime=0L
        set(value) {
            field=value
            messageDataDisplay.setTime(value)
            lastTimeShow=messageDataDisplay.toText()
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
    var msgType= ConversationType.Chat

    var userImage:String?="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F201908%2F19%2F20190819150344_ALnaX.thumb.1000_0.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1661675750&t=5ef85936f85dc6d63c6ef2e12beb26a0"
    var nickname:String?=null
    var isRealName=false
    var isVip=false
    var location:String?=null
    var ageShow:String?=null
    var occupation:String?=null
    var education:String?=null
    var unReaderCount=0
    var lastMassage:String?=null
    var lastTimeShow:String?=null
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
}