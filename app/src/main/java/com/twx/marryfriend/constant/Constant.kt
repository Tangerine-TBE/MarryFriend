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

    // 通知权限提示
    const val NOTICE_PERMISSION_TIP = "notice_permisssion_tip"

    // 后台弹出界面权限提示
    const val BACKGROUND_PERMISSION_TIP = "background_permisssion_tip"

    // 是否第一次启动app
    const val FIRST_START = "first_start"

    // 离线状态点击推送消息进入app后的动作
    const val PUSH_ACTION = "push_action"


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

    // 百度API access_token  (头像审核)
    const val ACCESS_TOKEN = "access_token"

    // 百度API access_token  (生活照审核)
    const val LIFE_ACCESS_TOKEN = "life_access_token"

    // 百度API access_token  (身份证id 与姓名)
    const val ID_ACCESS_TOKEN = "id_access_token"

    // --------------------- 基础信息 ----------------------

    // 基础信息填写完毕
    const val BASE_INFO_FINISH = "base_info_finish"

    // 详细信息填写完毕
    const val DETAIL_INFO_FINISH = "detail_info_finish"

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

    // 设置页填写至第几个弹窗
    const val SET_INFO_DIALOG_SUM = "set_info_dialog_sum"


    // 头像
    const val ME_AVATAR = "ME_avatar"

    // 审核中的头像
    const val ME_AVATAR_AUDIT = "ME_avatar_audit"

    // 审核失败的头像
    const val ME_AVATAR_FAIL = "ME_avatar_fail"

    // 生活照
    const val ME_LIFE_PHOTO_ONE = "ME_life_photo_one" // 第一张生活照(url)

    const val ME_LIFE_PHOTO_ONE_TEXT = "ME_life_photo_one_text" // 第一张生活照描述

    const val ME_LIFE_PHOTO_ONE_ID = "ME_life_photo_one_id" // 第一张生活照ID

    const val ME_LIFE_PHOTO_ONE_AUDIT = "ME_life_photo_one_audit" // 第一张生活照审核状态


    const val ME_LIFE_PHOTO_TWO = "ME_life_photo_two"// 第二张生活照(url)

    const val ME_LIFE_PHOTO_TWO_TEXT = "ME_life_photo_two_text"// 第二张生活照描述

    const val ME_LIFE_PHOTO_TWO_ID = "ME_life_photo_two_id" // 第二张生活照ID

    const val ME_LIFE_PHOTO_TWO_AUDIT = "ME_life_photo_two_audit" // 第二张生活照审核状态


    const val ME_LIFE_PHOTO_THREE = "ME_life_photo_three"// 第三张生活照(url)

    const val ME_LIFE_PHOTO_THREE_TEXT = "ME_life_photo_three_text"// 第三张生活照描述

    const val ME_LIFE_PHOTO_THREE_ID = "ME_life_photo_three_id" // 第三张生活照ID

    const val ME_LIFE_PHOTO_THREE_AUDIT = "ME_life_photo_three_audit" // 第三张生活照审核状态


    const val ME_LIFE_PHOTO_FOUR = "ME_life_photo_four"// 第四张生活照(url)

    const val ME_LIFE_PHOTO_FOUR_TEXT = "ME_life_photo_four_text"// 第四张生活照描述

    const val ME_LIFE_PHOTO_FOUR_ID = "ME_life_photo_four_id" // 第四张生活照ID

    const val ME_LIFE_PHOTO_FOUR_AUDIT = "ME_life_photo_four_audit" // 第四张生活照审核状态


    const val ME_LIFE_PHOTO_FIVE = "ME_life_photo_five"// 第五张生活照(url)

    const val ME_LIFE_PHOTO_FIVE_TEXT = "ME_life_photo_five_text"// 第五张生活照描述

    const val ME_LIFE_PHOTO_FIVE_ID = "ME_life_photo_five_id" // 第五张生活照ID

    const val ME_LIFE_PHOTO_FIVE_AUDIT = "ME_life_photo_five_audit" // 第五张生活照审核状态


    const val IS_IDENTITY_VERIFY = "is_identity_verify"    // 是否实名认证

    const val TRUE_NAME = "true_name"    // 真实姓名

    const val TRUE_ID = "true_id"    // 真实ID


    //         -----------  基础信息  ----------

    const val ME_NAME = "ME_name"  // 昵称

    const val ME_SEX = "ME_sex"  // 性别

    const val ME_AGE = "ME_age"  // 年龄

    const val ME_BIRTH = "ME_birth"  // 生日

    const val ME_BIRTH_YEAR = "ME_birth_year"  // 生日选择的年

    const val ME_BIRTH_MONTH = "ME_birth_month"  // 生日选择的月

    const val ME_BIRTH_DAY = "ME_birth_day"  // 生日选择的日

    const val ME_HEIGHT = "ME_height"  // 身高

    const val ME_SCHOOL = "ME_school"  // 学校

    const val ME_EDU = "ME_edu"  // 学历

    const val ME_INDUSTRY_CODE = "ME_industry_code"  // 行业编码
    const val ME_INDUSTRY_NAME = "ME_industry_name"  // 行业名字
    const val ME_INDUSTRY_PICK = "ME_industry_pick"  // 行业滚轮选择选项
    const val ME_OCCUPATION_CODE = "ME_occupation_code"  // 岗位编码
    const val ME_OCCUPATION_NAME = "ME_occupation_name"  // 岗位名字
    const val ME_OCCUPATION_PICK = "ME_occupation_pick"  // 岗位滚轮选择选项

    const val ME_WORK = "ME_work"  // 工作区域

    const val ME_WORK_PROVINCE_CODE = "ME_work_province_code"  // 工作省份编码
    const val ME_WORK_PROVINCE_NAME = "ME_work_province_name"  // 工作省份名字
    const val ME_WORK_PROVINCE_PICK = "ME_work_province_pick"  // 工作省份滚轮选择选项
    const val ME_WORK_CITY_CODE = "ME_work_city_code"  // 工作城市编码
    const val ME_WORK_CITY_NAME = "ME_work_city_name"  // 工作城市名字
    const val ME_WORK_CITY_PICK = "ME_work_city_pick"      // 籍贯城市滚轮选择选项


    const val ME_HOME_PROVINCE_CODE = "ME_home_province_code"  // 籍贯省份编码
    const val ME_HOME_PROVINCE_NAME = "ME_home_province_name"  // 籍贯省份名字
    const val ME_HOME_PROVINCE_PICK = "ME_home_province_pick"  // 籍贯省份滚轮选择选项
    const val ME_HOME_CITY_CODE = "ME_home_city_code"      // 籍贯城市编码
    const val ME_HOME_CITY_NAME = "ME_home_city_name"      // 籍贯城市名字
    const val ME_HOME_CITY_PICK = "ME_home_city_pick"      // 籍贯城市滚轮选择选项

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

    const val TA_INCOME_MIN = "TA_income_min"  // 最低月收入
    const val TA_INCOME_MAX = "TA_income_max"  // 最高月收入

    const val TA_INCOME = "TA_income"  // 月收入

    const val TA_EDU = "TA_edu"  // 学历

    const val TA_MARRY_STATE = "TA_marry_state"  // 婚况


    //         -----------  更多信息  ----------


    const val TA_BODY = "TA_body"  // 体型

    // 仅用于择偶条件的条件存储
    const val TA_WORK_PLACE = "TA_work_place"  // 期望工作地点
    const val WANT_WORK_PROVINCE_CODE = "want_work_province_code"  // 期望工作省份编码
    const val WANT_WORK_PROVINCE_NAME = "want_work_province_name"  // 期望工作省份姓名
    const val WANT_WORK_CITY_CODE = "want_work_city_code"  // 期望工作省份编码
    const val WANT_WORK_CITY_NAME = "want_work_city_name"  // 期望工作省份姓名

    const val TA_WORK_PROVINCE_NAME = "TA_work_province_name"  // 期望工作省份
    const val TA_WORK_PROVINCE_CODE = "TA_work_province_code"  // 期望工作省份编码
    const val TA_WORK_PROVINCE_PICK = "TA_work_province_pick"  // 期望工作省份滚轮选择选项
    const val TA_WORK_CITY_NAME = "TA_work_city_name"  // 期望工作城市
    const val TA_WORK_CITY_CODE = "TA_work_city_code"  // 期望工作城市编码
    const val TA_WORK_CITY_PICK = "TA_work_city_pick"  // 期望工作城市滚轮选择选项

    const val TA_HAVE_CHILD = "TA_have_child"  // 有没有孩子

    const val TA_WANT_CHILD = "TA_want_child"  // 是否想要孩子

    const val TA_SMOKE = "TA_smoke"  // 是否吸烟

    const val TA_DRINK = "TA_drink"  // 是否喝酒

    const val TA_HAVE_PHOTO = "TA_have_photo"  // 有无照片

    const val TA_MARRY = "TA_marry"  // 何时结婚

    const val TA_HOUSE = "TA_house"  // 购房情况

    const val TA_CAR = "TA_car"  // 购车情况


    // -------------------  招呼语信息  -----------------

    const val ME_GREET = "ME_greet"  // 招呼语（文字）

    const val ME_VOICE = "ME_voice"  // 招呼语（语音）

    const val ME_VOICE_LONG = "ME_voice_long"  // 招呼语语言的长度

    const val ME_VOICE_NAME = "ME_voice_name"  // 招呼语语言的文件命名


    // --------------------------------------  动态模块  ------------------------------------

    const val HIDE_REPORT_TIP = "hide_report_tip" // 是否展示举报方式的RelativeLayout


    // --------------------------------------  支付模块  ------------------------------------

    const val COIN_SUM = "coin_sum"  // 目前金币

    const val CLOSE_TIME_LOW = "close_time_low"  // 	低级过期时间

    const val CLOSE_TIME_HIGH = "close_time_high"  // 	高级过期时间


    // --------------------------------------  设置模块  ------------------------------------


    // “我的”界面  红点相关
    const val LAST_VIEW_TIME_REQUEST = "last_view_time_request" // 上次去请求"看过"数据的时间

    const val LAST_FOCUS_TIME_REQUEST = "last_focus_time_request" // 上次去请求"关注"数据的时间

    const val LAST_LIKE_TIME_REQUEST = "last_like_time_request" // 上次去请求"点赞"数据的时间

    const val LAST_COMMENT_TIME_REQUEST = "last_comment_time_request" // 上次去请求"评论"数据的时间


    // 界面内显示 是否已读 相关

    const val LAST_LIKE_ME_TIME_REQUEST = "last_like_me_time_request" // 上次去请求"谁点赞我"数据的时间

    const val LAST_LIKE_OTHER_TIME_REQUEST = "last_like_other_time_request" // 上次去请求"我点赞谁"数据的时间

    const val LAST_COMMENT_ME_TIME_REQUEST = "last_comment_me_time_request" // 上次去请求"谁评论我"数据的时间

    const val LAST_COMMENT_OTHER_TIME_REQUEST =
        "last_comment_other_time_request" // 上次去请求"我评论谁"数据的时间


    const val HIDE_STATE = "hide_state" // 是否隐藏在线状态

    const val HIDE_VIP = "hide_vip"  // 是否隐藏会员标识

    const val DATA_REVIEW_TIP = "data_review_tip"          // 资料审核通知(头像,声音,实名认证,动态相册,生活照)
    const val TA_LIKE_NOW_TIP = "ta_like_now_tip"          // TA刚刚喜欢了你
    const val TA_COMMENT_TIP = "ta_comment_tip"            // TA评论了你的动态
    const val TA_DIANZAN_TIP = "ta_dianzan_tip"            // TA点赞了你的动态
    const val TA_LOOK_NOW_TIP = "ta_look_now_tip"          // TA刚看了你的资料
    const val LIKE_TA_ONLINE_TIP = "like_ta_online_tip"    // 你喜欢的TA上线了
    const val LIKE_ALL_ONLINE_TIP = "like_all_online_tip"  // 和你相互喜欢的Ta上线了

    const val LIKE_ALL_TIP = "like_all_tip"                // 互相喜欢(相互喜欢配对成功后提醒)
    const val GET_GIFT = "get_gift"                  // 收到礼物


    // 外部SDK常量

    const val DEVICE_TOKEN = "device_Token"


}