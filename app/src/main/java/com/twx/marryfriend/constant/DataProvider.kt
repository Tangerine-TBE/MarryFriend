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

    val FirstSetData = arrayListOf(
        ItemBean("搜索", R.drawable.ic_set_search),
    )

    val SecondSetData = arrayListOf(
        ItemBean("消息通知", R.drawable.ic_set_message),
        ItemBean("帮助反馈", R.drawable.ic_set_feedback),
        ItemBean("账户与安全", R.drawable.ic_set_safe),
        ItemBean("恢复购买", R.drawable.ic_set_buy),
    )

    val ThirdSetData = arrayListOf(
        ItemBean("关于佳偶婚恋交友", R.drawable.ic_set_about),
        ItemBean("黑名单", R.drawable.ic_set_blacklist),
        ItemBean("好评", R.drawable.ic_set_praise),
    )


    val FeedbackTopData = arrayListOf(
        ItemBean("推荐滑卡问题", 0),
        ItemBean("聊天相关问题", 0),
        ItemBean("约会相关问题", 0),
        ItemBean("资料相关问题", 0),
        ItemBean("其他问题反馈", 0),
    )

    val FeedbackBottomData = arrayListOf(
        ItemBean("怎么获得更多得曝光与喜欢？", 0),
        ItemBean("我滑过的人还会再出现吗？", 0),
        ItemBean("不小心左滑了喜欢的人，还能反悔吗？", 0),
        ItemBean("为什么我喜欢了对方，但还不可以和ta聊天？", 0),
        ItemBean("不想和对方聊天了，怎么解除配对关系？", 0),
        ItemBean("对方有骚扰/营销/违法等行为，怎么举报？", 0),
        ItemBean("约好了时间，但临时有事不能赴约了怎么办？", 0),
        ItemBean("约会过程中突然中断视频或断网了怎么办？", 0),
        ItemBean("为什么约会迟到了直接显示约会失败？", 0),
        ItemBean("约会超过15分钟还想继续聊天怎么办？", 0),
        ItemBean("为什么头像/图片过不了审核？", 0),
        ItemBean("昵称/职业/公司/自我介绍文字过不了审核？", 0),
        ItemBean("实名认证能修改和取消吗？", 0),
        ItemBean("如何取消自动续费？", 0),
        ItemBean("我在使用中遇到了问题，该如何联系客服？", 0),
    )







}