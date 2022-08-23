package com.twx.marryfriend.set.web

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_set_web.*

class SetWebActivity : MainBaseViewActivity() {

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

        val webSettings: WebSettings = wv_web_container.settings
        webSettings.javaScriptEnabled = true
        wv_web_container.loadUrl(url)

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

    override fun onDestroy() {
        super.onDestroy()
        wv_web_container.loadUrl("about:blank");
        wv_web_container.destroy()
    }

}