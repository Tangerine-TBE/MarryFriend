package com.twx.marryfriend.message

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.message.ImMessageManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.databinding.ActivityChatBinding
import com.twx.marryfriend.message.adapter.ChatAdapter
import com.twx.marryfriend.message.model.ChatModel
import com.twx.marryfriend.utils.DynamicFileProvider
import com.twx.marryfriend.utils.emoji.EmojiUtils
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class ChatActivity:AppCompatActivity(/*R.layout.activity_chat*/) {
    companion object{
        private const val FRIEND_ID_KEY="friend_id_key"
        private const val NICKNAME_KEY="nickname_key"
        private const val HEAD_IMAGE_KEY="head_image_key"
        fun getIntent(context: Context, friendId: String,nickname:String?,headImage:String?):Intent{
            val intent=Intent(context,ChatActivity::class.java)
            intent.putExtra(FRIEND_ID_KEY,friendId)
            intent.putExtra(NICKNAME_KEY,nickname?:friendId)
            intent.putExtra(HEAD_IMAGE_KEY,headImage)
            return intent
        }

        private const val PICK_IMAGE_CODE=1
        private const val CAMERA_TAKE_CODE=2
    }
    private val friendId by lazy {
        intent?.getStringExtra(FRIEND_ID_KEY)
    }
    private val nickname by lazy {
        intent?.getStringExtra(NICKNAME_KEY)
    }
    private val headImage by lazy {
        intent?.getStringExtra(HEAD_IMAGE_KEY)
    }
    private val chatAdapter by lazy {
        ChatAdapter(friendId,nickname,headImage)
    }
    private val chatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }
    private lateinit var dataBinding :ActivityChatBinding
    private val chatModel by lazy {
        ChatModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding=DataBindingUtil.setContentView<ActivityChatBinding>(this,R.layout.activity_chat)
        KeyboardUtils.fixAndroidBug5497(this)
//        myActionBar.setTitle(nickname.toString())
        chatModel.apply {
            this.title=nickname.toString()
            this.isEmojiShow
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
            friendId.also { fid ->
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
//        EmojiUtils.getEmojiList()
        initListener()
    }

    private fun initListener(){
        sendMsg.setOnClickListener {
            val textMsg=chatModel.editText.get()?:return@setOnClickListener
            val msg=ImMessageManager.sendTextMsg(friendId?:return@setOnClickListener,textMsg)
            if (msg!=null) {
                chatAdapter.addItem(msg)
                chatModel.editText.set("")
                chatRecyclerView.scrollToPosition(0)
            }
        }
        swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                friendId.also { fid ->
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
            toast("弹出表情")
        }
        addAnnex.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            chatModel.isMoreMsgShow=true
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
                            val authority =
                                this@ChatActivity.packageName.toString() + ".fileProvider"
                            val contentUri: Uri =
                                DynamicFileProvider.getUriForFile(this@ChatActivity,
                                    authority,
                                    cf)
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
    }
    private var cacheFile:File?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PICK_IMAGE_CODE){
            val msg=ImMessageManager.sendImageMsg(friendId?:return,data?.data?:return)
            if (msg!=null) {
                chatAdapter.addItem(msg)
            }
        }else if (requestCode==CAMERA_TAKE_CODE){
            cacheFile?.also {
                val msg=ImMessageManager.sendImageMsg(friendId?:return,it.absolutePath)
                if (msg!=null) {
                    chatAdapter.addItem(msg)
                }
            }
        }
    }
}