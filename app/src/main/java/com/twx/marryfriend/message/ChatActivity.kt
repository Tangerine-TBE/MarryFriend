package com.twx.marryfriend.message

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hyphenate.chat.EMMessageBody
import com.message.ImMessageManager
import com.message.chat.*
import com.twx.marryfriend.R
import com.twx.marryfriend.databinding.ActivityChatBinding
import com.twx.marryfriend.dialog.createDialog
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.adapter.ChatAdapter
import com.twx.marryfriend.message.adapter.ChatEmojiAdapter
import com.twx.marryfriend.message.model.ChatModel
import com.twx.marryfriend.toUri
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ChatActivity:AppCompatActivity(/*R.layout.activity_chat*/) {
    companion object{
        private const val FRIEND_ID_KEY="friend_id_key"
        private const val NICKNAME_KEY="nickname_key"
        private const val HEAD_IMAGE_KEY="head_image_key"
        private const val IS_REAL_NAME_KEY="head_image_key"

        /**
         * @param conversationId 对方id
         * @param nickname 昵称
         * @param headImage 头像
         * @param isRealName 是否实名
         */
        fun getIntent(context: Context, conversationId: String, nickname:String?, headImage:String?, isRealName:Boolean):Intent{
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra(FRIEND_ID_KEY,conversationId)
            intent.putExtra(NICKNAME_KEY,nickname?:conversationId)
            intent.putExtra(HEAD_IMAGE_KEY,headImage)
            intent.putExtra(IS_REAL_NAME_KEY,isRealName)

            ImMessageManager.ackConversationRead(conversationId)
            return intent
        }

        private const val PICK_IMAGE_CODE=1
        private const val CAMERA_TAKE_CODE=2
    }
    private val conversationId by lazy {
        intent?.getStringExtra(FRIEND_ID_KEY)
    }
    private val nickname by lazy {
        intent?.getStringExtra(NICKNAME_KEY)
    }
    private val headImage by lazy {
        intent?.getStringExtra(HEAD_IMAGE_KEY)
    }
    private val isRealName by lazy {
        intent?.getBooleanExtra(IS_REAL_NAME_KEY,false)
    }
    private val chatAdapter by lazy {
        ChatAdapter(conversationId,nickname,headImage)
    }
    private val chatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }
    private lateinit var dataBinding :ActivityChatBinding
    private val chatModel by lazy {
        ChatModel()
    }
    private val chatEmojiAdapter by lazy {
        ChatEmojiAdapter()
    }
    private val chatSettingDialog by lazy {
        createDialog(R.layout.dialog_chat_setting)
            .also {dialog->
                dialog.window?.setGravity(Gravity.BOTTOM)
                dialog.findViewById<View>(R.id.closeDialog).setOnClickListener {
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.seeTaInfo).setOnClickListener {
                    startActivity(FriendInfoActivity.getIntent(this,conversationId?.toIntOrNull()?:return@setOnClickListener))
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.setTop).setOnClickListener {
                    toast("置顶")
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.blockFriends).setOnClickListener {
                    toast("屏蔽好友")
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.report).setOnClickListener {
                    toast("举报")
                    dialog.dismiss()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding=DataBindingUtil.setContentView<ActivityChatBinding>(this,R.layout.activity_chat)
        KeyboardUtils.fixAndroidBug5497(this)
//        myActionBar.setTitle(nickname.toString())
        chatModel.apply {
            this.title=nickname.toString()
            this.isEmojiShow
            this.isRealName=this@ChatActivity.isRealName?:false
        }
        dataBinding.chatModel=chatModel
        editText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                
            }

            override fun afterTextChanged(s: Editable?) {
                chatModel.isEditTextEmpty=(chatModel.editText.get()?.isEmpty()?:true)
            }

        })
        
        chatRecyclerView.layoutManager=LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true)
        chatRecyclerView.adapter=chatAdapter

        lifecycleScope.launch {
            conversationId.also { fid ->
                if (fid!=null) {
                    try {
                        val listChatItemModel=chatViewModel.getHistoryMessage(fid)
                        chatAdapter.setData(listChatItemModel)
                        ImMessageManager.markAllMsgAsRead(fid)
                    }catch (e:Exception){
                        eLog(e.stackTraceToString())
                    }
                }else{
                    toast("好友id为空")
                }
            }
        }
        ImMessageManager.observeNewMessage(this){ mutableList ->
            mutableList?:return@observeNewMessage
            mutableList.filter {
                it.from==conversationId
            }.forEach {
                chatAdapter.addItem(it)
                chatRecyclerView.scrollToPosition(0)
                ImMessageManager.markAllMsgAsRead(it.from)
                when(it){
                    is CmdMessage -> {
                        ImMessageManager.sendReadAck(it.emMessage)
                    }
                    is SendFlowerMessage -> {
                        ImMessageManager.sendReadAck(it.emMessage)
                    }
                    is FileMessage -> {

                    }
                    is ImageMessage -> {

                    }
                    is LocationMessage -> {
                        ImMessageManager.sendReadAck(it.emMessage)
                    }
                    is TxtMessage -> {
                        ImMessageManager.sendReadAck(it.emMessage)
                    }
                    is VideoMessage -> {

                    }
                    is VoiceMessage -> {

                    }
                }
            }
        }
        initListener()
        emojiRecyclerView.layoutManager=GridLayoutManager(this,8)
        emojiRecyclerView.adapter=chatEmojiAdapter
        chatEmojiAdapter.itemViewClick={
            editText.setText(editText.text.toString()+it)
            editText.setSelection(editText.text.length)
        }
    }

    private fun initListener(){
        chatSetting.setOnClickListener {
            chatSettingDialog.show()
        }
        friendRealName.setOnClickListener {
            toast("发送小助手消息,实名认证")
        }
        sendMsg.setOnClickListener {
            val textMsg=chatModel.editText.get()?:return@setOnClickListener
            val msg=ImMessageManager.sendTextMsg(conversationId?:return@setOnClickListener,textMsg)
            if (msg!=null) {
                chatAdapter.addItem(msg)
                chatModel.editText.set("")
                chatRecyclerView.scrollToPosition(0)
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                conversationId.also { fid ->
                    if (fid!=null) {
                        try {
                            val listChatItemModel=chatViewModel.getHistoryMessage(fid)
                            chatAdapter.addAllData(listChatItemModel)
                        }catch (e:Exception){
                            eLog(e.stackTraceToString())
                        }
                    }else{
                        toast("好友id为空")
                    }
                }
                swipeRefreshLayout.isRefreshing=false
            }
        }
        emoji.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            chatModel.isMoreMsgShow=false
            chatModel.isEmojiShow=true
        }
        keyboard.setOnClickListener {
            chatModel.isEmojiShow=false
            chatModel.isMoreMsgShow=false
            KeyboardUtils.showSoftInput(this)
        }
        addAnnex.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            chatModel.isMoreMsgShow=!chatModel.isMoreMsgShow
        }
        msgAlbum.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),PICK_IMAGE_CODE)
        }
        takePicture.setOnClickListener {
            XXPermissions.with(this)
                .permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>?,
                        all: Boolean,
                    ) {
                        val cf:File
                        cacheFile=File(this@ChatActivity.cacheDir.absolutePath+File.separator+"${System.currentTimeMillis()}.jpeg")
                            .also {
                                cf=it
                            }
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        // 如果在Android7.0以上,使用FileProvider获取Uri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            val contentUri: Uri =cf.toUri(this@ChatActivity)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                        } else {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(cf))
                        }
                        startActivityForResult(intent, CAMERA_TAKE_CODE)
                    }

                    override fun onDenied(
                        permissions: MutableList<String>?,
                        never: Boolean,
                    ) {
                        super.onDenied(permissions, never)
                        toast("请授予应用相关权限")
                    }

                })
        }
        sendFlowers.setOnClickListener {
            val msg=ImMessageManager.sendFlower(conversationId?:return@setOnClickListener)
            if (msg!=null) {
                chatAdapter.addItem(msg)
                chatRecyclerView.scrollToPosition(0)
            }
        }
        voiceAndKeyboard.setOnClickListener {
            it.isSelected=!it.isSelected
            chatModel.isVoiceShow=it.isSelected
        }

        val requestAudio = registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }
        inputVoice.onPressedListener={
            if (it){
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
                    chatViewModel.startRecorder()
                }else{
                    AlertDialog.Builder(this)
                        .setTitle("申请权限")
                        .setMessage("发送语音需要您授予应用录音权限，是否允许应用申请录音权限？")
                        .setPositiveButton("允许"){_,_->
                            requestAudio.launch(Manifest.permission.RECORD_AUDIO)
                        }
                        .setNegativeButton("不允许"){_,_->

                        }
                        .show()
                }
            }else{
                val uri=chatViewModel.stopRecorder()
                val id=conversationId
                if (uri!=null&&id!=null){
                    val msg=ImMessageManager.sendVoiceMsg(id,uri,chatViewModel.getVoiceLength())
                    if (msg!=null) {
                        sendMsg(msg)
                    }
                }
            }
        }
    }
    private var cacheFile:File?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PICK_IMAGE_CODE){
            val msg=ImMessageManager.sendImageMsg(conversationId?:return,data?.data?:return)
            if (msg!=null) {
                sendMsg(msg)
            }
        }else if (requestCode==CAMERA_TAKE_CODE){
            cacheFile?.also {
                val msg=ImMessageManager.sendImageMsg(conversationId?:return,it.absolutePath)
                if (msg!=null) {
                    sendMsg(msg)
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val intArray=IntArray(2)
        inputView.getLocationInWindow(intArray)

        val x=ev?.rawX?:0f
        val y=ev?.rawY?:0f
        if(y<intArray[1]){
            KeyboardUtils.hideSoftInput(this)
            chatModel.isMoreMsgShow=false
        }else{
            lifecycleScope.launch {
                delay(100)
                if (chatModel.isMoreMsgShow&&KeyboardUtils.isSoftInputVisible(this@ChatActivity)){
                    chatModel.isMoreMsgShow=false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun sendMsg(msg: Message<out EMMessageBody>){
        chatAdapter.addItem(msg)
        chatRecyclerView.scrollToPosition(0)
    }
}