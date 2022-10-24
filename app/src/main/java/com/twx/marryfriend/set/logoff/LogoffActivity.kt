package com.twx.marryfriend.set.logoff

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ThreadUtils
import com.blankj.utilcode.util.ToastUtils
import com.just.agentweb.AgentWeb
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.begin.BeginActivity
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.set.web.SetWebActivity
import com.twx.marryfriend.utils.SpLoginUtil
import com.twx.marryfriend.utils.SpUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_logoff.*
import kotlinx.android.synthetic.main.activity_safe.*
import kotlinx.android.synthetic.main.activity_set_web.*
import java.util.*

class LogoffActivity : MainBaseViewActivity() {

    private var mAgentWeb: AgentWeb? = null

    override fun getLayoutView(): Int = R.layout.activity_logoff

    private val mBeginTime: Long = 25000
    private var isCurrentDown = false
    private var mCountDownTimer: CountDownTimer? = null

    override fun initView() {
        super.initView()

        startCurrentDownTimer()

        mAgentWeb = AgentWeb.with(this) //传入Activity
            .setAgentWebParent(rl_logoff_container,
                RelativeLayout.LayoutParams(-1,
                    -1)) //传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
            .useDefaultIndicator() // 使用默认进度条
            .createAgentWeb() //
            .ready()
            .go(DataProvider.WebUrlData[6].url)

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

        tv_logoff_complete.setOnClickListener {
            startActivity(Intent(this, BeginActivity::class.java))
            ActivityUtils.finishAllActivities()
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

                    SpLoginUtil.deleteUserInfo()
                    ll_logoff_loading.visibility = View.VISIBLE


                    val task: TimerTask = object : TimerTask() {
                        override fun run() {
                            ThreadUtils.runOnUiThread {
                                ll_logoff_loading.visibility = View.GONE
                                ll_logoff_complete.visibility = View.VISIBLE
                            }
                        }
                    }
                    val timer = Timer()
                    timer.schedule(task, 1000)


                    dismiss()
                } else {
                    ToastUtils.showShort("请同意注销协议")
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
            startActivity(SetWebActivity.getIntent(this@LogoffActivity,
                "注销通知",
                DataProvider.WebUrlData[6].url))
        }
    }

}