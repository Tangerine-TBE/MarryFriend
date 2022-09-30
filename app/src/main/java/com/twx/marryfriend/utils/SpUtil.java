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

        SPStaticUtils.put(Constant.ME_NAME, loginBean.getData().getNick());
        SPStaticUtils.put(Constant.ME_AGE, loginBean.getData().getAge());
        SPStaticUtils.put(Constant.ME_SEX, loginBean.getData().getSex());
        SPStaticUtils.put(Constant.COIN_SUM, loginBean.getData().getJinbi_goldcoin());

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), loginBean.getData().getClose_time_high(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2);
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(), loginBean.getData().getClose_time_low(), TimeConstants.SEC) < 0) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1);
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0);
        }

        SPStaticUtils.put(Constant.ME_HOBBY, loginBean.getData().getDaily_hobbies());
        SPStaticUtils.put(Constant.ME_GREET, loginBean.getData().getZhaohuyu_content());
        SPStaticUtils.put(Constant.ME_INTRODUCE, loginBean.getData().getIntroduce_self());

        SPStaticUtils.put(Constant.ME_VOICE, loginBean.getData().getVoice_url());

        SPStaticUtils.put(Constant.ME_HAVE_CHILD, loginBean.getData().getChild_had());

        SPStaticUtils.put(Constant.ME_WANT_CHILD, loginBean.getData().getWant_child());

        SPStaticUtils.put(Constant.ME_HOUSE, loginBean.getData().getBuy_house());

        SPStaticUtils.put(Constant.ME_CAR, loginBean.getData().getBuy_car());

        SPStaticUtils.put(Constant.ME_INTRODUCE, loginBean.getData().getIntroduce_self());


//        "headface_count"         : [里面是个大数组，每行记录],
//        "photos_count"           : [里面是个大数组，每行记录],

        //爱好


//                "hometown_province_num"  :0,
//                "hometown_city_num"      :0,

//                "is_smoking"             :0,
//                "industry_num"           :0,
//                "occupation_num"         :0,


    }

    public static void saveUserInfo(AutoLoginBean autoLoginBean) {
        SPStaticUtils.put(Constant.USER_IS_LOGIN, true);
        String id = String.valueOf(autoLoginBean.getData().getUser_id());
        SPStaticUtils.put(Constant.USER_ID, id);

        SPStaticUtils.put(Constant.USER_ACCOUNT, autoLoginBean.getData().getUser_mobile());
        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, autoLoginBean.getData().getClose_time_low());
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, autoLoginBean.getData().getClose_time_high());
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis());

        SPStaticUtils.put(Constant.ME_NAME, autoLoginBean.getData().getNick());
        SPStaticUtils.put(Constant.ME_AGE, autoLoginBean.getData().getAge());
        SPStaticUtils.put(Constant.ME_SEX, autoLoginBean.getData().getSex());
        SPStaticUtils.put(Constant.COIN_SUM, autoLoginBean.getData().getJinbi_goldcoin());

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
