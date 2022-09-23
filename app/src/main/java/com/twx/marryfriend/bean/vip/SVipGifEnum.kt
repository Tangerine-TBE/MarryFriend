package com.twx.marryfriend.bean.vip

import com.twx.marryfriend.R

enum class SVipGifEnum(val code: Int, val res: Int) {

    Inbox(1, R.mipmap.gif_svip_1), // 收件箱
    Outbox(2, R.mipmap.gif_svip_2), // 发件箱
    Search(3, R.mipmap.gif_svip_3), // 高级搜索

    //    Online(4, R.mipmap.gif_svip_4), // 隐藏在线状态
    MoreView(4, R.mipmap.gif_svip_5), // 查看更多嘉宾，多推荐20个
    TopMessage(5, R.mipmap.gif_svip_6), // 消息置顶
    ViewDate(6, R.mipmap.gif_svip_7), // 访问他的资料
    SVip(7, R.mipmap.gif_svip_8), // 超级会员标志
    Read(8, R.mipmap.gif_svip_9), // 查看消息是否已读
    Chat(9, R.mipmap.gif_svip_10), // 随意聊天
    FocusMe(10, R.mipmap.gif_svip_11), // 查看关注我的人
    Highlight(11, R.mipmap.gif_svip_12), // 突出显示消息
    SeeMe(12, R.mipmap.gif_svip_13), // 谁看过我
    LoveMe(13, R.mipmap.gif_svip_14); // 谁喜欢我

}