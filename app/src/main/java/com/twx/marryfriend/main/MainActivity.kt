package com.twx.marryfriend.main

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.provider.FontRequest
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.message.ImMessageManager
import com.twx.marryfriend.ImUserInfoHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.TrendSaloonList
import com.twx.marryfriend.bean.vip.UpdateTokenBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.DynamicFragment
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.likeme.LikeMeFragment
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
import com.twx.marryfriend.push.PushManager
import com.twx.marryfriend.push.help.PushConstants
import com.twx.marryfriend.push.mfr.MyOppoPushCompat
import com.twx.marryfriend.recommend.RecommendFragment
import com.twx.marryfriend.recommend.RecommendViewModel
import com.twx.marryfriend.utils.BackgroundPopUtils
import com.twx.marryfriend.utils.NotificationUtil
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.message.PushAgent
import com.umeng.message.UmengMessageHandler
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.api.UPushRegisterCallback
import com.umeng.message.entity.UMessage
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import org.android.agoo.huawei.HuaWeiRegister
import org.android.agoo.mezu.MeizuRegister
import org.android.agoo.vivo.VivoRegister
import org.android.agoo.xiaomi.MiPushRegistar
import java.util.*

class MainActivity : MainBaseViewActivity(), IDoUpdateTokenCallback {

