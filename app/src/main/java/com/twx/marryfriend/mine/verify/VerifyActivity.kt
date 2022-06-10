package com.twx.marryfriend.mine.verify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_verify

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

        iv_verify_finish.setOnClickListener {
            finish()
        }

    }

}