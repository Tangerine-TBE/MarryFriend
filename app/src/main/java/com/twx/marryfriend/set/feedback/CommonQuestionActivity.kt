package com.twx.marryfriend.set.feedback

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import kotlinx.android.synthetic.main.activity_common_question.*

class CommonQuestionActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_common_question

    override fun initView() {
        super.initView()




    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_common_question_finish.setOnClickListener {
            finish()
        }

    }

}