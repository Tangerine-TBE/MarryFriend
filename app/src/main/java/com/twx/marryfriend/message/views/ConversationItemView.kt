package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.ItemListSessionMessageBinding
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.twx.marryfriend.mutual.MutualLikeDialogActivity
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.item_list_session_message.view.*

class ConversationItemView  @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {

    private val dataBindingView by lazy {
        DataBindingUtil.inflate<ItemListSessionMessageBinding>(
            LayoutInflater.from(context), R.layout.item_list_session_message,this,false)
    }
    init {
        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)

        this.addView(dataBindingView.root)
        initListener()
    }
    private var conversationsItemModel: ConversationsItemModel?=null

    fun setData(data: ConversationsItemModel?) {
        conversationsItemModel=data
        dataBindingView.conversationsItemModel=data
    }

    private fun initListener(){
        this.setOnClickListener {
            val data=dataBindingView.conversationsItemModel
            if (data==null){
                toast(context,"数据为空")
                return@setOnClickListener
            }
            context?.startActivity(ChatActivity.getIntent(context, data.conversationId,data.nickname,data.userImage,data.isRealName))
        }
        messageHead.setOnClickListener {
            context?.startActivity(MutualLikeDialogActivity.getIntent(context,conversationsItemModel?.userImage?:""))
        }
    }
}