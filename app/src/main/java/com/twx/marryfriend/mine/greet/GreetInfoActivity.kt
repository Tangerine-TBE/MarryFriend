package com.twx.marryfriend.mine.greet

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.GreetInfoBean
import com.twx.marryfriend.bean.UpdateGreetInfoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoUpdateGreetInfoCallback
import com.twx.marryfriend.net.callback.IGetGreetInfoCallback
import com.twx.marryfriend.net.impl.doUpdateGreetInfoPresentImpl
import com.twx.marryfriend.net.impl.getGreetInfoPresentImpl
import kotlinx.android.synthetic.main.activity_greet_info.*
import java.io.File
import java.util.*

class GreetInfoActivity : MainBaseViewActivity(), IGetGreetInfoCallback,
    IDoUpdateGreetInfoCallback {

    private var greetInfo = ""

    private lateinit var getGreetInfoPresent: getGreetInfoPresentImpl
    private lateinit var doUpdateGreetPresent: doUpdateGreetInfoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_greet_info

    override fun initView() {
        super.initView()

        getGreetInfoPresent = getGreetInfoPresentImpl.getsInstance()
        getGreetInfoPresent.registerCallback(this)

        doUpdateGreetPresent = doUpdateGreetInfoPresentImpl.getsInstance()
        doUpdateGreetPresent.registerCallback(this)

        greetInfo = SPStaticUtils.getString(Constant.ME_GREET, "")

        if (greetInfo != "") {
            rl_greet_info_empty.visibility = View.GONE
            ll_greet_info_container.visibility = View.VISIBLE

            tv_greet_info_container.text = greetInfo

        } else {
            rl_greet_info_empty.visibility = View.VISIBLE
            ll_greet_info_container.visibility = View.GONE
        }

    }

    override fun initLoadData() {
        super.initLoadData()

        getGreetInfo()

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_greet_info_finish.setOnClickListener {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }

        tv_greet_info_add.setOnClickListener {

            val intent = Intent(this, GreetEditActivity::class.java)
            intent.putExtra("greet", "")
            startActivityForResult(intent, 0)

        }

        tv_greet_info_edit.setOnClickListener {

            val intent = Intent(this, GreetEditActivity::class.java)
            intent.putExtra("greet", greetInfo)
            startActivityForResult(intent, 0)

        }

        tv_greet_info_delete.setOnClickListener {

            deleteGreetInfo()

        }

    }


    // 获取招呼语信息
    private fun getGreetInfo() {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
            getGreetInfoPresent.getGreetInfo(map)



    }


    // 删除招呼语信息
    private fun deleteGreetInfo() {



            val map: MutableMap<String, String> = TreeMap()
            map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
            map[Contents.GREET_UPDATE] = getGreetInfo("")
            doUpdateGreetPresent.doUpdateGreetInfo(map)




    }

    // 获取招呼语信息
    private fun getGreetInfo(greet: String): String {
        return " {\"zhaohuyu_content\":   \"$greet\"}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {

                    rl_greet_info_empty.visibility = View.GONE
                    ll_greet_info_container.visibility = View.VISIBLE

                    greetInfo = data?.getStringExtra("data").toString()

                    tv_greet_info_container.text = greetInfo

                }
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateGreetInfoSuccess(updateGreetInfoBean: UpdateGreetInfoBean?) {
        if (updateGreetInfoBean != null) {
            if (updateGreetInfoBean.code == 200) {

                greetInfo = ""

                SPStaticUtils.put(Constant.ME_GREET, "")

                rl_greet_info_empty.visibility = View.VISIBLE
                ll_greet_info_container.visibility = View.GONE

            } else {
                ToastUtils.showShort("删除失败，请稍后再试")
            }
        }
    }

    override fun onDoUpdateGreetInfoError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onGetGreetInfoSuccess(greetInfoBean: GreetInfoBean?) {
        if (greetInfoBean != null) {
            if (greetInfoBean.code == 200) {

                SPStaticUtils.put(Constant.ME_GREET, greetInfoBean.data.zhaohuyu_content)

                if (greetInfoBean.data.zhaohuyu_content != "") {
                    rl_greet_info_empty.visibility = View.GONE
                    ll_greet_info_container.visibility = View.VISIBLE

                    tv_greet_info_container.text = greetInfoBean.data.zhaohuyu_content

                } else {
                    rl_greet_info_empty.visibility = View.VISIBLE
                    ll_greet_info_container.visibility = View.GONE
                }
            }
        }
    }

    override fun onGetGreetInfoCodeError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()

        getGreetInfoPresent.unregisterCallback(this)
        doUpdateGreetPresent.unregisterCallback(this)

    }

}