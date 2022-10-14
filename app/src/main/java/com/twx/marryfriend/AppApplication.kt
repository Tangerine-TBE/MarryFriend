package com.twx.marryfriend

import android.app.Application
import android.content.Intent
import com.kingja.loadsir.core.LoadSir
import com.message.ImInit
import com.message.ImLoginHelper
import com.message.ImUserManager
import com.twx.marryfriend.base.BaseApplication
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.utils.SpUtil
import com.xyzz.myutils.MyUtils
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.longToast
import com.xyzz.myutils.show.toast


class AppApplication : BaseApplication() {
    companion object {
        lateinit var application: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        MyUtils.init(this)

        LoadSir.beginBuilder()
            .addCallback(DefEmptyDataCallBack()) //添加各种状态页
//            .addCallback(EmptyCallback())
//            .addCallback(LoadingCallback())
//            .addCallback(TimeoutCallback())
//            .addCallback(CustomCallback())
//            .setDefaultCallback(LoadingCallback::class.java) //设置默认状态页
            .commit()
       ImInit.init(this)

        ImInit.imLoginState.observeForever{ imId ->
            if (imId!=UserInfo.getUserId()){
                if (BuildConfig.DEBUG){
                    longToast("执行退出登录")
                    return@observeForever
                }
                ImLoginHelper.logout({

                },{code,msg->

                })
                SpUtil.deleteUserInfo()
                MyUtils.getLastResumedActivityLiveData().value.also { activity ->
                    if (activity is BeginActivity){
                        return@also
                    }else{
                        if (activity!=null&&!activity.isDestroyed){
                            activity.startActivity(Intent(activity, BeginActivity::class.java))
                        }else{
                            val intent=Intent(BaseConstant.application,BeginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            BaseConstant.application.startActivity(intent)
                        }
                    }
                    MyUtils.getAllRunActivity().forEach {
                        it.finish()
                    }
                }
            }
        }
    }
}