package com.twx.marryfriend.recommend

data class RecommendCall(val code:Int=200,val msg:String?=null){
    companion object{
        const val RECOMMEND_NOT_HAVE=400//推荐人数用完
        const val COIN_NOT_ENOUGH=401//金币不够
        const val RECOMMEND_OTHER=-1//推荐的其他错误
    }
}
