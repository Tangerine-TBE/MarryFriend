package com.twx.marryfriend.mine.greet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.TextVerifyBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import kotlinx.android.synthetic.main.activity_greet_edit.*
import java.util.*

class GreetEditActivity : MainBaseViewActivity(), IDoTextVerifyCallback {

    private var greet = ""

    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_greet_edit

    override fun initView() {
        super.initView()

        greet = intent.getStringExtra("greet").toString()

        if (greet != "") {
            et_greet_edit_container.setText(greet)
            tv_greet_edit_sum.text = greet.length.toString()

        } else {
            et_greet_edit_container.setText("")
            tv_greet_edit_sum.text = 0.toString()
        }

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_greet_edit_finish.setOnClickListener {
            finish()
        }

        et_greet_edit_container.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                greet = s.toString()

                tv_greet_edit_sum.text = greet.length.toString()

            }

        })

        tv_greet_edit_save.setOnClickListener {

            if (greet.length >= 10) {

                val map: MutableMap<String, String> = TreeMap()
                map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN,"")
                map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                map[Contents.TEXT] = greet
                doTextVerifyPresent.doTextVerify(map)

            } else {
                ToastUtils.showShort("请输入至少10字内容")
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

        if (textVerifyBean.conclusion == "合规") {

            SPStaticUtils.put(Constant.ME_GREET, greet)
            val intent = intent
            intent.putExtra("data", greet)
            setResult(RESULT_OK, intent)
            finish()

        } else {

            ToastUtils.showShort(textVerifyBean.data[0].msg)
            greet = ""

            et_greet_edit_container.setText("")
            tv_greet_edit_sum.text = 0.toString()

        }

    }

    override fun onDoTextVerifyError() {
        ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
    }

}