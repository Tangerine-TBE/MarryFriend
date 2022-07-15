package com.twx.marryfriend.love

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.kingja.loadsir.core.LoadSir
import com.twx.marryfriend.R
import com.xyzz.myutils.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.toast
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
            .addCallback(LoveEmptyData())
            .build()
    }
    private val loadService by lazy {
        loadSir.register(loveRecyclerView
        ) {
            iLog("重加载")
        }
    }
    private val loveViewModel by lazy {
        ViewModelProvider(this).get(LoveViewModel::class.java)
    }
    private val loveAdapter by lazy {
        LoveAdapter()
    }
    private var pager=1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loveRecyclerView.layoutManager=GridLayoutManager(requireContext(),2)
        loveRecyclerView.adapter=loveAdapter
        lifecycleScope.launch {
            loadingDialog.show()
            try {
                val data=loveViewModel.loadLoveMe(pager)
                loveAdapter.setData(data?.list?: emptyList())
                if (data?.total!=null&& data.total !=0){
                    loveCount.text="${data?.total}人喜欢我"
                }else{
                    loadService.showCallback(LoveEmptyData::class.java)
                }
            }catch (e:Exception){
                toast(e.message)
            }
            loadingDialog.dismiss()
        }
        initListener()
    }

    private fun initListener(){
        loveSwipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                pager++
                loadingDialog.show()
                loveViewModel.loadLoveMe(pager)
                loveAdapter
                loveSwipeRefreshLayout.isRefreshing=false
                loadingDialog.dismiss()
            }
        }
        openVip.setOnClickListener {
            toast("开通会员")
        }
    }
}