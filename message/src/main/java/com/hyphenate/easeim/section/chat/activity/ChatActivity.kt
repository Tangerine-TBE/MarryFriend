package com.hyphenate.easeim.section.chat.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.DemoHelper.Companion.instance
import com.hyphenate.easeim.R
import com.hyphenate.easeim.common.constant.DemoConstant
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback
import com.hyphenate.easeim.common.net.Resource
import com.hyphenate.easeim.section.base.BaseInitActivity
import com.hyphenate.easeim.section.chat.fragment.ChatFragment
import com.hyphenate.easeim.section.chat.fragment.ChatFragment.OnFragmentInfoListener
import com.hyphenate.easeim.section.chat.viewmodel.ChatViewModel
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel
import com.hyphenate.easeim.section.group.GroupHelper
import com.hyphenate.easeim.section.group.activity.ChatRoomDetailActivity
import com.hyphenate.easeim.section.group.activity.GroupDetailActivity
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.constants.EaseConstant
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.widget.EaseTitleBar.OnBackPressListener
import com.hyphenate.easeui.widget.EaseTitleBar.OnRightClickListener
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.demo_activity_chat.*

open class ChatActivity : BaseInitActivity(), OnBackPressListener, OnRightClickListener,
    OnFragmentInfoListener {
    override fun getLayoutId(): Int {
        return R.layout.demo_activity_chat
    }
    companion object {
        @JvmStatic
        fun actionStart(context: Context, conversationId: String?, chatType: Int) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType)
            context.startActivity(intent)
        }
    }

    protected val conversationId by lazy {
        intent.getStringExtra(EaseConstant.EXTRA_CONVERSATION_ID)
    }
    protected val chatType by lazy {
        intent.getIntExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE)
    }
    protected val historyMsgId by lazy { intent.getStringExtra(DemoConstant.HISTORY_MSG_ID) }
    private val fragment by lazy {
        getChatFragment()
    }
    private var viewModel: ChatViewModel? = null

    protected open fun getChatFragment():ChatFragment{
        return ChatFragment()
            .also {
                val bundle = Bundle()
                bundle.putString(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
                bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, chatType)
                bundle.putString(DemoConstant.HISTORY_MSG_ID, historyMsgId)
                bundle.putBoolean(EaseConstant.EXTRA_IS_ROAM, instance.model.isMsgRoaming)
                it.arguments = bundle
            }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        initChatFragment()
    }

    private fun initChatFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment, "chat")
            .commit()
    }

    override fun initListener() {
        super.initListener()
        fragment.setOnFragmentInfoListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            initIntent(intent)
            initChatFragment()
            initData()
        }
    }

    override fun initData() {
        super.initData()
        val conversation = EMClient.getInstance().chatManager().getConversation(conversationId)
        val messageViewModel = ViewModelProvider(this).get(
            MessageViewModel::class.java
        )
        viewModel = ViewModelProvider(this).get(
            ChatViewModel::class.java
        )
        viewModel!!.deleteObservable.observe(this) { response: Resource<Boolean?>? ->
            parseResource(response, object : OnResourceParseCallback<Boolean?>() {
                override fun onSuccess(data: Boolean?) {
                    finish()
                    val event =
                        EaseEvent.create(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE)
                    messageViewModel.setMessageChange(event)
                }
            })
        }
        viewModel!!.chatRoomObservable.observe(this) { response: Resource<EMChatRoom?>? ->
            parseResource(response, object : OnResourceParseCallback<EMChatRoom?>() {
                override fun onSuccess(data: EMChatRoom?) {
                    setDefaultTitle()
                }
            })
        }
        messageViewModel.messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent::class.java)
            .observe(this) { event: EaseEvent? ->
                if (event == null) {
                    return@observe
                }
                if (event.isGroupLeave && TextUtils.equals(conversationId, event.message)) {
                    finish()
                }
            }
        messageViewModel.messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent::class.java)
            .observe(this) { event: EaseEvent? ->
                if (event == null) {
                    return@observe
                }
                if (event.isChatRoomLeave && TextUtils.equals(conversationId, event.message)) {
                    finish()
                }
            }
        messageViewModel.messageChange.with(DemoConstant.MESSAGE_FORWARD, EaseEvent::class.java)
            .observe(this) { event: EaseEvent? ->
                if (event == null) {
                    return@observe
                }
                if (event.isMessageChange) {
                    showSnackBar(event.event)
                }
            }
        messageViewModel.messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent::class.java)
            .observe(this) { event: EaseEvent? ->
                if (event == null) {
                    return@observe
                }
                if (conversation == null) {
                    finish()
                }
            }
        setDefaultTitle()
    }

    fun sendMessage(message: EMMessage?){
        fragment.sendMessage(message)
    }

    private fun showSnackBar(event: String) {
        toast(event)
    }

    private fun setDefaultTitle() {
        val title: String?
        title = if (chatType == DemoConstant.CHATTYPE_GROUP) {
            GroupHelper.getGroupName(conversationId)
        } else if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
            val room = EMClient.getInstance().chatroomManager().getChatRoom(conversationId)
            if (room == null) {
                viewModel!!.getChatRoom(conversationId)
                return
            }
            if (TextUtils.isEmpty(room.name)) conversationId else room.name
        } else {
            val userProvider = EaseIM.getInstance().userProvider
            if (userProvider != null) {
                val user = userProvider.getUser(conversationId)
                if (user != null) {
                    user.nickname
                } else {
                    conversationId
                }
            } else {
                conversationId
            }
        }
        myActionBar.setTitle(title?:"")
    }

    override fun onBackPress(view: View) {
        onBackPressed()
    }

    override fun onRightClick(view: View) {
        if (chatType == DemoConstant.CHATTYPE_SINGLE) {
            //跳转到单聊设置页面
            SingleChatSetActivity.actionStart(mContext, conversationId)
        } else {
            // 跳转到群组设置
            if (chatType == DemoConstant.CHATTYPE_GROUP) {
                GroupDetailActivity.actionStart(mContext, conversationId)
            } else if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
                ChatRoomDetailActivity.actionStart(mContext, conversationId)
            }
        }
    }

    override fun onChatError(code: Int, errorMsg: String?) {
        showToast(errorMsg)
    }

    override fun onOtherTyping(action: String?) {
        if (TextUtils.equals(action, "TypingBegin")) {
            myActionBar.setTitle(getString(com.hyphenate.easeui.R.string.alert_during_typing))
        } else if (TextUtils.equals(action, "TypingEnd")) {
            setDefaultTitle()
        }
    }
}