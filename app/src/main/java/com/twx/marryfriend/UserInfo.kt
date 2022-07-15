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

    fun isVip():Boolean{
        return getUserVipLevel()>0
    }
    fun getHeadPortrait():String{
        return SPStaticUtils.getString(
            Constant.ME_AVATAR,
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.touxiangzhan.com%2Fupload%2Fimage%2F6a3584291326n2706035469t26.jpg&refer=http%3A%2F%2Fimg.touxiangzhan.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1660122823&t=4b34ed46057ad1bdde5370d18c273ffa")
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