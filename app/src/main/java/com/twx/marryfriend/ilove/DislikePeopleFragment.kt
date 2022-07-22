package com.twx.marryfriend.ilove

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingja.loadsir.core.LoadSir
import com.kingja.loadsir.core.Transport
import com.twx.marryfriend.R
import com.twx.marryfriend.friend.FriendInfoActivity
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.fragment_dis_like_people.*
import kotlinx.coroutines.launch

class DislikePeopleFragment:Fragment(R.layout.fragment_dis_like_people) {
    private val loadService by lazy {
        val loadSir= LoadSir.Builder()
            .addCallback(ILikeEmptyDataCallBack())
            .build()
        loadSir.register(disLikeRecyclerView
        ) {
            loadData()
            iLog("重加载")
        }
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(requireContext())
    }
    private val dislikeAdapter by lazy {
        LikeAdapter()
    }
    private val likeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(LikeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disLikeRecyclerView.layoutManager=LinearLayoutManager(requireContext())
        disLikeRecyclerView.adapter=dislikeAdapter

        loadData()
        initListener()
        loadService.setCallBack(ILikeEmptyDataCallBack::class.java,object : Transport {
            override fun order(context: Context?, view: View?) {
                view?.findViewById<TextView>(R.id.emptyDataTitle)?.text="暂时没有不喜欢的人"
                view?.findViewById<TextView>(R.id.emptyDataDes)?.text="可以到首页左滑不喜欢对方"
            }
        })
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            val data=likeViewModel.loadDisLike(1)
            dislikeAdapter.setData(data?.list?: emptyList())
            loadingDialog.dismiss()
            if(( data?.list?: emptyList()).isEmpty()){
                loadService.showCallback(ILikeEmptyDataCallBack::class.java)
            }else{
                loadService.showSuccess()
            }
        }
    }

    private fun initListener(){
        closeTip.setOnClickListener {
            closeView.visibility=View.GONE
        }
        dislikeAdapter.sendFlowerAction={
            lifecycleScope.launch {
                loadingDialog.show()
                try {
                    likeViewModel.superLike(it.guest_uid)
                    likeViewModel.onSuperLikeChange(it)
                    toast("送花成功")
                }catch (e:Exception){
                    toast(e.message)
                }
                loadingDialog.dismiss()
                loadData()
            }
        }
        dislikeAdapter.itemAction={
            startActivity(FriendInfoActivity.getIntent(requireContext(),it.guest_uid))
        }
    }
}