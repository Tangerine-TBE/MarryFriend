package com.twx.marryfriend.push.help;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPStaticUtils;
import com.luck.picture.lib.utils.ToastUtils;
import com.twx.marryfriend.constant.Constant;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.utils.UMUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.api.UPushRegisterCallback;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

/**
 * PushSDK集成帮助类
 */
public class PushHelper {

    private static final String TAG = "PushHelper";

    /**
     * 预初始化
     */
    public static void preInit(Context context) {
        UMConfigure.preInit(context, "62e74fde1f47e265d4e8aa28", "_360");
    }

    public static void init(Context context) {
        // 参数一：上下文context；
        // 参数二：应用申请的Appkey；
        // 参数三：发布渠道名称；
        // 参数四：设备类型，UMConfigure.DEVICE_TYPE_PHONE：手机；UMConfigure.DEVICE_TYPE_BOX：盒子；默认为手机
        // 参数五：Push推送业务的secret，填写Umeng Message Secret对应信息

        UMConfigure.init(context, "62e74fde1f47e265d4e8aa28", "_360", UMConfigure.DEVICE_TYPE_PHONE, "5e603f6a1afa1a199b2bfb7cded74761");


        //获取推送实例
        PushAgent pushAgent = PushAgent.getInstance(context);

        //修改为您app/src/main/AndroidManifest.xml中package值
//        pushAgent.setResourcePackageName("com.umeng.message.sample");

        //推送设置
        pushSetting(context);

        //注册推送服务，每次调用register方法都会回调该接口
        pushAgent.getInstance(context).register(new UPushRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {

                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i("guo", "deviceToken --> " + deviceToken);


                SPStaticUtils.put(Constant.DEVICE_TOKEN, deviceToken);

//                //获取deviceToken可通过接口：
//                PushAgent.getInstance(context).getRegistrationId();
//                //可设置别名，推送时使用别名推送
//                String alias = "123456";
//                String type = "aa";
//                PushAgent.getInstance(context).setAlias(alias, type, new UPushAliasCallback() {
//                    @Override
//                    public void onMessage(boolean success, String message) {
//                        Log.i(TAG, "setAlias " + success + " msg:" + message);
//                    }
//                });
            }

            @Override
            public void onFailure(String errCode, String errDesc) {
                Log.e("guo", "注册失败 " + "code:" + errCode + ", desc:" + errDesc);
            }
        });

        if (UMUtils.isMainProgress(context)) {
            registerDeviceChannel(context);
        }

    }


    /**
     * 注册设备推送通道（小米、华为等设备的推送）
     */
    private static void registerDeviceChannel(Context context) {
        //小米推送：填写您在小米后台APP对应的xiaomi id和key
        MiPushRegistar.register(context, "2882303761520176390", "5612017666390");

        //华为推送：注意华为推送的初始化参数在AndroidManifest.xml中配置
        HuaWeiRegister.register(context.getApplicationContext());
//        //OPPO推送：填写您在OPPO后台APP对应的app key和secret
//        OppoRegister.register(context, PushConstants.OPPO_KEY, PushConstants.OPPO_SECRET);
//        //vivo推送：注意vivo推送的初始化参数在AndroidManifest.xml中配置
//        VivoRegister.register(context);
    }

    //推送设置
    private static void pushSetting(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);

        //设置通知栏显示通知的最大个数（0～10），0：不限制个数
        pushAgent.setDisplayNotificationNumber(0);

        //推送消息处理
        UmengMessageHandler msgHandler = new UmengMessageHandler() {
            //处理通知栏消息
            @Override
            public void dealWithNotificationMessage(Context context, UMessage msg) {
                super.dealWithNotificationMessage(context, msg);
                Log.i(TAG, "notification receiver:" + msg.getRaw().toString());
            }

            //自定义通知样式，此方法可以修改通知样式等
            @Override
            public @Nullable
            Notification getNotification(Context context, UMessage msg) {
                return super.getNotification(context, msg);
            }

            //处理透传消息
            @Override
            public void dealWithCustomMessage(Context context, UMessage msg) {
                super.dealWithCustomMessage(context, msg);
                Log.i(TAG, "custom receiver:" + msg.getRaw().toString());
            }
        };
        pushAgent.setMessageHandler(msgHandler);

        //推送消息点击处理
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
                Log.i("guo", "click openActivity: " + msg.getRaw().toString());
            }

            @Override
            public void launchApp(Context context, UMessage msg) {
                super.launchApp(context, msg);
                Log.i("guo", "click launchApp: " + msg.getRaw().toString());
            }

            @Override
            public void dismissNotification(Context context, UMessage msg) {
                super.dismissNotification(context, msg);
                Log.i("guo", "click dismissNotification: " + msg.getRaw().toString());
            }

            @Override
            public void dealWithCustomAction(Context context, UMessage uMessage) {
                super.dealWithCustomAction(context, uMessage);
                Log.i("guo", uMessage.custom);

                switch (uMessage.custom) {
                    case "shenhe_tongzhi":
                        Log.i("guo","审核通知，跳小秘书");
                        break;
                    case "ta_gang_xihuan_ni":
                        Log.i("guo","它刚喜欢你 通知");
                        break;
                    case "pinglun_dongtai":
                        Log.i("guo","评论动态");
                        break;
                    case "dianzan_dongtai":
                        Log.i("guo","点赞动态");
                        break;
                    case "kanle_nide_ziliao":
                        Log.i("guo","看了你的资料");

//                        startActivity(Intent(context, RecentViewActivity::class.java))

                        break;
                    case "nixihuande_shangxian":
                        Log.i("guo","你喜欢的人 上线了");
                        break;
                    case "xianghu_xihuan_shangxian":
                        Log.i("guo","相互喜欢的 上线了");
                        break;
                    case "dianji_xianghu_xihuan":
                        Log.i("guo","点击相互喜欢 通知");
                        break;
                    case "shoudao_liwu_tongzhi":
                        Log.i("guo","收到礼物通知");
                        break;
                }


            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        //自定义接收并处理消息
//        pushAgent.setPushIntentServiceClass(MyCustomMessageService.class);

    }

}





