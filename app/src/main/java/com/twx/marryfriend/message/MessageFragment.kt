package com.twx.marryfriend.message

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.message.ImUserManager
import com.message.ImMessageManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.databinding.FragmentMessageBinding
import com.twx.marryfriend.message.adapter.MessageListAdapter
import com.twx.marryfriend.message.model.ConversationsModel
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.coroutines.launch
import kotlin.random.Random

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
        MessageListAdapter()
    }
    private var dataBinding:FragmentMessageBinding?=null
    private val loadingDialog by lazy {
        LoadingDialogManager
            .createLoadingDialog()
            .create(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding=DataBindingUtil.bind<FragmentMessageBinding>(view)
        dataBinding?.lifecycleOwner=this
        messageRecyclerView.layoutManager=LinearLayoutManager(context)
        messageRecyclerView.adapter=adapter
        loadData()
        test.setOnClickListener {
            iLog("向用户3发送信息")
            ImMessageManager.sendTextMsg("3","你好啊！收到我的消息了吗？")
        }
        initListener()
    }

    private fun initListener(){
        msgSmartRefresh.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData(){
        iLog("加载一下")
        loadingDialog.show()
        conversationsModel.laterLikeCount= Random.nextInt()
        dataBinding?.conversationsModel=conversationsModel
        lifecycleScope.launch {
            val data=try {
                viewModel.getAllConversations().also {
                    msgSmartRefresh.finishRefresh()
                }
            }catch (e:Exception){
                eLog(e.stackTraceToString())
                toast(e.message)
                msgSmartRefresh.finishRefresh(false)
                null
            }
            adapter.setData(data)
            loadingDialog.dismiss()
        }
    }
}