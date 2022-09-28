package com.twx.marryfriend.dynamic.send.dialog

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.twx.marryfriend.R

@SuppressLint("NonConstantResourceId")
class VideoWaterMarkDialog : BaseDialog(), View.OnClickListener {

    var tv: TextView? = null

    private var gravity = Gravity.BOTTOM
    private var canceledOutside = true
    private var mOnPromptListener: OnPromptListener? = null
    fun setContentGravity(gravity: Int) {
        this.gravity = gravity
    }

    override val layoutId: Int
        get() = R.layout.dialog_audio_watermark

    override fun init() {
        initView()
        initData()
    }

    private fun initView() {
        tv = findViewById(R.id.tv_watermark_progress) as TextView?

        this.gravity = Gravity.BOTTOM
    }

    private fun initData() {
        val args = arguments
        setCanceledOnTouchOutside(canceledOutside)

    }


    override fun onClick(view: View) {
    }

    fun setOnPromptListener(onPromptListener: OnPromptListener?) {
        mOnPromptListener = onPromptListener
    }

    fun setCanceledOutside(canceledOutside: Boolean) {
        this.canceledOutside = canceledOutside
    }

    fun setContent(progress: Int) {
        tv?.text = "视频处理中${progress}%"
    }


    interface OnPromptListener {
        /**
         * 返回用户点击【确定】/【取消】
         *
         * @param isPositive 是否点击确定
         */
        fun onPrompt(isPositive: Boolean)
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_BACK) {
                true
            } else false
        }
    }

}