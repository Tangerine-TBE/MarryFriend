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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyphenate.chat.EMConversation
import com.hyphenate.easeim.section.conversation.ConversationListFragment
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.adapter.EaseAdapterDelegate
import com.twx.marryfriend.*
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.mutual.MutualLikeActivity
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import kotlinx.android.synthetic.main.fragment_im_message.*
import kotlinx.coroutines.launch

class ImConversationFragment: ConversationListFragment() {
    companion object{
        const val MY_HELPER_ID="小秘书"//小秘书id
        const val LINE_SERVICE="1"
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(ConversationViewModel::class.java)
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

        ImUserInfoHelper.observableNewMessage.observe(this){
            iLog("开始刷新会话，${it}")
            if (it==true){
                conversationListLayout.refreshList()
            }
        }
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

    private val startActivityForResult =   registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            if (it.resultCode==Activity.RESULT_OK){
                val cid=ImChatActivity.getConversationId(it.data)
                if (cid!=null){
//                    makeConversationTop(cid)
                }
            }
        })
    override fun toChatActivity(item: EMConversation) {
        if (item.conversationId()==MY_HELPER_ID){
            startActivity(MyHelperActivity.getIntent(requireContext()))
            return
        }

        val imUserInfo=EaseIM.getInstance().userProvider.getUser(item.conversationId())
        val ext=imUserInfo.getUserExt()
        if (UserInfo.isVip()||ext?.isSuperVip==true||ext?.isMutualLike==true){
            val isRealName=ext?.isRealName?:false
            startActivityForResult.launch(ImChatActivity.getIntent(
                requireContext(),
                item.conversationId(),
                isRealName = isRealName
            ))
        }else{
            startActivity(IntentManager.getVipIntent(requireContext(), pId=item.conversationId(),vipGif = VipGifEnum.Inbox))
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
                mutualLikeView.visibility=if (!mutualLike.list.isNullOrEmpty()){
                    View.VISIBLE
                }else{
                    View.GONE
                }
                laterLikeCountView.text=(mutualLike.total?:0).toString()
                val list=mutualLike.list?: emptyList()

                val listView= listOf(mutualLikeView1,mutualLikeView2,mutualLikeView3,mutualLikeView4,mutualLikeView5)
                val imageViewList= listOf(mutualLikeHead1,mutualLikeHead2,mutualLikeHead3,mutualLikeHead4,mutualLikeHead5)
                for (i in 0 until 5){
                    if (list.size>i){
                        listView[i].visibility=View.VISIBLE
                        Glide.with(imageViewList[i])
                            .load(list[i].image_url)
                            .error(list[i].getSex().smallHead)
                            .placeholder(list[i].getSex().smallHead)
                            .into(imageViewList[i])
                    }else{
                        listView[i].visibility=View.GONE
                    }
                }
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
            if (UserInfo.isSuperVip()){
                it.context.startActivity(IntentManager.getFocusIntent(it.context))
            }else{
                it.context.startActivity(IntentManager.getSuperVipIntent(it.context, sVipGifEnum = SVipGifEnum.FocusMe))
            }
        }
    }

}