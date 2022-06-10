package com.twx.marryfriend.guide.baseInfo

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.BaseInfoUpdateBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents

import com.twx.marryfriend.guide.baseInfo.step.RegisterStep
import com.twx.marryfriend.guide.baseInfo.step.StepOne
import com.twx.marryfriend.guide.baseInfo.step.StepTwo
import com.twx.marryfriend.guide.detailInfo.DetailInfoActivity
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback
import com.twx.marryfriend.net.impl.doUpdateBaseInfoPresentImpl
import com.twx.marryfriend.utils.UnicodeUtils
import kotlinx.android.synthetic.main.activity_base_info.*
import kotlinx.android.synthetic.main.layout_guide_step_name.*
import kotlinx.android.synthetic.main.layout_guide_step_sex.*
import java.util.*


class BaseInfoActivity : MainBaseViewActivity(), IDoUpdateBaseInfoCallback {

    private var mStepOne: StepOne? = null
    private var mStepTwo: StepTwo? = null


    // 基本资料界面判断下一步是否准备就绪

    // 昵称是否填写
    private var haveName = false

    // 昵称
    private var name = ""

    // 性别是否选择
    private var chooseSex = false

    //性别
    private var sex = 1

    // 年龄是否选择
    private var chooseAge = false

    // 年龄
    private var age = 0

    // 身高是否选择
    private var chooseHeight = false

    // 身高
    private var height = 0

    // 敏感字
    private var banTextList: MutableList<String> = arrayListOf()

    // 是否具有敏感词
    private var haveBanText = false


    private lateinit var updateBaseInfoPresent: doUpdateBaseInfoPresentImpl


    override fun getLayoutView(): Int = R.layout.activity_base_info

    override fun initView() {
        super.initView()

        updateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        updateBaseInfoPresent.registerCallback(this)

        initStep()


    }

    override fun initLoadData() {
        super.initLoadData()

        val banText = SPStaticUtils.getString(Constant.BAN_TEXT)
        val array = banText.split(",")

        for (i in 0.until(array.size)) {
            banTextList.add(UnicodeUtils.decode(array[i]))
        }


        XXPermissions.with(this)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.ACCESS_FINE_LOCATION)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                }
            })


    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        tv_guide_base_previous.setOnClickListener {

            initStep()

            vf_guide_base_container.setInAnimation(this, R.anim.push_right_in)
            vf_guide_base_container.setOutAnimation(this, R.anim.push_right_out)

            when (vf_guide_base_container.displayedChild) {
                0 -> {
                    ToastUtils.showShort("此次是第一个界面，不可以返回了")
                }
                1 -> {
                    vf_guide_base_container.showPrevious()
                    tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    tv_guide_base_previous.visibility = View.INVISIBLE
                    tv_guide_base_tip.visibility = View.INVISIBLE
                }
            }

        }

        tv_guide_base_next.setOnClickListener {

            initStep()

            vf_guide_base_container.setInAnimation(this, R.anim.push_left_in)
            vf_guide_base_container.setOutAnimation(this, R.anim.push_left_out)

            when (vf_guide_base_container.displayedChild) {
                0 -> {

                    if (haveName) {
                        // 已经填写昵称
                        for (i in 0.until(banTextList.size)) {
                            if (name.contains(banTextList[i])) {
                                haveBanText = true
                            }
                        }

                        if (haveBanText) {
                            ToastUtils.showShort("输入中存在敏感字，请重新输入")
                            haveName = false
                            tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)

                            et_guide_name_name.setText("")

                            haveBanText = false
                        } else {

                            tv_guide_base_previous.visibility = View.VISIBLE
                            tv_guide_base_tip.visibility = View.VISIBLE
                            vf_guide_base_container.showNext()

                            if (chooseSex && chooseAge && chooseHeight) {
                                tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                            } else {
                                tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                            }

                        }

                    } else {
                        // 还未填写昵称
                        ToastUtils.showShort("请填写昵称")
                    }

                }
                1 -> {

                    if (chooseSex && chooseAge && chooseHeight) {

                        // 存储基础信息
//                        SPStaticUtils.put(Constant.BASE_INFO_FINISH, true)
                        SPStaticUtils.put(Constant.NICK_NAME, name)
                        SPStaticUtils.put(Constant.ME_SEX, sex)
                        SPStaticUtils.put(Constant.AGE, age)
                        SPStaticUtils.put(Constant.HEIGHT, height)


                        val str =
                            " {\"age\":$age, \"height\":$height, \"nick\":\"$name\", \"user_sex\":$sex}"

                        Log.i("guo", str)

                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
                        map[Contents.BASE_UPDATE] = str
                        updateBaseInfoPresent.doUpdateBaseInfo(map)

                    } else {
                        ToastUtils.showShort("请填写资料")
                    }

                }

            }
        }

        // -------------------  姓名界面  -----------------

        (mStepOne!!.findViewById(R.id.et_guide_name_name) as EditText).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int,
                    count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?, start: Int,
                    before: Int, count: Int,
                ) {
                }

                override fun afterTextChanged(s: Editable) {

                    name = s.toString()

                    (mStepOne!!.findViewById(R.id.tv_guide_name_name_sum) as TextView).text =
                        (8 - s.length).toString()

                    if (s.isEmpty()) {
                        haveName = false
                        tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    } else {
                        haveName = true
                        tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    }

                }
            })


        // -------------------  资本资料界面  -----------------

        (mStepTwo!!.findViewById(R.id.iv_guide_sex_male) as ImageView).setOnClickListener {

            sex = 1

            iv_guide_sex_male_check.setImageResource(R.drawable.ic_sex_check)
            iv_guide_sex_female_check.setImageResource(R.drawable.ic_sex_uncheck)

            chooseSex = true
            if (chooseSex && chooseAge && chooseHeight) {
                tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
            }
        }

        mStepTwo!!.findViewById(R.id.iv_guide_sex_female).setOnClickListener {

            sex = 2

            iv_guide_sex_male_check.setImageResource(R.drawable.ic_sex_uncheck)
            iv_guide_sex_female_check.setImageResource(R.drawable.ic_sex_check)

            chooseSex = true
            if (chooseSex && chooseAge && chooseHeight) {
                tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
            }
        }

        sb_guide_sex_age.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                age = progress + 18

                tv_guide_sex_age.text = "${progress + 18}岁"
                chooseAge = true
                if (chooseSex && chooseAge && chooseHeight) {
                    tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        sb_guide_sex_height.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                height = progress + 150

                tv_guide_sex_height.text = "${progress + 150}cm"
                chooseHeight = true
                if (chooseSex && chooseAge && chooseHeight) {
                    tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

    }

    private fun initStep(): RegisterStep? {
        when (vf_guide_base_container.displayedChild) {
            0 -> {
                if (mStepOne == null) {
                    mStepOne = StepOne(this, vf_guide_base_container.getChildAt(0))
                }

                if (mStepTwo == null) {
                    mStepTwo = StepTwo(this, vf_guide_base_container.getChildAt(1))
                }

            }
            1 -> {
                if (mStepTwo == null) {
                    mStepTwo = StepTwo(this, vf_guide_base_container.getChildAt(1))
                }
            }

        }
        return null
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppUtils.exitApp()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {

        val intent = Intent(this, DetailInfoActivity::class.java)
        startActivity(intent)
        this.finish()

        SPStaticUtils.put(Constant.BASE_INFO_FINISH, true)

    }

    override fun onDoUpdateBaseInfoError() {

    }


}