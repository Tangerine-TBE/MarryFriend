package com.twx.marryfriend.message

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.message.conversations.ConversationsBean
import com.twx.marryfriend.databinding.BaseDataBindingViewHolder
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.twx.marryfriend.message.views.ConversationItemView
import com.twx.marryfriend.message.views.TipConversationItemView

class MessageListAdapter: RecyclerView.Adapter<BaseDataBindingViewHolder>() {
    companion object{
        private const val TYPE_CHAT=1
        private const val TYPE_ASSISTANT=2
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
        val item=listData[position]
        return when (item.msgType){
            ConversationsBean.ConversationType.Chat -> TYPE_CHAT
            ConversationsBean.ConversationType.Assistant -> TYPE_ASSISTANT
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseDataBindingViewHolder {
        val view=if (viewType==TYPE_CHAT){
            ConversationItemView(parent)
        }else if (viewType==TYPE_ASSISTANT){
            TipConversationItemView(parent)
        }else{
            ConversationItemView(parent)
        }
        return BaseDataBindingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder, position: Int) {
        val itemView=holder.dataBindingView
        val item=listData[position]
        when(itemView){
            is ConversationItemView->{
                itemView.setData(item)
            }
            is TipConversationItemView->{
                itemView.setData(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}