    private var isFirst = true
    private var recommend: RecommendFragment? = null
    private var love: LikeMeFragment? = null
    private var dynamic: DynamicFragment? = null
    private var conversationListFragment: ImConversationFragment? = null
    private var mine: MineFragment? = null
    private var currentFragment: Fragment? = null
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }

    private lateinit var doUpdateTokenPresent: doUpdateTokenPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_main

    companion object {
        private const val ISJUMP = "isJump"
        private const val CUSTOM = "custom"

        fun getIntent(context: Context, isJump: Boolean? = false, custom: String? = ""): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ISJUMP, isJump)
            intent.putExtra(CUSTOM, custom)
            return intent
        }

    }

    override fun initView() {
        super.initView()

        lifecycleScope.launch {
            likeWoUnread2.isVisible = false
            try {
                val c = recommendViewModel.likeWoUnread()
                likeWoUnread.isVisible = c > 0
                likeWoUnread.text = "又有${c}个人喜欢你了"
            } catch (e: Exception) {
                iLog(e.stackTraceToString())
            }
        }

        doUpdateTokenPresent = doUpdateTokenPresentImpl.getsInstance()
        doUpdateTokenPresent.registerCallback(this)

        initEmojiCompat()
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
            val unread = ImMessageManager.getAllUnreadMessage()
            if (unread <= 0) {
                messageNumNew.visibility = View.GONE
            } else {
                messageNumNew.visibility = View.VISIBLE
                messageNumNew.text = unread.toString()
            }
        }

        val custom = SPStaticUtils.getString(Constant.PUSH_ACTION)

        if (custom != "") {

            when (custom) {
                "shenhe_tongzhi" -> {
                    Log.i("guo", "审核通知，跳小秘书");
                    startActivity(MyHelperActivity.getIntent(this))
                }
                "ta_gang_xihuan_ni" -> {
                    Log.i("guo", "它刚喜欢你 通知")
                    startActivity(Intent(this, ILikeActivity::class.java))
                }
                "pinglun_dongtai" -> {
                    Log.i("guo", "评论动态")
                    startActivity(Intent(this, RecentCommentActivity::class.java))
                }
                "dianzan_dongtai" -> {
                    Log.i("guo", "点赞动态")
                    startActivity(Intent(this, RecentLikeActivity::class.java))
                }
                "kanle_nide_ziliao" -> {
                    Log.i("guo", "看了你的资料")
                    startActivity(Intent(this, RecentViewActivity::class.java))
                }
                "nixihuande_shangxian" -> {
                    Log.i("guo", "你喜欢的人 上线了")
                    startActivity(Intent(this, ILikeActivity::class.java))
                }
                "xianghu_xihuan_shangxian" -> {
                    Log.i("guo", "相互喜欢的 上线了")
                    startActivity(Intent(this, MutualLikeActivity::class.java))
                }
                "dianji_xianghu_xihuan" -> {
                    Log.i("guo", "点击相互喜欢 通知")
                    startActivity(Intent(this, MutualLikeActivity::class.java))
                }
                "shoudao_liwu_tongzhi" -> {
                    Log.i("guo", "收到礼物通知")
                    ToastUtils.showShort("跳转至送礼界面")
                }
                else -> {
                    Log.i("guo", "无动作")
                }
            }

            SPStaticUtils.put(Constant.PUSH_ACTION, "")

        }

    }

    override fun onStart() {
        super.onStart()
        PushManager.handlerNotificationMsg()?.also {
            val from = ImUserInfoHelper.getUserInfo(it.from)
            if (UserInfo.isVip() || from?.isSuperVip == true || from?.isMutualLike == true) {
                startActivity(ImChatActivity.getIntent(this, it.from))
            } else {
                rb_main_message.performClick()
            }
        }
    }

    override fun initLoadData() {
        super.initLoadData()

    }

    override fun initPresent() {
        super.initPresent()

        // 判断通知权限是否打开
        if (SPStaticUtils.getBoolean(Constant.NOTICE_PERMISSION_TIP, true)) {
            if (!NotificationUtil.isNotifyEnabled(this)) {

                SPStaticUtils.put(Constant.NOTICE_PERMISSION_TIP, false)
                XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(NotificationDialog(this)).show()

            }
        } else if (SPStaticUtils.getBoolean(Constant.BACKGROUND_PERMISSION_TIP, true)) {

            if (!BackgroundPopUtils.isVivoBgStartPermissionAllowed(this)) {

                SPStaticUtils.put(Constant.BACKGROUND_PERMISSION_TIP, false)
                XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(PopDialog(this)).show()

            }

        }

    }

    override fun initEvent() {
        super.initEvent()
        rb_main_recommend.setOnClickListener {
            initRecommendFragment()

        }

        rb_main_love.setOnClickListener {
            lifecycleScope.launch {
                likeWoUnread.isVisible = false
                likeWoUnread2.isVisible = false
                try {
                    recommendViewModel.likeWoUnread(true)
                } catch (e: Exception) {
                    wLog(e.stackTraceToString())
                }
            }
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

        if (trendSaloonList != null) {
            dynamic!!.addData(trendSaloonList)
        }

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
            love = LikeMeFragment()
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
        val fontRequest = FontRequest("com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs)
        config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)

        config.setReplaceAll(true).registerInitCallback(object : EmojiCompat.InitCallback() {
            override fun onInitialized() {
            }

            override fun onFailed(@Nullable throwable: Throwable?) {
                Log.e("guo", "EmojiCompat initialization failed", throwable)
            }
        })
        EmojiCompat.init(config)
    }

    override fun onResume() {
        super.onResume()
        if (!isFirst) {
            lifecycleScope.launch {
                likeWoUnread.isVisible = false
                try {
                    val c = recommendViewModel.likeWoUnread()
                    likeWoUnread2.isVisible = c > 0
                    likeWoUnread2.text = "${c}"
                } catch (e: Exception) {
                    iLog(e.stackTraceToString())
                }
            }
        }
        isFirst = false
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
        UMConfigure.init(context,
            PushConstants.APP_KEY,
            PushConstants.CHANNEL,
            UMConfigure.DEVICE_TYPE_PHONE,
            PushConstants.MESSAGE_SECRET)

        //获取推送实例
        val pushAgent = PushAgent.getInstance(context)

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

                    Log.i("guo", "customUmengPush: ${uMessage}")

//                    Log.i("guo", "customUmengPush: ${uMessage.custom}")
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
        Log.i("PushMessageReceiver", "注册")
        MiPushRegistar.register(context, PushConstants.MI_ID, PushConstants.MI_KEY)

        //OPPO推送：填写您在OPPO后台APP对应的app key和secret
        MyOppoPushCompat.register(context,
            "ddfbd322e5f84b9f9518011417970964",
            "0dd23bca2294417ea0f49d822dc8df29")

        //华为推送：注意华为推送的初始化参数在AndroidManifest.xml中配置
        HuaWeiRegister.register(context.applicationContext)

        //魅族推送：填写您在魅族后台APP对应的app id和key
        MeizuRegister.register(context, "149579", "e1bccfc8ccde4d23acc9aa979ab5e3cb")

        //vivo推送：注意vivo推送的初始化参数在AndroidManifest.xml中配置
        VivoRegister.register(context)

        //荣耀推送：注意荣耀推送的初始化参数在AndroidManifest.xml中配置
//        HonorRegister.register(context)

    }


    override fun onDestroy() {
        super.onDestroy()

        Log.i("guo", "main-destory")

        doUpdateTokenPresent.unregisterCallback(this)
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