package com.twx.marryfriend.push.mfr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.twx.marryfriend.R;
import com.twx.marryfriend.ilove.ILikeActivity;
import com.twx.marryfriend.main.MainActivity;
import com.twx.marryfriend.message.MyHelperActivity;
import com.twx.marryfriend.mine.comment.RecentCommentActivity;
import com.twx.marryfriend.mine.like.RecentLikeActivity;
import com.twx.marryfriend.mine.view.RecentViewActivity;
import com.twx.marryfriend.mutual.MutualLikeActivity;
import com.umeng.message.UmengNotifyClick;
import com.umeng.message.entity.UMessage;


/**
 * 厂商通道配置启动的Activity
 * 点击小米、vivo等厂商渠道的推送通知消息后跳转的activity
 * <p>
 * 必须在AndroidManifest.xml中MfrMessageActivity2标签下配置：
 * 1. 配置 android:exported="true"
 * 2. 新增 intent-filter
 * <intent-filter>
 * <action android:name="android.intent.action.VIEW" />
 * <category android:name="android.intent.category.DEFAULT" />
 * <category android:name="android.intent.category.BROWSABLE" />
 * <data
 * android:host="${applicationId}"
 * android:path="/thirdpush"
 * android:scheme="agoo" />
 * </intent-filter>
 */
public class MfrMessageActivity2 extends Activity {
    private static final String TAG = "MfrMessageActivity";

    private final UmengNotifyClick mNotificationClick = new UmengNotifyClick() {
        @Override
        public void onMessage(UMessage msg) {
            switch (msg.custom) {
                case "shenhe_tongzhi":
                    Log.i("guo", "审核通知，跳小秘书");
                    startActivity(MyHelperActivity.Companion.getIntent(MfrMessageActivity2.this));
                    break;
                case "ta_gang_xihuan_ni":
                    Log.i("guo", "它刚喜欢你 通知");
                    startActivity(new Intent(MfrMessageActivity2.this, ILikeActivity.class));
                    break;
                case "pinglun_dongtai":
                    Log.i("guo", "评论动态");
                    startActivity(new Intent(MfrMessageActivity2.this, RecentCommentActivity.class));
                    break;
                case "dianzan_dongtai":
                    Log.i("guo", "点赞动态");
                    startActivity(new Intent(MfrMessageActivity2.this, RecentLikeActivity.class));
                    break;
                case "kanle_nide_ziliao":
                    Log.i("guo", "看了你的资料");
                    startActivity(new Intent(MfrMessageActivity2.this, RecentViewActivity.class));
                    break;
                case "nixihuande_shangxian":
                    Log.i("guo", "你喜欢的人 上线了");
                    startActivity(new Intent(MfrMessageActivity2.this, ILikeActivity.class));
                    break;
                case "xianghu_xihuan_shangxian":
                    Log.i("guo", "相互喜欢的 上线了");
                    startActivity(new Intent(MfrMessageActivity2.this, MutualLikeActivity.class));
                    break;
                case "dianji_xianghu_xihuan":
                    Log.i("guo", "点击相互喜欢 通知");
                    startActivity(new Intent(MfrMessageActivity2.this, MutualLikeActivity.class));
                    break;
                case "shoudao_liwu_tongzhi":
                    Log.i("guo", "收到礼物通知");
                    ToastUtils.showShort("跳转至送礼界面");
                    break;
                default:
                    Log.i("guo", "无动作，跳转至首页界面");
                    startActivity(new Intent(MfrMessageActivity2.this, MainActivity.class));
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mfr_message_layout);
        mNotificationClick.onCreate(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNotificationClick.onNewIntent(intent);
    }
}
