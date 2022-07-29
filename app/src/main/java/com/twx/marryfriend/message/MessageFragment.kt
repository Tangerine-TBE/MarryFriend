package com.twx.marryfriend.message

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.message.ImUserManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.databinding.BaseDataBindingListAdapter
import com.twx.marryfriend.databinding.BaseDataBindingViewHolder
import com.twx.marryfriend.databinding.FragmentMessageBinding
import com.twx.marryfriend.message.views.ConversationItemView
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.coroutines.launch

class MessageFragment : Fragment(R.layout.fragment_message) {
    init {
        ImUserManager.createOrLogin(UserInfo.getUserId())
    }

    private val viewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }
    private val conversationsModel by lazy {
        ConversationsModel()
    }
    private val adapter by lazy {
        object :BaseDataBindingListAdapter<ConversationItemView,ConversationsItemModel>(){
            override fun getItemViewType(position: Int): Int {
                return super.getItemViewType(position)
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): BaseDataBindingViewHolder<ConversationItemView, ConversationsItemModel> {
                return BaseDataBindingViewHolder(ConversationItemView(parent))
            }
        }
    }
    private var dataBinding:FragmentMessageBinding?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding=DataBindingUtil.bind<FragmentMessageBinding>(view)
        dataBinding?.conversationsModel=conversationsModel
        dataBinding?.lifecycleOwner=this
        messageRecyclerView.layoutManager=LinearLayoutManager(context)
        messageRecyclerView.adapter=adapter
        test.setOnClickListener {
            lifecycleScope.launch {
                val data=try {
                    viewModel.getAllConversations()
                }catch (e:Exception){
                    eLog(e.stackTraceToString())
                    toast(e.message)
                    null
                }
                adapter.setData(data)
            }
        }

    }
}