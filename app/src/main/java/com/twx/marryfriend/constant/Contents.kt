package com.twx.marryfriend.constant

object Contents {


    // 域名
    const val USER_URL =
//        "http://api.aijiaou.com"
        "http://test.aisou.club"


    //  百度api所需的 Access Token 所需域名
    const val BAIDU_API_URL = "https://aip.baidubce.com"


    //  百度地图api 所需的域名
    const val BAIDU_MAP_URL = "https://api.map.baidu.com"

    //  高德地图api 所需的域名
    const val GAODE_MAP_URL = "https://restapi.amap.com"


    // 常用 token值
    const val SERVICE = "service" //接口名
    const val SIGNATURE = "signature" //验证码
    const val NONCE = "nonce" //随机数
    const val TIMESTAMP = "timestamp" //时间戳
    const val TOKEN = "x389fh^feahykge" //token值


    // 发送手机验证码
    const val GET_VERIFY = "LoginRegister/sendCode"

    // 验证码登录注册
    const val PHONE_LOGIN = "LoginRegister/mobileLogister"

    // 阿里云一键登录
    const val AUTO_LOGIN = "LoginRegister/onekeyLogin"

    // 修改基础信息
    const val UPDATE_BASE_INFO = "LoginRegister/updateBaseInfo"

    // 修改更多信息
    const val UPDATE_MORE_INFO = "LoginRegister/updateMoreInfo"

    // 修改择偶需求
    const val UPDATE_DEMAND_INFO = "LoginRegister/updateDemandInfo"

    // 修改认证信息
    const val UPDATE_VERIFY_INFO = "LoginRegister/updateVerifyInfo"

    // 修改招呼语信息
    const val UPDATE_GREET_INFO = "LoginRegister/updateZhaohuyuInfo"

    // 增加择偶省市要求列表
    const val PLUS_DEMAND_ADDRESS = "LoginRegister/plusDemandShengshi"

    // 增加择偶省市要求列表
    const val GET_DEMAND_ADDRESS = "LoginRegister/demandShengshiList"


    // 获取 招呼语信息
    const val GET_GREET_INFO = "LoginRegister/getZhaohuyu"

    // 五个一起获取
    const val GET_FIVE_INFO = "LoginRegister/getFive"

    //查看列表(头像,三张,相册)
    const val PHOTO_LIST = "LoginRegister/photoList"

    //图片上传 生活照
    const val UPLOAD_PHOTO = "LoginRegister/uploadPhoto"

    //图片上传 头像
    const val UPLOAD_AVATAR = "LoginRegister/uploadHeadface"

    //查看头像
    const val VIEW_HEADFACE = "LoginRegister/viewHeadface"

    // 删除图片(生活照)
    const val DELETE_PHOTO = "LoginRegister/deletePhoto"

    // 更新资料完善度
    const val UPDATE_PROPORTION = "LoginRegister/proportionUpdate"

    // 最近来访 -- 谁看过我列表
    const val WHO_SEE_ME = "UserCenter/whoSeenMeList"

    // 最近来访 -- 我看过谁列表
    const val ME_SEE_WHO = "UserCenter/meSeenWhoList"

    // 我关注其它人，列表
    const val ME_FOCUS_WHO = "UserCenter/mineFocusOtherList"

    // 其它人关注我，列表
    const val WHO_FOCUS_ME = "UserCenter/otherFocusMineList"


    // 我点过谁赞列表
    const val ME_LIKE_WHO = "UserCenter/meDianzanWhoList"

    // 谁点过我赞列表
    const val WHO_LIKE_ME = "UserCenter/whoDianzanMeList"


    // 我评论过谁的列表
    const val ME_DISCUSS_WHO = "UserCenter/meDiscussWhoList"

    // 谁评论过我的列表
    const val WHO_DISCUSS_ME = "UserCenter/whoDiscussMeList"

    // 获取四个统计数字
    const val GET_FOUR_TOTAL = "UserCenter/getFourTotal"


    // ------------------------------- 动态 -------------------------------

    // 获取我的动态 列表
    const val GET_MY_TREND_LIST = "UserCenter/trendsList"

    // 获取其他人的动态 列表
    const val GET_OTHER_TREND_LIST = "UserCenter/othersTrendes"

    // 上传我的动态 列表
    const val DO_UPLOAD_TREND = "UserCenter/uploadTrend"

    // 查看自己的某一条动态
    const val DO_CHECK_TREND = "UserCenter/checkOne"

