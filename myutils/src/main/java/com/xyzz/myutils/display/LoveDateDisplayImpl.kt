package com.xyzz.myutils.display

import com.xyzz.myutils.parseTextDate
import com.xyzz.myutils.timeToDayAgo
import com.xyzz.myutils.timeToHourRemainder
import com.xyzz.myutils.timeToMinutesRemainder

/**
 * 婚恋喜欢我的页面时间展示
 */
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
            if (day>0){
                return "${day}天前"
            }
            val hour = it.timeToHourRemainder()
            if (hour>0){
                return "${hour}小时前"
            }
            val minutes = it.timeToMinutesRemainder()
            if (minutes>0){
                "${minutes}分钟前"
            }else{
                "刚刚"
            }
        }
    }
}