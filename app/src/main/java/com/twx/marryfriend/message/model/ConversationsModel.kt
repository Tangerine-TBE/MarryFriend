package com.twx.marryfriend.message.model

import com.twx.marryfriend.bean.message.mutual.MutualLikeData

class ConversationsModel {
//    val laterLike= ObservableField<List<String>>()
    var laterLikeCount=0
    var list: List<MutualLikeData>? = null
        set(v) {
            val value=v?.filter { it.image_url!=null }
            if (value==null){

            }else{
                when{
                    value.size>=5->{
                        imageHead1= value[0].image_url
                        imageHead2= value[1].image_url
                        imageHead3= value[2].image_url
                        imageHead4= value[3].image_url
                        imageHead5= value[4].image_url
                    }
                    value.size>=4->{
                        imageHead1= value[0].image_url
                        imageHead2= value[1].image_url
                        imageHead3= value[2].image_url
                        imageHead4= value[3].image_url
                    }
                    value.size>=3->{
                        imageHead1= value[0].image_url
                        imageHead2= value[1].image_url
                        imageHead3= value[2].image_url
                    }
                    value.size>=2->{
                        imageHead1= value[0].image_url
                        imageHead2= value[1].image_url
                    }
                    value.size>=1->{
                        imageHead1= value[0].image_url
                    }
                    else->{

                    }
                }
            }

            field=value
        }

    var imageHead1:String?=null
    var imageHead2:String?=null
    var imageHead3:String?=null
    var imageHead4:String?=null
    var imageHead5:String?=null
}