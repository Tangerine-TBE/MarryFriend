package com.twx.marryfriend.message

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyphenate.chat.EMConversation
import com.hyphenate.easeim.section.conversation.ConversationListFragment
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.adapter.EaseAdapterDelegate
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.databinding.FragmentImMessageBinding
import com.twx.marryfriend.databinding.ItemMessageFollowBinding
import com.twx.marryfriend.getUserExt
import com.twx.marryfriend.message.model.ConversationsModel
import com.twx.marryfriend.mutual.MutualLikeActivity
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import kotlinx.android.synthetic.main.fragment_im_message.*
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ImConversationFragment: ConversationListFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(ConversationViewModel::class.java)
    }
    private val conversationsModel by lazy {
        ConversationsModel()
    }

    private val loadingDialog by lazy {
        LoadingDialogManager
            .createLoadingDialog()
            .create(requireContext())
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        llRoot.addView(LayoutInflater.from(requireContext()).inflate(R.layout.fragment_im_message,llRoot,false), 0)

        conversationListLayout.listAdapter.emptyLayoutId = R.layout.layout_conversation_not_data
    }

    /**
     * 会话适配器
     * ConversationViewModel#getFriendsInfo 用户信息接口
     */
    override fun getConversationDelegate(): EaseAdapterDelegate<*, *> {
        return MySingleConversationDelegate()
    }


     override fun initListener(){
         super.initListener()
         mutualLikeView.setOnClickListener {
            startActivity(Intent(requireContext(), MutualLikeActivity::class.java))
        }
    }

    fun makeConversationTop(item: EMConversation){
        val index=conversationListLayout.listAdapter.data.indexOfFirst {
            it.info==item
        }
        if (index!=-1){
            val info=conversationListLayout.getItem(index)
            conversationListLayout.makeConversationTop(index,info)
        }
    }

    /**
     * TODO
     */
    fun makeConversationTop(conversationId: String){
        val item=conversationListLayout.listAdapter.data.find {
            val i=it.info
            i is EMConversation&&i.conversationId()==conversationId
        }?.info as? EMConversation
        makeConversationTop(item?:return)
    }

    private val startActivityForResult =   registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode==Activity.RESULT_OK){
                val cid=ImChatActivity.getConversationId(it.data)
                if (cid!=null){
                    makeConversationTop(cid)
                }
            }
        })
    override fun toChatActivity(item: EMConversation) {
        val imUserInfo=EaseIM.getInstance().userProvider.getUser(item.conversationId())
        val ext=imUserInfo.getUserExt()
        if (UserInfo.isVip()||ext?.isSuperVip==true||ext?.isMutualLike==true){
            val isRealName=ext?.isRealName?:false
            startActivityForResult.launch(ImChatActivity.getIntent(requireContext(),item.conversationId(), isRealName = isRealName))
        }else{
            startActivity(IntentManager.getVipIntent(requireContext(), vipGif = VipGifEnum.Inbox))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshMutualLike()
        refreshFollow()
        conversationListLayout.refreshList()
    }

    private fun refreshMutualLike(){
        iLog("加载一下")
        loadingDialog.show()
        lifecycleScope.launch {
            try {
                val mutualLike=viewModel.getMutualLike()
                conversationsModel.laterLikeCount = mutualLike.total?:0
                conversationsModel.list = mutualLike.list

                mutualLikeView.visibility=if (!conversationsModel.list.isNullOrEmpty()){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                laterLikeCountView.text=conversationsModel.laterLikeCount.toString()

                mutualLikeView1.visibility=if (conversationsModel.imageHead1!=null){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                mutualLikeView2.visibility=if (conversationsModel.imageHead2!=null){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                mutualLikeView3.visibility=if (conversationsModel.imageHead3!=null){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                mutualLikeView4.visibility=if (conversationsModel.imageHead4!=null){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                mutualLikeView5.visibility=if (conversationsModel.imageHead5!=null){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                Glide.with(mutualLikeHead1)
                    .load(conversationsModel.imageHead1)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead1)

                Glide.with(mutualLikeHead2)
                    .load(conversationsModel.imageHead2)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead2)

                Glide.with(mutualLikeHead3)
                    .load(conversationsModel.imageHead3)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead3)

                Glide.with(mutualLikeHead4)
                    .load(conversationsModel.imageHead4)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead4)

                Glide.with(mutualLikeHead5)
                    .load(conversationsModel.imageHead5)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead5)
            }catch (e:Exception){
                eLog(e.stackTraceToString())
            }
            loadingDialog.dismiss()
        }
    }

    private var followHolder:BaseViewHolder?=null
    private var isAdd=false
    private fun refreshFollow(){
        lifecycleScope.launch {
            val result=try {
                viewModel.getFollowCountAndImg()
            }catch (e:Exception){
                null
            }
            if (result!=null){
                if (followHolder==null&&!isAdd){
                    isAdd=true
                    conversationListLayout.addHeaderAdapter(object :RecyclerView.Adapter<BaseViewHolder>(){

                        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                            return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_follow,parent,false))
                        }

                        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
                            followHolder=holder
                            holder.bindView(result)
                        }

                        override fun getItemCount(): Int {
                            return 1
                        }

                    })
                }
                followHolder?.bindView(result)
            }
        }
    }

    private fun BaseViewHolder.bindView(result:Pair<Int,String>){
        this.setText(R.id.messageUserNickname,result.first.toString()+"人关注了我")
        this.setImage(R.id.messageHead,UserInfo.getReversedDefHeadImage())
        val imageView=this.getView<ImageView>(R.id.messageHead)
        Glide.with(imageView)
            .load(result.second)
            .error(UserInfo.getReversedDefHeadImage())
            .placeholder(UserInfo.getReversedDefHeadImage())
            .into(imageView)
        this.itemView.setOnClickListener {
            if (UserInfo.isVip()){
                it.context.startActivity(IntentManager.getFocusIntent(it.context))
            }else{
                it.context.startActivity(IntentManager.getSuperVipIntent(it.context, sVipGifEnum = SVipGifEnum.FocusMe))
            }
        }
    }

}