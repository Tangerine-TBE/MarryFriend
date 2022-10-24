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
import com.twx.marryfriend.showUploadHeadDialog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_dis_like_people.*
import kotlinx.coroutines.launch

class DislikePeopleFragment:Fragment(R.layout.fragment_dis_like_people) {
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(requireContext())
    }
    private val likeAdapter by lazy {
        LikeAdapter()
    }
    private val likeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(LikeViewModel::class.java)
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
        disLikeRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        disLikeRecyclerView.adapter=likeAdapter

        loadData()
        initListener()
        view.findViewById<TextView>(R.id.emptyDataTitle)?.text="暂时没有不喜欢的人"
        view.findViewById<TextView>(R.id.emptyDataDes)?.text="可以到首页左滑不喜欢对方"
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            val data=likeViewModel.loadDisLike(page)?.list?: emptyList()
            if (page==1){
                likeAdapter.setData(data)
                if(data.isNotEmpty().xor(viewSwitch.currentView==disLikeRecyclerView)){
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
        likeAdapter.sendFlowerAction={
            if (!uploadHeadDialog.showUploadHeadDialog()){
                lifecycleScope.launch {
                    loadingDialog.show()
                    try {
                        likeViewModel.superLike(it.guest_uid){
                            coinInsufficientDialog.show(it.image_url)
                        }
                        likeViewModel.onSuperLikeChange(it)
                        toast("送花成功")
                    }catch (e:Exception){
                        toast(e.message)
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