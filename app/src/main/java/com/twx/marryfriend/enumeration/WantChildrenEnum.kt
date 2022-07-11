package com.twx.marryfriend.enumeration

enum class WantChildrenEnum(val title:String,val code:Int) {
    unlimited("不限",0),option1("没有孩子",1),
    option2("有孩子且住一起",2),option3("有孩子偶尔会住在一起",3),
    option4("有孩子但不在身边",4);
}