    // 删除我的动态
    const val DO_DELETE_TREND = "UserCenter/deleteTrend"

    // 动态的点赞列表
    const val GET_LIKE_LIST = "UserCenter/likeList"

    // 给动态点击送赞
    const val DO_LICK_CLICK = "UserCenter/likeClick"

    // 给动态取消点赞
    const val DO_LICK_CANCEL = "UserCenter/likeCancel"

    // 一级评论 动态的父评论列表
    const val GET_COMMENT_ONE = "UserCenter/discussListOne"

    // 一级评论 给动态提交父评论
    const val DO_COMMENT_ONE_CREATE = "UserCenter/discussOneCreate"

    // 一级评论 删除动态的父评价
    const val DO_COMMENT_ONE_DELETE = "UserCenter/discussOneDelete"

    // 二级评论 动态的子评论列表
    const val GET_COMMENT_TWO = "UserCenter/discussListTwo"

    // 二级评论 动态的子评论增加
    const val DO_COMMENT_TWO_CREATE = "UserCenter/discussTwoCreate"

    // 二级评论 动态的子评论删除
    const val DO_COMMENT_TWO_DELETE = "UserCenter/discussTwoDelete"

    // 我关注其它人，列表
    const val MINE_FOCUS_OTHER_LIST = "UserCenter/mineFocusOtherList"

    // 其它人关注我，列表
    const val OTHER_FOCUS_MINE_LIST = "UserCenter/otherFocusMineList"

    // 点击添加关注别人
    const val DO_PLUS_FOCUS_OTHER = "UserCenter/plusFocusOther"

    // 点击取消关注别人
    const val DO_CANCEL_FOCUS_OTHER = "UserCenter/cancelFocusOther"


    // 动态沙龙列表
    const val GET_TREND_SALOON = "TrendsNotice/trendsSaloon"

    // 关注的人动态列表
    const val GET_TREND_FOCUS = "TrendsNotice/focousTrends"

    // 获取评论和点赞的未读次数
    const val GET_TOTAL_COUNT = "TrendsNotice/totalCount"


    // 点赞未读列表
    const val GET_TREND_TIP = "TrendsNotice/dianzanUnreadList"


    const val GET_COMMENT_TIP = "TrendsNotice/discussUnreadList"

    // 未读改成已读
    const val DO_DELETE_TIPS = "TrendsNotice/UnreadToRead"


    // 会员价格列表
    const val GET_VIP_PRICE = "MemberCharge/vipList"

    // 金币价格列表
    const val GET_COIN_PRICE = "MemberCharge/jinbiList"

    // 苹果安卓发起支付宝
    const val DO_ALI_PAY = "MemberCharge/alibabaPayment"

    // 预览对方
    const val DO_PREVIEW_OTHER = "MemberCharge/previewOther"

    // 金币收支记录列表
    const val GET_COIN_RECORD = "MemberCharge/jinbiRecordList"

    // 刷新金币与会员
    const val DO_REFRESH_SELF = "MemberCharge/refreshSelf"

    // 上传用户反馈
    const val DO_UPLOAD_FEEDBACK = "MemberCharge/uploadFeedback"

    // 投诉举报其它人
    const val DO_REPORT_OTHER = "MemberCharge/uploadComplaint"

    // 修改友盟token
    const val DO_UPDATE_TOKEN = "MemberCharge/updateToken"

    // 修改友盟推送设置
    const val DO_UPDATE_PUSH_SET = "MemberCharge/umengPushSet"

    // 获取友盟推送状态
    const val GET_PUSH_SET = "MemberCharge/getPushStatus"

    // 屏蔽列表
    const val GET_BLACK_LIST = "TrendsNotice/blockSessionList"

    // 删除屏蔽会话
    const val DELETE_BLACK_LIST = "/marryfriend/TrendsNotice/deleteBlockSession"


    // ------------------------------- 高德地图 -------------------------------

    // 高德地图-地点检索
    const val PLACE_SEARCH = "place/around"

    // 百度行政区划区域检索
    const val BAIDU_SEARCH = "v2/search"


    // ------------------------------- 常量 -------------------------------

    // 获取学校
    const val GET_CITY = "GetParameter/shengShi"

    // 获取学校
    const val GET_SCHOOL = "GetParameter/manySchool"

    // 获取敏感字
    const val GET_BAN = "GetParameter/minganWenzi"

    // 获取行业
    const val GET_INDUSTRY = "GetParameter/hangYe"

