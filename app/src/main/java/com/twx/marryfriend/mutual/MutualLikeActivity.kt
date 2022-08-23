package com.twx.marryfriend.mutual

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.message.mutual.MutualLikeData
import com.xyzz.myutils.show.eLog
import kotlinx.android.synthetic.main.activity_mutual_like.*
import kotlinx.coroutines.launch

class MutualLikeActivity:AppCompatActivity(R.layout.activity_mutual_like) {
    private val viewModel by lazy {
        ViewModelProvider(this).get(MutualLikeViewModel::class.java)
    }
    private val adapter by lazy {
        MutualLikeAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mutualRecyclerView.layoutManager=GridLayoutManager(this,2)
        mutualRecyclerView.adapter=adapter
        loadData()
    }

    private fun loadData(){
        lifecycleScope.launch {
            try {
                viewModel.getMutualLike().also {
                    myActionBar.setTitle("互相喜欢的人(${it.total?:0}人)")
                    adapter.setData(it.list?: emptyList())
                }
            }catch (e:Exception){
                eLog(e.stackTraceToString())
            }
        }
    }
}