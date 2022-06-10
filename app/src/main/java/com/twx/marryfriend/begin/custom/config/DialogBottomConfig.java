package com.twx.marryfriend.begin.custom.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.AuthUIControlClickListener;
import com.mobile.auth.gatewayauth.CustomInterface;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.twx.marryfriend.R;
import com.twx.marryfriend.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class DialogBottomConfig extends BaseUIConfig {

    public DialogBottomConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        super(activity, authHelper);
    }

    @Override
    public void configAuthPage() {
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        int dialogHeight = (int) (mScreenHeightDp * 0.5f);
        //sdk默认控件的区域是marginTop50dp
        int designHeight = dialogHeight - 50 ;
        int unit = designHeight / 10;

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
                        //点击授权页默认样式的切换其他登录方式 会关闭授权页
                    case ResultCode.CODE_ERROR_USER_SWITCH:
                        Intent intent = new Intent(mActivity, LoginActivity.class);
                        mActivity.startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()

                .setNavHidden(true)

                .setNumberColor(Color.parseColor("#404040"))
                .setNumberSize(22)
                .setNumFieldOffsetY(designHeight / 10)

                .setSloganTextColor(Color.parseColor("#FFFFFF"))

                .setLogBtnText("本机号码一键登录")
                .setLogBtnTextColor(Color.parseColor("#FFFFFF"))
                .setLogBtnTextSize(16)
                .setLogBtnBackgroundPath("shape_bg_common_next")
                .setLogBtnOffsetY(designHeight / 4)

                .setSwitchAccHidden(false)
                .setSwitchAccText("其他手机号登录")
                .setSwitchAccTextColor(Color.parseColor("#9A9A9A"))
                .setSwitchAccTextSize(15)
                .setSwitchOffsetY((designHeight * 5) / 10)

                .setPrivacyState(false)
                .setCheckboxHidden(false)
                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#002E00"))
                .setPrivacyOffsetY((designHeight * 7) / 10)


                .setPageBackgroundPath("dialog_page_background")
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setAppPrivacyColor(Color.parseColor("#9A9A9A"), Color.parseColor("#FF35E4"))

                .setDialogHeight(dialogHeight)
                .setDialogBottom(true)
                .setScreenOrientation(authPageOrientation)
                .create());

    }
}
