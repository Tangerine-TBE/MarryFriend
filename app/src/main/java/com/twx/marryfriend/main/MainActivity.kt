package com.twx.marryfriend.main

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.huawei.hms.aaid.HmsInstanceId
import com.huawei.hms.common.ApiException
import com.hyphenate.easeim.common.livedatas.LiveDataBus
import com.hyphenate.easeim.common.model.ChatInfoBean
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.message.ImInit
import com.message.ImMessageManager
import com.twx.marryfriend.ImUserInfoHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.TrendSaloonList
import com.twx.marryfriend.bean.vip.UpdateTokenBean
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.DynamicFragment
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.likeme.LoveFragment
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.message.ImConversationFragment
import com.twx.marryfriend.message.MyHelperActivity
import com.twx.marryfriend.mine.MineFragment
import com.twx.marryfriend.mine.comment.RecentCommentActivity
import com.twx.marryfriend.mine.like.RecentLikeActivity
import com.twx.marryfriend.mine.view.RecentViewActivity
import com.twx.marryfriend.mutual.MutualLikeActivity
import com.twx.marryfriend.net.callback.vip.IDoUpdateTokenCallback
import com.twx.marryfriend.net.impl.vip.doUpdateTokenPresentImpl
import com.twx.marryfriend.push.help.PushConstants
import com.twx.marryfriend.push.help.PushHelper
import com.twx.marryfriend.recommend.RecommendFragment
import com.twx.marryfriend.utils.BackgroundPopUtils
import com.twx.marryfriend.utils.NotificationUtil
import com.twx.marryfriend.utils.SpUtil
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.api.UPushRegisterCallback
import com.umeng.message.entity.UMessage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.oppo.OppoRegister
import org.android.agoo.vivo.VivoRegister
import org.android.agoo.xiaomi.MiPushRegistar
import java.util.*

class MainActivity : MainBaseViewActivity(), IDoUpdateTokenCallback {

    private var recommend: RecommendFragment? = null
    private var love: LoveFragment? = null
    private var dynamic: DynamicFragment? = null
    private var conversationListFragment: ImConversationFragment? = null
    private var mine: MineFragment? = null
    private var currentFragment: Fragment? = null
    private val imLoginStateObserver= androidx.lifecycle.Observer<Boolean> { aBoolean ->
        if (aBoolean==false){
            SpUtil.deleteUserInfo()
            startActivity(Intent(this, BeginActivity::class.java))
        }
    }

    private lateinit var doUpdateTokenPresent: doUpdateTokenPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_main

    override fun initView() {
        super.initView()

        doUpdateTokenPresent = doUpdateTokenPresentImpl.getsInstance()
        doUpdateTokenPresent.registerCallback(this)

        initEmojiCompat()
        ImUserInfoHelper.init()
        initRecommendFragment()

        initPush(applicationContext)


        if (ImMessageManager.getAllUnreadMessage() > 0) {
            messageNumNew.visibility = View.VISIBLE
            messageNumNew.text = ImMessageManager.getAllUnreadMessage().toString()
        } else {
            messageNumNew.visibility = View.GONE
        }
        ImMessageManager.newMessageLiveData.observe(this) {
            if (currentFragment == conversationListFragment) {
                return@observe
            }
            messageNumNew.visibility = View.VISIBLE
            messageNumNew.text = ImMessageManager.getAllUnreadMessage().toString()
        }
        ImInit.imLoginState.observeForever(imLoginStateObserver)
    }

    override fun initLoadData() {
        super.initLoadData()

        getToken()

    }

    override fun initPresent() {
        super.initPresent()

        // 判断通知权限是否打开

        Log.i("guo", "notification : ${NotificationUtil.isNotifyEnabled(this)}")



        if (SPStaticUtils.getBoolean(Constant.NOTICE_PERMISSION_TIP, true)) {
            if (!NotificationUtil.isNotifyEnabled(this)) {

                SPStaticUtils.put(Constant.NOTICE_PERMISSION_TIP, false)
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(NotificationDialog(this))
                    .show()

            }
        } else if (SPStaticUtils.getBoolean(Constant.BACKGROUND_PERMISSION_TIP, true)) {

            if (!BackgroundPopUtils.isVivoBgStartPermissionAllowed(this)) {

                SPStaticUtils.put(Constant.BACKGROUND_PERMISSION_TIP, false)
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(PopDialog(this))
                    .show()

            }

        }

    }

    override fun initEvent() {
        super.initEvent()
        LiveDataBus.get().with("huanXin_push", ChatInfoBean::class.java).observeForever {
            Log.i("guo", "LiveDataBus 接收信息");
            startActivity(ImChatActivity.getIntent(this, it.targetId))
        }

//        LiveDataBus.get().with("huanXin_push", ChatInfoBean::class.java).removeObserver()

        rb_main_recommend.setOnClickListener {
            initRecommendFragment()
        }

        rb_main_love.setOnClickListener {
            initLoveFragment()
        }

        rb_main_dynamic.setOnClickListener {
            initDynamicFragment()
        }

        rb_main_message.setOnClickListener {
            messageNumNew.visibility = View.GONE
            initMessageFragment()
        }

        rb_main_mine.setOnClickListener {
            initMineFragment()
        }

    }


