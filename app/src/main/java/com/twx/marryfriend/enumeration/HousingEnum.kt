package com.twx.marryfriend.enumeration

enum class HousingEnum(val title:String,val code:Int) {
    withFamily("和家人同住",1),purchasedHouse("已购房",2),
    rentHouse("租房",3),marryBuyHouse("打算婚后购房",4),
    dormitory("住在单位宿舍",5);
}