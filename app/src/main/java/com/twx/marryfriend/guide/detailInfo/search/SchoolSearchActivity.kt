package com.twx.marryfriend.guide.detailInfo.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.FullScreenDialog
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.BanBean
import com.twx.marryfriend.bean.SchoolBean
import com.twx.marryfriend.bean.TextVerifyBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.BaseInfoActivity
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.IGetSchoolCallback
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.getSchoolPresentImpl
import com.twx.marryfriend.utils.UnicodeUtils
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_school_search.*
import java.util.*


class SchoolSearchActivity : MainBaseViewActivity(), IGetSchoolCallback {

    // 自定义的学校名
    private var diySchool = ""

    // 用户搜索时输入的学校名
    private var inputSchool = ""

    // 所有学校数据存放数组
    private var mList: MutableList<String> = arrayListOf()

    // 符合搜索结果的学校数据存放数组
    private var mSearchList: MutableList<String> = arrayListOf()

    // 敏感字
    private var banTextList: MutableList<String> = arrayListOf()

    // 是否具有敏感词
    private var haveBanText = false

    private lateinit var schoolAdapter: SchoolSearchAdapter
    private lateinit var getSchoolPresent: getSchoolPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_school_search

    override fun initView() {
        super.initView()

        getSchoolPresent = getSchoolPresentImpl.getsInstance()
        getSchoolPresent.registerCallback(this)

        schoolAdapter = SchoolSearchAdapter(mSearchList)

        rv_search_school_container.layoutManager = LinearLayoutManager(this)
        rv_search_school_container.adapter = schoolAdapter

    }

    override fun initLoadData() {
        super.initLoadData()
        // 查看本地是否存储过数据，若无，则请求，若有，则直接从本地取出
        if (!SPStaticUtils.getBoolean(Constant.SCHOOL_HAVE, false)) {



                val map: MutableMap<String, String> = TreeMap()
                getSchoolPresent.getSchool(map)




        } else {
            getData()
        }

    }

    override fun initPresent() {
        super.initPresent()

        // 获取焦点
        et_school_container.isFocusable = true;
        et_school_container.isFocusableInTouchMode = true;
        et_school_container.requestFocus();

        // 弹出软键盘
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    override fun initEvent() {
        super.initEvent()

        iv_search_school_finish.setOnClickListener {
            finish()
        }

        et_school_container.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                KeyboardUtils.hideSoftInput(this)

                mSearchList.clear()
                val searchText = et_school_container.text.toString().trim { it <= ' ' }

                for (i in 0.until(mList.size)) {
                    if (mList[i].contains(searchText)) {
                        mSearchList.add(mList[i])
                    }
                }
                schoolAdapter.notifyDataSetChanged()
            }
            false
        }

        et_school_container.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {

                inputSchool = s.toString()

                mSearchList.clear()
                for (i in 0.until(mList.size)) {
                    if (mList[i].contains(s.toString())) {
                        mSearchList.add(mList[i])
                    }
                }
                schoolAdapter.notifyDataSetChanged()
            }

        })

        schoolAdapter.setOnItemClickListener(object : SchoolSearchAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {

                val intent = intent
                intent.putExtra("schoolName", mSearchList[position])
                setResult(RESULT_OK, intent)
                finish()

            }
        })

        tv_search_school_create.setOnClickListener {

            //点击没有找到,创建一个
            MobclickAgent.onEvent(this, "10020_education_add_school");

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(SchoolCreateDialog(this))
                .show()
        }

    }

    private fun getData() {
        val size = SPStaticUtils.getInt(Constant.SCHOOL_SUM, 0)
        for (i in 0.until(size)) {
            mList.add(SPStaticUtils.getString("school_item_$i", null))
        }
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetSchoolSuccess(schoolBean: SchoolBean) {
        SPStaticUtils.put(Constant.SCHOOL_SUM, schoolBean.data.size)
        for (i in 0.until(schoolBean.data.size)) {
            SPStaticUtils.put("school_item_$i", schoolBean.data[i])
            mList.add(schoolBean.data[i])
        }
        SPStaticUtils.put(Constant.SCHOOL_HAVE, true)
    }

    override fun onGetSchoolCodeError() {
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    inner class SchoolCreateDialog(context: Context) : FullScreenPopupView(context),
        IDoTextVerifyCallback {

        private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

        override fun getImplLayoutId(): Int = R.layout.dialog_school_create

        override fun onCreate() {
            super.onCreate()

            doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
            doTextVerifyPresent.registerCallback(this)

            findViewById<EditText>(R.id.et_dialog_school_name).setText(inputSchool)
            findViewById<EditText>(R.id.et_dialog_school_name).setSelection(inputSchool.length);

            diySchool = inputSchool

            if (inputSchool.length >= 4) {
                findViewById<TextView>(R.id.tv_dialog_school_confirm).setBackgroundResource(R.drawable.shape_bg_common_next)
            } else {
                findViewById<TextView>(R.id.tv_dialog_school_confirm).setBackgroundResource(R.drawable.shape_bg_common_next_non)
            }

            findViewById<EditText>(R.id.et_dialog_school_name).addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {

                    diySchool = s.toString()

                    if (s.length >= 4) {
                        findViewById<TextView>(R.id.tv_dialog_school_confirm).setBackgroundResource(
                            R.drawable.shape_bg_common_next)
                    } else {
                        findViewById<TextView>(R.id.tv_dialog_school_confirm).setBackgroundResource(
                            R.drawable.shape_bg_common_next_non)
                    }

                }

            })

            findViewById<ImageView>(R.id.iv_dialog_school_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_school_confirm).setOnClickListener {
                if (diySchool.length < 4) {
                    ToastUtils.showShort("学校名称至少为4个字")
                } else {



                        //输入学校名,点击创建
                        MobclickAgent.onEvent(context, "10021_education_add_school_success");

                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                        map[Contents.TEXT] = diySchool
                        doTextVerifyPresent.doTextVerify(map)




                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

        override fun onLoading() {

        }

        override fun onError() {
        }

        override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

            if (textVerifyBean.conclusion == "合规") {
                // 保存数据

                dismiss()

                val intent = intent
                intent.putExtra("schoolName", diySchool)
                setResult(RESULT_OK, intent)
                finish()

            } else {
                if (textVerifyBean.error_msg != null) {
                    ToastUtils.showShort(textVerifyBean.error_msg)
                } else {
                    ToastUtils.showShort(textVerifyBean.data[0].msg)
                }

                diySchool = ""
                findViewById<EditText>(R.id.et_dialog_school_name).setText("")
                haveBanText = false
            }

        }

        override fun onDoTextVerifyError() {
            ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        getSchoolPresent.unregisterCallback(this)

    }

}