package com.twx.marryfriend.bean.likeme

/**
 * {
"code": 200,
"msg": "success",
"data": {
"total": 1,
"list": [
{
"id": 25,
"host_uid": 5,
"guest_uid": 15,
"create_time": "2022-07-12 17:36:47",
"nick": "你们都是我",
"user_sex": 2,
"age": 30,
"height": 183,
"education": 3,
"occupation_str": "销售总监",
"work_city_str": "深圳",
"image_url": "https://ioslove2.bj.bcebos.com/5/20220623153105.png",
"online_time": "2022-07-13 19:30:21"
}
]
}
}
 */
data class LikeMeItemBean(
    var age: Int? = 0,
    var create_time: String? = "",
    var education: Int? = 0,
    var guest_uid: Int? = 0,
    var height: Int? = 0,
    var host_uid: Int? = 0,
    var id: Int? = 0,
    var image_url: String? = "",
    var nick: String? = "",
    var occupation_str: String? = "",
    var online_time: String? = "",
    var user_sex: Int? = 0,
    var work_city_str: String? = ""
)