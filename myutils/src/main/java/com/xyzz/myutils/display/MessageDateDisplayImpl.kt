package com.xyzz.myutils.display

import com.xyzz.myutils.parseTextDate
import com.xyzz.myutils.timeToDayAgo
import com.xyzz.myutils.timeToHourRemainder
import com.xyzz.myutils.timeToMinutesRemainder
import java.text.NumberFormat
import java.util.*

/**
 * 婚恋消息页面时间展示
 */
class MessageDateDisplayImpl:IDateDisplay {
    private var dateTime=0L
    private val calendar by lazy {
        Calendar.getInstance()
    }

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
        calendar.timeInMillis=System.currentTimeMillis()
        val currentYear=calendar.get(Calendar.YEAR)
        val currentDay=calendar.get(Calendar.DAY_OF_MONTH)

        calendar.timeInMillis=dateTime
        val timeYear=calendar.get(Calendar.YEAR)
        val timeMonth=calendar.get(Calendar.MONTH)
        val timeDay=calendar.get(Calendar.DAY_OF_MONTH)
        val timeHour=calendar.get(Calendar.HOUR_OF_DAY)
        val timeMinute=calendar.get(Calendar.MINUTE)

        val numberFormat=NumberFormat.getNumberInstance().also {
            it.minimumIntegerDigits=2
        }
        return if (currentYear!=timeYear){
            "${timeYear}/${numberFormat.format(timeMonth)}/${numberFormat.format(timeDay)}"
        }else if (currentDay!=timeDay){
            "${numberFormat.format(timeMonth)}/${numberFormat.format(timeDay)}"
        }else{
            "${numberFormat.format(timeHour)}:${numberFormat.format(timeMinute)}"
        }
    }
}