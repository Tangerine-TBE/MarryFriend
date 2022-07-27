package com.twx.marryfriend

import android.content.Context
import android.content.Intent
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
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
        toast(context,"TODO 上传生活照")
        return null
    }

    /**
     *上传语音
     */
    fun getUpVoiceIntent(context: Context):Intent?{
        toast(context,"TODO 上传语音")
        return null
    }
}