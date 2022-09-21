package com.twx.marryfriend.utils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.constant.Constant;

public class SpUtil {

    public static void saveUserInfo(PhoneLoginBean loginBean) {
        SPStaticUtils.put(Constant.USER_IS_LOGIN, true);
        String id = String.valueOf(loginBean.getData().getUser_id());
        SPStaticUtils.put(Constant.USER_ID, id);
        SPStaticUtils.put(Constant.USER_ACCOUNT, loginBean.getData().getUser_mobile());
        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, loginBean.getData().getClose_time_low());
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, loginBean.getData().getClose_time_high());
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis());

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), loginBean.getData().getClose_time_high(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2);
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), loginBean.getData().getClose_time_low(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1);
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }
    }

    public static void saveUserInfo(AutoLoginBean autoLoginBean) {
        SPStaticUtils.put(Constant.USER_IS_LOGIN, true);
        String id = String.valueOf(autoLoginBean.getData().getUser_id());
        SPStaticUtils.put(Constant.USER_ID, id);
        SPStaticUtils.put(Constant.USER_ACCOUNT, autoLoginBean.getData().getUser_mobile());
        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, autoLoginBean.getData().getClose_time_low());
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, autoLoginBean.getData().getClose_time_high());
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis());

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), autoLoginBean.getData().getClose_time_high(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2);
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), autoLoginBean.getData().getClose_time_low(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1);
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }

    }

    // 刷新用户信息
    public static void refreshUserInfo(RefreshSelfBean refreshSelfBean) {

        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, refreshSelfBean.getData().getClose_time_low());
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, refreshSelfBean.getData().getClose_time_high());
        SPStaticUtils.put(Constant.COIN_SUM, refreshSelfBean.getData().getJinbi_goldcoin());

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), refreshSelfBean.getData().getClose_time_high(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2);
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), refreshSelfBean.getData().getClose_time_low(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1);
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }

    }

    // 单独存储会员时间信息
    public static void storeVipInfo(String lowTime, String highTime) {
        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, lowTime);
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, highTime);

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), highTime, TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2);
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), lowTime, TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1);
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }
    }

    // 获取会员等级
    public static int getVipLevel(String lowTime, String highTime) {
        int vipLevel = 0;
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), highTime, TimeConstants.SEC) < 0) {
            vipLevel = 2;
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), lowTime, TimeConstants.SEC) < 0) {
            vipLevel = 1;
        } else {
            vipLevel = 0;
        }
        return vipLevel;
    }


    // 获取金币数量
    public static int getCoin() {
        return SPStaticUtils.getInt(Constant.COIN_SUM, 0);
    }

    // 更新金币数量
    public static void updateCoin(Integer coin) {
        SPStaticUtils.put(Constant.COIN_SUM, coin);
    }

    // 删除本地存储信息
    public static void deleteUserInfo() {
        SPStaticUtils.clear();
    }


}