    // 动态列表界面添加动态数据
    fun addDynamicFragment(trendSaloonList: TrendSaloonList?) {

        rb_main_dynamic.isChecked = true

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (dynamic == null) {
            dynamic = DynamicFragment().newInstance(this)
            transaction.add(R.id.fl_main_container, dynamic!!)
        }
        currentFragment = dynamic
        hideFragment(transaction)
        transaction.show(dynamic!!)
        transaction.commit()

        if (trendSaloonList!=null) {
            dynamic!!.addData(trendSaloonList)
        }

    }

    private fun getToken() {
        // 创建一个新线程
        object : Thread() {
            override fun run() {
                try {
                    // 从agconnect-services.json文件中读取APP_ID
                    val appId = "106852163"

                    // 输入token标识"HCM"
                    val tokenScope = "HCM"
                    val token =
                        HmsInstanceId.getInstance(this@MainActivity).getToken(appId, tokenScope)

                    // 判断token是否为空
                    if (!TextUtils.isEmpty(token)) {
                        Log.e("guo", "get token success, $token")
                        sendRegTokenToServer(token)
                    }

                } catch (e: ApiException) {
                    Log.e("guo", "get token failed, $e")
                }
            }
        }.start()
    }

    private fun sendRegTokenToServer(token: String?) {
    }

