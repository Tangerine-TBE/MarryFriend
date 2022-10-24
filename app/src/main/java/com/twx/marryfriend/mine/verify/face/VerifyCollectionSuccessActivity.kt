package com.twx.marryfriend.mine.verify.face

import android.text.TextUtils
import android.util.Log
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


        val identityCode = SPStaticUtils.getString(Constant.TRUE_ID, "")

        SPStaticUtils.put(Constant.ME_SEX, isSex(identityCode))

        SPStaticUtils.put(Constant.ME_BIRTH_YEAR, identityCode.substring(6, 10).toInt() - TimeUtils.date2String(TimeUtils.getNowDate(), "yyyy").toInt() + 100)
        SPStaticUtils.put(Constant.ME_BIRTH_MONTH, identityCode.substring(10, 12).toInt() - 1)
        SPStaticUtils.put(Constant.ME_BIRTH_DAY, identityCode.substring(12, 14).toInt() - 1)


        SPStaticUtils.put(Constant.ME_BIRTH,
            "${identityCode.substring(6, 10).toInt()}" + "-${
                identityCode.substring(10, 12).toInt()
            }-${identityCode.substring(12, 14).toInt()}")

        val intent = intent
        setResult(RESULT_OK, intent)
        finish()

    }

    /**
     * 1 man 2 girl 果是奇数性别为男，偶数则为女。
     */
    open fun isSex(idCard: String): Int {
        return if (!TextUtils.isEmpty(idCard) && idCard.length == 18) {
            if (idCard.substring(16, 17).toInt() % 2 == 0) {
                2
            } else {
                1
            }
        } else 0
    }


}