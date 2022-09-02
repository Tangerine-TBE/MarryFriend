package com.twx.marryfriend.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hyphenate.chat.EMConversation
import com.hyphenate.easeim.section.conversation.ConversationListFragment
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.FragmentHxMessageBinding
import com.twx.marryfriend.message.model.ConversationsModel
import com.twx.marryfriend.mutual.MutualLikeActivity
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import kotlinx.android.synthetic.main.fragment_hx_message.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HxMessageFragment: ConversationListFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }
    private val conversationsModel by lazy {
        ConversationsModel()
    }
    private var dataBinding: FragmentHxMessageBinding?=null

    private val loadingDialog by lazy {
        LoadingDialogManager
            .createLoadingDialog()
            .create(requireContext())
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding=DataBindingUtil.inflate<FragmentHxMessageBinding>(LayoutInflater.from(requireContext()),R.layout.fragment_hx_message,llRoot,false)
        dataBinding?.lifecycleOwner=this
        llRoot.addView(dataBinding?.root, 0)

        loadData()
    }


     override fun initListener(){
         super.initListener()
        mutualLike.setOnClickListener {
            startActivity(Intent(requireContext(), MutualLikeActivity::class.java))
        }
    }

    override fun toChatActivity(item: EMConversation) {
        startActivity(ImChatActivity.getIntent(requireContext(),item.conversationId(), isRealName = false))
    }

    private fun loadData(){
        iLog("加载一下")
        loadingDialog.show()
        dataBinding?.conversationsModel=conversationsModel
        lifecycleScope.launch {
            val task2=async() {
                try {
                    val mutualLike=viewModel.getMutualLike()
                    conversationsModel.laterLikeCount = mutualLike.total?:0
                    conversationsModel.list = mutualLike.list
                }catch (e:Exception){
                    eLog(e.stackTraceToString())
                }
            }
            task2.await()
            loadingDialog.dismiss()
        }
    }
}