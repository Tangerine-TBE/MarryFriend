package com.message.conversations

import com.hyphenate.chat.EMConversation

enum class ConversationType(title:String){
    Chat("单聊"),//EMConversation.EMConversationType.Chat
    Assistant("恋爱小管家");//EMConversation.EMConversationType.HelpDesk
    fun toImType(): EMConversation.EMConversationType{
        return when(this){
            Chat -> EMConversation.EMConversationType.Chat
            Assistant -> EMConversation.EMConversationType.HelpDesk
        }
    }
    companion object{
        fun toMyType(type:EMConversation.EMConversationType):ConversationType?{
            return when(type){
                EMConversation.EMConversationType.Chat -> {
                    ConversationType.Chat
                }
                EMConversation.EMConversationType.GroupChat -> {
                    null
                }
                EMConversation.EMConversationType.ChatRoom -> {
                    null
                }
                EMConversation.EMConversationType.DiscussionGroup -> {
                    null
                }
                EMConversation.EMConversationType.HelpDesk -> {
                    ConversationType.Assistant                }
                null -> {
                    null
                }
            }
        }
    }
}