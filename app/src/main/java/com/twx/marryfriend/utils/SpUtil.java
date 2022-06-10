package com.twx.marryfriend.utils;

import android.widget.FrameLayout;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.constant.Constant;

import java.util.HashMap;
import java.util.Map;

public class SpUtil {

    public static void saveUserInfo(PhoneLoginBean loginBean) {
        SPStaticUtils.put(Constant.USER_IS_LOGIN, true);
        String id = String.valueOf(loginBean.getData().getUser_id());
        SPStaticUtils.put(Constant.USER_ID, id);
        SPStaticUtils.put(Constant.USER_ACCOUNT, loginBean.getData().getUser_mobile());
        SPStaticUtils.put(Constant.USER_VIP_LEVEL, loginBean.getData().getUser_vipLevel());
        SPStaticUtils.put(Constant.USER_VIP_TIME, loginBean.getData().getUser_vipExpire());
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis());

        // 时间够了，重置会员等级为0
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), loginBean.getData().getUser_vipExpire(), TimeConstants.SEC) > 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }
    }

    public static void saveUserInfo(AutoLoginBean autoLoginBean) {
        SPStaticUtils.put(Constant.USER_IS_LOGIN, true);
        String id = String.valueOf(autoLoginBean.getData().getUser_id());
        SPStaticUtils.put(Constant.USER_ID, id);
        SPStaticUtils.put(Constant.USER_ACCOUNT, autoLoginBean.getData().getUser_mobile());
        SPStaticUtils.put(Constant.USER_VIP_LEVEL, autoLoginBean.getData().getUser_vipLevel());
        SPStaticUtils.put(Constant.USER_VIP_TIME, autoLoginBean.getData().getUser_vipExpire());
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis());

        // 时间够了，重置会员等级为0
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), autoLoginBean.getData().getUser_vipExpire(), TimeConstants.SEC) > 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }
    }

    public static void deleteUserInfo() {
        SPStaticUtils.put(Constant.USER_IS_LOGIN, false);
        SPStaticUtils.put(Constant.USER_ID, "");
        SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        SPStaticUtils.put(Constant.USER_VIP_TIME, "");
        SPStaticUtils.put(Constant.USER_ACCOUNT, "");
    }

}
