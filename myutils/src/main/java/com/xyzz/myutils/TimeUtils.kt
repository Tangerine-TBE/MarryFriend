package com.xyzz.myutils

import com.xyzz.myutils.show.wLog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun parseTextDate(textDate:String,pattern:String):Date?{
    if (textDate.isBlank()){
        return null
    }
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

private val simpleDateFormat by lazy {
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
}
/**
 * 将 yyyy-MM-dd HH:mm:ss
 * 转换为 long类型
 */
fun String.textTimeToTimeInMillis():Long?{
    return try {
        simpleDateFormat.parse(this)?.time
    }catch (e:Exception){
        wLog(e.stackTraceToString())
        null
    }
}