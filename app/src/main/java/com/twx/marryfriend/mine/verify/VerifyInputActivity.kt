package com.twx.marryfriend.mine.verify

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.AccessTokenBean
import com.twx.marryfriend.bean.IdentityVerifyBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoIdentityVerifyCallback
import com.twx.marryfriend.net.impl.doIdentityVerifyPresentImpl
import kotlinx.android.synthetic.main.activity_verify_input.*
import java.util.*

class VerifyInputActivity : MainBaseViewActivity(), IDoIdentityVerifyCallback {

    // 实名认证 姓名
    private var name = ""

    // 实名认证 身份证号
    private var identityCode = ""

    private lateinit var doIdentityVerifyPresent: doIdentityVerifyPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_verify_input

    override fun initView() {
        super.initView()

        doIdentityVerifyPresent = doIdentityVerifyPresentImpl.getsInstance()
        doIdentityVerifyPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        et_verify_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
            }

        })

        et_verify_id.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                identityCode = s.toString()

                if (s.length == 18) {
                    KeyboardUtils.hideSoftInput(this@VerifyInputActivity)
                }
            }

        })

        tv_verify_verify.setOnClickListener {

            if (name != "" && identityCode != "") {

                if (RegexUtils.isIDCard18Exact(identityCode)) {
                    val map: MutableMap<String, String> = TreeMap()

                    map[Contents.ACCESS_TOKEN] =
                        SPStaticUtils.getString(Constant.ACCESS_TOKEN)
                    map[Contents.CONTENT_TYPE] = "application/json"
                    map[Contents.ID_CARD_NUMBER] = identityCode
                    map[Contents.NAME] = name
                    doIdentityVerifyPresent.doIdentityVerify(map)

                    ll_verify_input_loading.visibility = View.VISIBLE

                } else {
                    ToastUtils.showShort("请输入正确的身份证号")
                }
            } else {
                ToastUtils.showShort("请填写详细信息")
            }

        }

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoIdentityVerifySuccess(identityVerifyBean: IdentityVerifyBean) {

        ToastUtils.showShort("身份证验证通过，准备开始传递数据")

        SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, true)

        ll_verify_input_loading.visibility = View.GONE

        val intent = intent
        intent.putExtra("name", name)
        intent.putExtra("id", identityCode)
        setResult(RESULT_OK, intent)
        finish()

    }

    override fun onDoIdentityVerifyError() {
        ll_verify_input_loading.visibility = View.GONE
    }

}