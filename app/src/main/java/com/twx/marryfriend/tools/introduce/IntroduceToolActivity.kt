package com.twx.marryfriend.tools.introduce

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.BaseInfoUpdateBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback
import com.twx.marryfriend.net.impl.doUpdateBaseInfoPresentImpl
import kotlinx.android.synthetic.main.activity_dynamic_other_show.*
import kotlinx.android.synthetic.main.activity_introduce_tool.*
import java.util.*

class IntroduceToolActivity : MainBaseViewActivity(), IDoUpdateBaseInfoCallback {

    private var introduce = ""

    private lateinit var doUpdateBaseInfoPresent: doUpdateBaseInfoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_introduce_tool

    override fun initView() {
        super.initView()

        doUpdateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        doUpdateBaseInfoPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_introduce_finish.setOnClickListener {
            finish()
        }

        iv_introduce_upload.setOnClickListener {
            introduce = et_introduce_content.text.toString().trim { it <= ' ' }
            // 上传信息
            if (introduce.length > 10) {
                doUploadInfo(introduce)
                ll_introduce_loading.visibility = View.VISIBLE
            } else {
                ToastUtils.showShort("您的自我介绍太短了，请添加更多信息")
            }

        }

        et_introduce_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    tv_introduce_sum.text = s.length.toString()
                }
            }

        })

    }

    private fun doUploadInfo(introduce: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.BASE_UPDATE] = "{\"introduce_self\":\"$introduce\"}"
        doUpdateBaseInfoPresent.doUpdateBaseInfo(map)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {
        ll_introduce_loading.visibility = View.GONE
        if (baseInfoUpdateBean != null) {
            if (baseInfoUpdateBean.code == 200) {

                SPStaticUtils.put(Constant.ME_INTRODUCE, introduce)

                introduce = ""
                et_introduce_content.setText("")

                this.finish()

            }
        }

    }

    override fun onDoUpdateBaseInfoError() {
        ll_introduce_loading.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()

        doUpdateBaseInfoPresent.unregisterCallback(this)

    }

}