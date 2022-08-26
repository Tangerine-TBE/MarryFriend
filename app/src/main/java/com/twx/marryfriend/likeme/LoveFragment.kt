package com.twx.marryfriend.likeme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.kingja.loadsir.core.LoadSir
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_love.*
import kotlinx.coroutines.launch

class LoveFragment : Fragment(R.layout.fragment_love) {
    private val loadingDialog by lazy {
        LoadingDialogManager
            .createLoadingDialog()
            .create(requireContext())
    }
    private val loadSir by lazy {
        LoadSir.Builder()
            .addCallback(LiveEmptyData())
            .build()
    }
    private val loadService by lazy {
        loadSir.register(loveSwipeRefreshLayout
        ) {
            iLog("重加载")
        }
    }
    private val liveViewModel by lazy {
        ViewModelProvider(this).get(LiveViewModel::class.java)
    }
    private val liveAdapter by lazy {
        LoveAdapter()
    }
    private var pager=1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loveRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        loveRecyclerView.adapter=liveAdapter
        lifecycleScope.launch {
            loadingDialog.show()
            try {
                val data=liveViewModel.loadLoveMe(pager)
                liveAdapter.setData(data?.list?: emptyList())

                loveCount.text="${data?.total?:0}人喜欢我"
                text2.text="${data?.total?:0}"
                if (data?.list.isNullOrEmpty()){
                    loadService.showCallback(LiveEmptyData::class.java)
                }
            }catch (e:Exception){
                toast(e.message)
                loadService.showCallback(LiveEmptyData::class.java)
            }
            loadingDialog.dismiss()
        }
        initListener()
    }

    private fun initListener(){
        loveSwipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
//                pager++
                loadingDialog.show()
                liveViewModel.loadLoveMe(pager)
                liveAdapter
                loveSwipeRefreshLayout.isRefreshing=false
                loadingDialog.dismiss()
            }
        }
        liveAdapter.itemAction={
            if (!UserInfo.isVip()){
                toast("请先开通vip")
            }else{
                startActivity(FriendInfoActivity.getIntent(requireContext(),it.host_uid,true,true))
            }
        }
        if (UserInfo.isVip()){
            openVip.visibility=View.GONE
        }else{
            openVip.setOnClickListener {
                startActivity(IntentManager.getVipIntent(requireContext()))
            }
        }
    }
}