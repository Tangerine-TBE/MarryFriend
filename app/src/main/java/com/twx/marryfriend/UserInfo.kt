package com.twx.marryfriend

import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.constant.Constant

object UserInfo {
    fun getUserId():String{
        if(BuildConfig.DEBUG){
            return "3"//3,4,6,11,16//
        }
        return SPStaticUtils.getString(
            Constant.USER_ID,
            "default")
    }
    fun getUserVipLevel():Int{
        /*return SPStaticUtils.getInt(Constant.USER_VIP_LEVEL,0)*/
        return 1
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
//        if(BuildConfig.DEBUG){
//            return 1//3,4,6,11,16//
//        }
        return SPStaticUtils.getInt(Constant.ME_SEX, 2)
    }

    fun isSexMail(c:Int?):Boolean{
        return c==1
    }

    fun reversalSex(sex:Int):Int{
        return (sex%2)+1
    }

    fun getReversedDefHeadImage():Int{
        val sex= reversalSex(getUserSex())
        return if (sex==1){
            R.mipmap.icon_mine_male_default
        }else{
            R.mipmap.icon_mine_female_default
        }
    }

    fun getReversedDefHelloHeadImage():Int{
        val sex= reversalSex(getUserSex())
        return if (sex==1){
            R.mipmap.ic_def_hello_male
        }else{
            R.mipmap.ic_def_hello_female
        }
    }

    fun getDefHeadImage(sex: Int):String{
        if (sex==1){
//            R.mipmap.icon_mine_female_default
//            R.mipmap.icon_mine_male_default
            return "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimg.tukuppt.com%2Fpng_preview%2F00%2F09%2F06%2FPhiqi5eUUD.jpg!%2Ffw%2F780&imgrefurl=https%3A%2F%2Fwww.tukuppt.com%2Fmuban%2Fenxkwrae.html&tbnid=Q_a4GmZuCbOVMM&vet=12ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygmegUIARCPAg..i&docid=ysTt9zGjULxHLM&w=780&h=724&q=%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F&ved=2ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygmegUIARCPAg"
        }else{
            return "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01c13f5b56bd8ba8012036be3d28b7.png%401280w_1l_2o_100sh.png&imgrefurl=https%3A%2F%2Fm.zcool.com.cn%2Fwork%2FZMjkxOTkxNDA%3D.html&tbnid=4rBY98RMBUB6SM&vet=12ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygTegUIARDkAQ..i&docid=91X0_gUlFywQoM&w=265&h=265&q=%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F&ved=2ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygTegUIARDkAQ"
        }

    }
}