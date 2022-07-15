package com.twx.marryfriend.mine.verify.face

import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.TimeUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.constant.Constant

open class VerifyCollectionSuccessActivity : MainBaseViewActivity() {

    override fun getLayoutView(): Int = R.layout.activity_collect_success

    override fun initView() {
        super.initView()
    }

    override fun initLoadData() {
        super.initLoadData()
        update()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()
    }

    // 开始上传信息
    private fun update() {

        SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, true)

        val identityCode = SPStaticUtils.getString(Constant.TRUE_ID, "")

        SPStaticUtils.put(Constant.ME_BIRTH_YEAR, identityCode.substring(6, 10).toInt() - TimeUtils.date2String(TimeUtils.getNowDate(), "yyyy").toInt() + 100)
        SPStaticUtils.put(Constant.ME_BIRTH_MONTH, identityCode.substring(10, 12).toInt() - 1)
        SPStaticUtils.put(Constant.ME_BIRTH_DAY, identityCode.substring(12, 14).toInt() - 1)

        val intent = intent
        setResult(RESULT_OK, intent)
        finish()

    }

}