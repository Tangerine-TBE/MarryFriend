package com.twx.marryfriend.enumeration

enum class EduEnum(val title:String,val code:Int) {
    unlimited("不限",0),specialtyDown("大专以下",1),
    specialty("大专",2),undergraduate("本科",3),
    master("硕士",4),doctor("博士",5),
    doctorUp("博士以上",6);
}