package com.twx.marryfriend.mine.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.mine.focus.mine.FocusMineFragment
import com.twx.marryfriend.mine.focus.other.FocusOtherFragment
import com.twx.marryfriend.mine.view.mine.ViewMineFragment
import com.twx.marryfriend.mine.view.other.ViewOtherFragment
import kotlinx.android.synthetic.main.activity_recent_focus.*
import kotlinx.android.synthetic.main.activity_recent_view.*

class RecentViewActivity : MainBaseViewActivity() {

    private lateinit var mine: ViewMineFragment
    private lateinit var other: ViewOtherFragment

    override fun getLayoutView(): Int = R.layout.activity_recent_view

    override fun initView() {
        super.initView()

        mine = ViewMineFragment().newInstance(this)
        other = ViewOtherFragment().newInstance(this)

        //添加适配器
        vp_view_recent_container.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> mine
                    1 -> other
                    else -> mine
                }
            }
        }

        TabLayoutMediator(tb_view_recent_indicator,
            vp_view_recent_container) { tab, position ->
            when (position) {
                0 -> tab.text = "谁看过我"
                1 -> tab.text = "我看过谁"
                else -> tab.text = "谁看过我"
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

        iv_view_recent_finish.setOnClickListener {
            finish()
        }

    }

}