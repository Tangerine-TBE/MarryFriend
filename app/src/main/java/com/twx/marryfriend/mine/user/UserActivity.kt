package com.twx.marryfriend.mine.user

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.mine.user.data.DataFragment
import com.twx.marryfriend.mine.user.target.TargetFragment
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : MainBaseViewActivity() {

    private lateinit var dateFragment: DataFragment
    private lateinit var targetFragment: TargetFragment

    override fun getLayoutView(): Int = R.layout.activity_user

    override fun initView() {
        super.initView()

        dateFragment = DataFragment()
        targetFragment = TargetFragment()

        //添加适配器
        vp_user_container.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> dateFragment
                    1 -> targetFragment
                    else -> dateFragment
                }
            }
        }

        TabLayoutMediator(tb_user_indicator, vp_user_container) { tab, position ->
            when (position) {
                0 -> tab.text = "我的资料"
                1 -> tab.text = "择偶条件"
                else -> tab.text = "我的资料"
            }
        }.attach()

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_user_finish.setOnClickListener {
            finish()
        }

    }

}