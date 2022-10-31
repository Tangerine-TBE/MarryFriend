package com.twx.marryfriend.base

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.mine.comment.RecentCommentActivity
import com.twx.marryfriend.mine.like.RecentLikeActivity
import com.twx.marryfriend.mine.view.RecentViewActivity
import com.twx.marryfriend.mutual.MutualLikeActivity
import com.twx.marryfriend.push.help.PushHelper
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.message.*
import com.umeng.message.api.UPushRegisterCallback
import com.umeng.message.entity.UMessage
import com.xyzz.myutils.show.iLog
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.oppo.OppoRegister
import org.android.agoo.vivo.VivoRegister
import org.android.agoo.xiaomi.MiPushRegistar

open class BaseApplication : Application() {

    private var onForegroundListener: OnForegroundListener? = null

    open fun getInstance(): BaseApplication? {
        if (baseApplication == null) {
            baseApplication = BaseApplication()
        }
        return baseApplication
    }

    companion object {
        lateinit var application: Application
        var appContext: Context? = null
        var baseApplication: BaseApplication? = null
        fun getContext(): Context {
            return appContext!!
        }

        var mMainHandler: Handler? = null
        fun getMainHandler(): Handler {
            return mMainHandler!!
        }

        lateinit var packName: String

    }


    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        iLog("BaseApplication.application初始化")
        application = this
        appContext = baseContext
        mMainHandler = Handler()
        packName = packageName


        /**
         * 初始化友盟SDK
         */

        //日志开关
        UMConfigure.setLogEnabled(true)
        //预初始化
        PushHelper.preInit(this)
        //是否同意隐私政策


        initChild()

    }


    open fun initChild() {
    }


    interface OnForegroundListener {
        fun listener();
    }

    fun setOnForegroundListener(listener: OnForegroundListener) {
        this.onForegroundListener = listener
    }

}

