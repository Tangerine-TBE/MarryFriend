package com.twx.marryfriend.dynamic.saloon.tip

import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.TrendTipBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import kotlinx.android.synthetic.main.activity_tips.*
import java.util.*

class TipsActivity : MainBaseViewActivity(),
    com.twx.marryfriend.net.callback.dynamic.IGetTrendTipsCallback {


    private lateinit var getTrendTipsPresent: com.twx.marryfriend.net.impl.dynamic.getTrendTipsPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_tips

    override fun initView() {
        super.initView()

        getTrendTipsPresent = com.twx.marryfriend.net.impl.dynamic.getTrendTipsPresentImpl.getsInstance()
        getTrendTipsPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()

        getData(1)
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        rv_tip_finish.setOnClickListener {
            finish()
        }

    }


    private fun getData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.UP_DOWN] = SPStaticUtils.getString(Constant.USER_ID, "13")

        getTrendTipsPresent.getTrendTips(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetTrendTipsSuccess(trendTipBean: TrendTipBean?) {

    }

    override fun onGetTrendTipsError() {

    }

}