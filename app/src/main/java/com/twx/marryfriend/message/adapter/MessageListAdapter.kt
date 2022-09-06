package com.twx.marryfriend.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.message.conversations.ConversationType
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.BaseDataBindingViewHolder
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.twx.marryfriend.message.views.ConversationItemView
import com.twx.marryfriend.message.views.TipConversationItemView

class MessageListAdapter: RecyclerView.Adapter<BaseDataBindingViewHolder>() {
    companion object{
        private const val TYPE_CHAT=1//正常会话
        private const val TYPE_ASSISTANT=2
        private const val TYPE_FOLLOW=3
    }

    private val listData by lazy {
        ArrayList<ConversationsItemModel>()
    }

    fun setData(data:List<ConversationsItemModel>?){
        data?:return
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        if (position==0){
            return TYPE_FOLLOW
        }else{
            val item=listData[position-1]
            return when (item.msgType){
                ConversationType.Chat -> TYPE_CHAT
                ConversationType.Assistant -> TYPE_ASSISTANT
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingViewHolder {
        val view=if (viewType== TYPE_CHAT){
            ConversationItemView(parent.context)
        }else if (viewType== TYPE_ASSISTANT){//小助手
            TipConversationItemView(parent.context)
        }else if (viewType== TYPE_FOLLOW){//关注的人
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_follow,parent,false)
        }else{
            ConversationItemView(parent.context)
        }
        return BaseDataBindingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder, position: Int) {
        if (position==0){

        }else{
            val itemView=holder.itemView
            val item=listData[position-1]
            when(itemView){
                is ConversationItemView->{
                    itemView.setData(item)
                }
                is TipConversationItemView->{
                    itemView.setData(item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size+1
    }
}