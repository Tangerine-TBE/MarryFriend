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
import kotlinx.android.synthetic.main.fragment_superlike_people.*
import kotlinx.coroutines.launch

class SuperLikePeopleFragment:Fragment(R.layout.fragment_superlike_people)  {
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(requireContext())
    }
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val likeAdapter by lazy {
        LikeAdapter(true)
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
        superLikeRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        superLikeRecyclerView.adapter=likeAdapter

        loadData()
        initListener()
        view.findViewById<TextView>(R.id.emptyDataTitle)?.text="暂时没有超喜欢的人"
        view.findViewById<TextView>(R.id.emptyDataDes)?.text="可以送小红花表达心意"
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val data=likeViewModel.loadSuperLike(1)?.list?: emptyList()
                if (page==1){
                    likeAdapter.setData(data)
                    if(data.isNotEmpty().xor(viewSwitch.currentView==superLikeRecyclerView)){
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
            }catch (e:Exception){
                toast(e.message)
                if (page==1){
                    smartRefresh.finishRefresh(false)
                }else{
                    smartRefresh.finishLoadMore(false)
                }
            }
            loadingDialog.dismiss()

        }
    }

    private fun initListener(){
        closeTip.setOnClickListener {
            closeView.visibility=View.GONE
        }
        smartRefresh.setOnRefreshListener {
            page=1
            smartRefresh.resetNoMoreData()
            loadData()
        }
        smartRefresh.setOnLoadMoreListener {
            page++
            loadData()
        }
        likeAdapter.chatAction={
            toast("聊天")
        }
        likeAdapter.sendFlowerAction={
            if (!uploadHeadDialog.showUploadHeadDialog()){
                lifecycleScope.launch {
                    loadingDialog.show()
                    try {
                        recommendViewModel.superLike(it.guest_uid){
                            coinInsufficientDialog.show(it.image_url)
                        }.also {
                            if (it.code==200){
                                toast("送花成功")
                            }else{
                                toast(it.msg)
                            }
                        }
                    }catch (e:Exception){
                        toast(e.message)
                    }
                    loadingDialog.dismiss()
                }
            }

        }
        likeViewModel.addSuperLikeChangeListener {
            loadData()
        }
        likeAdapter.itemAction={
            startActivity(FriendInfoActivity.getIntent(requireContext(), it.guest_uid))
        }
    }
}