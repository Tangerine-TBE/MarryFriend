package com.twx.marryfriend.set.report

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.dynamic.saloon.tip.TipsActivity
import com.twx.marryfriend.set.adapter.ReportAdapter
import kotlinx.android.synthetic.main.activity_report_reason.*

class ReportReasonActivity : MainBaseViewActivity() {

    private var hostId = ""

    private var guestId = ""

    private lateinit var adapter: ReportAdapter

    companion object {

        /**
         *
         * HOST_ID : 举报者ID
         *
         * GUEST_ID ： 被举报者ID
         *
         * */

        private const val HOST_ID = "host_Id"
        private const val GUEST_ID = "guest_Id"

        fun getIntent(context: Context, hostId: String, guestId: String): Intent {
            val intent = Intent(context, ReportReasonActivity::class.java)
            intent.putExtra(HOST_ID, hostId)
            intent.putExtra(GUEST_ID, guestId)
            return intent
        }
    }

    override fun getLayoutView(): Int = R.layout.activity_report_reason

    override fun initView() {
        super.initView()

        hostId = intent.getStringExtra("host_Id").toString()
        guestId = intent.getStringExtra("guest_Id").toString()

        adapter = ReportAdapter(DataProvider.ReportData)

        rv_report_reason_container.layoutManager = LinearLayoutManager(this)
        rv_report_reason_container.adapter = adapter

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_report_reason_finish.setOnClickListener {
            finish()
        }

        adapter.setOnItemClickListener(object : ReportAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                startActivityForResult(ReportActivity.getIntent(this@ReportReasonActivity,
                    hostId,
                    guestId,
                    position), 0)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    finish()
                }

            }
        }
    }

}