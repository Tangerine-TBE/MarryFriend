package com.twx.marryfriend.utils

import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import com.blankj.utilcode.util.ResourceUtils
import com.twx.marryfriend.R

/**
 * @author: Administrator
 * @date: 2022/8/9
 */
object AnimalUtils {

    /**
     * 点赞动画
     * */
    fun getAnimal(view: ImageView) {
        val animationFriendDrawable = AnimationDrawable()
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_01), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_02), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_03), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_04), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_05), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_06), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_07), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_08), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_09), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_10), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_11), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_12), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_13), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_14), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_15), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_16), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_17), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_18), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_19), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_20), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_21), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_22), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_23), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_24), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_25), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_26), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_27), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_28), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_29), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_30), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_31), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_32), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_33), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_34), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_35), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_36), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_37), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_38), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_39), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_40), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_41), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_42), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_43), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_44), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_45), 50)
        animationFriendDrawable.addFrame(ResourceUtils.getDrawable(R.mipmap.like_46), 50)
        animationFriendDrawable.isOneShot = true
        view.setImageDrawable(animationFriendDrawable)
        animationFriendDrawable.start()
    }

}