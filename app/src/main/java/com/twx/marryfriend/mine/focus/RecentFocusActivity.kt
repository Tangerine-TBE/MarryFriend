package com.twx.marryfriend.mine.focus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.mine.comment.mine.CommentMineFragment
import com.twx.marryfriend.mine.comment.other.CommentOtherFragment
import com.twx.marryfriend.mine.focus.mine.FocusMineFragment
import com.twx.marryfriend.mine.focus.other.FocusOtherFragment
import kotlinx.android.synthetic.main.activity_recent_comment.*
import kotlinx.android.synthetic.main.activity_recent_focus.*

class RecentFocusActivity : MainBaseViewActivity() {

    private lateinit var mine: FocusMineFragment
    private lateinit var other: FocusOtherFragment

    override fun getLayoutView(): Int = R.layout.activity_recent_focus

    override fun initView() {
        super.initView()

        mine = FocusMineFragment().newInstance(this)
        other = FocusOtherFragment().newInstance(this)

        //添加适配器
        vp_focus_recent_container.adapter = object : FragmentStateAdapter(this) {
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

        TabLayoutMediator(tb_focus_recent_indicator,
            vp_focus_recent_container) { tab, position ->
            when (position) {
                0 -> tab.text = "关注我的人"
                1 -> tab.text = "我关注的人"
                else -> tab.text = "关注我的人"
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

        iv_focus_recent_finish.setOnClickListener {
            finish()
        }

    }

}