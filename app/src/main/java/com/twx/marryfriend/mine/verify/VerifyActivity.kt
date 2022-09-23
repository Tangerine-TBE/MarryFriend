package com.twx.marryfriend.mine.verify

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.BaseInfoUpdateBean
import com.twx.marryfriend.bean.UpdateVerifyInfoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback
import com.twx.marryfriend.net.callback.IDoUpdateVerifyInfoCallback
import com.twx.marryfriend.net.impl.doUpdateBaseInfoPresentImpl
import com.twx.marryfriend.net.impl.doUpdateVerifyInfoPresentImpl
import com.twx.marryfriend.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_verify.*
import java.util.*

class VerifyActivity : MainBaseViewActivity(), IDoUpdateVerifyInfoCallback,
    IDoUpdateBaseInfoCallback {

    private lateinit var doUpdateVerifyInfoPresent: doUpdateVerifyInfoPresentImpl
    private lateinit var doUpdateBaseInfoPresent: doUpdateBaseInfoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_verify


    override fun initView() {
        super.initView()

        doUpdateVerifyInfoPresent = doUpdateVerifyInfoPresentImpl.getsInstance()
        doUpdateVerifyInfoPresent.registerCallback(this)

        doUpdateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        doUpdateBaseInfoPresent.registerCallback(this)


        if (SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {
            rl_verify_verify_success.visibility = View.VISIBLE
            rl_verify_verify_non.visibility = View.GONE

            tv_verify_name.text = hideName(SPStaticUtils.getString(Constant.TRUE_NAME, ""))
            tv_verify_id.text = hideId(SPStaticUtils.getString(Constant.TRUE_ID, ""))

        } else {
            rl_verify_verify_success.visibility = View.GONE
            rl_verify_verify_non.visibility = View.VISIBLE
        }

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_verify_finish.setOnClickListener {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }

        tv_verify_verify_non.setOnClickListener {
            val intent = Intent(this, VerifyInputActivity::class.java)
            startActivityForResult(intent, 0)
        }

    }

    private fun updateVerifyInfo() {

        val verifyInfoMap: MutableMap<String, String> = TreeMap()
        verifyInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        verifyInfoMap[Contents.VERIFY_UPDATE] = getVerifyInfo()
        doUpdateVerifyInfoPresent.doUpdateVerifyInfo(verifyInfoMap)
    }

    private fun updateBaseInfo() {

        val baseInfoMap: MutableMap<String, String> = TreeMap()
        baseInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        baseInfoMap[Contents.BASE_UPDATE] = getBaseInfo()
        doUpdateBaseInfoPresent.doUpdateBaseInfo(baseInfoMap)

    }

    private fun getVerifyInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 1)
        val name = SPStaticUtils.getString(Constant.TRUE_NAME, "")
        val id = SPStaticUtils.getString(Constant.TRUE_ID, "")
        val ta = 1

        return " {      \"user_sex\":         \"$sex\"," +       // 性别
                "\"identity_name\":    \"$name\"," +      // 身份证名字
                "\"identity_number\":  \"$id\"," +        // 身份证号码
                " \"identity_status\":   $ta}"
    }

    private fun getBaseInfo(): String {

        val age = TimeUtil.birthdayToAge(SPStaticUtils.getString(Constant.ME_BIRTH, ""))
        val birthday = SPStaticUtils.getString(Constant.ME_BIRTH, "")

        return " {      \"age\":         \"$age\"," +       // 用户年龄
                "\"birthday\":      \"$birthday\"}"            // 出生年月日
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {

                    Log.i("guo", "验证信息")
                    // 这个时候需要加一个数据上传
                    updateVerifyInfo()

                    rl_verify_verify_success.visibility = View.VISIBLE
                    rl_verify_verify_non.visibility = View.GONE

                    tv_verify_name.text = hideName(SPStaticUtils.getString(Constant.TRUE_NAME, ""))
                    tv_verify_id.text = hideId(SPStaticUtils.getString(Constant.TRUE_ID, ""))

                }
            }
        }
    }


    // 取出第一个汉字，然后添加星号

    private fun hideName(name: String): String {
        var newName = name.substring(0, 1)
        for (i in 0.until(name.length - 1)) {
            newName = "$newName*"
        }
        return newName
    }

    // 取出前6个数字，然后添加星号

    private fun hideId(name: String): String {
        var newId = name.substring(0, 6)
        for (i in 0.until(name.length - 6)) {
            newId = "$newId*"
        }
        return newId
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {
        if (baseInfoUpdateBean != null) {
            if (baseInfoUpdateBean.code == 200) {
                SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, true)
            } else {
                ToastUtils.showShort("数据上传失败，请重新认证")
            }
        }
    }

    override fun onDoUpdateBaseInfoError() {
        ToastUtils.showShort("数据上传失败，请重新认证")
    }

    override fun onDoUpdateVerifyInfoSuccess(updateVerifyInfoBean: UpdateVerifyInfoBean?) {
        if (updateVerifyInfoBean != null) {
            if (updateVerifyInfoBean.code == 200) {

                updateBaseInfo()

            } else {
                ToastUtils.showShort("数据上传失败，请重新认证")
            }
        }
    }

    override fun onDoUpdateVerifyInfoError() {
        ToastUtils.showShort("数据上传失败，请重新认证")
    }

    override fun onDestroy() {
        super.onDestroy()

        doUpdateVerifyInfoPresent.unregisterCallback(this)
        doUpdateBaseInfoPresent.unregisterCallback(this)

    }

}