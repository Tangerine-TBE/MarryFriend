package com.twx.marryfriend.constant

object Contents {

    // 域名
    const val USER_URL = "http://test.aisou.club"

    //  百度api所需的 Access Token 所需域名
    const val BAIDU_API_URL = "https://aip.baidubce.com"


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

}
