package com.twx.marryfriend

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.core.content.FileProvider
import com.twx.marryfriend.dialog.UploadHeadDialog
import com.twx.marryfriend.recommend.widget.RecommendGuideView
import com.xyzz.myutils.show.wLog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun DisplayMetrics.dp2Px(dp:Float):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,this)
}

fun File.toUri(context: Context):Uri{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        val authority =
            context.packageName.toString() + ".fileProvider"
        return FileProvider.getUriForFile(context, authority, this)
    }else{
        return Uri.fromFile(this)
    }
}

/**
 * 根据生日获取年龄
 * yyyy-MM-dd
 */
fun String.getAgeFromBirthday():Int?{
//    val birthdayTime=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(this)?.time?:return null
//    val birthdayTimeCal=Calendar.getInstance().also {
//        it.timeInMillis=birthdayTime
//    }
    val birthdayYear:Int
    val birthdayMonth:Int
    val birthdayDay:Int
    this.split("-").also {
        birthdayYear=it[0].toIntOrNull()?:return null
        birthdayMonth=it[1].toIntOrNull()?:return null
        birthdayDay=it[2].toIntOrNull()?:return null
    }

    val currentTimeCal=Calendar.getInstance().also {
        it.timeInMillis=System.currentTimeMillis()
    }
    val currentYear=currentTimeCal.get(Calendar.YEAR)
    val currentMonth=currentTimeCal.get(Calendar.MONTH)+1
    val currentDay=currentTimeCal.get(Calendar.DAY_OF_MONTH)

    return (currentYear-birthdayYear).let {
        if (currentMonth>birthdayMonth){//当前时间已经过了生日月
            it
        }else if (currentMonth==birthdayMonth&&currentDay>=birthdayDay){//当前时间在生日月里
            it
        }else{
            it-1
        }
    }
}

fun UploadHeadDialog.showUploadHeadDialog():Boolean{
    if (RecommendGuideView.isShowGuide()){
        return false
    }
    return if (!UserInfo.isHaveHeadImage()){
        this.show()
        true
    }else{
        false
    }
}
