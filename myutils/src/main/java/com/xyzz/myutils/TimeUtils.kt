package com.xyzz.myutils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun parseTextDate(textDate:String,pattern:String):Date?{
    val simpleDateFormat=SimpleDateFormat(pattern, Locale.CHINA)
    return simpleDateFormat.parse(textDate)
}

/**
 * 多少天前
 */
fun Long.timeToDayAgo():Long{
    return (System.currentTimeMillis()-this)/TimeUnit.DAYS.toMillis(1)
}

/**
 * 求余
 * @return 多少小时前
 */
fun Long.timeToHourRemainder():Int{
    return ((System.currentTimeMillis()-this)%TimeUnit.DAYS.toMillis(1)/TimeUnit.HOURS.toMillis(1)).toInt()
}

/**
 * 求余
 * @return 多少分钟前
 */
fun Long.timeToMinutesRemainder():Int{
    return ((System.currentTimeMillis()-this)%TimeUnit.DAYS.toMillis(1)%TimeUnit.HOURS.toMillis(1)/TimeUnit.MINUTES.toMillis(1)).toInt()
}