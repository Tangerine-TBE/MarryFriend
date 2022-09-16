package com.twx.marryfriend.bean.vip

import com.twx.marryfriend.R

enum class VipGifEnum(val code: Int, val res: Int) {
    Inbox(1, R.mipmap.gif_vip_1), // 收件箱
    Message(2, R.mipmap.gif_vip_2), // 消息发送
    Search(3, R.mipmap.gif_vip_3), // 高级搜索
//    Online(4, R.mipmap.gif_vip_4), // 隐藏在线状态
    Like(4, R.mipmap.gif_vip_5), // 无限喜欢次数
    Highlight(5, R.mipmap.gif_vip_6), // 突出显示消息
    SeeMe(6, R.mipmap.gif_vip_7); // 谁看过我
}