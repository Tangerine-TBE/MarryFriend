package com.twx.marryfriend.main

import android.content.Intent
import android.view.MotionEvent
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.dynamic.DynamicFragment
import com.twx.marryfriend.likeme.LiveFragment
import com.twx.marryfriend.message.MessageFragment
import com.twx.marryfriend.mine.MineFragment
import com.twx.marryfriend.recommend.RecommendFragment
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MainBaseViewActivity() {

    private var recommend: RecommendFragment? = null
    private var love: LiveFragment? = null
    private var dynamic: DynamicFragment? = null
    private var message: MessageFragment? = null
    private var mine: MineFragment? = null

    override fun getLayoutView(): Int = R.layout.activity_main

    override fun initView() {
        super.initView()

        initRecommendFragment()
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


    private fun initRecommendFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (recommend == null) {
            recommend = RecommendFragment()
            transaction.add(R.id.fl_main_container, recommend!!)
        }
        hideFragment(transaction)
        transaction.show(recommend!!)
        transaction.commit()
    }

    private fun initLoveFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (love == null) {
            love = LiveFragment()
            transaction.add(R.id.fl_main_container, love!!)
        }
        hideFragment(transaction)
        transaction.show(love!!)
        transaction.commit()
    }

    private fun initDynamicFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (dynamic == null) {
            dynamic = DynamicFragment()
            transaction.add(R.id.fl_main_container, dynamic!!)
        }
        hideFragment(transaction)
        transaction.show(dynamic!!)
        transaction.commit()
    }

    private fun initMessageFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (message == null) {
            message = MessageFragment()
            transaction.add(R.id.fl_main_container, message!!)
        }
        hideFragment(transaction)
        transaction.show(message!!)
        transaction.commit()
    }

    private fun initMineFragment() {
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    if (data != null) {
                        mine?.handlePhotoCropResult(data)
                    };
                }
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        mine?.handlePhotoCropError(data)
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        recommend?.dispatchTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

}