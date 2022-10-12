package com.twx.marryfriend.utils

import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.TimeUtils
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant

/**
 * @author: Administrator
 * @date: 2022/9/26
 */
object SpLoginUtil {

    fun saveUserInfo(loginBean: PhoneLoginBean) {


        SPStaticUtils.put(Constant.USER_IS_LOGIN, true)
        val id = loginBean.data.user_id.toString()
        SPStaticUtils.put(Constant.USER_ID, id)
        SPStaticUtils.put(Constant.USER_ACCOUNT, loginBean.data.user_mobile)
        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, loginBean.data.close_time_low)
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, loginBean.data.close_time_high)
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis())
        SPStaticUtils.put(Constant.ME_NAME, loginBean.data.nick)
        SPStaticUtils.put(Constant.ME_AGE, loginBean.data.age)
        SPStaticUtils.put(Constant.ME_SEX, loginBean.data.sex)
        SPStaticUtils.put(Constant.COIN_SUM, loginBean.data.jinbi_goldcoin)

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(),
                loginBean.data.close_time_high,
                TimeConstants.SEC) < 0
        ) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2)
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(),
                loginBean.data.close_time_low,
                TimeConstants.SEC) < 0
        ) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1)
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0)
        }


        SPStaticUtils.put(Constant.ME_HOBBY, loginBean.data.daily_hobbies)
        SPStaticUtils.put(Constant.ME_GREET, loginBean.data.zhaohuyu_content)
        SPStaticUtils.put(Constant.ME_INTRODUCE, loginBean.data.introduce_self)
        SPStaticUtils.put(Constant.ME_VOICE, loginBean.data.voice_url)
        SPStaticUtils.put(Constant.ME_HAVE_CHILD, loginBean.data.child_had)
        SPStaticUtils.put(Constant.ME_WANT_CHILD, loginBean.data.want_child)
        SPStaticUtils.put(Constant.ME_HOUSE, loginBean.data.buy_house)
        SPStaticUtils.put(Constant.ME_CAR, loginBean.data.buy_car)
        SPStaticUtils.put(Constant.ME_SMOKE, loginBean.data.is_smoking)

        SPStaticUtils.put(Constant.WANT_WORK_PROVINCE_CODE,
            loginBean.data.hometown_province_num.toString())
        SPStaticUtils.put(Constant.WANT_WORK_PROVINCE_NAME, loginBean.data.hometown_province_str)
        SPStaticUtils.put(Constant.WANT_WORK_CITY_CODE, loginBean.data.hometown_city_num.toString())
        SPStaticUtils.put(Constant.WANT_WORK_CITY_NAME, loginBean.data.hometown_city_str)


        SPStaticUtils.put(Constant.ME_INDUSTRY_CODE, loginBean.data.industry_num)
        SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, loginBean.data.industry_str)
        SPStaticUtils.put(Constant.ME_OCCUPATION_CODE, loginBean.data.occupation_num)
        SPStaticUtils.put(Constant.ME_OCCUPATION_NAME, loginBean.data.occupation_str)



        when (loginBean.data.headface_count.size) {
            0 -> {
                SPStaticUtils.put(Constant.ME_AVATAR, "")
                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
            }
            1 -> {
                when (loginBean.data.headface_count[0].status) {
                    0 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                            loginBean.data.headface_count[0].image_url)
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                    }
                    1 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR,
                            loginBean.data.headface_count[0].image_url)
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                    }
                    2 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                            loginBean.data.headface_count[0].image_url)
                    }
                }
            }
            2 -> {
                when (loginBean.data.headface_count[0].status) {
                    0 -> {
                        // 第一张为审核中
                        when (loginBean.data.headface_count[1].status) {
                            0 -> {
                                // 第二张为审核中
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    loginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            1 -> {
                                // 第二张为审核通过
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    loginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    loginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")

                            }
                            2 -> {
                                // 第二张为审核拒绝
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    loginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    loginBean.data.headface_count[1].image_url)
                            }
                        }
                    }
                    1 -> {
                        // 第一张为审核通过

                        when (loginBean.data.headface_count[1].status) {
                            0 -> {
                                // 第二张为审核中
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    loginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    loginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            1 -> {
                                // 第二张为审核通过
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    loginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            2 -> {
                                // 第二张为审核拒绝
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    loginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    loginBean.data.headface_count[1].image_url)
                            }
                        }
                    }
                    2 -> {
                        // 第一张为审核拒绝
                        when (loginBean.data.headface_count[1].status) {
                            0 -> {
                                // 第二张为审核中
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    loginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    loginBean.data.headface_count[0].image_url)
                            }
                            1 -> {
                                // 第二张为审核通过
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    loginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    loginBean.data.headface_count[0].image_url)
                            }
                            2 -> {
                                // 第二张为审核拒绝
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    loginBean.data.headface_count[0].image_url)
                            }
                        }
                    }
                }
            }
        }

        when (loginBean.data.photos_count.size) {
            0 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")

            }
            1 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    loginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    loginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    loginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    loginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            2 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    loginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    loginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    loginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    loginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    loginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    loginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    loginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    loginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            3 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    loginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    loginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    loginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    loginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    loginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    loginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    loginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    loginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                    loginBean.data.photos_count[2].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                    loginBean.data.photos_count[2].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                    loginBean.data.photos_count[2].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                    loginBean.data.photos_count[2].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            4 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    loginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    loginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    loginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    loginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    loginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    loginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    loginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    loginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                    loginBean.data.photos_count[2].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                    loginBean.data.photos_count[2].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                    loginBean.data.photos_count[2].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                    loginBean.data.photos_count[2].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR,
                    loginBean.data.photos_count[3].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT,
                    loginBean.data.photos_count[3].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID,
                    loginBean.data.photos_count[3].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT,
                    loginBean.data.photos_count[3].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            5 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    loginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    loginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    loginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    loginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    loginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    loginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    loginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    loginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                    loginBean.data.photos_count[2].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                    loginBean.data.photos_count[2].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                    loginBean.data.photos_count[2].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                    loginBean.data.photos_count[2].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR,
                    loginBean.data.photos_count[3].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT,
                    loginBean.data.photos_count[3].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID,
                    loginBean.data.photos_count[3].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT,
                    loginBean.data.photos_count[3].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE,
                    loginBean.data.photos_count[4].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT,
                    loginBean.data.photos_count[4].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID,
                    loginBean.data.photos_count[4].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT,
                    loginBean.data.photos_count[4].status.toString())
            }
        }


        SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, loginBean.data.identity_status == 1)
        SPStaticUtils.put(Constant.TRUE_NAME, loginBean.data.identity_name)
        SPStaticUtils.put(Constant.TRUE_ID, loginBean.data.identity_number)


        val interdictionBean = InterdictionBean().also {
            it.blacklist_permanent = loginBean.data.blacklist_permanent
            it.blacklist_close_time = loginBean.data.blacklist_close_time
            it.blacklist_status = loginBean.data.blacklist_status
        }

        InterdictionBean.putInterdictionState(interdictionBean)


    }


    fun saveUserInfo(autoLoginBean: AutoLoginBean) {


        SPStaticUtils.put(Constant.USER_IS_LOGIN, true)
        val id = autoLoginBean.data.user_id.toString()
        SPStaticUtils.put(Constant.USER_ID, id)
        SPStaticUtils.put(Constant.USER_ACCOUNT, autoLoginBean.data.user_mobile)
        SPStaticUtils.put(Constant.CLOSE_TIME_LOW, autoLoginBean.data.close_time_low)
        SPStaticUtils.put(Constant.CLOSE_TIME_HIGH, autoLoginBean.data.close_time_high)
        SPStaticUtils.put(Constant.USER_LOGIN_TIME, System.currentTimeMillis())
        SPStaticUtils.put(Constant.ME_NAME, autoLoginBean.data.nick)
        SPStaticUtils.put(Constant.ME_AGE, autoLoginBean.data.age)
        SPStaticUtils.put(Constant.ME_SEX, autoLoginBean.data.sex)
        SPStaticUtils.put(Constant.COIN_SUM, autoLoginBean.data.jinbi_goldcoin)

        // 更新会员等级
        if (TimeUtils.getTimeSpan(TimeUtils.getNowString(),
                autoLoginBean.data.close_time_high,
                TimeConstants.SEC) < 0
        ) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 2)
        } else if (TimeUtils.getTimeSpan(TimeUtils.getNowString(),
                autoLoginBean.data.close_time_low,
                TimeConstants.SEC) < 0
        ) {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 1)
        } else {
            SPStaticUtils.put(Constant.USER_VIP_LEVEL, 0)
        }


        SPStaticUtils.put(Constant.ME_HOBBY, autoLoginBean.data.daily_hobbies)
        SPStaticUtils.put(Constant.ME_GREET, autoLoginBean.data.zhaohuyu_content)
        SPStaticUtils.put(Constant.ME_INTRODUCE, autoLoginBean.data.introduce_self)
        SPStaticUtils.put(Constant.ME_VOICE, autoLoginBean.data.voice_url)
        SPStaticUtils.put(Constant.ME_HAVE_CHILD, autoLoginBean.data.child_had)
        SPStaticUtils.put(Constant.ME_WANT_CHILD, autoLoginBean.data.want_child)
        SPStaticUtils.put(Constant.ME_HOUSE, autoLoginBean.data.buy_house)
        SPStaticUtils.put(Constant.ME_CAR, autoLoginBean.data.buy_car)
        SPStaticUtils.put(Constant.ME_SMOKE, autoLoginBean.data.is_smoking)


        SPStaticUtils.put(Constant.WANT_WORK_PROVINCE_CODE,
            autoLoginBean.data.hometown_province_num.toString())
        SPStaticUtils.put(Constant.WANT_WORK_PROVINCE_NAME,
            autoLoginBean.data.hometown_province_str)
        SPStaticUtils.put(Constant.WANT_WORK_CITY_CODE,
            autoLoginBean.data.hometown_city_num.toString())
        SPStaticUtils.put(Constant.WANT_WORK_CITY_NAME, autoLoginBean.data.hometown_city_str)


        SPStaticUtils.put(Constant.ME_INDUSTRY_CODE, autoLoginBean.data.industry_num)
        SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, autoLoginBean.data.industry_str)
        SPStaticUtils.put(Constant.ME_OCCUPATION_CODE, autoLoginBean.data.occupation_num)
        SPStaticUtils.put(Constant.ME_OCCUPATION_NAME, autoLoginBean.data.occupation_str)

        when (autoLoginBean.data.headface_count.size) {
            0 -> {
                SPStaticUtils.put(Constant.ME_AVATAR, "")
                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
            }
            1 -> {
                when (autoLoginBean.data.headface_count[0].status) {
                    0 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                            autoLoginBean.data.headface_count[0].image_url)
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                    }
                    1 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR,
                            autoLoginBean.data.headface_count[0].image_url)
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                    }
                    2 -> {
                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                        SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                            autoLoginBean.data.headface_count[0].image_url)
                    }
                }
            }
            2 -> {
                when (autoLoginBean.data.headface_count[0].status) {
                    0 -> {
                        // 第一张为审核中
                        when (autoLoginBean.data.headface_count[1].status) {
                            0 -> {
                                // 第二张为审核中
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    autoLoginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            1 -> {
                                // 第二张为审核通过
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    autoLoginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    autoLoginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")

                            }
                            2 -> {
                                // 第二张为审核拒绝
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    autoLoginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    autoLoginBean.data.headface_count[1].image_url)
                            }
                        }
                    }
                    1 -> {
                        // 第一张为审核通过

                        when (autoLoginBean.data.headface_count[1].status) {
                            0 -> {
                                // 第二张为审核中
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    autoLoginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    autoLoginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            1 -> {
                                // 第二张为审核通过
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    autoLoginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL, "")
                            }
                            2 -> {
                                // 第二张为审核拒绝
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    autoLoginBean.data.headface_count[0].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    autoLoginBean.data.headface_count[1].image_url)
                            }
                        }
                    }
                    2 -> {
                        // 第一张为审核拒绝
                        when (autoLoginBean.data.headface_count[1].status) {
                            0 -> {
                                // 第二张为审核中
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT,
                                    autoLoginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    autoLoginBean.data.headface_count[0].image_url)
                            }
                            1 -> {
                                // 第二张为审核通过
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR,
                                    autoLoginBean.data.headface_count[1].image_url)
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    autoLoginBean.data.headface_count[0].image_url)
                            }
                            2 -> {
                                // 第二张为审核拒绝
                                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, "")
                                SPStaticUtils.put(Constant.ME_AVATAR, "")
                                SPStaticUtils.put(Constant.ME_AVATAR_FAIL,
                                    autoLoginBean.data.headface_count[0].image_url)
                            }
                        }
                    }
                }
            }
        }

        when (autoLoginBean.data.photos_count.size) {
            0 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")

            }
            1 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    autoLoginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    autoLoginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    autoLoginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    autoLoginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            2 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    autoLoginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    autoLoginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    autoLoginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    autoLoginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    autoLoginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    autoLoginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    autoLoginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    autoLoginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            3 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    autoLoginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    autoLoginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    autoLoginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    autoLoginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    autoLoginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    autoLoginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    autoLoginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    autoLoginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                    autoLoginBean.data.photos_count[2].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                    autoLoginBean.data.photos_count[2].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                    autoLoginBean.data.photos_count[2].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                    autoLoginBean.data.photos_count[2].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            4 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    autoLoginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    autoLoginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    autoLoginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    autoLoginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    autoLoginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    autoLoginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    autoLoginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    autoLoginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                    autoLoginBean.data.photos_count[2].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                    autoLoginBean.data.photos_count[2].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                    autoLoginBean.data.photos_count[2].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                    autoLoginBean.data.photos_count[2].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR,
                    autoLoginBean.data.photos_count[3].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT,
                    autoLoginBean.data.photos_count[3].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID,
                    autoLoginBean.data.photos_count[3].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT,
                    autoLoginBean.data.photos_count[3].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")
            }
            5 -> {
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE,
                    autoLoginBean.data.photos_count[0].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT,
                    autoLoginBean.data.photos_count[0].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID,
                    autoLoginBean.data.photos_count[0].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT,
                    autoLoginBean.data.photos_count[0].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO,
                    autoLoginBean.data.photos_count[1].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT,
                    autoLoginBean.data.photos_count[1].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID,
                    autoLoginBean.data.photos_count[1].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                    autoLoginBean.data.photos_count[1].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE,
                    autoLoginBean.data.photos_count[2].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT,
                    autoLoginBean.data.photos_count[2].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID,
                    autoLoginBean.data.photos_count[2].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                    autoLoginBean.data.photos_count[2].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR,
                    autoLoginBean.data.photos_count[3].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT,
                    autoLoginBean.data.photos_count[3].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID,
                    autoLoginBean.data.photos_count[3].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT,
                    autoLoginBean.data.photos_count[3].status.toString())

                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE,
                    autoLoginBean.data.photos_count[4].image_url)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT,
                    autoLoginBean.data.photos_count[4].content)
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID,
                    autoLoginBean.data.photos_count[4].id.toString())
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT,
                    autoLoginBean.data.photos_count[4].status.toString())
            }
        }


        SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, autoLoginBean.data.identity_status == 1)
        SPStaticUtils.put(Constant.TRUE_NAME, autoLoginBean.data.identity_name)
        SPStaticUtils.put(Constant.TRUE_ID, autoLoginBean.data.identity_number)

        val interdictionBean = InterdictionBean().also {
            it.blacklist_permanent = autoLoginBean.data.blacklist_permanent
            it.blacklist_close_time = autoLoginBean.data.blacklist_close_time
            it.blacklist_status = autoLoginBean.data.blacklist_status
        }

        InterdictionBean.putInterdictionState(interdictionBean)

    }

}