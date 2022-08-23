package com.twx.marryfriend.utils

import com.blankj.utilcode.util.TimeUtils
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/13
 */
object TimeUtil {

    /** 将服务器返回的时间转化为我的动态时间（与公共动态的时间区分开）  */
    fun getCommonTime(originalTime: String): String {
        var convertTime = ""
        if (TimeUtils.isToday(originalTime)) {

            val hours =
                if (TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY) < 10) {
                    "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)}"
                } else {
                    TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)
                }

            val min = if (TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE) < 10) {
                "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            } else {
                "${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            }

            convertTime = "${hours}: " + min

        } else {

            val day = TimeUtils.getValueByCalendarField(originalTime, Calendar.DAY_OF_YEAR)
            val nowDay =
                TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(), Calendar.DAY_OF_YEAR)

            val hours =
                if (TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY) < 10) {
                    "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)}"
                } else {
                    TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)
                }

            val min = if (TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE) < 10) {
                "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            } else {
                TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)
            }

            if (day - nowDay == -1) {
                // 是昨天
                convertTime = "昨天" +
                        "${hours}: " +
                        "$min"
            } else {
                convertTime =
                    "${TimeUtils.getValueByCalendarField(originalTime, Calendar.YEAR)}年" +
                            "${
                                TimeUtils.getValueByCalendarField(originalTime,
                                    Calendar.MONTH) + 1
                            }月" +
                            "${
                                TimeUtils.getValueByCalendarField(originalTime,
                                    Calendar.DAY_OF_MONTH)
                            }日" +
                            "${hours}: " +
                            "$min"
            }
        }
        return convertTime
    }


    /** 将服务器返回的时间转化为金币记录的时间 （ 只显示年月日 ）  */
    fun getCoinTime(originalTime: String): String {

        return "${
            TimeUtils.getValueByCalendarField(originalTime, Calendar.YEAR)
        }/${
            TimeUtils.getValueByCalendarField(originalTime, Calendar.MONTH) + 1
        }/${TimeUtils.getValueByCalendarField(originalTime, Calendar.DAY_OF_MONTH)}"
    }


    /** 谁看过我/我看过谁的时间（最近、三天前、两天前、昨天、今天具体时间）  */
    fun getViewTime(originalTime: String): String {
        var convertTime = ""

        if (TimeUtils.isToday(originalTime)) {
            val hours =
                if (TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY) < 10) {
                    "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)}"
                } else {
                    TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)
                }

            val min = if (TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE) < 10) {
                "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            } else {
                "${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            }

            convertTime = "${hours}: " + min

        } else {

            val day = TimeUtils.getValueByCalendarField(originalTime, Calendar.DAY_OF_YEAR)
            val nowDay = TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(), Calendar.DAY_OF_YEAR)

            convertTime = when(nowDay - day){
                1 -> {
                    "昨天"
                }
                2 -> {
                    "两天前"
                }
                3 -> {
                    "三天前"
                }
                else -> {
                    "最近"
                }
            }
        }
        return convertTime
    }

}