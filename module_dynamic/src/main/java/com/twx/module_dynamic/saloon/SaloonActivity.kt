package com.twx.module_dynamic.saloon

import androidx.fragment.app.FragmentTransaction
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_dynamic.R

class SaloonActivity : MainBaseViewActivity() {

    private var dynamic: DynamicFragment? = null

    override fun getLayoutView(): Int = R.layout.activity_saloon

    override fun initView() {
        super.initView()
        initRecommendFragment()
    }

    private fun initRecommendFragment() {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (dynamic == null) {
            dynamic = DynamicFragment()
            transaction.add(R.id.fl_main_container, dynamic!!)
        }
        hideFragment(transaction)
        transaction.show(dynamic!!)
        transaction.commit()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        if (dynamic != null) {
            transaction.hide(dynamic!!)
        }
    }

}