    // 获取岗位
    const val GET_JOB = "GetParameter/hangYe"


    // 获取 百度api所需的 Access Token
    const val GET_ACCESS_TOKEN = "2.0/token"

    // 百度身份证验证
    const val DO_IDENTITY_VERIFY = "2.0/face/v3/person/idmatch"

    // 百度人脸实名认证
    const val DO_FACE_VERIFY = "2.0/face/v3/person/verify"

    // 百度人脸识别(鉴黄)
    const val DO_FACE_DETECT = "2.0/solution/v1/img_censor/v2/user_defined"

    // 百度文字审核识别
    const val DO_TEXT_VERIFY = "2.0/solution/v1/text_censor/v2/user_defined"


    //----------------------------- 发送参数值 -----------------------------//

    // 发送手机验证码
    const val Mobile = "mobile"     // 手机号

    // 验证码登录注册
    // const val Mobile = "mobile"     // 手机号
    const val VERIFY_CODE = "code_key"     // 验证码
    const val DEVICE_CODE = "equipment_number"     // 唯一设备编码
    const val VERSION = "user_version"     // 版本号

    // const val USER_PLATFORM = "user_platform"     // 渠道
    const val PACKAGE_ENGLISH = "user_package"     // 包英文名(固定)
    const val SYSTEM = "user_system"     // 系统
    const val PACKAGE_CHINESE = "user_pkg_chn"     // 包中文名(固定)

    // 阿里云一键登录
    const val ALI_TOKEN = "ali_token" // 阿里令牌
    const val EQUIPMENT_NUMBER = "equipment_number" // 唯一设备编码
    const val USER_VERSION = "user_version" // 版本号
    const val USER_PLATFORM = "user_platform" // 渠道
    const val USER_PACKAGE = "user_package" // 包英文名(固定)
    const val USER_SYSTEM = "user_system" // 系统
    const val USER_PKG_CHN = "user_pkg_chn" // 婚恋交友安卓


    // 修改基础信息
    const val USER_ID = "user_id"
    const val BASE_UPDATE = "base_update"

    // 修改更多信息
    //    const val USER_ID = "user_id"
    const val MORE_UPDATE = "more_update"


    // 修改择偶需求
    //    const val USER_ID = "user_id"
    const val DEMAND_UPDATE = "demand_update"

    // 修改认证信息
    //    const val USER_ID = "user_id"
    const val VERIFY_UPDATE = "verify_update"

    // 修改招呼语
    //    const val USER_ID = "user_id"
    const val GREET_UPDATE = "zhaohuyu_update"

    // 修改工作区域
    const val SHENG_SHI = "sheng_shi"

    //查看列表(头像,三张,相册)
    //    const val USER_ID = "user_id"
    //    const val KIND = "kind"

    // 图片上传(头像,三张,相册)
    //    const val USER_ID = "user_id"
    const val IMAGE_URL = "image_url"
    const val FILE_TYPE = "file_type"
    const val FILE_NAME = "file_name"
    const val CONTENT = "content"
    const val KIND = "kind"


    // 更新资料完善度
    //    const val USER_ID = "user_id"
    const val PROPORTION = "finish_proportion"


    // --------------------------------------  动态  --------------------------------------

    const val TRENDS_TYPE = "trends_type"
    const val PAGE = "page"
    const val SIZE = "size"
    const val ID = "id"
    const val TREND_ID = "trend_id"    // 动态id
    const val HOST_UID = "host_uid"    // 动态主人uid
    const val TRENDS_ID = "trends_id"    // 动态id
    const val GUEST_UID = "guest_uid"    // 浏览客人uid
    const val PARENT_ID = "parent_id"   // 动态的父级评论id
    const val ONE_LEVEL_UID = "one_level_uid" // 动态的父级用户id
    const val TREND_INFO = "trend_info"
    const val CONTENT_ONE = "content_one"  // 父评论内容

    const val FIRST_UID = "first_uid"  // 前一个用户uid
    const val LAST_UID = "last_uid"  // 现在这个用户uid
    const val CONTENT_TWO = "content_two"  // 子评论内容

    // 动态沙龙列表
    const val UP_DOWN = "up_down"  // 首页 上页 下页 三选一
    const val MAX_ID = "max_id"  // 当前页的最大动态id，首页为1
    const val MIN_ID = "min_id"  // 当前页的最小动态id，首页为0
    // const val SIZE = "size"   // 每页加载条数，默认10


