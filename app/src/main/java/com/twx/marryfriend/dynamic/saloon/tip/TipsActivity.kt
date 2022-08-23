package com.twx.marryfriend.dynamic.saloon.tip

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.LikeCancelBean
import com.twx.marryfriend.bean.dynamic.TrendTipBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.saloon.friend.DynamicFriendFragment
import com.twx.marryfriend.dynamic.saloon.recommned.DynamicRecommendFragment
import com.twx.marryfriend.dynamic.saloon.tip.comment.CommentFragment
import com.twx.marryfriend.dynamic.saloon.tip.like.LikeFragment
import com.twx.marryfriend.message.ChatActivity
import kotlinx.android.synthetic.main.activity_tips.*
import kotlinx.android.synthetic.main.fragment_dynamic.*
import java.util.*

class TipsActivity : MainBaseViewActivity() {

    private lateinit var comment: CommentFragment
    private lateinit var like: LikeFragment

    private var commentSum = 0
    private var likeSum = 0

    override fun getLayoutView(): Int = R.layout.activity_tips

    companion object {

        private const val COMMENT_SUM = "comment_sum"
        private const val LIKE_SUM = "like_sum"

        fun getIntent(context: Context, commentSum: Int, likeSum: Int): Intent {
            val intent = Intent(context, TipsActivity::class.java)
            intent.putExtra(COMMENT_SUM, commentSum)
            intent.putExtra(LIKE_SUM, likeSum)
            return intent
        }
    }


    override fun initView() {
        super.initView()

        commentSum = intent.getIntExtra("comment_sum", 0)
        likeSum = intent.getIntExtra("like_sum", 0)

        comment = CommentFragment().newInstance(this)
        like = LikeFragment().newInstance(this)


        //添加适配器
        vp_dynamic_tips_container.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return 2
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> comment
                    1 -> like
                    else -> comment
                }
            }
        }

        TabLayoutMediator(tb_dynamic_tips_indicator, vp_dynamic_tips_container) { tab, position ->
            when (position) {
                0 -> tab.text = "评论"
                1 -> tab.text = "点赞"
                else -> tab.text = "评论"
            }
        }.attach()

    }

    override fun initLoadData() {
        super.initLoadData()

        if (commentSum != 0) {
            tb_dynamic_tips_indicator.getTabAt(0)?.let { tab ->
                tab.orCreateBadge.apply {
                    backgroundColor = Color.RED
                    maxCharacterCount = 3
                    number = commentSum
                    badgeTextColor = Color.WHITE
                }
            }
        }

        if (likeSum != 0) {
            tb_dynamic_tips_indicator.getTabAt(1)?.let { tab ->
                tab.orCreateBadge.apply {
                    backgroundColor = Color.RED
                    maxCharacterCount = 3
                    number = likeSum
                    badgeTextColor = Color.WHITE
                }
            }
        }

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_dynamic_tips_finish.setOnClickListener {
            finish()
        }

    }

}