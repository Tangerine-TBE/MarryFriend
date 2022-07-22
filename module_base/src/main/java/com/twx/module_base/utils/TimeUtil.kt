package com.twx.module_base.utils

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

            val min = if (TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE) < 10) {
                "0${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            } else {
                "${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            }

            convertTime = "${TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)}: " + min
        } else {
            val day = TimeUtils.getValueByCalendarField(originalTime, Calendar.DAY_OF_YEAR)
            val nowDay =
                TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(), Calendar.DAY_OF_YEAR)
            if (day - nowDay == -1) {
                // 是昨天
                convertTime = "昨天" +
                        "${
                            TimeUtils.getValueByCalendarField(originalTime, Calendar.HOUR_OF_DAY)
                        }: " +
                        "${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            } else {
                convertTime =
                    "${
                        TimeUtils.getValueByCalendarField(originalTime, Calendar.YEAR)
                    }年" +
                            "${
                                TimeUtils.getValueByCalendarField(originalTime, Calendar.MONTH)
                            }月" +
                            "${
                                TimeUtils.getValueByCalendarField(originalTime,
                                    Calendar.DAY_OF_MONTH)
                            }日" +
                            "${
                                TimeUtils.getValueByCalendarField(originalTime,
                                    Calendar.HOUR_OF_DAY)
                            }: " +
                            "${TimeUtils.getValueByCalendarField(originalTime, Calendar.MINUTE)}"
            }
        }
        return convertTime
    }

}