    // 其他人动态列表
    const val MYSELF_UID = "myself_uid"  // 自己的uid
    const val FRIEND_UID = "friend_uid"  // 对方的uid

    // 未读改成已读
    const val DISCUSS_ID = "discuss_id"  // 评论最大id
    const val LIKE_ID = "like_id"  // 点赞最大id


    // 举报
    const val REASON_CODE = "reason_code"  // 原因编码
    const val REASON_TEXT = "reason_text"  // 原因文字
    const val MARK_NOTICE = "mark_notice"  // 备注信息


    // 意见反馈
    const val CONTENT_TEXT = "content_text"   // 反馈内容
    const val CONTACT_1 = "contact_1"   // 联系方式一
    const val CONTACT_2 = "contact_2"   // 联系方式二
    const val IMAGES_URL = "images_url"   // 图片地址一
    const val IMAGE_1 = "image_1"   // 图片地址一
    const val IMAGE_2 = "image_2"   // 图片地址二
    const val IMAGE_3 = "image_3"   // 图片地址一


    // ------------------------------------ 支付 ------------------------------------

    const val PLATFORM = "platform"  // 充值渠道
    const val TYPE_KIND = "type_kind" // 安卓苹果 二选一
    const val VIP_LEVEL = "vip_level" // 会员等级

    const val BUY_ORDER_NUMBER = "buy_order_number" // 订单号
    const val FEE = "fee" // 金额
    const val BODY = "body" // 描述


    const val UMENG_TOKEN = "token" // 友盟token


    // ------------------------------------ 推送 ------------------------------------

    const val SHENHE_TONGZHI = "shenhe_tongzhi" // 审核通知，跳小秘书
    const val TA_GANG_XIHUAN_NI = "ta_gang_xihuan_ni" // 它刚喜欢你 通知
    const val PINGLUN_DONGTAI = "pinglun_dongtai" // 评论动态
    const val DIANZAN_DONGTAI = "dianzan_dongtai" // 点赞动态
    const val KANLE_NIDE_ZILIAO = "kanle_nide_ziliao" // 看了你的资料
    const val NIXIHUANDE_SHANGXIAN = "nixihuande_shangxian" // 你喜欢的人 上线了
    const val XIANGHU_XIHUAN_SHANGXIAN = "xianghu_xihuan_shangxian" // 相互喜欢的 上线了
    const val DIANJI_XIANGHU_XIHUAN = "dianji_xianghu_xihuan" // 点击相互喜欢 通知
    const val SHOUDAO_LIWU_TONGZHI = "shoudao_liwu_tongzhi" // 收到礼物通知


    // ------------------------------------ 外部的api ------------------------------------

    // 获取 百度api所需的 Access Token

    const val GRANT_TYPE = "grant_type"     // 必须参数，固定为client_credentials；
    const val CLIENT_ID = "client_id"            // 必须参数，应用的API Key；
    const val CLIENT_SECRET = "client_secret"    // 必须参数，应用的Secret Key；


    // 百度身份证验证

    const val ACCESS_TOKEN = "access_token" // 通过API Key和Secret Key获取的access_token
    const val CONTENT_TYPE = "Content-Type" // 固定值 ： application/json
    const val ID_CARD_NUMBER = "id_card_number"  // 身份证号
    const val NAME = "name"   // 姓名（注：需要是UTF-8编码的中文）


    // 百度身份证验证

    // const val ACCESS_TOKEN = "access_token" // 通过API Key和Secret Key获取的access_token
    // const val CONTENT_TYPE = "Content-Type" // 固定值 ： application/json
    const val IMAGE = "image" // 图片信息
    const val IMAGE_TYPE = "image_type" // 图片类型 BASE64、URL、FACE_TOKEN

    const val TEXT = "text" // 待审核文本字符串


    // 百度地图地点搜索
    const val KEY = "key"
    const val LOCATION = "location"
    const val KEYWORDS = "keywords"
    const val TYPES = "types"
    const val CITY = "city"
    const val RADIUS = "radius"
    const val SORTRULE = "sortrule"
    const val OFFSET = "offset"

    //    const val PAGE = "page"
    const val EXTENSIONS = "extensions"
    const val SIG = "sig"
    const val CALLBACK = "callback"


    // 百度行政区划区域检索
    const val QUERY = "query" // 检索关键字
    const val TAG = "tag"     // 检索分类偏好
    const val REGION = "region"  // 检索行政区划区域
    const val OUTPUT = "output"  // 输出格式  json或者xml
    const val AK = "ak"          // 开发者的访问密钥


}
