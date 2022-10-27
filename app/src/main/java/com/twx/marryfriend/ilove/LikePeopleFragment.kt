package com.twx.marryfriend.ilove

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.twx.marryfriend.R
import com.twx.marryfriend.dialog.ReChargeCoinDialog
import com.twx.marryfriend.dialog.UploadHeadDialog
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.recommend.RecommendViewModel
import com.twx.marryfriend.showUploadHeadDialog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_like_people.*
import kotlinx.coroutines.launch

class LikePeopleFragment:Fragment(R.layout.fragment_like_people) {
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(requireContext())
    }
    private val likeAdapter by lazy {
        LikeAdapter()
    }
    private val likeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(LikeViewModel::class.java)
    }
    private val recommendViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RecommendViewModel::class.java)
    }
    private val coinInsufficientDialog by lazy {
        ReChargeCoinDialog(requireActivity())
    }
    private val uploadHeadDialog by lazy {
        UploadHeadDialog(requireContext())
    }
    private var page=1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        likeRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        likeRecyclerView.adapter=likeAdapter

        loadData()
        initListener()
        view.findViewById<TextView>(R.id.emptyDataTitle)?.text="暂时没有喜欢的人"
        view.findViewById<TextView>(R.id.emptyDataDes)?.text="可以到首页右滑喜欢对方"
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            val data=likeViewModel.loadLike(page)?.list?: emptyList()
            if (page==1){
                likeAdapter.setData(data)
                if(data.isNotEmpty().xor(viewSwitch.currentView==likeRecyclerView)){
                    viewSwitch.showNext()
                }
                smartRefresh.finishRefresh()
            }else{
                if (data.isEmpty()){
                    smartRefresh.finishLoadMoreWithNoMoreData()
                }else{
                    likeAdapter.addData(data)
                    smartRefresh.finishLoadMore()
                }
            }
            loadingDialog.dismiss()
        }
    }

    private fun initListener(){
        smartRefresh.setOnRefreshListener {
            page=1
            smartRefresh.resetNoMoreData()
            loadData()
        }
        smartRefresh.setOnLoadMoreListener {
            page++
            loadData()
        }
        closeTip.setOnClickListener {
            closeView.visibility=View.GONE
        }
        likeAdapter.sendFlowerAction={item->
            if (!uploadHeadDialog.showUploadHeadDialog()){
                lifecycleScope.launch {
                    loadingDialog.show()
                    recommendViewModel.otherSuperLike(item.guest_uid){
                        coinInsufficientDialog.show(item.image_url)
                    }.also {
                        if (it.code==200){
                            likeViewModel.onSuperLikeChange(item)
                            toast("送花成功")
                        }else{
                            toast(it.msg)
                        }
                    }
                    loadingDialog.dismiss()
                    loadData()
                }
            }
        }
        likeAdapter.itemAction={
            startActivity(FriendInfoActivity.getIntent(requireContext(), it.guest_uid))
        }
    }
}