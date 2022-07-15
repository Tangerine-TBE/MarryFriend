package com.twx.marryfriend.mine.verify

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.Constant
import kotlinx.android.synthetic.main.activity_verify.*

class VerifyActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_verify

    override fun initView() {
        super.initView()

        if (SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {
            rl_verify_verify_success.visibility = View.VISIBLE
            rl_verify_verify_non.visibility = View.GONE

            tv_verify_name.text = hideName(SPStaticUtils.getString(Constant.TRUE_NAME, ""))
            tv_verify_id.text = hideId(SPStaticUtils.getString(Constant.TRUE_ID, ""))

        } else {
            rl_verify_verify_success.visibility = View.GONE
            rl_verify_verify_non.visibility = View.VISIBLE
        }

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
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }

        tv_verify_verify_non.setOnClickListener {
            val intent = Intent(this, VerifyInputActivity::class.java)
            startActivityForResult(intent, 0)
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    rl_verify_verify_success.visibility = View.VISIBLE
                    rl_verify_verify_non.visibility = View.GONE

                    tv_verify_name.text = hideName(SPStaticUtils.getString(Constant.TRUE_NAME, ""))
                    tv_verify_id.text = hideId(SPStaticUtils.getString(Constant.TRUE_ID, ""))

                    SPStaticUtils.put(Constant.ME_TURE_NAME,
                        hideName(SPStaticUtils.getString(Constant.TRUE_NAME, "")))
                    SPStaticUtils.put(Constant.ME_TURE_ID,
                        hideId(SPStaticUtils.getString(Constant.TRUE_ID, "")))
                }
            }
        }
    }


    // 取出第一个汉字，然后添加星号

    private fun hideName(name: String): String {
        var newName = name.substring(0, 1)
        for (i in 0.until(name.length - 1)) {
            newName = "$newName*"
        }
        return newName
    }

    // 取出前6个数字，然后添加星号

    private fun hideId(name: String): String {
        var newId = name.substring(0, 6)
        for (i in 0.until(name.length - 6)) {
            newId = "$newId*"
        }
        return newId
    }

}