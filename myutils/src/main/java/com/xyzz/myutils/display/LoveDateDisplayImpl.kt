package com.xyzz.myutils.display

import com.xyzz.myutils.parseTextDate
import com.xyzz.myutils.timeToDayAgo
import com.xyzz.myutils.timeToHourRemainder
import com.xyzz.myutils.timeToMinutesRemainder

class LoveDateDisplayImpl:IDateDisplay {
    private var dateTime=0L

    override fun textDateInit(textDate: String, pattern: String): IDateDisplay {
        dateTime=parseTextDate(textDate,pattern)?.time?: throw Exception("${textDate}转换成日期异常，pattern=${pattern}")
        return this
    }

    override fun setTime(time: Long) {
        dateTime=time
    }

    override fun getTime(): Long {
        return dateTime
    }

    override fun toText(): String {
        return dateTime.let {
            val day = it.timeToDayAgo()
            val hour = it.timeToHourRemainder()
            val minutes = it.timeToMinutesRemainder()
            if (day>0){
                "${day}天前"
            }else if (hour>0){
                "${hour}小时前"
            }else if (minutes>0){
                "${minutes}分钟前"
            }else{
                "刚刚"
            }
        }
    }
}