package com.twx.module_dynamic.saloon

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.module_dynamic.R
import com.twx.module_dynamic.saloon.friend.DynamicFriendFragment
import com.twx.module_dynamic.saloon.recommned.DynamicRecommendFragment
import kotlinx.android.synthetic.main.fragment_dynamic.*

class DynamicFragment : Fragment() {


    private lateinit var mContext: Context

    private lateinit var recommendFragment: DynamicRecommendFragment
    private lateinit var dynamicFragment: DynamicFriendFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_dynamic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    fun newInstance(context: Context): DynamicFragment {
        val fragment = DynamicFragment()
        fragment.mContext = context
        return fragment
    }

    private fun initView() {

        if (isAdded) {

            recommendFragment = DynamicRecommendFragment().newInstance(requireContext())
            dynamicFragment = DynamicFriendFragment().newInstance(requireContext())

            //添加适配器
            vp_dynamic_container.adapter = object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int {
                    return 2
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> recommendFragment
                        1 -> dynamicFragment
                        else -> recommendFragment
                    }
                }
            }

            TabLayoutMediator(tb_dynamic_indicator, vp_dynamic_container) { tab, position ->
                when (position) {
                    0 -> tab.text = "推荐"
                    1 -> tab.text = "关注"
                    else -> tab.text = "推荐"
                }
            }.attach()

        } else {
            ToastUtils.showShort("数据加载失败，请重启应用重试")
        }

        recommendFragment.view?.findViewById<RecyclerView>(R.id.rv_dynamic_recommend_container)
            ?.setOnClickListener{
                ToastUtils.showShort("123456")
            }

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

    }

}