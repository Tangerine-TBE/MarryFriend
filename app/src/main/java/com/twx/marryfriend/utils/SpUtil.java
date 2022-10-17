package com.twx.marryfriend.utils;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.twx.marryfriend.bean.AutoLoginBean;
import com.twx.marryfriend.bean.PhoneLoginBean;
import com.twx.marryfriend.bean.vip.RefreshSelfBean;
import com.twx.marryfriend.constant.Constant;

public class SpUtil {

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

    public static void updateAvatar() {
        SPStaticUtils.put(Constant.ME_AVATAR, "");
        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "");
        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "");
    }

    public static void updateAvatar(String avatarUrl, Integer avatarState) {

        if (avatarState == 0) {
            SPStaticUtils.put(Constant.ME_AVATAR, "");
            SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, avatarUrl);
            SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "");
        } else {
            SPStaticUtils.put(Constant.ME_AVATAR, avatarUrl);
            SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "");
            SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "");
        }

    }


    // 删除本地存储信息
    public static void deleteUserInfo() {
        SPStaticUtils.clear();

        SPStaticUtils.put(Constant.FIRST_START, false);


//        SPStaticUtils.remove(Constant.USER_ID);
//
//        SPStaticUtils.remove(Constant.USER_ACCOUNT);
//
//        SPStaticUtils.remove(Constant.USER_VIP_LEVEL);
//
//        SPStaticUtils.remove(Constant.USER_VIP_TIME);
//
//        SPStaticUtils.remove(Constant.USER_IS_LOGIN);
//
//        SPStaticUtils.remove(Constant.USER_LOGIN_TIME);
//
//
//        SPStaticUtils.remove(Constant.BASE_INFO_FINISH);
//
//        SPStaticUtils.remove(Constant.DETAIL_INFO_FINISH);
//
//        SPStaticUtils.remove(Constant.SET_INFO_DIALOG_SUM);
//
//        SPStaticUtils.remove(Constant.ME_AVATAR);
//
//        SPStaticUtils.remove(Constant.ME_AVATAR_AUDIT);
//
//        SPStaticUtils.remove(Constant.ME_AVATAR_FAIL);
//
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_ONE);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_ONE_TEXT);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_ONE_ID);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_ONE_AUDIT);
//
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_TWO);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_TWO_TEXT);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_TWO_ID);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_TWO_AUDIT);
//
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_THREE);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_THREE_TEXT);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_THREE_ID);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_THREE_AUDIT);
//
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FOUR);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FOUR_TEXT);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FOUR_ID);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FOUR_AUDIT);
//
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FIVE);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FIVE_TEXT);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FIVE_ID);
//
//        SPStaticUtils.remove(Constant.ME_LIFE_PHOTO_FIVE_AUDIT);
//
//
//        SPStaticUtils.remove(Constant.IS_IDENTITY_VERIFY);
//
//        SPStaticUtils.remove(Constant.TRUE_NAME);
//
//        SPStaticUtils.remove(Constant.TRUE_ID);
//
//
//        SPStaticUtils.remove(Constant.ME_NAME);
//
//
//        SPStaticUtils.remove(Constant.ME_SEX);
//
//        SPStaticUtils.remove(Constant.ME_AGE);
//
//        SPStaticUtils.remove(Constant.ME_BIRTH);
//
//        SPStaticUtils.remove(Constant.ME_BIRTH_YEAR);
//
//        SPStaticUtils.remove(Constant.ME_BIRTH_MONTH);
//
//        SPStaticUtils.remove(Constant.ME_BIRTH_DAY);
//
//
//        SPStaticUtils.remove(Constant.ME_HEIGHT);
//
//        SPStaticUtils.remove(Constant.ME_SCHOOL);
//
//        SPStaticUtils.remove(Constant.ME_EDU);
//
//
//        SPStaticUtils.remove(Constant.ME_INDUSTRY_CODE);
//
//        SPStaticUtils.remove(Constant.ME_INDUSTRY_NAME);
//
//        SPStaticUtils.remove(Constant.ME_INDUSTRY_PICK);
//
//        SPStaticUtils.remove(Constant.ME_OCCUPATION_CODE);
//
//        SPStaticUtils.remove(Constant.ME_OCCUPATION_NAME);
//
//        SPStaticUtils.remove(Constant.ME_OCCUPATION_PICK);
//
//
//        SPStaticUtils.remove(Constant.ME_WORK);
//
//
//        SPStaticUtils.remove(Constant.ME_WORK_PROVINCE_CODE);
//
//        SPStaticUtils.remove(Constant.ME_WORK_PROVINCE_NAME);
//
//        SPStaticUtils.remove(Constant.ME_WORK_PROVINCE_PICK);
//
//        SPStaticUtils.remove(Constant.ME_WORK_CITY_CODE);
//
//        SPStaticUtils.remove(Constant.ME_WORK_CITY_NAME);
//
//        SPStaticUtils.remove(Constant.ME_WORK_CITY_PICK);
//
//
//        SPStaticUtils.remove(Constant.ME_HOME_PROVINCE_CODE);
//
//        SPStaticUtils.remove(Constant.ME_HOME_PROVINCE_NAME);
//
//        SPStaticUtils.remove(Constant.ME_HOME_PROVINCE_PICK);
//
//        SPStaticUtils.remove(Constant.ME_HOME_CITY_CODE);
//
//        SPStaticUtils.remove(Constant.ME_HOME_CITY_NAME);
//
//        SPStaticUtils.remove(Constant.ME_HOME_CITY_PICK);
//
//
//        SPStaticUtils.remove(Constant.ME_INCOME);
//
//        SPStaticUtils.remove(Constant.ME_MARRY_STATE);
//
//        SPStaticUtils.remove(Constant.ME_INTRODUCE);
//
//        SPStaticUtils.remove(Constant.ME_HOBBY);
//
//        SPStaticUtils.remove(Constant.ME_TA);
//
//
//        SPStaticUtils.remove(Constant.ME_HAVE_CHILD);
//
//        SPStaticUtils.remove(Constant.ME_WANT_CHILD);
//
//        SPStaticUtils.remove(Constant.ME_HOUSE);
//
//        SPStaticUtils.remove(Constant.ME_CAR);
//
//        SPStaticUtils.remove(Constant.ME_WEIGHT);
//
//        SPStaticUtils.remove(Constant.ME_BODY);
//
//        SPStaticUtils.remove(Constant.ME_SMOKE);
//
//        SPStaticUtils.remove(Constant.ME_DRINK);
//
//        SPStaticUtils.remove(Constant.ME_BLOOD);
//
//        SPStaticUtils.remove(Constant.ME_CONSTELLATION);
//
//        SPStaticUtils.remove(Constant.ME_NATIONALITY);
//
//        SPStaticUtils.remove(Constant.ME_LOVE_TARGET);
//
//        SPStaticUtils.remove(Constant.ME_LOVE_TARGET_SHOW);
//
//        SPStaticUtils.remove(Constant.ME_MARRY_TIME);
//
//
//        SPStaticUtils.remove(Constant.TA_AGE_MIN);
//
//        SPStaticUtils.remove(Constant.TA_AGE_MAX);
//
//        SPStaticUtils.remove(Constant.TA_HEIGHT_MIN);
//
//        SPStaticUtils.remove(Constant.TA_HEIGHT_MAX);
//
//
//        SPStaticUtils.remove(Constant.TA_INCOME_MIN);
//
//        SPStaticUtils.remove(Constant.TA_INCOME_MAX);
//
//        SPStaticUtils.remove(Constant.TA_INCOME);
//
//        SPStaticUtils.remove(Constant.TA_EDU);
//
//        SPStaticUtils.remove(Constant.TA_MARRY_STATE);
//
//
//        SPStaticUtils.remove(Constant.TA_BODY);
//
//        SPStaticUtils.remove(Constant.TA_WORK_PLACE);
//
//        SPStaticUtils.remove(Constant.WANT_WORK_PROVINCE_CODE);
//
//        SPStaticUtils.remove(Constant.WANT_WORK_PROVINCE_NAME);
//
//        SPStaticUtils.remove(Constant.WANT_WORK_CITY_CODE);
//
//        SPStaticUtils.remove(Constant.WANT_WORK_CITY_NAME);
//
//
//        SPStaticUtils.remove(Constant.TA_WORK_PROVINCE_NAME);
//
//        SPStaticUtils.remove(Constant.TA_WORK_PROVINCE_CODE);
//
//        SPStaticUtils.remove(Constant.TA_WORK_PROVINCE_PICK);
//
//        SPStaticUtils.remove(Constant.TA_WORK_CITY_NAME);
//
//        SPStaticUtils.remove(Constant.TA_WORK_CITY_CODE);
//
//        SPStaticUtils.remove(Constant.TA_WORK_CITY_PICK);
//
//
//        SPStaticUtils.remove(Constant.TA_HAVE_CHILD);
//
//        SPStaticUtils.remove(Constant.TA_WANT_CHILD);
//
//        SPStaticUtils.remove(Constant.TA_SMOKE);
//
//        SPStaticUtils.remove(Constant.TA_DRINK);
//
//        SPStaticUtils.remove(Constant.TA_HAVE_PHOTO);
//
//        SPStaticUtils.remove(Constant.TA_MARRY);
//
//        SPStaticUtils.remove(Constant.TA_HOUSE);
//
//        SPStaticUtils.remove(Constant.TA_CAR);
//
//
//        SPStaticUtils.remove(Constant.ME_GREET);
//
//        SPStaticUtils.remove(Constant.ME_VOICE);
//
//        SPStaticUtils.remove(Constant.ME_VOICE_LONG);
//
//        SPStaticUtils.remove(Constant.ME_VOICE_NAME);
//
//
//        SPStaticUtils.remove(Constant.HIDE_REPORT_TIP);
//
//        SPStaticUtils.remove(Constant.COIN_SUM);
//
//        SPStaticUtils.remove(Constant.CLOSE_TIME_LOW);
//
//        SPStaticUtils.remove(Constant.CLOSE_TIME_HIGH);
//
//
//        SPStaticUtils.remove(Constant.LAST_VIEW_TIME_REQUEST);
//
//        SPStaticUtils.remove(Constant.LAST_FOCUS_TIME_REQUEST);
//
//        SPStaticUtils.remove(Constant.LAST_LIKE_TIME_REQUEST);
//
//        SPStaticUtils.remove(Constant.LAST_COMMENT_TIME_REQUEST);
//
//
//        SPStaticUtils.remove(Constant.LAST_LIKE_ME_TIME_REQUEST);
//
//        SPStaticUtils.remove(Constant.LAST_LIKE_OTHER_TIME_REQUEST);
//
//        SPStaticUtils.remove(Constant.LAST_COMMENT_ME_TIME_REQUEST);
//
//        SPStaticUtils.remove(Constant.LAST_COMMENT_OTHER_TIME_REQUEST);
//
//
//        SPStaticUtils.remove(Constant.HIDE_STATE);
//
//        SPStaticUtils.remove(Constant.HIDE_VIP);
//
//
//        SPStaticUtils.remove(Constant.DATA_REVIEW_TIP);
//
//        SPStaticUtils.remove(Constant.TA_LIKE_NOW_TIP);
//
//        SPStaticUtils.remove(Constant.TA_COMMENT_TIP);
//
//        SPStaticUtils.remove(Constant.TA_DIANZAN_TIP);
//
//        SPStaticUtils.remove(Constant.TA_LOOK_NOW_TIP);
//
//        SPStaticUtils.remove(Constant.LIKE_TA_ONLINE_TIP);
//
//        SPStaticUtils.remove(Constant.LIKE_ALL_ONLINE_TIP);
//
//
//        SPStaticUtils.remove(Constant.LIKE_ALL_TIP);
//
//        SPStaticUtils.remove(Constant.GET_GIFT);


    }


}
