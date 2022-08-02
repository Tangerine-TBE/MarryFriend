package com.twx.marryfriend

import android.content.Context
import android.content.Intent
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.mine.life.LifePhotoActivity
import com.twx.marryfriend.mine.user.UserActivity
import com.xyzz.myutils.show.toast

object IntentManager {
    fun getPhotoPreviewIntent(context: Context, list: List<String>, index:Int):Intent{
        return ImagePreviewActivity.getIntent(context, list.toMutableList(),index)
    }

    /**
     *
     */
    fun getDynamicIntent(context: Context):Intent?{
        toast(context,"TODO 跳到动态")
        return null
    }

    /**
     *
     */
    fun getUpLifeIntent(context: Context):Intent?{
        val intent=Intent(context, LifePhotoActivity::class.java)
        intent.putExtra("activity", "data")
        return intent
    }

    /**
     *上传语音
     */
    fun getUpVoiceIntent(context: Context):Intent?{
        val intent=Intent(context, UserActivity::class.java)
        return intent
    }
}