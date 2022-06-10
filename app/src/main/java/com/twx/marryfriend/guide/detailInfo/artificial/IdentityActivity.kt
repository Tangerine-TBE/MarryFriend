package com.twx.marryfriend.guide.detailInfo.artificial

import android.R.attr.phoneNumber
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_detail_info.*
import kotlinx.android.synthetic.main.activity_identity.*


class IdentityActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_identity

    override fun initView() {
        super.initView()

        val str = "海外用户人工认证请拨打：4000066261"
        val stringBuilder = SpannableStringBuilder(str)
        val span = indentityTextViewSpan()
        stringBuilder.setSpan(span, 12, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_identity_phone.text = stringBuilder
        tv_identity_phone.movementMethod = LinkMovementMethod.getInstance()

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_identity_finish.setOnClickListener {
            finish()
        }

        ll_identity_idCard_upload.setOnClickListener {
            ToastUtils.showShort("上传身份证照片")
        }

        tv_identity_idCard_reUpload.setOnClickListener {
            ToastUtils.showShort("重新上传身份证照片")
        }

        ll_identity_photo_upload.setOnClickListener {
            ToastUtils.showShort("上传自拍照")
        }

        tv_identity_photo_reUpload.setOnClickListener {
            ToastUtils.showShort("重新上传自拍照")
        }

    }


    inner class indentityTextViewSpan : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            //点击事件

            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:4000066261"))
            startActivity(intent)

        }
    }

}