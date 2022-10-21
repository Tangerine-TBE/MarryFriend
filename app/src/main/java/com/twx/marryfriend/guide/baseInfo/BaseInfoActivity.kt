package com.twx.marryfriend.guide.baseInfo

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.blankj.utilcode.util.*
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.step.RegisterStep
import com.twx.marryfriend.guide.baseInfo.step.StepOne
import com.twx.marryfriend.guide.baseInfo.step.StepTwo
import com.twx.marryfriend.guide.detailInfo.DetailInfoActivity
import com.twx.marryfriend.net.callback.IDoFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback
import com.twx.marryfriend.net.callback.IGetAccessTokenCallback
import com.twx.marryfriend.net.impl.doFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.doUpdateBaseInfoPresentImpl
import com.twx.marryfriend.net.impl.getAccessTokenPresentImpl
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_base_info.*
import kotlinx.android.synthetic.main.layout_guide_step_name.*
import kotlinx.android.synthetic.main.layout_guide_step_sex.*
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

class BaseInfoActivity : MainBaseViewActivity(), IDoUpdateBaseInfoCallback, IDoTextVerifyCallback,
    IGetAccessTokenCallback {

    private var mStepOne: StepOne? = null
    private var mStepTwo: StepTwo? = null

    private var isTextVerify = false


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
    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

    private lateinit var getAccessTokenPresent: getAccessTokenPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_base_info

    override fun initView() {
        super.initView()

        updateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        updateBaseInfoPresent.registerCallback(this)

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

        getAccessTokenPresent = getAccessTokenPresentImpl.getsInstance()
        getAccessTokenPresent.registerCallback(this)

        //进入填写昵称页面
        MobclickAgent.onEvent(this, "10012_nickname");

        initStep()


    }

    override fun initLoadData() {
        super.initLoadData()


//        val banBean: BanBean = GsonUtils.fromJson(SPStaticUtils.getString(Constant.BAN_TEXT), BanBean::class.java)
//
//        val x = EncodeUtils.base64Decode(banBean.data.array_string)
//
//        val y = String(x)
//        var yy = "{\"data\":$y}"
//        var aa = com.twx.marryfriend.utils.GsonUtils.parseObject(yy, Test::class.java)
//
//        for (i in 0.until(aa.data.size)) {
//            banTextList.add(aa.data[i])
//        }

    }

    class Test {
        var data: Array<String> = arrayOf()
    }

    fun getChars(bytes: ByteArray): CharArray? {
        val cs: Charset = StandardCharsets.UTF_8
        val bb: ByteBuffer = ByteBuffer.allocate(bytes.size)
        bb.put(bytes)
        bb.flip()
        val cb: CharBuffer = cs.decode(bb)
        return cb.array()
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

                        //输入昵称,点击下一步
                        MobclickAgent.onEvent(this, "10014_nickname_next");

                        if (SPStaticUtils.getString(Constant.ACCESS_TOKEN, "") != null) {

                            Log.i("guo", SPStaticUtils.getString(Constant.ACCESS_TOKEN, ""))

                            val map: MutableMap<String, String> = TreeMap()
                            map[Contents.ACCESS_TOKEN] =
                                SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                            map[Contents.TEXT] = name
                            isTextVerify = true
                            doTextVerifyPresent.doTextVerify(map)

                        } else {
                            ToastUtils.showShort("数据加载失败，请重新打开应用")
                        }

                    } else {
                        // 还未填写昵称
                        ToastUtils.showShort("请填写昵称")
                    }

                }
                1 -> {


                    if (chooseSex && chooseAge && chooseHeight) {

                        //填写基本资料,点击下一步
                        MobclickAgent.onEvent(this, "10017_information_next");

                        // 存储基础信息
//                        SPStaticUtils.put(Constant.BASE_INFO_FINISH, true)
                        SPStaticUtils.put(Constant.ME_NAME, name)
                        SPStaticUtils.put(Constant.ME_SEX, sex)
                        SPStaticUtils.put(Constant.ME_AGE, age)
                        SPStaticUtils.put(Constant.ME_HEIGHT, height)


                        val str =
                            " {\"age\":$age, \"height\":$height, \"nick\":\"$name\", \"user_sex\":$sex}"

                        Log.i("guo", str)

                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
                        map[Contents.BASE_UPDATE] = str
                        updateBaseInfoPresent.doUpdateBaseInfo(map)

                    } else {

                        if (!chooseSex) {
                            ToastUtils.showShort("请填写您的性别信息")
                        } else {
                            if (!chooseAge) {
                                ToastUtils.showShort("请填写您的年龄信息")
                            } else {
                                if (!chooseHeight) {
                                    ToastUtils.showShort("请填写您的身高信息")
                                }
                            }
                        }


                    }

                }

            }
        }

        // -------------------  姓名界面  -----------------

        (mStepOne!!.findViewById(R.id.et_guide_name_name) as EditText).addTextChangedListener(object :
            TextWatcher {
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

            iv_guide_sex_male.setImageResource(R.mipmap.icon_guide_sex_male)
            iv_guide_sex_female.setImageResource(R.mipmap.icon_guide_sex_female_non)
            iv_guide_sex_male_check.setImageResource(R.drawable.ic_sex_check)
            iv_guide_sex_female_check.setImageResource(R.drawable.ic_sex_uncheck)

            chooseSex = true
            if (chooseSex && chooseAge && chooseHeight) {
                tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
            }
        }

        mStepTwo!!.findViewById(R.id.iv_guide_sex_female).setOnClickListener {

            sex = 2

            iv_guide_sex_male.setImageResource(R.mipmap.icon_guide_sex_male_non)
            iv_guide_sex_female.setImageResource(R.mipmap.icon_guide_sex_female)
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

    // 获取 头像 审核的 accessToken
    private fun getAccessToken() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.GRANT_TYPE] = "client_credentials"
        map[Contents.CLIENT_ID] = "jjKDyljlCOX3TcEcnXidYCcU"
        map[Contents.CLIENT_SECRET] = "GQcKzFuA87uEQZhIDnlcxTpkjT2oLxdX"
        getAccessTokenPresent.getAccessToken(map)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppUtils.exitApp()
        }
        return super.onKeyDown(keyCode, event)
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

    override fun onGetAccessTokenSuccess(accessTokenBean: AccessTokenBean?) {
        if (accessTokenBean != null) {
            SPStaticUtils.put(Constant.ACCESS_TOKEN, accessTokenBean.access_token)
        }
    }

    override fun onGetAccessTokenFail() {

    }

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

        if (isTextVerify) {
            if (textVerifyBean.conclusion == "合规") {
                tv_guide_base_previous.visibility = View.VISIBLE
                tv_guide_base_tip.visibility = View.VISIBLE

                //进入基本资料页面
                MobclickAgent.onEvent(this, "10015_information");

                vf_guide_base_container.showNext()

                if (chooseSex && chooseAge && chooseHeight) {
                    tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                } else {
                    tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                }

                isTextVerify = false

            } else {


                if (textVerifyBean.error_msg != null) {
                    ToastUtils.showShort(textVerifyBean.error_msg)
                } else {
                    ToastUtils.showShort(textVerifyBean.data[0].msg)
                }
                haveName = false
                tv_guide_base_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)

                et_guide_name_name.setText("")

                haveBanText = false


                ToastUtils.showShort("再次请求token数据")

                getAccessToken()

            }
        }

    }


    override fun onDoTextVerifyError() {
        ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {

        SPStaticUtils.put(Constant.USER_IS_LOGIN, true)

        val intent = Intent(this, DetailInfoActivity::class.java)
        startActivity(intent)
        this.finish()

        SPStaticUtils.put(Constant.BASE_INFO_FINISH, true)

    }

    override fun onDoUpdateBaseInfoError() {

    }

    override fun onDestroy() {
        super.onDestroy()

        updateBaseInfoPresent.unregisterCallback(this)
        doTextVerifyPresent.unregisterCallback(this)

    }

}