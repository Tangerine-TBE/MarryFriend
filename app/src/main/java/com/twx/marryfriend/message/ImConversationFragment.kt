package com.twx.marryfriend.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyphenate.chat.EMConversation
import com.hyphenate.easeim.section.conversation.ConversationListFragment
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.adapter.EaseAdapterDelegate
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
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

class ImConversationFragment: ConversationListFragment() {
    private val viewModel by lazy {
        ViewModelProvider(this).get(ConversationViewModel::class.java)
    }
    private val conversationsModel by lazy {
        ConversationsModel()
    }
    private var dataBinding: FragmentImMessageBinding?=null

    private val loadingDialog by lazy {
        LoadingDialogManager
            .createLoadingDialog()
            .create(requireContext())
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        dataBinding=DataBindingUtil.inflate<FragmentImMessageBinding>(LayoutInflater.from(requireContext()),R.layout.fragment_im_message,llRoot,false)
        dataBinding?.lifecycleOwner=this
        llRoot.addView(dataBinding?.root, 0)
        conversationListLayout.listAdapter.emptyLayoutId = R.layout.layout_conversation_not_data
        lifecycleScope.launch {
            val result=try {
                viewModel.getFollowCountAndImg()
            }catch (e:Exception){
                null
            }
            if (result!=null){
                conversationListLayout.addHeaderAdapter(object :RecyclerView.Adapter<BaseViewHolder>(){

                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                        val followView=DataBindingUtil.inflate<ItemMessageFollowBinding>(LayoutInflater.from(requireContext()),R.layout.item_message_follow,conversationListLayout,false)
                        return BaseViewHolder(followView.root)
                    }

                    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
                        holder.setText(R.id.messageUserNickname,result.first.toString()+"人关注了我")
                        holder.setImage(R.id.messageHead,UserInfo.getReversedDefHeadImage())
                        val imageView=holder.getView<ImageView>(R.id.messageHead)
                        Glide.with(imageView)
                            .load(result.second)
                            .error(UserInfo.getReversedDefHeadImage())
                            .placeholder(UserInfo.getReversedDefHeadImage())
                            .into(imageView)
//                        holder.setImage(R.id.messageHead,result.second,UserInfo.getReversedDefHeadImage())
                    }

                    override fun getItemCount(): Int {
                        return 1
                    }

                })
            }else{

            }
        }

//        findViewById<EaseRecyclerView>(com.hyphenate.easeui.R.id.rv_conversation_list).also {
//            val followView=DataBindingUtil.inflate<ItemMessageFollowBinding>(LayoutInflater.from(requireContext()),R.layout.item_message_follow,it,false)
//            it.addHeaderView(followView.root)
//        }
        dataBinding?.conversationsModel=conversationsModel
    }

    override fun getConversationDelegate(): EaseAdapterDelegate<*, *> {
        return MySingleConversationDelegate()
    }


     override fun initListener(){
         super.initListener()
        mutualLike.setOnClickListener {
            startActivity(Intent(requireContext(), MutualLikeActivity::class.java))
        }
    }

    override fun toChatActivity(item: EMConversation) {
        val isRealName=EaseIM.getInstance().userProvider.getUser(item.conversationId()).getUserExt()?.isRealName?:false
        startActivity(ImChatActivity.getIntent(requireContext(),item.conversationId(), isRealName = isRealName))
    }

    override fun onResume() {
        super.onResume()
        iLog("加载一下")
        loadingDialog.show()
        lifecycleScope.launch {
            try {
                val mutualLike=viewModel.getMutualLike()
                conversationsModel.laterLikeCount = mutualLike.total?:0
                conversationsModel.list = mutualLike.list
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
                    .load(conversationsModel.imageHead1)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead3)

                Glide.with(mutualLikeHead4)
                    .load(conversationsModel.imageHead1)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead4)

                Glide.with(mutualLikeHead5)
                    .load(conversationsModel.imageHead1)
                    .error(UserInfo.getReversedDefHeadImage())
                    .placeholder(UserInfo.getReversedDefHeadImage())
                    .into(mutualLikeHead5)
            }catch (e:Exception){
                eLog(e.stackTraceToString())
            }
            loadingDialog.dismiss()
        }
    }
}