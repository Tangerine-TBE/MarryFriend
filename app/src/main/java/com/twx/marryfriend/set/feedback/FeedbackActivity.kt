package com.twx.marryfriend.set.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.set.adapter.FeedbackAdapter
import kotlinx.android.synthetic.main.activity_feedback.*
import kotlinx.android.synthetic.main.activity_set_web.*
import okhttp3.internal.wait

class FeedbackActivity : MainBaseViewActivity() {


    override fun getLayoutView(): Int = R.layout.activity_feedback

    override fun initView() {
        super.initView()

        val webSettings: WebSettings = wv_feedback_container.settings
        webSettings.javaScriptEnabled = true
        wv_feedback_container.loadUrl(DataProvider.WebUrlData[0].url)

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initEvent() {
        super.initEvent()

        iv_feedback_finish.setOnClickListener {
            finish()
        }


        ll_feedback_service.setOnClickListener {
            ToastUtils.showShort("在线客服")
        }

        ll_feedback_feedback.setOnClickListener {
            ToastUtils.showShort("意见反馈")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        wv_feedback_container.loadUrl("about:blank");
        wv_feedback_container.destroy()
    }

}