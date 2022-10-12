package com.twx.marryfriend.bean

import com.google.gson.Gson
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.textTimeToTimeInMillis

class InterdictionBean {
    companion object{
        private const val INTERDICTION_KEY="INTERDICTION_KEY"
        private val gson by lazy { Gson() }
        private var currentInterDiction:InterdictionBean?=null
        fun putInterdictionState(interdictionBean:InterdictionBean?){
            if (interdictionBean==null){
                SPUtil.instance.remove(INTERDICTION_KEY)
            }else{
                SPUtil.instance.putString(INTERDICTION_KEY, gson.toJson(interdictionBean))
            }
        }
        private fun getInterdictionBean():InterdictionBean?{
            val str=SPUtil.instance.getString(INTERDICTION_KEY)
            return if (!str.isNullOrBlank()){
                gson.fromJson(str,InterdictionBean::class.java)
            }else{
                null
            }
        }
        fun getInterdictionState():Boolean{
            return (currentInterDiction?:getInterdictionBean())?.isInterdiction()?:false
        }
    }

    var blacklist_permanent:Int=0//永久封
    var blacklist_close_time:String?=null//封的时间
    var blacklist_status:Int=0//封的状态

    fun isInterdiction():Boolean{
        if (blacklist_permanent==1){
            return true
        }else{
            val time=blacklist_close_time?.textTimeToTimeInMillis()?:0L
            return time>System.currentTimeMillis()&&blacklist_status==1
        }
    }
}