package com.twx.marryfriend.mine.comment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.mine.comment.mine.CommentMineFragment
import com.twx.marryfriend.mine.comment.other.CommentOtherFragment
import kotlinx.android.synthetic.main.activity_recent_comment.*
import kotlinx.android.synthetic.main.activity_tips.*

class RecentCommentActivity : MainBaseViewActivity() {

    private lateinit var mine: CommentMineFragment
    private lateinit var other: CommentOtherFragment

    override fun getLayoutView(): Int = R.layout.activity_recent_comment

    override fun initView() {
        super.initView()

        mine = CommentMineFragment().newInstance(this)
        other = CommentOtherFragment().newInstance(this)


        //添加适配器
        vp_comment_recent_container.adapter = object : FragmentStateAdapter(this) {
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

        TabLayoutMediator(tb_comment_recent_indicator,
            vp_comment_recent_container) { tab, position ->
            when (position) {
                0 -> tab.text = "收到的评论"
                1 -> tab.text = "发出的评论"
                else -> tab.text = "收到的评论"
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

        iv_comment_recent_finish.setOnClickListener {
            finish()
        }

    }

}