package com.twx.marryfriend.bean

import com.twx.marryfriend.R

enum class Sex(val code: Int, val homeBigHead: Int, val smallHead: Int,val uploadHeadDef:Int) {
    male(1, R.mipmap.ic_user_not_man_head, R.drawable.ic_mine_male_default,R.mipmap.ic_man_upload_head),
    female(2, R.mipmap.ic_user_not_woman_head, R.drawable.ic_mine_female_default,R.mipmap.ic_woman_upload_head),
    unknown(0, R.mipmap.ic_user_not_woman_head, R.drawable.ic_mine_female_default,R.mipmap.ic_woman_upload_head);

    fun reversal():Sex{
        return when(this){
            male -> female
            female -> male
            unknown -> unknown
        }
    }

    companion object {
        fun toSex(sexCode: Int?): Sex {
            return when (sexCode) {
                1 -> {
                    male
                }
                2 -> {
                    female
                }
                else -> {
                    unknown
                }
            }
        }
    }
}