package com.twx.marryfriend.message

import com.hyphenate.chat.EMConversation
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo
import com.hyphenate.easeui.modules.conversation.presenter.EaseConversationPresenterImpl
import com.message.ImMessageManager
import com.xyzz.myutils.show.iLog

class MyEaseConversationPresenter: EaseConversationPresenterImpl() {

    override fun deleteConversation(position: Int, info: EaseConversationInfo?) {
        super.deleteConversation(position, info)
        iLog("删除会话${(info?.info as? EMConversation)?.conversationId()}")
        val cid=(info?.info as? EMConversation)?.conversationId()
        if (cid!=null) {
            ImMessageManager.onDeleteConversation(cid)
        }
    }
}