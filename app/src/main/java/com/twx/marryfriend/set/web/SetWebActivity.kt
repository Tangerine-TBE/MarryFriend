package com.twx.marryfriend.set.web

import android.content.Context
import android.content.Intent
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_set_web.*

open class SetWebActivity : MainBaseViewActivity() {

    private var mAgentWeb: AgentWeb? = null

    override fun getLayoutView(): Int = R.layout.activity_set_web

    companion object {
        private const val WEB_NAME = "web_name"
        private const val WEB_URL = "web_url"
        fun getIntent(context: Context, name: String, url: String): Intent {
            val intent = Intent(context, SetWebActivity::class.java)
            intent.putExtra(WEB_NAME, name)
            intent.putExtra(WEB_URL, url)
            return intent
        }
    }

    override fun initView() {
        super.initView()

        val name = intent.getStringExtra("web_name").toString()

        val url = intent.getStringExtra("web_url").toString()

        tv_web_name.text = name

        mAgentWeb = AgentWeb.with(this) //传入Activity
            .setAgentWebParent(ll_web_container, LinearLayout.LayoutParams(-1, -1)) //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
            .useDefaultIndicator() // 使用默认进度条
            .createAgentWeb() //
            .ready()
            .go(url)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_web_finish.setOnClickListener {
            finish()
        }

    }


    override fun onPause() {
        mAgentWeb!!.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb!!.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        //mAgentWeb.destroy();
        mAgentWeb!!.webLifeCycle.onDestroy()
    }

}