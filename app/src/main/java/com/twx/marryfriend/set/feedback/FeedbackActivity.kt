package com.twx.marryfriend.set.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.set.adapter.FeedbackAdapter
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : MainBaseViewActivity() {

    private lateinit var adapter1: FeedbackAdapter
    private lateinit var adapter2: FeedbackAdapter

    override fun getLayoutView(): Int = R.layout.activity_feedback

    override fun initView() {
        super.initView()

        adapter1 = FeedbackAdapter(DataProvider.FeedbackTopData)
        adapter2 = FeedbackAdapter(DataProvider.FeedbackBottomData)

        rv_feedback_container_top.layoutManager = LinearLayoutManager(this)
        rv_feedback_container_bottom.layoutManager = LinearLayoutManager(this)

        rv_feedback_container_top.adapter = adapter1
        rv_feedback_container_bottom.adapter = adapter2

        adapter1.notifyDataSetChanged()
        adapter2.notifyDataSetChanged()

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


        adapter1.setOnItemClickListener(object : FeedbackAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                ToastUtils.showShort(DataProvider.FeedbackTopData[position].title)
            }
        })

        adapter2.setOnItemClickListener(object : FeedbackAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                ToastUtils.showShort(DataProvider.FeedbackBottomData[position].title)
            }
        })


        ll_feedback_service.setOnClickListener {
            ToastUtils.showShort("在线客服")
        }

        ll_feedback_feedback.setOnClickListener {
            ToastUtils.showShort("意见反馈")
        }

    }

}