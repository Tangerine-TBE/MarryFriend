package com.twx.marryfriend.begin.custom.config;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.AuthUIControlClickListener;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.twx.marryfriend.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * xml文件方便预览
 * 可以通过addAuthRegisterXmlConfig一次性统一添加授权页的所有自定义view
 */
public class CustomXmlConfig extends BaseUIConfig {

    public CustomXmlConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        super(activity, authHelper);
    }

    @Override
    public void configAuthPage() {
//        mAuthHelper.removeAuthRegisterXmlConfig();
//        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        mAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.layout_custom_login, new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {

                        AssetManager assetManager = mActivity.getAssets();

                        InputStream bg = null;
                        InputStream icon = null;
                        try {
                            bg = assetManager.open("pic/pic_login_bg.png");
                            icon = assetManager.open("pic/pic_custom_icon.png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        findViewById(R.id.rl_custom_login_bg).setBackground(new BitmapDrawable(BitmapFactory.decodeStream(bg)));
                        findViewById(R.id.iv_custom_login).setBackground(new BitmapDrawable(BitmapFactory.decodeStream(icon)));

                        // 退出一键登录界面
                        findViewById(R.id.iv_custom_login_finish).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAuthHelper.quitLoginPage();
                            }
                        });
                    }
                })
                .build());

        mAuthHelper.setUIClickListener(new AuthUIControlClickListener() {
            @Override
            public void onClick(String code, Context context, String jsonString) {
                JSONObject jsonObj = null;
                try {
                    if (!TextUtils.isEmpty(jsonString)) {
                        jsonObj = new JSONObject(jsonString);
                    }
                } catch (JSONException e) {
                    jsonObj = new JSONObject();
                }
                switch (code) {
                    //点击授权页默认样式的返回按钮
                    case ResultCode.CODE_ERROR_USER_CANCEL:
                        mAuthHelper.quitLoginPage();
                        break;
                    //点击授权页默认样式的切换其他登录方式 会关闭授权页
                    case ResultCode.CODE_ERROR_USER_SWITCH:
//                        Intent intent = new Intent(mActivity, LoginActivity.class);
//                        mActivity.startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()

                .setStatusBarColor(Color.parseColor("#FFFFFF"))
                .setLightColor(true)

                .setNumberColor(Color.parseColor("#FFFFFF"))
                .setNumberSizeDp(30)
                .setNumFieldOffsetY(251)
                .setNumberLayoutGravity(Gravity.CENTER)

                .setLogBtnText(" ")
                .setLogBtnTextColor(Color.parseColor("#FFFFFF"))
                .setLogBtnBackgroundPath("pic/pic_login_login.png")
                .setLogBtnTextSize(18)
                .setLogBtnMarginLeftAndRight(24)
                .setLogBtnOffsetY(300)
                .setLogBtnLayoutGravity(Gravity.CENTER)

                .setAppPrivacyColor(Color.parseColor("#80FFFFFF"), Color.parseColor("#FFFFFF"))
                .setPrivacyOffsetY_B(60)
                .setPrivacyState(false)
                .setPrivacyTextSize(15)
                .setPrivacyMargin(24)
                .setCheckboxHidden(false)

                .setNavHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(true)
                .setSwitchAccHidden(true)
                .setPrivacyState(false)

                .setSwitchAccHidden(false)
                .setSwitchAccText("其他账号登录")
                .setSwitchAccTextColor(Color.parseColor("#0BFBB1"))
                .setSwitchAccTextSize(14)
                .setSwitchOffsetY(370)

                .setLightColor(true)
                .setWebViewStatusBarColor(Color.TRANSPARENT)
                .setStatusBarColor(Color.TRANSPARENT)
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .setWebNavTextSizeDp(20)

                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
//                .setPageBackgroundPath("page_background_color")

//                .setLogBtnBackgroundPath("")
                .setScreenOrientation(authPageOrientation)
                .create());
    }
}
