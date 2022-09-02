package com.twx.marryfriend.mine.like

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.SPStaticUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.mine.comment.mine.CommentMineFragment
import com.twx.marryfriend.mine.comment.other.CommentOtherFragment
import com.twx.marryfriend.mine.like.mine.LikeMineFragment
import com.twx.marryfriend.mine.like.other.LikeOtherFragment
import kotlinx.android.synthetic.main.activity_recent_comment.*
import kotlinx.android.synthetic.main.activity_recent_like.*

class RecentLikeActivity : MainBaseViewActivity() {

    private lateinit var mine: LikeMineFragment
    private lateinit var other: LikeOtherFragment

    override fun getLayoutView(): Int = R.layout.activity_recent_like

    override fun initView() {
        super.initView()


        mine = LikeMineFragment().newInstance(this)
        other = LikeOtherFragment().newInstance(this)

        //添加适配器
        vp_like_recent_container.adapter = object : FragmentStateAdapter(this) {
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

        TabLayoutMediator(tb_like_recent_indicator,
            vp_like_recent_container) { tab, position ->
            when (position) {
                0 -> tab.text = "收到的点赞"
                1 -> tab.text = "发出的点赞"
                else -> tab.text = "收到的点赞"
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

        iv_like_recent_finish.setOnClickListener {
            finish()
        }

    }

}