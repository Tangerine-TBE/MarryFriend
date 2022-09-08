package com.twx.marryfriend.dynamic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.dynamic.saloon.friend.DynamicFriendFragment
import com.twx.marryfriend.dynamic.saloon.recommned.DynamicRecommendFragment
import com.twx.marryfriend.dynamic.send.DynamicSendActivity
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {//fragment可见
            Log.i("guo", "动态界面刷新")

            recommendFragment.autoRefresh()

        }
    }

    fun newInstance(context: Context): DynamicFragment {
        val fragment = DynamicFragment()
        fragment.mContext = context
        return fragment
    }

    private fun initView() {

        if (isAdded) {
            context?.apply {
                recommendFragment = DynamicRecommendFragment().newInstance(this)
                dynamicFragment = DynamicFriendFragment().newInstance(this)
            }

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
            ?.setOnClickListener {
                ToastUtils.showShort("123456")
            }

        //设置 分割线
        for (index in 0..tb_dynamic_indicator.tabCount) {
            val linearLayout = tb_dynamic_indicator.getChildAt(index) as? LinearLayout
            linearLayout?.let {
                it.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                it.dividerDrawable = context?.let { it1 ->
                    ContextCompat.getDrawable(it1,
                        R.drawable.shape_tab_divider)
                }
                it.dividerPadding = 50
            }
        }

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        iv_dynamic_send.setOnClickListener {
            val intent = Intent(mContext, DynamicSendActivity::class.java)
            startActivity(intent)
        }

    }

}