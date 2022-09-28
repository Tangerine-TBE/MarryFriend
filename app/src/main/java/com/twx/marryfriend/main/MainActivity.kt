package com.twx.marryfriend.main

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.message.ImMessageManager
import com.twx.marryfriend.ImHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.TrendSaloonList
import com.twx.marryfriend.bean.vip.UpdateTokenBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.DynamicFragment
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.likeme.LoveFragment
import com.twx.marryfriend.message.ImConversationFragment
import com.twx.marryfriend.mine.MineFragment
import com.twx.marryfriend.mine.comment.RecentCommentActivity
import com.twx.marryfriend.mine.like.RecentLikeActivity
import com.twx.marryfriend.mine.view.RecentViewActivity
import com.twx.marryfriend.mutual.MutualLikeActivity
import com.twx.marryfriend.net.callback.vip.IDoUpdateTokenCallback
import com.twx.marryfriend.net.impl.vip.doUpdateTokenPresentImpl
import com.twx.marryfriend.recommend.RecommendFragment
import com.twx.marryfriend.utils.NotificationUtil
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

    private lateinit var doUpdateTokenPresent: doUpdateTokenPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_main

    override fun initView() {
        super.initView()

        doUpdateTokenPresent = doUpdateTokenPresentImpl.getsInstance()
        doUpdateTokenPresent.registerCallback(this)

        initEmojiCompat()
        ImHelper.init()
        initRecommendFragment()
        initPush(this)

        PushAgent.getInstance(this).onAppStart()

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
    }

    override fun initLoadData() {
        super.initLoadData()

        Log.i("guo", "iamge: ${SPStaticUtils.getString(Constant.ME_AVATAR)}")

    }

    override fun initPresent() {
        super.initPresent()

        // 判断通知权限是否打开

        Log.i("guo", "notification : ${NotificationUtil.isNotifyEnabled(this)}")


        if (!NotificationUtil.isNotifyEnabled(this)) {

            startActivity(NotificationUtil.getNotifyIntent(this))


        }

    }

    override fun initEvent() {
        super.initEvent()

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
    fun addDynamicFragment(trendSaloonList: TrendSaloonList) {

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

        dynamic!!.addData(trendSaloonList)

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


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppUtils.exitApp()
        }
        return super.onKeyDown(keyCode, event)
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
            "62e74fde1f47e265d4e8aa28",
            "_360",
            UMConfigure.DEVICE_TYPE_PHONE,
            "5e603f6a1afa1a199b2bfb7cded74761")

        //推送设置
        pushSetting(context)

        //注册推送服务，每次调用register方法都会回调该接口
        PushAgent.getInstance(context).register(object : UPushRegisterCallback {
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
                            ToastUtils.showShort("跳转至小秘书")
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


        Log.i("guo", "设备厂商 :${DeviceUtils.getManufacturer()}")


        when (DeviceUtils.getManufacturer()) {

            "Xiaomi" -> {
                Log.i("guo", "XIAOMI推送")

//                小米推送：填写您在小米后台APP对应的xiaomi id和key
                MiPushRegistar.register(context, "2882303761520176390", "5612017666390")
            }


            "vivo" -> {
                Log.i("guo", "VIVO推送")

//                vivo推送：注意vivo推送的初始化参数在AndroidManifest.xml中配置
                VivoRegister.register(context);
            }

            "OPPO" -> {
                Log.i("guo", "OPPO推送")

//                OPPO推送：填写您在OPPO后台APP对应的app key和secret
                OppoRegister.register(context,
                    "ddfbd322e5f84b9f9518011417970964",
                    "0dd23bca2294417ea0f49d822dc8df29");

            }

            "HUAWEI" -> {
                Log.i("guo", "华为推送")

//                华为推送：注意华为推送的初始化参数在AndroidManifest.xml中配置
                HuaWeiRegister.register(context.applicationContext)
            }

            "HONOR" -> {
                Log.i("guo", "荣耀推送")

                //荣耀推送：注意荣耀推送的初始化参数在AndroidManifest.xml中配置
//                HonorRegister.register(context);
            }


//            MeizuRegister.register(context, "149579", "41e4f6a38cb24c9aa9a83d2041be6555");


        }


    }


    override fun onDestroy() {
        super.onDestroy()

        doUpdateTokenPresent.unregisterCallback(this)

    }

}