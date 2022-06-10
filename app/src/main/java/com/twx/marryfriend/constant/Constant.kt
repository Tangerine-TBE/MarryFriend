package com.twx.marryfriend.constant

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
object Constant {

    // 渠道
    const val CHANNEL = "channel"

    // 版本
    const val VERSION = "version"


    // 用户id
    const val USER_ID = "user_id"

    // 用户手机号
    const val USER_ACCOUNT = "user_account"

    // 会员等级
    const val USER_VIP_LEVEL = "user_vip_level"

    // 会员持续时间
    const val USER_VIP_TIME = "user_vip_time"

    // 用户登录状态
    const val USER_IS_LOGIN = "user_is_login"

    // 用户本次登陆时间
    const val USER_LOGIN_TIME = "user_login_time"


    // --------------------- 百度信息 ----------------------

    // 百度API access_token
    const val ACCESS_TOKEN = "access_token"

    // --------------------- 基础信息 ----------------------

    // 基础信息填写完毕
    const val BASE_INFO_FINISH = "base_info_finish"

    // 详细信息填写完毕
    const val DETAIL_INFO_FINISH = "detail_info_finish"

    // 昵称
    const val NICK_NAME = "nick_name"

    // 年龄
    const val AGE = "age"

    // 身高
    const val HEIGHT = "height"


    //---------------------- 教育信息 ----------------------

    // 数据库中学校总数量
    const val SCHOOL_SUM = "school_sum"

    // 数据库中是否储存学校数据
    const val SCHOOL_HAVE = "school_have"

    //---------------------- 职业收入情况 ----------------------

    // 数据库中行业总数量
    const val INDUSTRY_SUM = "industry_sum"

    // 数据库中是否存储行业数据
    const val INDUSTRY_HAVE = "industry_have"

    // 数据库中是否存储行业数据
    const val JOB_HAVE = "job_have"

    // 城市json数据
    const val CITY_JSON_DATE = "city_json_date"


    // -------------------  居住地和家乡界面  -----------------

    // 数据库中是否存储省市县数据
    const val CITY_HAVE = "city_have"

    // -------------------  敏感字符  -----------------
    const val BAN_TEXT = "ban_text"


    // -------------------  我的资料  -----------------

    // 是否实名认证
    const val IS_IDENTITY_VERIFY = "is_identity_verify"

    //         -----------  基础信息  ----------

    const val ME_NAME = "ME_name"  // 昵称

    const val ME_SEX = "ME_sex"  // 性别

    const val ME_AGE = "ME_age"  // 年龄

    const val ME_BIRTH = "ME_birth"  // 生日

    const val ME_HEIGHT = "ME_height"  // 身高

    const val ME_SCHOOL = "ME_school"  // 学校

    const val ME_EDU = "ME_edu"  // 学历

    const val ME_INDUSTRY_CODE = "ME_industry_code"  // 行业编码

    const val ME_INDUSTRY_NAME = "ME_industry_name"  // 行业名字

    const val ME_OCCUPATION_CODE = "ME_occupation_code"  // 岗位编码

    const val ME_OCCUPATION_NAME = "ME_occupation_name"  // 岗位名字

    const val ME_WORK_PROVINCE_CODE = "ME_work_province_code"  // 工作省份编码

    const val ME_WORK_CITY_CODE = "ME_work_city_code"  // 工作城市编码

    const val ME_WORK_CITY_NAME = "ME_work_city_name"  // 工作城市名字

    const val ME_HOME = "ME_home"  // 故乡

    const val ME_INCOME = "ME_income"  // 月收入

    const val ME_MARRY_STATE = "ME_marry_state"  // 当前婚况

    const val ME_INTRODUCE = "ME_introduce"  // 文字自我介绍

    const val ME_HOBBY = "ME_hobby"  // 日常兴趣爱好

    const val ME_TA = "ME_ta"  // 我心目中的Ta


    //         -----------  更多信息  ----------


    const val ME_HAVE_CHILD = "ME_have_child"  // 有没有孩子

    const val ME_WANT_CHILD = "ME_want_child"  // 是否想要孩子

    const val ME_HOUSE = "ME_house"  // 购房情况

    const val ME_CAR = "ME_car"  // 购车情况

    const val ME_WEIGHT = "ME_weight"  // 体重

    const val ME_BODY = "ME_body"  // 体型

    const val ME_SMOKE = "ME_smoke"  // 抽烟

    const val ME_DRINK = "ME_drink"  // 喝酒

    const val ME_BLOOD = "ME_blood"   // 血型

    const val ME_CONSTELLATION = "ME_constellation" // 星座

    const val ME_NATIONALITY = "ME_nationality" // 民族

    const val ME_LOVE_TARGET = "ME_love_target" // 恋爱目标

    const val ME_LOVE_TARGET_SHOW = "ME_love_target_show" // 恋爱目标是否展示

    const val ME_MARRY_TIME = "ME_marry_time"  // 结婚时间


    // -------------------  择偶信息（期望对方）  -----------------

    //         -----------  基础信息  ----------

    const val TA_AGE_MIN = "TA_age_min"  // 最小年龄

    const val TA_AGE_MAX = "TA_age_max"  // 最大年龄

    const val TA_HEIGHT_MIN = "TA_height_min"  // 最小身高

    const val TA_HEIGHT_MAX = "TA_height_max"  // 最大身高

    const val TA_INCOME = "TA_income"  // 月收入

    const val TA_EDU = "TA_edu"  // 学历

    const val TA_MARRY_STATE = "TA_marry_state"  // 婚况


    //         -----------  更多信息  ----------


    const val TA_BODY = "TA_body"  // 体型

    const val TA_WORK_PLACE = "TA_work_place"  // 期望工作地点

    const val TA_HAVE_CHILD = "TA_have_child"  // 有没有孩子

    const val TA_WANT_CHILD = "TA_want_child"  // 是否想要孩子

    const val TA_SMOKE = "TA_smoke"  // 是否吸烟

    const val TA_DRINK = "TA_drink"  // 是否喝酒

    const val TA_HAVE_PHOTO = "TA_have_photo"  // 有无照片

    const val TA_MARRY = "TA_marry"  // 何时结婚

}