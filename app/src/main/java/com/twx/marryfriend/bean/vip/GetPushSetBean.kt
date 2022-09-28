package com.twx.marryfriend.bean.vip

/**
 * @author: Administrator
 * @date: 2022/5/13
 */
data class GetPushSetBean(
    val code: Int,
    val data: GetPushSetData,
    val msg: String,
)

data class GetPushSetData(
    val create_time: String,
    val dianji_xianghu_xihuan: Int,
    val dianzan_dongtai: Int,
    val id: Int,
    val kanle_nide_ziliao: Int,
    val nixihuande_shangxian: Int,
    val pinglun_dongtai: Int,
    val shenhe_tongzhi: Int,
    val shoudao_liwu_tongzhi: Int,
    val ta_gang_xihuan_ni: Int,
    val update_time: Any,
    val user_id: Int,
    val xianghu_xihuan_shangxian: Int,
)
