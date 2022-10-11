package com.hyphenate.easeim.section.chat.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easecallkit.EaseCallKit
import com.hyphenate.easecallkit.base.EaseCallType
import com.hyphenate.easeim.DemoHelper
import com.hyphenate.easeim.ImDemoInit.application
import com.hyphenate.easeim.R
import com.hyphenate.easeim.common.constant.DemoConstant
import com.hyphenate.easeim.common.livedatas.LiveDataBus
import com.hyphenate.easeim.common.model.EmojiconExampleGroupData
import com.hyphenate.easeim.section.av.VideoCallActivity
import com.hyphenate.easeim.section.base.BaseActivity
import com.hyphenate.easeim.section.chat.activity.ForwardMessageActivity
import com.hyphenate.easeim.section.chat.activity.PickAtUserActivity
import com.hyphenate.easeim.section.chat.activity.SelectUserCardActivity
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel
import com.hyphenate.easeim.section.conference.ConferenceInviteActivity
import com.hyphenate.easeim.section.contact.activity.ContactDetailActivity
import com.hyphenate.easeim.section.dialog.DemoListDialogFragment
import com.hyphenate.easeim.section.dialog.FullEditDialogFragment
import com.hyphenate.easeim.section.dialog.LabelEditDialogFragment
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment
import com.hyphenate.easeim.section.group.GroupHelper
import com.hyphenate.easeim.section.me.activity.UserDetailActivity
import com.hyphenate.easeui.constants.EaseConstant
import com.hyphenate.easeui.domain.EaseUser
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.modules.chat.EaseChatFragment
import com.hyphenate.easeui.modules.chat.interfaces.OnRecallMessageResultListener
import com.hyphenate.easeui.modules.menu.EasePopupWindowHelper
import com.hyphenate.easeui.modules.menu.MenuItemBean
import com.hyphenate.easeui.utils.EaseCommonUtils
import com.hyphenate.util.EMLog
import com.message.ImMessageManager


open class ChatFragment : EaseChatFragment(), OnRecallMessageResultListener {
    private var viewModel: MessageViewModel? = null
    protected var clipboard: ClipboardManager? = null
    private var infoListener: OnFragmentInfoListener? = null
    private var dialog: Dialog? = null
    override fun initView() {
        super.initView()
        clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        viewModel = ViewModelProvider(this).get(
            MessageViewModel::class.java
        )

        //获取到聊天列表控件
        val messageListLayout = chatLayout.chatMessageListLayout
        messageListLayout.apply {
            //设置聊天列表背景
            this.background = ColorDrawable(Color.parseColor("#FFF5F5F5"))
            //设置默认头像
            this.setAvatarDefaultSrc(getAvatarDefaultSrc())
            //设置头像形状：0 为默认，1 为圆形，2 为方形
            this.setAvatarShapeType(1);
            //设置文本字体大小
            this.setItemTextSize( EaseCommonUtils.sp2px(requireContext(), 15f).toInt())
            //设置文本字体颜色
            this.setItemTextColor(Color.parseColor("#FF404040"));
            //设置时间线的背景
            //this.setTimeBackground(ContextCompat.getDrawable(mContext, R.color.gray_normal));
            //设置时间线的文本大小
            this.setTimeTextSize(EaseCommonUtils.sp2px(requireContext(), 13f).toInt())
            //设置时间线的文本颜色
            this.setTimeTextColor(Color.parseColor("#FF9A9A9A"));
            //设置聊天列表样式：两侧及均位于左侧
            //this.setItemShowType(EaseChatMessageListLayout.ShowType.LEFT);
        }

        //获取到菜单输入父控件
        val chatInputMenu = chatLayout.chatInputMenu;
//获取到菜单输入控件
        val primaryMenu = chatInputMenu.primaryMenu
//获取到扩展区域控件
        val chatExtendMenu = chatInputMenu.chatExtendMenu
//获取到表情区域控件
        val emojiconMenu = chatInputMenu.emojiconMenu

        chatLayout.setTargetLanguageCode(DemoHelper.instance.model.targetLanguage)
    }

    protected open fun getAvatarDefaultSrc():Drawable?{
        return ContextCompat.getDrawable(requireContext(), R.drawable.ease_default_avatar)
    }

