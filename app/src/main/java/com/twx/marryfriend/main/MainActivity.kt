package com.twx.marryfriend.main

import androidx.fragment.app.FragmentTransaction
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.dynamic.DynamicFragment
import com.twx.marryfriend.love.LoveFragment
import com.twx.marryfriend.message.MessageFragment
import com.twx.marryfriend.mine.MineFragment
import com.twx.marryfriend.recommend.RecommendFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MainBaseViewActivity() {

    private var recommend: RecommendFragment? = null
    private var love: LoveFragment? = null
    private var dynamic: DynamicFragment? = null
    private var message: MessageFragment? = null
    private var mine: MineFragment? = null

    override fun getLayoutView(): Int = R.layout.activity_main

    override fun initView() {
        super.initView()

        initMineFragment()

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        rb_main_recommend.setOnClickListener {
            initRecommendFragment()
        }

        rb_main_love.setOnClickListener {
            initLoveFragment()
        }

        rb_main_dynamic.setOnClickListener {
            initDynamicFragment()
        }

        rb_main_message.setOnClickListener {
            initMessageFragment()
        }

        rb_main_mine.setOnClickListener {
            initMineFragment()
        }

    }


    fun initRecommendFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (recommend == null) {
            recommend = RecommendFragment()
            transaction.add(R.id.fl_main_container, recommend!!)
        }
        hideFragment(transaction)
        transaction.show(recommend!!)
        transaction.commit()
    }

    fun initLoveFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (love == null) {
            love = LoveFragment()
            transaction.add(R.id.fl_main_container, love!!)
        }
        hideFragment(transaction)
        transaction.show(love!!)
        transaction.commit()
    }

    fun initDynamicFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (dynamic == null) {
            dynamic = DynamicFragment()
            transaction.add(R.id.fl_main_container, dynamic!!)
        }
        hideFragment(transaction)
        transaction.show(dynamic!!)
        transaction.commit()
    }

    fun initMessageFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (message == null) {
            message = MessageFragment()
            transaction.add(R.id.fl_main_container, message!!)
        }
        hideFragment(transaction)
        transaction.show(message!!)
        transaction.commit()
    }

    fun initMineFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (mine == null) {
            mine = MineFragment()
            transaction.add(R.id.fl_main_container, mine!!)
        }
        hideFragment(transaction)
        transaction.show(mine!!)
        transaction.commit()
    }


    private fun hideFragment(transaction: FragmentTransaction) {
        if (recommend != null) {
            transaction.hide(recommend!!)
        }
        if (love != null) {
            transaction.hide(love!!)
        }
        if (dynamic != null) {
            transaction.hide(dynamic!!)
        }
        if (message != null) {
            transaction.hide(message!!)
        }
        if (mine != null) {
            transaction.hide(mine!!)
        }
    }

}