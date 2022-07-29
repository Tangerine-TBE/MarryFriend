package com.twx.marryfriend.set.logoff

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_logoff.*
import kotlinx.android.synthetic.main.activity_safe.*

class LogoffActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_logoff

    private val mBeginTime: Long = 25000
    private var isCurrentDown = false
    private var mCountDownTimer: CountDownTimer? = null

    override fun initView() {
        super.initView()

        startCurrentDownTimer()
    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_logoff_finish.setOnClickListener {
            finish()
        }

        tv_logoff_cancel.setOnClickListener {
            finish()
        }

        tv_logoff_continue.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LogoffDialog(this))
                .show()
        }
    }


    // 验证码倒计时
    private fun startCurrentDownTimer() {
        tv_logoff_continue.isEnabled = false
        isCurrentDown = true
        mCountDownTimer = object : CountDownTimer(mBeginTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_logoff_continue.text = "继续注销（${millisUntilFinished.div(1000).toString()}s）"
            }

            override fun onFinish() {
                tv_logoff_continue.text = "继续注销"
                tv_logoff_continue.isEnabled = true
                isCurrentDown = false
            }
        }.start()
    }

    inner class LogoffDialog(context: Context) : FullScreenPopupView(context) {

        private var isAgree = false

        override fun getImplLayoutId(): Int = R.layout.dialog_logoff

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_logoff_close)
            val check = findViewById<ImageView>(R.id.iv_dialog_logoff_check)
            val agreement = findViewById<TextView>(R.id.tv_dialog_logoff_detail)
            val cancel = findViewById<TextView>(R.id.tv_dialog_logoff_cancel)
            val confirm = findViewById<TextView>(R.id.tv_dialog_logoff_open)

            val str = resources.getString(R.string.logoff_agreement)
            val stringBuilder = SpannableStringBuilder(str)
            val span1 = TextViewSpan1()
            stringBuilder.setSpan(span1, 2, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            agreement.text = stringBuilder
            agreement.movementMethod = LinkMovementMethod.getInstance()

            close.setOnClickListener {
                dismiss()
            }

            cancel.setOnClickListener {
                dismiss()
            }

            check.setOnClickListener {
                if (isAgree) {
                    check.setImageResource(R.drawable.ic_logoff_agree_non)
                } else {
                    check.setImageResource(R.drawable.ic_logoff_agree)
                }
                isAgree = !isAgree
            }


            confirm.setOnClickListener {
                if (isAgree) {
                    ToastUtils.showShort("注销")
                    dismiss()
                } else {
                    ToastUtils.showShort("请同意《注销佳偶婚恋交友》")
                    dismiss()
                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()

        }

    }

    inner class TextViewSpan1 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.purple_500)
        }

        override fun onClick(widget: View) {
            //点击事件
            ToastUtils.showShort(R.string.logoff_agreement)
        }
    }

}