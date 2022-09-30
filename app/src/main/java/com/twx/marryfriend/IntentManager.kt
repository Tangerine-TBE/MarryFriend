package com.twx.marryfriend

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.SPStaticUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.dynamic.other.OtherDynamicActivity
import com.twx.marryfriend.dynamic.preview.image.ImagePreviewActivity
import com.twx.marryfriend.dynamic.send.DynamicSendActivity
import com.twx.marryfriend.guide.jumpInfo.JumpActivity
import com.twx.marryfriend.mine.focus.RecentFocusActivity
import com.twx.marryfriend.mine.greet.GreetInfoActivity
import com.twx.marryfriend.mine.life.LifePhotoActivity
import com.twx.marryfriend.mine.verify.VerifyActivity
import com.twx.marryfriend.mine.voice.VoiceActivity
import com.twx.marryfriend.set.report.ReportReasonActivity
import com.twx.marryfriend.tools.avatar.AvatarToolActivity
import com.twx.marryfriend.tools.hobby.HobbyToolActivity
import com.twx.marryfriend.tools.introduce.IntroduceToolActivity
import com.twx.marryfriend.vip.VipActivity
import com.xyzz.myutils.show.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object IntentManager {
    fun getSuperVipIntent(context: Context,pId:String?=null,sVipGifEnum: SVipGifEnum?=null):Intent{
        return VipActivity.getSVipIntent(context,id=pId?.toIntOrNull()?:0,SVip=sVipGifEnum)
    }

    fun getVipIntent(context: Context,pId:String?=null,vipGif: VipGifEnum?=null):Intent{
        return VipActivity.getVipIntent(context,id=pId?.toIntOrNull()?:0,vip=vipGif)
    }

    /**
     * 举报
     */
    fun getReportIntent(context: Context,id:Int):Intent?{
        return ReportReasonActivity.getIntent(context,UserInfo.getUserId().toString(),id.toString())
    }

    private const val DAY_ONE_FILL_IN="day_one_fill_in"
    fun isOpenOneFillIn():Boolean{
        val date=
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
        return !SPStaticUtils.getBoolean(DAY_ONE_FILL_IN+"_"+date,false)
    }

    fun onOpenOneFillIn(){
        val date=
            SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
        SPStaticUtils.put(DAY_ONE_FILL_IN+"_"+date,true)
    }

    fun toFillInDialogIntent(context: Context):Intent?{
        if (isOpenOneFillIn()){
            onOpenOneFillIn()
            return Intent(context, JumpActivity::class.java)
        }else{
            return null
        }
    }

    fun getPhotoPreviewIntent(context: Context, list: List<String>, index:Int):Intent{
        return ImagePreviewActivity.getIntent(context, list.toMutableList(),index)
    }

    /**
     *
     */
    fun getDynamicIntent(context: Context,userId:Int?,sex:Int,nickname:String,avatar:String?):Intent{
        return OtherDynamicActivity.getIntent(context,nickname,sex,avatar?:"",userId.toString(),true)
    }

    /**
     *生活照
     */
    fun getUpLifeIntent(context: Context):Intent?{
        val intent=Intent(context, LifePhotoActivity::class.java)
        intent.putExtra("activity", "data")
        return intent
    }

    fun getDynamicIntent(context: Context):Intent?{
        val intent=Intent(context, DynamicSendActivity::class.java)
        return intent
    }

    fun getUpRealNameIntent(context: Context):Intent?{
        val intent=Intent(context, VerifyActivity::class.java)
//        intent.putExtra("activity", "data")
        return intent
    }

    fun getUpHeadImageIntent(context: Context):Intent?{
        val intent=Intent(context, AvatarToolActivity::class.java)
        intent.putExtra("activity", "data")
        return intent
    }
    fun getUpFillInHobbyIntent(context: Context):Intent?{
        val intent=Intent(context, HobbyToolActivity::class.java)
        intent.putExtra("activity", "data")
        return intent
    }

    /**
     * 招呼语
     */
    fun getUpFillInGreetIntent(context: Context):Intent{
        val intent=Intent(context, GreetInfoActivity::class.java)
        return intent
    }
    fun getUpFillInIntroduceIntent(context: Context):Intent?{
        val intent=Intent(context, IntroduceToolActivity::class.java)
        intent.putExtra("activity", "data")
        return intent
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

    fun getFocusIntent(context: Context):Intent{
        return if (UserInfo.isVip()){
            Intent(context, RecentFocusActivity::class.java)
        }else{
            getVipIntent(context)
        }
    }
}