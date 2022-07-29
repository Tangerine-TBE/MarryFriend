package com.twx.marryfriend.set.safe

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.twx.marryfriend.set.logoff.LogoffActivity
import kotlinx.android.synthetic.main.activity_safe.*

class SafeActivity : MainBaseViewActivity() {

    private var mStealthList: MutableList<String> = arrayListOf()

    override fun getLayoutView(): Int = R.layout.activity_safe

    override fun initView() {
        super.initView()

    }

    override fun initLoadData() {
        super.initLoadData()

        mStealthList.add("我已脱单")
        mStealthList.add("不想被别人看到")
        mStealthList.add("以后在用")
        mStealthList.add("其他")

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_safe_finish.setOnClickListener {
            finish()
        }

        rl_safe_phone.setOnClickListener {
            ToastUtils.showShort("修改手机号码")
        }

        rl_safe_logoff.setOnClickListener {
            ToastUtils.showShort("账号注销")
            startActivity(Intent(this, LogoffActivity::class.java))
        }

        rl_safe_public.setOnClickListener {
            ToastUtils.showShort("资料是否隐身")
        }

        sw_safe_switch.setOnClickListener {

            if (!sw_safe_switch.isChecked) {
                ToastUtils.showShort("您已关闭隐藏资料")
            } else {
                sw_safe_switch.isChecked = false

                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(StealthDialog(this))
                    .show()

            }

        }

    }


    inner class StealthDialog(context: Context) : FullScreenPopupView(context) {

        private var needOpen = false
        private var reason = ""

        override fun getImplLayoutId(): Int = R.layout.dialog_safe_stealth

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_safe_stealth_close)
            val wheel = findViewById<WheelPicker>(R.id.wp_dialog_safe_stealth_container)
            val edit = findViewById<EditText>(R.id.et_dialog_safe_stealth_reason)
            val cancel = findViewById<TextView>(R.id.tv_dialog_safe_stealth_cancel)
            val open = findViewById<TextView>(R.id.tv_dialog_safe_stealth_open)


            wheel.data = mStealthList

            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER


            close.setOnClickListener {
                dismiss()
            }

            cancel.setOnClickListener {
                dismiss()
            }

            wheel.setOnItemSelectedListener { picker, data, position ->
                when (position) {
                    3 -> {
                        reason = ""
                        edit.visibility = View.VISIBLE
                    }
                    else -> {
                        reason = mStealthList[position]
                        edit.visibility = View.GONE
                    }
                }
            }

            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    reason = s.toString()
                }

            })

            open.setOnClickListener {
                if (reason != "") {
                    needOpen = true
                    ToastUtils.showShort(reason)
                    dismiss()
                } else {
                    ToastUtils.showShort("请选择原因")
                }

            }


        }

        override fun onDismiss() {
            super.onDismiss()
            if (needOpen) {
                sw_safe_switch.isChecked = true
            }
        }

    }

}

