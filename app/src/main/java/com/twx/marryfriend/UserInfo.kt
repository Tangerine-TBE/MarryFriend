package com.twx.marryfriend

import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.constant.Constant

object UserInfo {
    fun getUserId():String{
        return SPStaticUtils.getString(
            Constant.USER_ID,
            "default")
    }
    fun getUserVipLevel():Int{
        return SPStaticUtils.getInt(Constant.USER_VIP_LEVEL,0)
    }
    /**
     * 1：男
     * 2：女
     */
    fun getUserSex():Int{
        return SPStaticUtils.getInt(Constant.ME_SEX, 0)
    }

    fun reversalSex(sex:Int):Int{
        return (sex%2)+1
    }

}