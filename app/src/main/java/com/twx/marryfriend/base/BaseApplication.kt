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
import com.umeng.message.*
import com.umeng.message.api.UPushRegisterCallback
import com.umeng.message.entity.UMessage
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
//        PushHelper.preInit(this)
//        UMConfigure.preInit(this, "62e74fde1f47e265d4e8aa28", "_360")


        initUMengSettings()


    }


    open fun initChild() {
    }

    fun initPush(context: Context) {

        UMConfigure.init(context,
            "62e74fde1f47e265d4e8aa28",
            "_360",
            UMConfigure.DEVICE_TYPE_PHONE,
            "5e603f6a1afa1a199b2bfb7cded74761")

        //推送设置
        pushSetting(context)

        val pushAgent = PushAgent.getInstance(context)

        pushAgent.resourcePackageName = "com.twx.marryfriend"

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(object : UPushRegisterCallback {
            override fun onSuccess(deviceToken: String) {

                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                SPStaticUtils.put(Constant.DEVICE_TOKEN, deviceToken)

                ThreadUtils.runOnUiThread {

                    when (DeviceUtils.getManufacturer()) {
                        "Xiaomi" -> {
//                小米推送：填写您在小米后台APP对应的xiaomi id和key
                            Log.i("guo", "XIAOMI推送")
                            MiPushRegistar.register(context, "2882303761520176390", "5612017666390")
                        }
                        "vivo" -> {
//                vivo推送：注意vivo推送的初始化参数在AndroidManifest.xml中配置
                            Log.i("guo", "VIVO推送")
                            VivoRegister.register(context);
                        }
                        "OPPO" -> {
//                OPPO推送：填写您在OPPO后台APP对应的app key和secret
                            Log.i("guo", "OPPO推送")
                            OppoRegister.register(context,
                                "ddfbd322e5f84b9f9518011417970964",
                                "0dd23bca2294417ea0f49d822dc8df29");
                        }
                        "HUAWEI" -> {
//                华为推送：注意华为推送的初始化参数在AndroidManifest.xml中配置
                            Log.i("guo", "华为推送")
                            HuaWeiRegister.register(context)
                        }
                        "HONOR" -> {
//                荣耀推送：注意荣耀推送的初始化参数在AndroidManifest.xml中配置
                            Log.i("guo", "荣耀推送")
//                HonorRegister.register(context);
                        }
//            MeizuRegister.register(context, "149579", "41e4f6a38cb24c9aa9a83d2041be6555");
                    }

                }


            }

            override fun onFailure(errCode: String, errDesc: String) {
                Log.e("guo", "注册失败 code:$errCode, desc:$errDesc")
            }

        })
//        if (UMUtils.isMainProgress(context)) {
//            registerDeviceChannel(context)
//        }
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


    private fun initUMengSettings() {
        // 初始化 SDK

        UMConfigure.init(
            this,
            "62e74fde1f47e265d4e8aa28",
            "_360",
            UMConfigure.DEVICE_TYPE_PHONE,
            "5e603f6a1afa1a199b2bfb7cded74761"
        )
        initUMengPush(this)

        MiPushRegistar.register(this, "2882303761520176390", "5612017666390")
        VivoRegister.register(this);
        OppoRegister.register(this,
            "ddfbd322e5f84b9f9518011417970964",
            "0dd23bca2294417ea0f49d822dc8df29");
        HuaWeiRegister.register(this)

    }

    /**
     * 初始化友盟消息推送
     */
    private fun initUMengPush(context: Context) {
        // 获取消息推送代理示例
        val pushAgent = PushAgent.getInstance(context)
        // 注册推送服务，每次调用 register 方法都会回调该接口
        pushAgent.register(mIUmengRegisterCallback)
        // 设置点击通知栏打开操作
        pushAgent.notificationClickHandler = mNotificationClickHandler

        initUMengPushSettings(pushAgent)
    }

    /**
     * 注册推送服务
     */
    private val mIUmengRegisterCallback = object : IUmengRegisterCallback {
        override fun onSuccess(deviceToken: String?) {
            Log.i("guo", "-------> 注册成功：deviceToken：--------> $deviceToken")
        }

        override fun onFailure(s: String?, s1: String?) {
            Log.i("guo", "-------> 注册失败：s ---> $s ||| s1 ---> $s1")
        }
    }

    /**
     * 点击通知栏 后续操作
     */
    private val mNotificationClickHandler = object : UmengNotificationClickHandler() {

        /**
         * 处理用户点击通知栏消息
         */
        override fun dealWithCustomAction(context: Context?, uMessage: UMessage?) {
            super.dealWithCustomAction(context, uMessage)
            context ?: return
            uMessage ?: return
            // 后台接口传递过来的参数都在 map 中
            val extraMap = uMessage.extra
            // 这里演示下获取俩个值
            val openType = extraMap["type"]
            val openID = extraMap["id"]
            // 。。。
        }

    }

    /**
     * 推送基础信息配置
     */
    private fun initUMengPushSettings(pushAgent: PushAgent) {
        // 设置最多显示通知条数 参数 number 可以设置为 0~10 之间任意整数。当参数为 0 时，表示不合并通知；
        pushAgent.displayNotificationNumber = 0
        // 设置客户端允许声音提醒
        pushAgent.notificationPlaySound = MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE
        // 设置客户端允许呼吸灯点亮
        pushAgent.notificationPlayLights = MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE
        // 设置客户端允许震动
        pushAgent.notificationPlayVibrate = MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE
        // 通知免打扰 SDK默认在“23:00”到“7:00”之间收到通知消息时不响铃，不振动，不闪灯
        pushAgent.setNoDisturbMode(23, 0, 7, 0)
        // 设置冷却时间 避免一分钟内出现多条通知而被替换
        pushAgent.muteDurationSeconds = 600
    }


    interface OnForegroundListener {
        fun listener();
    }

    fun setOnForegroundListener(listener: OnForegroundListener) {
        this.onForegroundListener = listener
    }

}

