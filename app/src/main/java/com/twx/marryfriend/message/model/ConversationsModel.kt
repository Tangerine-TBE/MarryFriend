package com.twx.marryfriend.message.model

import androidx.databinding.BaseObservable
import com.twx.marryfriend.bean.message.mutual.MutualLikeData

class ConversationsModel:BaseObservable() {
//    val laterLike= ObservableField<List<String>>()
    var laterLikeCount=0
    set(value) {
        field=value
        notifyChange()
    }
    var list: List<MutualLikeData>? = null
        set(value) {
            imageHead1=value?.get(0)?.image_url
            imageHead1=value?.get(1)?.image_url
            imageHead1=value?.get(2)?.image_url
            imageHead1=value?.get(3)?.image_url
            imageHead1=value?.get(4)?.image_url
            field=value
            notifyChange()
        }

    var imageHead1:String?=null
    var imageHead2:String?=null
    var imageHead3:String?=null
    var imageHead4:String?=null
    var imageHead5:String?=null
}