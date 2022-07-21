package com.twx.module_dynamic.saloon.tip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.SPStaticUtils
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.TrendTipBean
import com.twx.module_dynamic.net.callback.IGetTrendTipsCallback
import com.twx.module_dynamic.net.impl.getTrendTipsPresentImpl
import kotlinx.android.synthetic.main.activity_tips.*
import java.util.*

class TipsActivity : MainBaseViewActivity(), IGetTrendTipsCallback {


    private lateinit var getTrendTipsPresent: getTrendTipsPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_tips

    override fun initView() {
        super.initView()

        getTrendTipsPresent = getTrendTipsPresentImpl.getsInstance()
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