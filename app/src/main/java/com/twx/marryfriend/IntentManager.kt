package com.twx.marryfriend

import android.content.Context
import android.content.Intent
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.dynamic.other.OtherDynamicActivity
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.send.location.LocationActivity
import com.twx.marryfriend.mine.greet.GreetInfoActivity
import com.twx.marryfriend.mine.life.LifePhotoActivity
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.mine.verify.VerifyActivity
import com.twx.marryfriend.mine.voice.VoiceActivity
import com.xyzz.myutils.show.toast
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object IntentManager {
    fun getPhotoPreviewIntent(context: Context, list: List<String>, index:Int):Intent{
        return ImagePreviewActivity.getIntent(context, list.toMutableList(),index)
    }

    /**
     *
     */
    fun getDynamicIntent(context: Context,userId:Int?,sex:Int,nickname:String,avatar:String?):Intent{
        return OtherDynamicActivity.getIntent(context,nickname,sex,avatar?:"",userId.toString())
    }

    /**
     *
     */
    fun getUpLifeIntent(context: Context):Intent?{
        val intent=Intent(context, LifePhotoActivity::class.java)
        intent.putExtra("activity", "data")
        return intent
    }

    fun getUpRealNameIntent(context: Context):Intent?{
        val intent=Intent(context, VerifyActivity::class.java)
//        intent.putExtra("activity", "data")
        return intent
    }

    fun getUpHeadImageIntent(context: Context):Intent?{
        val intent=Intent(context, LifePhotoActivity::class.java)
        intent.putExtra("activity", "data")
        return null
    }
    fun getUpFillInHobbyIntent(context: Context):Intent?{
        val intent=Intent(context, LifePhotoActivity::class.java)
        intent.putExtra("activity", "data")
        return null
    }
    fun getUpFillInGreetIntent(context: Context):Intent?{
        val intent=Intent(context, GreetInfoActivity::class.java)
        return intent
    }
    fun getUpFillInIntroduceIntent(context: Context):Intent?{
        val intent=Intent(context, LifePhotoActivity::class.java)
        intent.putExtra("activity", "data")
        return null
    }
//    fun getUpFillInVoiceIntent(context: Context):Intent?{
//        val intent=Intent(context, LifePhotoActivity::class.java)
//        intent.putExtra("activity", "data")
//        return null
//    }

    suspend fun getUpFillInVoiceIntent(context: Context)= suspendCoroutine<Intent?>{//上传语音
        XXPermissions.with(context)
            .permission(Permission.RECORD_AUDIO)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    val intent=Intent(context, VoiceActivity::class.java)
                    it.resume(intent)
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    toast(context,"请授予应用相应权限")
                    it.resume(null)
                }
            })
    }
}