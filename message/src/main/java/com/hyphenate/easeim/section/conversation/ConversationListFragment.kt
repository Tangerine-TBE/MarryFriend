package com.hyphenate.easeim.section.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import com.hyphenate.easeui.modules.conversation.EaseConversationListFragment
import com.hyphenate.easeim.section.conversation.viewmodel.ConversationListViewModel
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo
import com.hyphenate.chat.EMConversation
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment
import com.hyphenate.easeim.common.livedatas.LiveDataBus
import com.hyphenate.easeim.common.constant.DemoConstant
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeim.DemoHelper
import com.hyphenate.chat.EMClient
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.easeim.R
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback
import com.hyphenate.easeim.common.net.Resource
import com.hyphenate.easeim.common.utils.ToastUtils
import com.hyphenate.easeim.section.base.BaseActivity
import com.hyphenate.easeim.section.chat.activity.ChatActivity
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel
import com.hyphenate.easeui.manager.EaseSystemMsgManager
import com.hyphenate.easeim.section.message.SystemMsgsActivity
import com.hyphenate.easeui.modules.conversation.EaseConversationListLayout
import com.hyphenate.easeui.utils.EaseCommonUtils

open class ConversationListFragment : EaseConversationListFragment() {
    private var mViewModel: ConversationListViewModel? = null
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        conversationListLayout.listAdapter.emptyLayoutId = R.layout.ease_layout_default_no_data
        initViewModel()
    }

    override fun onMenuItemClick(item: MenuItem, position: Int): Boolean {
        val info = conversationListLayout.getItem(position)
        val `object` = info.info
        if (`object` is EMConversation) {
            if (item.itemId == com.hyphenate.easeui.R.id.action_con_make_top) {
                conversationListLayout.makeConversationTop(position, info)
                return true
            } else if (item.itemId == com.hyphenate.easeui.R.id.action_con_cancel_top) {
                conversationListLayout.cancelConversationTop(position, info)
                return true
            } else if (item.itemId == com.hyphenate.easeui.R.id.action_con_delete) {
                showDeleteDialog(position, info)
                return true
            }
        }
        return super.onMenuItemClick(item, position)
    }

    private fun showDeleteDialog(position: Int, info: EaseConversationInfo) {
        SimpleDialogFragment.Builder(mContext as BaseActivity)
            .setTitle(R.string.delete_conversation)
            .setOnConfirmClickListener(R.string.delete) {
                conversationListLayout.deleteConversation(position, info)
                LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE)
                    .postValue(EaseEvent(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE))
            }
            .showCancelButton(true)
            .show()
    }

    override fun initListener() {
        super.initListener()
    }

    private fun test(){
        conversationListLayout.listAdapter
        val conversationListView=findViewById<EaseConversationListLayout>(com.hyphenate.easeui.R.id.list_conversation)
    }

    override fun initData() {
        //需要两个条件，判断是否触发从服务器拉取会话列表的时机，一是第一次安装，二则本地数据库没有会话列表数据
        if (DemoHelper.instance.isFirstInstall && EMClient.getInstance()
                .chatManager().allConversations.isEmpty()
        ) {
            mViewModel!!.fetchConversationsFromServer()
        } else {
            super.initData()
        }
    }

    private fun initViewModel() {
        mViewModel = ViewModelProvider(this).get(
            ConversationListViewModel::class.java
        )
        mViewModel?.deleteObservable?.observe(viewLifecycleOwner) { response: Resource<Boolean>? ->
            object : OnResourceParseCallback<Boolean>() {
                override fun onSuccess(data: Boolean?) {
                    LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(
                        EaseEvent(
                            DemoConstant.MESSAGE_CHANGE_CHANGE,
                            EaseEvent.TYPE.MESSAGE
                        )
                    )
                    //mViewModel.loadConversationList();
                    conversationListLayout.loadDefaultData()
                }
            }.let { parseResource(response, it) }
        }
        mViewModel!!.readObservable.observe(viewLifecycleOwner) { response: Resource<Boolean>? ->
            object : OnResourceParseCallback<Boolean>() {
                override fun onSuccess(data: Boolean?) {
                    LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(
                        EaseEvent(
                            DemoConstant.MESSAGE_CHANGE_CHANGE,
                            EaseEvent.TYPE.MESSAGE
                        )
                    )
                    conversationListLayout.loadDefaultData()
                }
            }.let { parseResource(response, it) }
        }
        mViewModel!!.conversationInfoObservable.observe(viewLifecycleOwner) { response: Resource<List<EaseConversationInfo?>?>? ->
            object : OnResourceParseCallback<List<EaseConversationInfo?>?>(true) {
                override fun onSuccess(data: List<EaseConversationInfo?>?) {
                    conversationListLayout.setData(data)
                }
            }.let {
                parseResource<List<EaseConversationInfo?>?>(
                    response,
                    it
                )
            }
        }
        val messageViewModel = ViewModelProvider(this).get(
            MessageViewModel::class.java
        )
        val messageChange = messageViewModel.messageChange
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.CONVERSATION_DELETE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.CONVERSATION_READ, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.CONTACT_ADD, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.CONTACT_UPDATE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { change: EaseEvent? -> loadList(change) }
        messageChange.with(DemoConstant.MESSAGE_CALL_SAVE, Boolean::class.java).observe(
            viewLifecycleOwner
        ) { event: Boolean? -> refreshList(event) }
        messageChange.with(DemoConstant.MESSAGE_NOT_SEND, Boolean::class.java).observe(
            viewLifecycleOwner
        ) { event: Boolean? -> refreshList(event) }
    }

    private fun refreshList(event: Boolean?) {
        if (event == null) {
            return
        }
        if (event) {
            conversationListLayout.loadDefaultData()
        }
    }

    private fun loadList(change: EaseEvent?) {
        if (change == null) {
            return
        }
        if (change.isMessageChange || change.isNotifyChange
            || change.isGroupLeave || change.isChatRoomLeave
            || change.isContactChange
            || change.type == EaseEvent.TYPE.CHAT_ROOM || change.isGroupChange
        ) {
            conversationListLayout.loadDefaultData()
        }
    }

    /**
     * 解析Resource<T>
     * @param response
     * @param callback
     * @param <T>
    </T></T> */
    fun <T> parseResource(response: Resource<T>?, callback: OnResourceParseCallback<T>) {
        if (mContext is BaseActivity) {
            (mContext as BaseActivity).parseResource(response, callback)
        }
    }

    /**
     * toast by string
     * @param message
     */
    fun showToast(message: String?) {
        ToastUtils.showToast(message)
    }

    override fun onItemClick(view: View, position: Int) {
        super.onItemClick(view, position)
        val item = conversationListLayout.getItem(position).info
        if (item is EMConversation) {
            if (EaseSystemMsgManager.getInstance().isSystemConversation(item)) {
                SystemMsgsActivity.actionStart(mContext)
            } else {
                toChatActivity(item)
            }
        }
    }

    protected open fun toChatActivity(item:EMConversation){
        ChatActivity.actionStart(
            mContext, item.conversationId(), EaseCommonUtils.getChatType(
                item
            )
        )
    }

    override fun notifyItemChange(position: Int) {
        super.notifyItemChange(position)
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE)
            .postValue(EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE))
    }

    override fun notifyAllChange() {
        super.notifyAllChange()
    }
}