package com.twx.marryfriend.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.dialog.UploadHeadDialog
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.recommend.RecommendCall
import com.twx.marryfriend.recommend.RecommendViewModel
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.android.synthetic.main.item_search_empty_data.*
import kotlinx.coroutines.launch

//https://lanhuapp.com/web/#/item/project/detailDetach?pid=0f172b45-d776-4080-a03e-618374ed56e4&image_id=f9fe8766-8e25-453c-a61c-4537300f7b45&tid=5173cb5f-00ad-4a38-b103-b616ccec0e12&project_id=0f172b45-d776-4080-a03e-618374ed56e4&fromEditor=true&type=image
class SearchResultActivity :AppCompatActivity(R.layout.activity_search_result){
    companion object{
        private const val PARAM_KEY="param_key"
        fun getIntent(context: Context,mapParams:Map<String,String>):Intent{
            val intent=Intent(context,SearchResultActivity::class.java)
            intent.putExtra(PARAM_KEY,Gson().toJson(mapParams))
            return intent
        }
        private const val SEARCH_TEXT="search_text_k"
        fun getIntent(context: Context,searchText:String):Intent{
            return Intent(context,SearchResultActivity::class.java).also {
                it.putExtra(SEARCH_TEXT,searchText)
            }
        }
    }
    private val searchText by lazy {
        intent?.getStringExtra(SEARCH_TEXT)
    }
    private val searchMap by lazy {
        intent.getStringExtra(PARAM_KEY).let {
            if (it.isNullOrBlank()){
                null
            }else{
                val a= HashMap<String,String>()
                Gson().fromJson(it,a.javaClass)
            }
        }
    }
    private val searchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java).also {
            it.setParameter(searchMap)
            it.searchText=searchText
        }
    }
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(this)
    }

    private val searchResultAdapter by lazy { SearchResultAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResultRecyclerView.layoutManager=LinearLayoutManager(this)
        searchResultRecyclerView.adapter=searchResultAdapter
        startSearch()
        initListener()
    }
    private val uploadHeadDialog by lazy {
        UploadHeadDialog(this)
    }
    private fun initListener(){
        Glide.with(myHead)
            .load(UserInfo.getHeadPortrait())
            .placeholder(UserInfo.getDefHeadImage())
            .error(UserInfo.getDefHeadImage())
            .into(myHead)
        searchResultAdapter.chatAction={
            val userid=it.user_id
            if (userid?.toString()==UserInfo.getUserId()){
                toast("不能和自己聊天")
            }else{
                if(it.isSendMsg()){
                    startActivity(ImChatActivity.getIntent(this,userid.toString(),it.isRealName()))
                }else{
                    startActivity(IntentManager.getVipIntent(this, vipGif = VipGifEnum.Message))
                }
            }
        }
        closeMutual.setOnClickListener {
            if (this@SearchResultActivity.viewSwitch.currentView!=contentView){
                viewSwitch.showNext()
            }
        }
        searchResultAdapter.likeAction={item,view->
            lifecycleScope.launch {
                if (item.user_id?.toString()==UserInfo.getUserId()){
                    toast("不能喜欢自己")
                    return@launch
                }else if (!UserInfo.isHaveHeadImage()){
                    uploadHeadDialog.show()
                    return@launch
                }
                recommendViewModel.otherLike(item.user_id?:return@launch toast("对方id为空")) {
                    if (this@SearchResultActivity.viewSwitch.currentView != mutualView) {
                        viewSwitch.showNext()
                    }
                    Glide.with(taHead)
                        .load(item.getHeadImage())
                        .placeholder(UserInfo.getReversedDefHeadImage())
                        .error(UserInfo.getReversedDefHeadImage())
                        .into(taHead)
                    sendMsg.setOnClickListener {
                        if (this@SearchResultActivity.viewSwitch.currentView != contentView) {
                            viewSwitch.showNext()
                        }
                        startActivity(
                            ImChatActivity.getIntent(
                                this@SearchResultActivity,
                                item.user_id?.toString() ?: return@setOnClickListener
                            )
                        )
                    }
                }.also { t->
                    if (t.code==200){
                        item.setLikeTa()
                        view.isSelected=true
                        item.like()
                        toast("喜欢成功")
                    }else{
                        if (t.code== RecommendCall.RECOMMEND_NOT_HAVE){
                            if (UserInfo.isSuperVip()){
                                toast("今日推荐嘉宾次数已用完")
                            }else if (UserInfo.isVip()){
                                startActivity(IntentManager.getSuperVipIntent(this@SearchResultActivity, sVipGifEnum = SVipGifEnum.MoreView))
                            }else{
                                startActivity(IntentManager.getVipIntent(this@SearchResultActivity, vipGif = VipGifEnum.MoreView))
                            }
                        }
                        toast(t.msg)
                    }
                }
            }
        }
        searchResultAdapter.itemAction={
            startActivity(FriendInfoActivity.getIntent(this, it.user_id))
        }
        searchResultRefreshLayout.setOnLoadMoreListener {
            lifecycleScope.launch {
                val result=try {
                    searchViewModel.nextPage()
                }catch (e:Exception){
                    null
                }
                if (result.isNullOrEmpty()){
                    searchResultRefreshLayout.finishLoadMoreWithNoMoreData()
                }else{
                    searchResultRefreshLayout.finishLoadMore(true)
                    searchResultAdapter.addAllData(result)
                }
            }
        }
        searchResultRefreshLayout.setOnRefreshListener {
            startSearch()
        }
    }

    private fun startSearch(){
        lifecycleScope.launch() {
            loadingDialog.show()
            try {
                searchResultRefreshLayout.resetNoMoreData()
                val result=searchViewModel.refreshData()
                searchResultAdapter.setData(result)
                if(result.isEmpty()){
                    if (UserInfo.isInterdiction()){
                        emptyDataDes.text="你已经被系统封禁,请联系客服"
                    }
                    searchResultRefreshLayout.finishRefresh()
                }else{
                    searchResultRefreshLayout.finishRefresh(false)
                }
                if (result.isNotEmpty().xor(contentViw.currentView==searchResultRecyclerView)){
                    contentViw.showNext()
                }
            }catch (e:Exception){
                toast(e.message?:"")
            }
            loadingDialog.dismiss()
        }
    }
}