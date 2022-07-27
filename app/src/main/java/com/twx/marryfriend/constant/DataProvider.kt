package com.twx.marryfriend.constant

import com.twx.marryfriend.R
import com.twx.marryfriend.bean.ItemBean

/**
 * @author: Administrator
 * @date: 2022/5/31
 */
object DataProvider {

    val dataBaseData = arrayListOf(
        "昵称",
        "性别",
        "生日",
        "身高",
        "月收入",
        "工作地区",
        "学历",
        "婚姻",
        "恋爱目标",
    )

    val dataMoreData = arrayListOf(
        "有没有孩子",
        "是否想要孩子",
        "职业",
        "购房情况",
        "购车情况",
        "籍贯",
        "体重",
        "体型",
        "是否吸烟",
        "是否喝酒",
        "民族",
        "何时结婚",
    )

    val targetBaseData = arrayListOf(
        "年龄",
        "身高",
        "月收入",
        "学历",
        "婚况",
    )

    val targetMoreData = arrayListOf(
        "体型",
        "工作地区",
        "有没有孩子",
        "是否想要孩子",
        "是否吸烟",
        "是否喝酒",
        "有无照片",
        "何时结婚",
    )

    val setInfoDialogData = arrayListOf(
        "上传真实头像,才能获取曝光推荐",
        "上传正面生活照,让大家更了解你",
        "实名认证后,脱单概率加倍",
        "说说你的爱好,吸引兴趣相投的异性",
        "添加一段打招呼语,消息被回复概率加倍",
        "认真介绍自己,帮你快点找到另一半",
        "录一段语音介绍最,聊聊你憧憬的另一半",
    )


    val NormalVipData = arrayListOf(
        ItemBean("解锁收件箱", R.mipmap.icon_normal_1),
        ItemBean("解锁消息发送", R.mipmap.icon_normal_2),
        ItemBean("搜索想看的人", R.mipmap.icon_normal_3),
        ItemBean("消息突出", R.mipmap.icon_normal_4),
        ItemBean("隐私访问", R.mipmap.icon_normal_5),
        ItemBean("无限喜欢次数", R.mipmap.icon_normal_6),
        ItemBean("尊贵会员标识", R.mipmap.icon_normal_7),
        ItemBean("无限次数评论", R.mipmap.icon_normal_8),
    )

    val SuperVipData = arrayListOf(
        ItemBean("解锁收件箱", R.mipmap.icon_super_1),
        ItemBean("解锁消息发送", R.mipmap.icon_super_2),
        ItemBean("搜索想看的人", R.mipmap.icon_super_3),
        ItemBean("消息突出", R.mipmap.icon_super_4),
        ItemBean("隐私访问", R.mipmap.icon_super_5),
        ItemBean("无限喜欢次数", R.mipmap.icon_super_6),
        ItemBean("尊贵会员标识", R.mipmap.icon_super_7),
        ItemBean("无限次数评论", R.mipmap.icon_super_8),
        ItemBean("消息置顶", R.mipmap.icon_super_9),
        ItemBean("悄悄看ta", R.mipmap.icon_super_10),
        ItemBean("查看关注", R.mipmap.icon_super_11),
        ItemBean("消息已读", R.mipmap.icon_super_12),
        ItemBean("随心畅聊", R.mipmap.icon_super_13),
    )

}