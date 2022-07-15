package com.xyzz.myutils.display

interface IDateDisplay {
    fun textDateInit(textDate:String,pattern:String):IDateDisplay

    fun setTime(time:Long)

    fun getTime():Long

    fun toText():String
}