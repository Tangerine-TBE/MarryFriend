package com.twx.marryfriend.vip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity

class VipActivity : MainBaseViewActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vip)
    }

    override fun getLayoutView(): Int = R.layout.activity_vip

    override fun initView() {
        super.initView()
    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()
    }

}