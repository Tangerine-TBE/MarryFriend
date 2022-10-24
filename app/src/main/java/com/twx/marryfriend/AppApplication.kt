package com.twx.marryfriend

import android.app.Application
import android.content.Intent
import android.view.View
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.HMSPushHelper
import com.hyphenate.easeim.section.base.WebViewActivity
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.model.EaseNotifier
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.easeui.utils.EaseUserUtils
import com.message.ImUserManager
import com.message.chat.CustomEvent
import com.message.custom.IImEventListener
import com.message.custom.ImCustomEventListenerManager
import com.message.custom.MyHelperAdapterDelegate
import com.twx.marryfriend.base.BaseApplication
import com.twx.marryfriend.base.BaseConstant
import com.twx.marryfriend.bean.Sex
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.push.PushManager
import com.twx.marryfriend.utils.SpLoginUtil
import com.umeng.commonsdk.utils.UMUtils
import com.xyzz.myutils.MyUtils
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.longToast
import com.xyzz.myutils.show.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AppApplication : BaseApplication() {
    companion object {
        lateinit var application: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        if (UMUtils.isMainProgress(this)){
            MyUtils.init(this)
            ImUserManager.userNameState.observeForever{ imId ->
                if (imId!=UserInfo.getUserId()){
                    if (BuildConfig.DEBUG){
                        longToast("执行退出登录")
                        return@observeForever
                    }
                    ImUserManager.logout({

                    },{code, message ->

                    })
                    SpLoginUtil.deleteUserInfo()
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
                }else{
                    ImUserInfoHelper.initUserInfo()
                    // 获取华为 HMS 推送 token
                    HMSPushHelper.getInstance().getHMSToken(BaseConstant.application)
                    MyHelperAdapterDelegate.sexAction={
                        UserInfo.getOriginalUserSex()
                    }
                    EaseUserUtils.setManDefHead(Sex.male.smallHead)
                    EaseUserUtils.setWomanDefHead(Sex.woman.smallHead)
                    EaseUserUtils.setSex(UserInfo.getOriginalUserSex())
                    EaseIM.getInstance().notifier.setNotificationInfoProvider(object :
                        EaseNotifier.EaseNotificationInfoProvider{
                        override fun getDisplayedText(message: EMMessage?): String {
                            message?:return "点击查看"
                            val text=if (message.type == EMMessage.Type.CUSTOM){
                                val body=message.body as EMCustomMessageBody
                                CustomEvent.getTip(body.event()) ?: EaseCommonUtils.getMessageDigest(message, this@AppApplication)
                            }else{
                                val t= EaseCommonUtils.getMessageDigest(message, this@AppApplication)
                                t
                            }
                            return text
                        }

                        override fun getLatestText(
                            message: EMMessage?,
                            fromUsersNum: Int,
                            messageNum: Int
                        ): String {
                            return getDisplayedText(message).also {
                                iLog("详情:${it}")
                            }
                        }

                        override fun getTitle(message: EMMessage?): String {
                            return "您收到一条消息"
                        }

                        override fun getSmallIcon(message: EMMessage?): Int {
                            return R.mipmap.ic_launcher
                        }

                        override fun getLaunchIntent(message: EMMessage?): Intent? {
                            val t=message?.to
                            val f=message?.from
                            if (t!=null&&f!=null) {
                                PushManager.onNotificationMessageClicked(this@AppApplication, t, f)
                            }
                            return null
                        }

                    })
                    ImCustomEventListenerManager.addListener(object : IImEventListener {
                        override fun click(view: View, event: CustomEvent, emMessage: EMMessage) {
                            when(event){
                                CustomEvent.flower -> {

                                }
                                CustomEvent.security -> {
                                    WebViewActivity.actionStart(view.context,"http://test.aisou.club/userManual/fraud.html")
                                }
                                CustomEvent.openSuperVip -> {
                                    view.context.startActivity(IntentManager.getSuperVipIntent(view.context, sVipGifEnum = SVipGifEnum.TopMessage))
                                }
                                CustomEvent.upload_head -> {
                                    view.context.startActivity(IntentManager.getUpHeadImageIntent(view.context))
                                }
                                CustomEvent.dazhaohu_str -> {
                                    view.context.startActivity(IntentManager.getUpFillInGreetIntent(view.context))
                                }
                                CustomEvent.greetext_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.greetext_fail -> {
                                    view.context.startActivity(IntentManager.getUpFillInGreetIntent(view.context))
                                }
                                CustomEvent.putong_xihuan -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.touxiang_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.touxiang_fail -> {
                                    view.context.startActivity(IntentManager.getUpHeadImageIntent(view.context))
                                }
                                CustomEvent.yuying_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.yuying_fail -> {
                                    GlobalScope.launch(Dispatchers.Main) {
                                        view.context.startActivity(IntentManager.getUpFillInVoiceIntent(view.context,true))
                                    }
                                }
                                CustomEvent.shiming_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.shiming_fail -> {
                                    view.context.startActivity(IntentManager.getUpRealNameIntent(view.context))
                                }
                                CustomEvent.xiangce_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.xiangce_fail -> {
                                    view.context.toast(event.title)
//                        view.context.startActivity(IntentManager.getUpRealNameIntent(view.context))
                                }
                                CustomEvent.shenghuo_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.shenghuo_fail -> {
                                    view.context.startActivity(IntentManager.getUpLifeIntent(view.context))
                                }
                                CustomEvent.dongtai_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.dongtai_fail -> {
                                    view.context.startActivity(IntentManager.getDynamicIntent(view.context))
                                }
                                CustomEvent.jubao_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.jubao_fail -> {//举报
                                    view.context.toast(event.title)
//                        view.context.startActivity(IntentManager.getReportIntent(view.context))
                                }
                                CustomEvent.interdi_pass -> {
                                    view.context.toast(event.title)
                                }
                                CustomEvent.HELPER_VIP_EXPIRE -> {
                                    view.context.startActivity(IntentManager.getVipIntent(view.context))
                                }
                                CustomEvent.interdi_fail -> {

                                }
                            }
                        }
                    })
                }
            }
        }
    }
}