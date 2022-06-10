package com.twx.marryfriend.base

import android.os.Bundle
import com.twx.marryfriend.base.BaseActivity

open abstract class BaseViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setChildTheme()
        setContentView(getLayoutView())
        initView()
        initPresent()
        initLoadData()
        initEvent()
    }

    open fun setChildTheme() {
    }

    fun showLoading() {

    }

    fun dismissLoading() {

    }

    abstract fun getLayoutView(): Int

    open fun initEvent() {

    }

    open fun initLoadData() {

    }

    open fun initPresent() {

    }

    open fun initView() {

    }

}