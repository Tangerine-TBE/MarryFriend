package com.twx.module_base.constant

object Contents {

    // 域名
    const val USER_URL = "http://test.aisou.club"

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

    //查看列表(头像,三张,相册)
    const val PHOTO_LIST = "LoginRegister/photoList"

    //图片上传(头像,三张,相册)
    const val UPLOAD_PHOTO = "LoginRegister/uploadPhoto"

    // 更新资料完善度
    const val UPDATE_PROPORTION = "LoginRegister/proportionUpdate"

    // ------------------------------- 动态 -------------------------------

    // 获取我的动态 列表
    const val GET_MY_TREND_LIST = "UserCenter/trendsList"

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

    // ------------------------------- 高德地图 -------------------------------

    // 高德地图-地点检索
    const val PLACE_SEARCH = "place/around"

    // 百度行政区划区域检索
    const val BAIDU_SEARCH = "v2/search"


    // ------------------------------- 常量 -------------------------------

    // 获取学校
    const val GET_CITY = "GetParameter/provinceCity"

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


    //----------------------------- 发送参数值 -----------------------------//

    // 发送手机验证码
    const val Mobile = "mobile"     // 手机号

    // 验证码登录注册
    // const val Mobile = "mobile"     // 手机号
    const val VERIFY_CODE = "code_key"     // 验证码
    const val DEVICE_CODE = "equipment_number"     // 唯一设备编码
    const val VERSION = "user_version"     // 版本号
    const val PLATFORM = "user_platform"     // 渠道
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

    // 修改招呼语
    //    const val USER_ID = "user_id"
    const val GREET_UPDATE = "zhaohuyu_update"


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