    /**
     * 发送附件
     * 送花、发照片、发视频
     */
    protected open fun resetChatExtendMenu() {
        val chatExtendMenu = chatLayout.chatInputMenu.chatExtendMenu
        chatExtendMenu.clear()
        chatExtendMenu.registerMenuItem(
            com.hyphenate.easeui.R.string.attach_picture,
            com.hyphenate.easeui.R.drawable.ease_chat_image_selector,
            com.hyphenate.easeui.R.id.extend_item_picture
        )
        chatExtendMenu.registerMenuItem(
            com.hyphenate.easeui.R.string.attach_take_pic,
            com.hyphenate.easeui.R.drawable.ease_chat_takepic_selector,
            com.hyphenate.easeui.R.id.extend_item_take_picture
        )
        chatExtendMenu.registerMenuItem(
            com.hyphenate.easeui.R.string.attach_video,
            com.hyphenate.easeui.R.drawable.em_chat_video_selector,
            com.hyphenate.easeui.R.id.extend_item_video
        )

/*        //添加扩展槽
        //视频
        if (chatType == EaseConstant.CHATTYPE_SINGLE) {
            //inputMenu.registerExtendMenuItem(R.string.attach_voice_call, R.drawable.em_chat_voice_call_selector, EaseChatInputMenu.ITEM_VOICE_CALL, this);
            chatExtendMenu.registerMenuItem(
                com.hyphenate.easeui.R.string.attach_media_call,
                com.hyphenate.easeui.R.drawable.em_chat_video_call_selector,
                R.id.extend_item_video_call
            )
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) { // 音视频会议
            chatExtendMenu.registerMenuItem(
                com.hyphenate.easeui.R.string.voice_and_video_conference,
                com.hyphenate.easeui.R.drawable.em_chat_video_call_selector,
                R.id.extend_item_conference_call
            )
            //目前普通模式也支持设置主播和观众人数，都建议使用普通模式
            //inputMenu.registerExtendMenuItem(R.string.title_live, R.drawable.em_chat_video_call_selector, EaseChatInputMenu.ITEM_LIVE, this);
        }*/
        chatExtendMenu.registerMenuItem(
            com.hyphenate.easeui.R.string.attach_location,
            com.hyphenate.easeui.R.drawable.ease_chat_location_selector,
            com.hyphenate.easeui.R.id.extend_item_location
        )
        chatExtendMenu.registerMenuItem(
            com.hyphenate.easeui.R.string.attach_file,
            com.hyphenate.easeui.R.drawable.em_chat_file_selector,
            com.hyphenate.easeui.R.id.extend_item_file
        )
        //名片扩展
//        chatExtendMenu.registerMenuItem(
//            R.string.attach_user_card,
//            R.drawable.em_chat_user_card_selector,
//            R.id.extend_item_user_card
//        )
        //群组类型，开启消息回执，且是owner
        if (chatType == EaseConstant.CHATTYPE_GROUP && EMClient.getInstance().options.requireAck) {
            val group = DemoHelper.instance.groupManager.getGroup(conversationId)
            if (GroupHelper.isOwner(group)) {
                chatExtendMenu.registerMenuItem(
                    R.string.em_chat_group_delivery_ack,
                    R.drawable.demo_chat_delivery_selector,
                    R.id.extend_item_delivery
                )
            }
        }
        //添加扩展表情
        chatLayout.chatInputMenu.emojiconMenu.addEmojiconGroup(EmojiconExampleGroupData.getData())
    }

    override fun initListener() {
        super.initListener()
        chatLayout.setOnRecallMessageResultListener(this)
    }

