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
import com.xyzz.myutils.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.fragment_superlike_people.*
import kotlinx.coroutines.launch

class SuperLikePeopleFragment:Fragment(R.layout.fragment_superlike_people)  {
    private val loadService by lazy {
        val loadSir= LoadSir.Builder()
            .addCallback(ILikeEmptyDataCallBack())
            .build()
        loadSir.register(superLikeRootView
        ) {
            loadData()
            iLog("重加载")
        }
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog().create(requireContext())
    }
    private val likeAdapter by lazy {
        LikeAdapter(true)
    }
    private val likeViewModel by lazy {
        ViewModelProvider(requireActivity()).get(LikeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        superLikeRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        superLikeRecyclerView.adapter=likeAdapter

        loadData()
        initListener()
        loadService.setCallBack(ILikeEmptyDataCallBack::class.java,object : Transport{
            override fun order(context: Context?, view: View?) {
                view?.findViewById<TextView>(R.id.emptyDataTitle)?.text="暂时没有超喜欢的人"
                view?.findViewById<TextView>(R.id.emptyDataDes)?.text="可以送小红花表达心意"
            }
        })
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val data=likeViewModel.loadSuperLike(1)
                likeAdapter.setData(data?.list?: emptyList())
                if(( data?.list?: emptyList()).isEmpty()){
                    loadService.showCallback(ILikeEmptyDataCallBack::class.java)
                }else{
                    loadService.showSuccess()
                }
                superLikeRefresh.finishRefresh(true)
            }catch (e:Exception){
                toast(e.message)
                superLikeRefresh.finishRefresh(false)
            }
            loadingDialog.dismiss()

        }
    }

    private fun initListener(){
        superLikeRefresh.setEnableLoadMore(false)
        superLikeRefresh.setOnRefreshListener {
            loadData()
        }
        likeAdapter.chatAction={
            toast("聊天")
        }
        likeViewModel.addSuperLikeChangeListener {
            loadData()
        }
    }
}