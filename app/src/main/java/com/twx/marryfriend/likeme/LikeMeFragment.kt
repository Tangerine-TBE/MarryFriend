package com.twx.marryfriend.likeme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_like_me.*
import kotlinx.coroutines.launch

class LikeMeFragment : Fragment(R.layout.fragment_like_me) {
    private val loadingDialog by lazy {
        LoadingDialogManager
            .createLoadingDialog()
            .create(requireContext())
    }
    private val liveViewModel by lazy {
        ViewModelProvider(this).get(LiveViewModel::class.java)
    }
    private val liveAdapter by lazy {
        LoveAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loveRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        loveRecyclerView.adapter = liveAdapter
        initListener()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun refreshData() {
        lifecycleScope.launch {
            loveSwipeRefreshLayout.resetNoMoreData()
            loadingDialog.show()
            try {
                val data = liveViewModel.refresh()
                liveAdapter.setData(data?.list ?: emptyList())
                loveCount.text = "${data?.total ?: 0}人喜欢我"
                text2.text = "${data?.total ?: 0}"
                if ((!data?.list.isNullOrEmpty()).xor(viewSwitch.currentView==loveRecyclerView)){
                    viewSwitch.showNext()
                }
                loveSwipeRefreshLayout.finishRefresh()
            } catch (e: Exception) {
                toast(e.message)
                if (viewSwitch.currentView==loveRecyclerView){
                    viewSwitch.showNext()
                }
                loveSwipeRefreshLayout.finishRefresh(false)
            }
            loadingDialog.dismiss()
        }
    }

    private fun getNextData() {
        lifecycleScope.launch {
            try {
                val data = liveViewModel.getNextPage()?.list ?: emptyList()
                liveAdapter.addAllData(data)
                if (data.isEmpty()) {
                    loveSwipeRefreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    loveSwipeRefreshLayout.finishLoadMore()
                }
            } catch (e: Exception) {
                toast(e.message)
                loveSwipeRefreshLayout.finishLoadMore(false)
            }
        }
    }

    private fun initListener() {
        loveSwipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
        loveSwipeRefreshLayout.setOnLoadMoreListener {
            getNextData()
        }
        liveAdapter.itemAction = {
            if (!UserInfo.isVip()) {
                startActivity(IntentManager.getVipIntent(requireContext(), vipGif = VipGifEnum.LoveMe))
            } else {
                startActivity(FriendInfoActivity.getIntent(
                    requireContext(),
                    it.host_uid
                ))
            }
        }
        if (UserInfo.isVip()) {
            openVip.visibility = View.GONE
        } else {
            openVip.setOnClickListener {
                startActivity(IntentManager.getVipIntent(requireContext(), vipGif = VipGifEnum.LoveMe))
            }
        }
    }
}