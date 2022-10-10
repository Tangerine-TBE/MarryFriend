package com.twx.marryfriend.bean

import com.xyzz.myutils.textTimeToTimeInMillis

class InterdictionBean {
    var isPermanentInterdiction=false
    var interdictionTime:String?=null

    fun isInterdiction():Boolean{
        if (isPermanentInterdiction){
            return true
        }else{
            val time=interdictionTime?.textTimeToTimeInMillis()?:return false
            return time>System.currentTimeMillis()
        }
    }
}