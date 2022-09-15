package com.twx.marryfriend.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.kingja.loadsir.core.LoadSir
import com.twx.marryfriend.ImHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.recommend.RecommendViewModel
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_search_result.*
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
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(this)
    }

    private val searchResultAdapter by lazy { SearchResultAdapter() }
    private val loadService by lazy {
        val loadSir=LoadSir.Builder()
            .addCallback(SearchEmptyDataCallBack())
            .build()
        loadSir.register(searchResultRecyclerView
        ) {
            startSearch()
            iLog("重加载")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResultRecyclerView.layoutManager=LinearLayoutManager(this)
        searchResultRecyclerView.adapter=searchResultAdapter
        startSearch()
        initListener()
    }

    private fun initListener(){
        searchResultAdapter.chatAction={
            val userid=it.user_id
            if (userid!=null){
                startActivity(ChatActivity.getIntent(this,userid.toString(),it.nick,it.getHeadImage(),it.isRealName()))
            }
        }
        searchResultAdapter.likeAction={item,view->
            lifecycleScope.launch {
                try {
                    val isMutualLike=recommendViewModel.otherLike(item.user_id?:return@launch toast("对方id为空"))
                    view.isSelected=true
                    item.like()
                    toast("喜欢成功")
                }catch (e:Exception){
                    toast(e.message?:"喜欢失败")
                }
            }
        }
        searchResultAdapter.itemAction={
            startActivity(FriendInfoActivity.getIntent(this,it.user_id))
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
                val result=if(searchMap!=null){
                    searchViewModel.setParameter(searchMap?:return@launch)
                    searchViewModel.refreshData()
                }else if (searchText!=null){
                    searchViewModel.accurateSearch(searchText?:return@launch)
                }else{
                    null
                }?:return@launch
                searchResultAdapter.setData(result)
                if(result.isEmpty()){
                    loadService.showCallback(SearchEmptyDataCallBack::class.java)
                    searchResultRefreshLayout.finishRefresh()
                }else{
                    searchResultRefreshLayout.finishRefresh(false)
                }
            }catch (e:Exception){
                toast(e.message?:"")
            }
            loadingDialog.dismiss()
        }
    }
}