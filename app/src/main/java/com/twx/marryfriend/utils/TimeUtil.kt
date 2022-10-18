package com.twx.marryfriend.utils

import android.util.Log
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.TimeUtils
import com.twx.marryfriend.dialog.OneClickHelloDialog
import java.text.SimpleDateFormat
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
                convertTime = "昨天" + "${hours}: " + "$min"
            } else {
                convertTime =
                    "${TimeUtils.getValueByCalendarField(originalTime, Calendar.YEAR)}年" + "${
                        TimeUtils.getValueByCalendarField(originalTime, Calendar.MONTH) + 1
                    }月" + "${
                        TimeUtils.getValueByCalendarField(originalTime, Calendar.DAY_OF_MONTH)
                    }日" + "${hours}: " + "$min"
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
            val nowDay =
                TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(), Calendar.DAY_OF_YEAR)

            convertTime = when (nowDay - day) {
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

    private var age = 0

    //出生年、月、日
    private var year = 0
    private var month = 0
    private var day = 0

    fun birthdayToAge(birthday: String): Int {
        stringToInt(birthday, "yyyy-MM-dd")
        // 得到当前时间的年、月、日
        val cal = Calendar.getInstance()
        val yearNow = cal[Calendar.YEAR]
        val monthNow = cal[Calendar.MONTH] + 1
        val dayNow = cal[Calendar.DATE]
        // 用当前年月日减去出生年月日
        val yearMinus = yearNow - year
        val monthMinus = monthNow - month
        val dayMinus = dayNow - day
        age = yearMinus // 先大致赋值
        if (yearMinus <= 0) {
            age = 0
            return age
        }
        if (monthMinus < 0) {
            age -= 1
        } else if (monthMinus == 0) {
            if (dayMinus < 0) {
                age -= 1
            }
        }
        return age
    }

    /**
     * String类型转换为long类型
     * .............................
     * strTime为要转换的String类型时间
     * formatType时间格式
     * formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * strTime的时间格式和formatType的时间格式必须相同
     */
    private fun stringToInt(strTime: String, formatType: String) {
        try {
            //String类型转换为date类型
            val calendar = Calendar.getInstance()
            val date: Date = TimeUtils.string2Date(strTime, formatType)
            calendar.time = date
            //date类型转成long类型
            year = calendar[Calendar.YEAR]
            month = calendar[Calendar.MONTH] + 1
            day = calendar[Calendar.DAY_OF_MONTH]
        } catch (e: Exception) {
            Log.d("guo", "Exception：$e")
        }
    }


    /**
     * 判断今天是否显示过我的资料弹窗
     */
    fun isShowMyData(): Boolean {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
        return !SPStaticUtils.getBoolean("MY_DATA_$date", false)
    }

    fun onShowMyData() {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
        SPStaticUtils.put("MY_DATA_$date", true)
    }

    /**
     * 判断今天是否显示过我的择偶条件弹窗
     */
    fun isShowMyTarget(): Boolean {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
        return !SPStaticUtils.getBoolean("MY_TARGET_$date", false)
    }

    fun onShowMyTarget() {
        val date =
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
        SPStaticUtils.put("MY_TARGET_$date", true)
    }

}