    override fun initData() {
        super.initData()
        resetChatExtendMenu()
        chatLayout.chatInputMenu.primaryMenu.editText.setText(unSendMsg)
        chatLayout.turnOnTypingMonitor(DemoHelper.instance.model.isShowMsgTyping)
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE)
            .postValue(EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE))
        LiveDataBus.get().with(DemoConstant.MESSAGE_CALL_SAVE, Boolean::class.java).observe(
            viewLifecycleOwner
        ) { event: Boolean? ->
            if (event == null) {
                return@observe
            }
            if (event) {
                chatLayout.chatMessageListLayout.refreshToLatest()
            }
        }
        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event: EaseEvent? ->
            if (event == null) {
                return@observe
            }
            if (event.isMessageChange) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event: EaseEvent? ->
            if (event == null) {
                return@observe
            }
            if (event.isMessageChange) {
                chatLayout.chatMessageListLayout.refreshToLatest()
            }
        }
        LiveDataBus.get().with(DemoConstant.CONVERSATION_READ, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event: EaseEvent? ->
            if (event == null) {
                return@observe
            }
            if (event.isMessageChange) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }

        //更新用户属性刷新列表
        LiveDataBus.get().with(DemoConstant.CONTACT_ADD, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event: EaseEvent? ->
            if (event == null) {
                return@observe
            }
            if (event != null) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }
        LiveDataBus.get().with(DemoConstant.CONTACT_UPDATE, EaseEvent::class.java).observe(
            viewLifecycleOwner
        ) { event: EaseEvent? ->
            if (event == null) {
                return@observe
            }
            if (event != null) {
                chatLayout.chatMessageListLayout.refreshMessages()
            }
        }
    }

    private fun showDeliveryDialog() {
        FullEditDialogFragment.Builder(mContext as BaseActivity)
            .setTitle(R.string.em_chat_group_read_ack)
            .setOnConfirmClickListener(R.string.em_chat_group_read_ack_send) { view, content ->
                chatLayout.sendTextMessage(
                    content,
                    true
                )
            }
            .setConfirmColor(R.color.em_color_brand)
            .setHint(R.string.em_chat_group_read_ack_hint)
            .show()
    }

    private fun showSelectDialog() {
        DemoListDialogFragment.Builder(mContext as BaseActivity) //.setTitle(R.string.em_single_call_type)
            .setData(calls)
            .setCancelColorRes(R.color.black)
            .setWindowAnimations(R.style.animate_dialog)
            .setOnItemClickListener { view, position ->
                when (position) {
                    0 -> EaseCallKit.getInstance().startSingleCall(
                        EaseCallType.SINGLE_VIDEO_CALL,
                        conversationId,
                        null,
                        VideoCallActivity::class.java
                    )
                    1 -> EaseCallKit.getInstance().startSingleCall(
                        EaseCallType.SINGLE_VOICE_CALL,
                        conversationId,
                        null,
                        VideoCallActivity::class.java
                    )
                }
            }
            .show()
    }

    override fun onUserAvatarClick(username: String) {
        if (!TextUtils.equals(username, DemoHelper.instance.currentUser)) {
            var user = DemoHelper.instance.getUserInfo(username)
            if (user == null) {
                user = EaseUser(username)
            }
            val isFriend = DemoHelper.instance.model.isContact(username)
            if (isFriend) {
                user.contact = 0
            } else {
                user.contact = 3
            }
            ContactDetailActivity.actionStart(mContext, user)
        } else {
            UserDetailActivity.actionStart(mContext, null, null)
        }
    }

    override fun onUserAvatarLongClick(username: String) {}
    override fun onBubbleLongClick(v: View, message: EMMessage): Boolean {
        return false
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!chatLayout.chatMessageListLayout.isGroupChat) {
            return
        }
        if (count == 1 && "@" == s[start].toString()) {
            PickAtUserActivity.actionStartForResult(
                this@ChatFragment,
                conversationId,
                REQUEST_CODE_SELECT_AT_USER
            )
        }
    }

    override fun onBubbleClick(message: EMMessage): Boolean {
        return false
    }

    /**
     * 点加号后的事件
     */
    override fun onChatExtendMenuItemClick(view: View, itemId: Int) {
        super.onChatExtendMenuItemClick(view, itemId)
        when (itemId) {
            R.id.extend_item_video_call -> {
                showSelectDialog()
            }
            R.id.extend_item_conference_call -> {
                val intent = Intent(
                    context,
                    ConferenceInviteActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(DemoConstant.EXTRA_CONFERENCE_GROUP_ID, conversationId)
                context?.startActivity(intent)
            }
            R.id.extend_item_delivery -> { //群消息回执
                showDeliveryDialog()
            }
            R.id.extend_item_user_card -> {
                EMLog.d(TAG, "select user card")
                val userCardIntent = Intent(this.context, SelectUserCardActivity::class.java)
                userCardIntent.putExtra("toUser", conversationId)
                startActivityForResult(userCardIntent, REQUEST_CODE_SELECT_USER_CARD)
            }
            R.id.extend_item_send_flower->{
                onClickSendFlower()
            }
        }
    }

    protected open fun onClickSendFlower(){
        val msg=ImMessageManager.getFlowerMsg(conversationId)
        sendMessage(msg)
    }

    fun sendMessage(message:EMMessage?){
        chatLayout.sendMessage(message)
    }

    override fun onChatError(code: Int, errorMsg: String) {
        if (infoListener != null) {
            infoListener!!.onChatError(code, errorMsg)
        }
    }

    override fun onOtherTyping(action: String) {
        if (infoListener != null) {
            infoListener!!.onOtherTyping(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_AT_USER -> if (data != null) {
                    val username = data.getStringExtra("username")
                    chatLayout.inputAtUsername(username, false)
                }
                REQUEST_CODE_SELECT_USER_CARD -> if (data != null) {
                    val user = data.getSerializableExtra("user") as EaseUser?
                    user?.let { sendUserCardMessage(it) }
                }
            }
        }
    }

    /**
     * Send user card message
     * @param user
     */
    private fun sendUserCardMessage(user: EaseUser) {
        val message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM)
        val body = EMCustomMessageBody(DemoConstant.USER_CARD_EVENT)
        val params: MutableMap<String, String> = HashMap()
        params[DemoConstant.USER_CARD_ID] = user.username
        params[DemoConstant.USER_CARD_NICK] = user.nickname
        params[DemoConstant.USER_CARD_AVATAR] = user.avatar
        body.params = params
        message.body = body
        message.to = conversationId
        chatLayout.sendMessage(message)
    }

    override fun onStop() {
        super.onStop()
        //保存未发送的文本消息内容
        if (mContext != null && mContext.isFinishing) {
            if (chatLayout.chatInputMenu != null) {
                saveUnSendMsg(chatLayout.inputContent)
                LiveDataBus.get().with(DemoConstant.MESSAGE_NOT_SEND).postValue(true)
            }
        }
    }
    //================================== for video and voice start ====================================
    /**
     * 保存未发送的文本消息内容
     * @param content
     */
    private fun saveUnSendMsg(content: String) {
        DemoHelper.instance.model.saveUnSendMsg(conversationId, content)
    }

    private val unSendMsg: String
        private get() = DemoHelper.instance.model.getUnSendMsg(conversationId)

    override fun onPreMenu(helper: EasePopupWindowHelper, message: EMMessage, v: View) {
        //默认两分钟后，即不可撤回
        if (System.currentTimeMillis() - message.msgTime > 2 * 60 * 1000) {
            helper.findItemVisible(R.id.action_chat_recall, false)
        }
        val type = message.type
        helper.findItemVisible(R.id.action_chat_forward, false)
        when (type) {
            EMMessage.Type.TXT -> {
                if (!message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)
                    && !message.getBooleanAttribute(DemoConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)
                ) {
                    helper.findItemVisible(R.id.action_chat_forward, true)
                }
                if (v.id == com.hyphenate.easeui.R.id.subBubble) {
                    helper.findItemVisible(R.id.action_chat_forward, false)
                }
            }
            EMMessage.Type.IMAGE -> helper.findItemVisible(R.id.action_chat_forward, true)
        }
        if (chatType == DemoConstant.CHATTYPE_CHATROOM) {
            helper.findItemVisible(R.id.action_chat_forward, true)
        }
    }

    override fun onMenuItemClick(item: MenuItemBean, message: EMMessage): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_chat_forward) {
            ForwardMessageActivity.actionStart(mContext, message.msgId)
            return true
        } else if (itemId == R.id.action_chat_delete) {
            showDeleteDialog(message)
            return true
        } else if (itemId == R.id.action_chat_recall) {
            showProgressBar()
            chatLayout.recallMessage(message)
            return true
        } else if (itemId == com.hyphenate.easeui.R.id.action_chat_reTranslate) {
            AlertDialog.Builder(context)
                .setTitle(mContext.getString(R.string.using_translate))
                .setMessage(mContext.getString(R.string.retranslate_prompt))
                .setPositiveButton(mContext.getString(R.string.confirm)) { dialog, which ->
                    chatLayout.translateMessage(
                        message,
                        false
                    )
                }
                .show()
            return true
        } else if (itemId == com.hyphenate.easeui.R.id.action_chat_label) {
            showLabelDialog(message)
            return true
        }
        return false
    }

    private fun showProgressBar() {
        val view = View.inflate(mContext, R.layout.demo_layout_progress_recall, null)
        dialog = Dialog(mContext, R.style.dialog_recall)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog!!.setContentView(view, layoutParams)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.show()
    }

    private fun showDeleteDialog(message: EMMessage) {
        SimpleDialogFragment.Builder(mContext as BaseActivity)
            .setTitle(getString(R.string.em_chat_delete_title))
            .setConfirmColor(R.color.red)
            .setOnConfirmClickListener(getString(R.string.delete)) {
                chatLayout.deleteMessage(
                    message
                )
            }
            .showCancelButton(true)
            .show()
    }

    private fun showLabelDialog(message: EMMessage) {
        DemoListDialogFragment.Builder(mContext as BaseActivity)
            .setData(labels)
            .setCancelColorRes(R.color.black)
            .setWindowAnimations(R.style.animate_dialog)
            .setOnItemClickListener { view, position -> showLabelDialog(message, labels[position]) }
            .show()
    }

    private fun showLabelDialog(message: EMMessage, label: String) {
        LabelEditDialogFragment.Builder(mContext as BaseActivity)
            .setOnConfirmClickListener { view, reason ->
                EMLog.e(
                    "ReportMessage：",
                    "msgId: " + message.msgId + "label: " + label + " reason: " + reason
                )
                SimpleDialogFragment.Builder(mContext as BaseActivity)
                    .setTitle(getString(R.string.report_delete_title))
                    .setConfirmColor(R.color.em_color_brand)
                    .setOnConfirmClickListener(getString(R.string.confirm)) {
                        EMClient.getInstance().chatManager()
                            .asyncReportMessage(message.msgId, label, reason, object : EMCallBack {
                                override fun onSuccess() {
                                    EMLog.e("ReportMessage：", "onSuccess 举报成功")
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            "举报成功",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onError(code: Int, error: String) {
                                    EMLog.e("ReportMessage：", "onError 举报失败: code $code  : $error")
                                    runOnUiThread {
                                        Toast.makeText(
                                            context,
                                            "举报失败： code: $code desc: $error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onProgress(progress: Int, status: String) {}
                            })
                    }
                    .showCancelButton(true)
                    .show()
            }.show()
    }

    fun setOnFragmentInfoListener(listener: OnFragmentInfoListener?) {
        infoListener = listener
    }

    override fun recallSuccess(message: EMMessage) {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    override fun recallFail(code: Int, errorMsg: String) {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    interface OnFragmentInfoListener {
        fun onChatError(code: Int, errorMsg: String?)
        fun onOtherTyping(action: String?)
    }

    override fun translateMessageFail(message: EMMessage, code: Int, error: String) {
        AlertDialog.Builder(context)
            .setTitle(mContext.getString(R.string.unable_translate))
            .setMessage("$error.")
            .setPositiveButton(mContext.getString(R.string.confirm)) { dialog, which -> dialog.dismiss() }
            .show()
    }

    companion object {
        private val TAG = ChatFragment::class.java.simpleName
        private const val REQUEST_CODE_SELECT_USER_CARD = 20
        private const val REQUEST_CODE_SELECT_AT_USER = 15
        private val calls = arrayOf(
            application!!.applicationContext.getString(R.string.video_call),
            application!!.applicationContext.getString(
                R.string.voice_call
            )
        )
        private val labels = arrayOf(
            application!!.applicationContext.getString(R.string.tab_politics),
            application!!.applicationContext.getString(R.string.tab_yellow_related),
            application!!.applicationContext.getString(R.string.tab_advertisement),
            application!!.applicationContext.getString(R.string.tab_abuse),
            application!!.applicationContext.getString(R.string.tab_violent),
            application!!.applicationContext.getString(R.string.tab_contraband),
            application!!.applicationContext.getString(R.string.tab_other)
        )
    }
}