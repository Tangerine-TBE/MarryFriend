package com.twx.marryfriend.ilove

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingja.loadsir.core.LoadSir
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
        loadSir.register(superLikeRecyclerView
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
        ViewModelProvider(this).get(LikeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        superLikeRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        superLikeRecyclerView.adapter=likeAdapter

        loadData()
        initListener()
    }

    private fun loadData(){
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            val data=likeViewModel.loadSuperLike(1)
            likeAdapter.setData(data?.list?: emptyList())
            loadingDialog.dismiss()
            if(( data?.list?: emptyList()).isEmpty()){
                loadService.showCallback(ILikeEmptyDataCallBack::class.java)
            }else{
                loadService.showSuccess()
            }
        }
    }

    private fun initListener(){
        likeAdapter.sendFlowerAction={
            toast("送花")
        }
    }
}