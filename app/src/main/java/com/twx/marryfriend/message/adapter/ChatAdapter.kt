package com.twx.marryfriend.message.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMMessageBody
import com.message.chat.*
import com.twx.marryfriend.databinding.BaseDataBindingViewHolder
import com.twx.marryfriend.message.model.ChatItemModel
import com.twx.marryfriend.message.views.ChatImageMsgItemView
import com.twx.marryfriend.message.views.ChatImageRedFlowerItemView
import com.twx.marryfriend.message.views.ChatTextMsgItemView

class ChatAdapter:RecyclerView.Adapter<BaseDataBindingViewHolder>() {
    companion object{
        private const val CMD_TYPE=1
        private const val CUSTOM_TYPE=2
        private const val FILE_TYPE=3
        private const val IMAGE_TYPE=4
        private const val LOCATION_TYPE=5
        private const val TXT_TYPE=6
        private const val VIDEO_TYPE=7
        private const val VOICE_TYPE=8
    }

    private val listData=ArrayList<ChatItemModel<Message<out EMMessageBody>>>()

    override fun getItemViewType(position: Int): Int {
        when(listData[position].data){
            is CmdMessage -> {
                CMD_TYPE
            }
            is CustomMessage -> {
                CUSTOM_TYPE
            }
            is FileMessage -> {
                FILE_TYPE
            }
            is ImageMessage -> {
                IMAGE_TYPE
            }
            is LocationMessage -> {
                LOCATION_TYPE
            }
            is TxtMessage -> {
                TXT_TYPE
            }
            is VideoMessage -> {
                VIDEO_TYPE
            }
            is VoiceMessage -> {
                VOICE_TYPE
            }
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDataBindingViewHolder {
//        ChatImageRedFlowerItemView(parent)
        val view=when(viewType){
            TXT_TYPE->{
                ChatTextMsgItemView(parent)
            }
            IMAGE_TYPE->{
                ChatImageMsgItemView(parent)
            }
            VIDEO_TYPE->{
                ChatTextMsgItemView(parent)
            }
            FILE_TYPE->{
                null
            }
            LOCATION_TYPE->{
                null
            }
            VOICE_TYPE->{
                ChatImageMsgItemView(parent)
            }
            CMD_TYPE->{
                null
            }
            CUSTOM_TYPE->{
                null
            }
            else->{
                null
            }
        }
        return BaseDataBindingViewHolder(view?:ChatTextMsgItemView(parent))
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder, position: Int) {
        val dataBindingView=holder.dataBindingView
        val item=listData[position]
        when(item.data){
            is CmdMessage -> {
                
            }
            is CustomMessage -> {
                
            }
            is FileMessage -> {

            }
            is ImageMessage -> {
                (dataBindingView as? ChatImageMsgItemView)?.setData(item as ChatItemModel<ImageMessage>)
            }
            is LocationMessage -> {
                
            }
            is TxtMessage -> {
                (dataBindingView as? ChatTextMsgItemView)?.chatItemModel=(item as ChatItemModel<TxtMessage>)
            }
            is VideoMessage -> {
                
            }
            is VoiceMessage -> {
                
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}