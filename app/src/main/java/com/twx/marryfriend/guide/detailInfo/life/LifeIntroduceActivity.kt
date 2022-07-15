package com.twx.marryfriend.guide.detailInfo.life

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.TextVerifyBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import kotlinx.android.synthetic.main.activity_base_info.*
import kotlinx.android.synthetic.main.activity_life_introduce.*
import kotlinx.android.synthetic.main.layout_guide_step_name.*
import java.util.*

class LifeIntroduceActivity : MainBaseViewActivity(), IDoTextVerifyCallback {

    private var picPath = ""
    private var introduce = ""

    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_life_introduce

    override fun initView() {
        super.initView()

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

        val requestOptions = RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE);

        picPath = intent.getStringExtra("path").toString()
        introduce = intent.getStringExtra("introduce").toString()

        Glide.with(this)
            .load(picPath)
            .apply(requestOptions)
            .into(iv_life_introduce_container)

        et_life_introduce_introduce.setText(introduce)

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_life_introduce_finish.setOnClickListener {
            finish()
            this.finish()
        }

        tv_life_introduce_confirm.setOnClickListener {

            introduce = et_life_introduce_introduce.text.toString().trim { it <= ' ' }

            if (introduce.isNotEmpty()) {
                val map: MutableMap<String, String> = TreeMap()
                map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN,"")
                map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                map[Contents.TEXT] = introduce
                doTextVerifyPresent.doTextVerify(map)

            } else {
                val intent = intent
                intent.putExtra("introduce", introduce)
                setResult(RESULT_OK, intent)
                finish()
            }

        }

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

        if (textVerifyBean.conclusion == "合规") {

            val intent = intent
            intent.putExtra("introduce", introduce)
            setResult(RESULT_OK, intent)
            finish()

        } else {
            ToastUtils.showShort(textVerifyBean.data[0].msg)
            et_life_introduce_introduce.setText("")
        }

    }


    override fun onDoTextVerifyError() {
        ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
    }

}