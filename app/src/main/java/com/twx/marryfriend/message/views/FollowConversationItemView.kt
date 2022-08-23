package com.twx.marryfriend.message.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.databinding.ItemMessageFollowBinding
import com.twx.marryfriend.message.model.ConversationsItemModel
import com.xyzz.myutils.show.toast

class FollowConversationItemView  @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {
    private val dataBindingView by lazy {
        DataBindingUtil.bind<ItemMessageFollowBinding>(this)
    }
    init {
        initListener()
    }
    fun setData(data: ConversationsItemModel?) {
        dataBindingView
        initListener()
    }

    private fun initListener(){
        setOnClickListener {
//            context?.startActivity(Intent(context,))
            toast(context,"跳到关注页面")
        }
    }
}