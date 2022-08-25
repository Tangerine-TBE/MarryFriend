package com.twx.marryfriend.mine.greet

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.SPStaticUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.Constant
import kotlinx.android.synthetic.main.activity_greet_info.*
import java.io.File

class GreetInfoActivity : MainBaseViewActivity() {

    private var greetInfo = ""

    override fun getLayoutView(): Int = R.layout.activity_greet_info

    override fun initView() {
        super.initView()

        greetInfo = SPStaticUtils.getString(Constant.ME_GREET, "")

        if (greetInfo != "") {
            rl_greet_info_empty.visibility = View.GONE
            ll_greet_info_container.visibility = View.VISIBLE

            tv_greet_info_container.text = greetInfo

        } else {
            rl_greet_info_empty.visibility = View.VISIBLE
            ll_greet_info_container.visibility = View.GONE
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

        iv_greet_info_finish.setOnClickListener {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }

        tv_greet_info_add.setOnClickListener {

            val intent = Intent(this, GreetEditActivity::class.java)
            intent.putExtra("greet", "")
            startActivityForResult(intent, 0)

        }

        tv_greet_info_edit.setOnClickListener {

            val intent = Intent(this, GreetEditActivity::class.java)
            intent.putExtra("greet", greetInfo)
            startActivityForResult(intent, 0)

        }

        tv_greet_info_delete.setOnClickListener {

            greetInfo = ""

            SPStaticUtils.put(Constant.ME_GREET, "")

            rl_greet_info_empty.visibility = View.VISIBLE
            ll_greet_info_container.visibility = View.GONE

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {

                    rl_greet_info_empty.visibility = View.GONE
                    ll_greet_info_container.visibility = View.VISIBLE

                    greetInfo = data?.getStringExtra("data").toString()

                    tv_greet_info_container.text = greetInfo

                }
            }
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

}