    private fun updateToken(token: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.UMENG_TOKEN] = token
        doUpdateTokenPresent.doUpdateToken(map)
    }

    private fun initRecommendFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (recommend == null) {
            recommend = RecommendFragment()
            transaction.add(R.id.fl_main_container, recommend!!)
        }
        currentFragment = recommend
        hideFragment(transaction)
        transaction.show(recommend!!)
        transaction.commit()
    }

    private fun initLoveFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (love == null) {
            love = LoveFragment()
            transaction.add(R.id.fl_main_container, love!!)
        }
        currentFragment = love
        hideFragment(transaction)
        transaction.show(love!!)
        transaction.commit()
    }

    private fun initDynamicFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (dynamic == null) {
            dynamic = DynamicFragment().newInstance(this)
            transaction.add(R.id.fl_main_container, dynamic!!)
        }
        currentFragment = dynamic
        hideFragment(transaction)
        transaction.show(dynamic!!)
        transaction.commit()
    }

    private fun initMessageFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (conversationListFragment == null) {
            conversationListFragment = ImConversationFragment()
            transaction.add(R.id.fl_main_container, conversationListFragment!!)
        } else {
            conversationListFragment?.onResume()
        }
        currentFragment = conversationListFragment
        hideFragment(transaction)
        transaction.show(conversationListFragment!!)
        transaction.commit()
    }

    private fun initMineFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (mine == null) {
            mine = MineFragment()
            transaction.add(R.id.fl_main_container, mine!!)
        }
        currentFragment = mine
        hideFragment(transaction)
        transaction.show(mine!!)
        transaction.commit()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        if (recommend != null) {
            transaction.hide(recommend!!)
        }
        if (love != null) {
            transaction.hide(love!!)
        }
        if (dynamic != null) {
            transaction.hide(dynamic!!)
        }
        if (conversationListFragment != null) {
            transaction.hide(conversationListFragment!!)
        }
        if (mine != null) {
            transaction.hide(mine!!)
        }
    }

    // 加载Emoji
    private fun initEmojiCompat() {
        val config: EmojiCompat.Config
        // Use a downloadable font for EmojiCompat
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs)
        config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)

        config.setReplaceAll(true)
            .registerInitCallback(object : EmojiCompat.InitCallback() {
                override fun onInitialized() {
                }

                override fun onFailed(@Nullable throwable: Throwable?) {
                    Log.e("guo", "EmojiCompat initialization failed", throwable)
                }
            })
        EmojiCompat.init(config)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        mine?.handlePhotoCropResult(data)
                    };
                }
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        mine?.handlePhotoCropError(data)
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        recommend?.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateTokenSuccess(updateTokenBean: UpdateTokenBean?) {
        if (updateTokenBean != null) {
            if (updateTokenBean.code == 200) {
                ToastUtils.showShort("token上传")
            }
        }
    }

    override fun onDoUpdateTokenError() {

    }


    /**
     *
     * 参数一：上下文context；
     * 参数二：应用申请的Appkey；
     * 参数三：发布渠道名称；
     * 参数四：设备类型，UMConfigure.DEVICE_TYPE_PHONE：手机；UMConfigure.DEVICE_TYPE_BOX：盒子；默认为手机
     * 参数五：Push推送业务的secret，填写Umeng Message Secret对应信息
     * */
    fun initPush(context: Context) {
        UMConfigure.init(context, PushConstants.APP_KEY, PushConstants.CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE, PushConstants.MESSAGE_SECRET)

        //获取推送实例
        val pushAgent = PushAgent.getInstance(context)

        //修改为您app/src/main/AndroidManifest.xml中package值
        pushAgent.resourcePackageName = "com.twx.marryfriend"

        //推送设置
        pushSetting(context)

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(object : UPushRegisterCallback {
            override fun onSuccess(deviceToken: String) {

                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                SPStaticUtils.put(Constant.DEVICE_TOKEN, deviceToken)

                Log.i("guo", "token ： ${deviceToken}")

                // 上传token
                updateToken(deviceToken)
            }

            override fun onFailure(errCode: String, errDesc: String) {
                Log.e("guo", "注册失败 code:$errCode, desc:$errDesc")
            }

        })
        if (UMUtils.isMainProgress(context)) {
            registerDeviceChannel(context)
        }
    }


    //推送设置
    private fun pushSetting(context: Context) {
        val pushAgent = PushAgent.getInstance(context)

        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        pushAgent.displayNotificationNumber = 0

        //推送消息处理
        val msgHandler: UmengMessageHandler = object : UmengMessageHandler() {
            //处理通知栏消息
            override fun dealWithNotificationMessage(context: Context, msg: UMessage) {
                super.dealWithNotificationMessage(context, msg)
            }

            //自定义通知样式，此方法可以修改通知样式等
            override fun getNotification(context: Context, msg: UMessage): Notification? {
                return super.getNotification(context, msg)
            }

            //处理透传消息
            override fun dealWithCustomMessage(context: Context, msg: UMessage) {
                super.dealWithCustomMessage(context, msg)
            }
        }
        pushAgent.messageHandler = msgHandler

        //推送消息点击处理
        val notificationClickHandler: UmengNotificationClickHandler =
            object : UmengNotificationClickHandler() {
                override fun openActivity(context: Context, msg: UMessage) {
                    super.openActivity(context, msg)
                }

                override fun launchApp(context: Context, msg: UMessage) {
                    super.launchApp(context, msg)
                }

                override fun dismissNotification(context: Context, msg: UMessage) {
                    super.dismissNotification(context, msg)
                }

                override fun dealWithCustomAction(context: Context, uMessage: UMessage) {
                    super.dealWithCustomAction(context, uMessage)
                    Log.i("guo", "custom: ${uMessage.custom}")
                    when (uMessage.custom) {
                        "shenhe_tongzhi" -> {
                            Log.i("guo", "审核通知，跳小秘书")
                            startActivity(MyHelperActivity.getIntent(this@MainActivity))
                        }
                        "ta_gang_xihuan_ni" -> {
                            Log.i("guo", "它刚喜欢你 通知")
                            startActivity(Intent(context, ILikeActivity::class.java))
                        }
                        "pinglun_dongtai" -> {
                            Log.i("guo", "评论动态")
                            startActivity(Intent(context, RecentCommentActivity::class.java))
                        }
                        "dianzan_dongtai" -> {
                            Log.i("guo", "点赞动态")
                            startActivity(Intent(context, RecentLikeActivity::class.java))
                        }
                        "kanle_nide_ziliao" -> {
                            Log.i("guo", "看了你的资料")
                            startActivity(Intent(context, RecentViewActivity::class.java))
                        }
                        "nixihuande_shangxian" -> {
                            Log.i("guo", "你喜欢的人 上线了")
                            startActivity(Intent(context, ILikeActivity::class.java))
                        }
                        "xianghu_xihuan_shangxian" -> {
                            Log.i("guo", "相互喜欢的 上线了")
                            startActivity(Intent(context, MutualLikeActivity::class.java))
                        }
                        "dianji_xianghu_xihuan" -> {
                            Log.i("guo", "点击相互喜欢 通知")
                            startActivity(Intent(context, MutualLikeActivity::class.java))
                        }
                        "shoudao_liwu_tongzhi" -> {
                            Log.i("guo", "收到礼物通知")
                            ToastUtils.showShort("跳转至送礼界面")
                        }
                    }
                }
            }
        pushAgent.notificationClickHandler = notificationClickHandler

        //自定义接收并处理消息
//        pushAgent.setPushIntentServiceClass(MyCustomMessageService.class);
    }


    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     */
    private fun registerDeviceChannel(context: Context) {
        MiPushRegistar.register(context, PushConstants.MI_ID, PushConstants.MI_KEY)
        VivoRegister.register(context)
        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET)
        HuaWeiRegister.register(context.applicationContext)
    }


    override fun onDestroy() {
        super.onDestroy()

        Log.i("guo", "main-destory")

        doUpdateTokenPresent.unregisterCallback(this)
        ImInit.imLoginState.removeObserver(imLoginStateObserver)
    }


    // 消息通知
    inner class NotificationDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_photo_notification

        override fun onCreate() {
            super.onCreate()

            findViewById<ImageView>(R.id.iv_dialog_notice_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_notice_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_notice_confirm).setOnClickListener {
                startActivity(NotificationUtil.getNotifyIntent(context))
                dismiss()
            }

        }
    }

    // 消息通知
    inner class PopDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_photo_pop

        override fun onCreate() {
            super.onCreate()

            findViewById<ImageView>(R.id.iv_dialog_pop_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_pop_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_pop_confirm).setOnClickListener {
                PermissionUtils.launchAppDetailsSettings()
                dismiss()
            }

        }
    }


}