package com.twx.marryfriend.guide.jumpInfo

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import com.aigestudio.wheelpicker.WheelPicker
import com.baidu.idl.face.platform.common.LogHelper
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.BaseInfoUpdateBean
import com.twx.marryfriend.bean.CityBean
import com.twx.marryfriend.bean.UpdateMoreInfoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.step.RegisterStep
import com.twx.marryfriend.guide.detailInfo.step.*
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback
import com.twx.marryfriend.net.callback.IDoUpdateMoreInfoCallback
import com.twx.marryfriend.net.impl.doUpdateBaseInfoPresentImpl
import com.twx.marryfriend.net.impl.doUpdateMoreInfoPresentImpl
import com.twx.marryfriend.net.present.IDoUpdateMoreInfoPresent
import kotlinx.android.synthetic.main.activity_base_info.*
import kotlinx.android.synthetic.main.activity_detail_info.*
import kotlinx.android.synthetic.main.activity_jump.*
import kotlinx.android.synthetic.main.layout_guide_step_name.*
import kotlinx.android.synthetic.main.layout_main_guide_car.*
import kotlinx.android.synthetic.main.layout_main_guide_havechild.*
import kotlinx.android.synthetic.main.layout_main_guide_home.*
import kotlinx.android.synthetic.main.layout_main_guide_house.*
import kotlinx.android.synthetic.main.layout_main_guide_job.*
import kotlinx.android.synthetic.main.layout_main_guide_smoke.*
import kotlinx.android.synthetic.main.layout_main_guide_wantchild.*
import java.util.*

class JumpActivity : MainBaseViewActivity(), IDoUpdateMoreInfoCallback, IDoUpdateBaseInfoCallback {

    // 基础数据是否上传完成
    private var isCompleteUpdateBaseInfo = false

    // 更多数据是否上传完成
    private var isCompleteUpdateMoreInfo = false

    // 是否允许跳转 (逻辑是,点击一次之后继续展示下一界面,再点击一次则跳过,跳转至主界面)
    private var isReadyJump = false

    // 城市数据

    // 家乡城市是否填写（滚轮滑动即视作已填写）
    private var isHomeReady = false

    private lateinit var cityDate: CityBean

    private var mCityFirstPosition = 0
    private var mCitySecondPosition = 0

    private var mCityFirstList: MutableList<String> = arrayListOf()
    private var mCityIdFirstList: MutableList<Int> = arrayListOf()
    private var mCitySecondList: MutableList<String> = arrayListOf()
    private var mCityIdSecondList: MutableList<Int> = arrayListOf()

    // 工作数据

    // 工作是否填写（滚轮滑动即视作已填写）
    private var isJobReady = false

    private var mFirstJobPosition = 0
    private var mSecondJobPosition = 0

    private var mJobFirstList: MutableList<String> = arrayListOf()
    private var mJobIdFirstList: MutableList<Int> = arrayListOf()
    private var mJobSecondList: MutableList<String> = arrayListOf()
    private var mJobIdSecondList: MutableList<Int> = arrayListOf()

    private var mStepDetailOne: StepDetailOne? = null   //  购车情况
    private var mStepDetailTwo: StepDetailTwo? = null   //  家乡
    private var mStepDetailThree: StepDetailThree? = null   //  抽烟
    private var mStepDetailFour: StepDetailFour? = null   //  是否想要孩子
    private var mStepDetailFive: StepDetailFive? = null   //  是否有孩子
    private var mStepDetailSix: StepDetailSix? = null   //  工作
    private var mStepDetailSeven: StepDetailSeven? = null   //  住房情况

    private lateinit var doUpdateMoreInfoPresent: doUpdateMoreInfoPresentImpl
    private lateinit var doUpdateBaseInfoPresent: doUpdateBaseInfoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_jump

    override fun initView() {
        super.initView()

        initStep()
        initNextAnimal()

        doUpdateMoreInfoPresent = doUpdateMoreInfoPresentImpl.getsInstance()
        doUpdateMoreInfoPresent.registerCallback(this)

        doUpdateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        doUpdateBaseInfoPresent.registerCallback(this)

    }

    override fun initLoadData() {

        cityDate = GsonUtils.fromJson(SPStaticUtils.getString(Constant.CITY_JSON_DATE),
            CityBean::class.java)

        getJobCityFirstList()
        getJobCitySecondList(0)

        val size = SPStaticUtils.getInt(Constant.INDUSTRY_SUM, 0)
        for (i in 0.until(size)) {
            mJobFirstList.add(SPStaticUtils.getString("industry_item_name_$i", ""))
            mJobIdFirstList.add(SPStaticUtils.getInt("industry_item_id_$i", 0))
        }

        val jobSecondSize = SPStaticUtils.getInt("job_second_0 _sum", 0)
        for (i in 0.until(jobSecondSize)) {
            mJobSecondList.add(SPStaticUtils.getString("job_second_0 _name_$i", ""))
            mJobIdSecondList.add(SPStaticUtils.getInt("job_second_0 _id_$i", 0))
        }

        initHomeWheelPicker()

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        ll_guide_jump_skip.setOnClickListener {

            if (isReadyJump) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                vf_guide_jump_container.showNext()
                isReadyJump = true
            }

        }

        ll_guide_jump_next.setOnClickListener {
            when (vf_guide_jump_container.displayedChild) {
                1 -> {
                    if (isHomeReady) {

                        val home =
                            "${mCityFirstList[mCityFirstPosition]}-${mCitySecondList[mCitySecondPosition]}}"

                        Log.i("guo", home)

                        SPStaticUtils.put(Constant.ME_HOME, home)

                        vf_guide_jump_container.showNext()

                        ll_guide_jump_next.visibility = View.GONE

                    }else{
                        val home = "${mCityFirstList[0]}-${mCitySecondList[0]}]}"

                        Log.i("guo", home)

                        SPStaticUtils.put(Constant.ME_HOME, home)

                        vf_guide_jump_container.showNext()

                        ll_guide_jump_next.visibility = View.GONE
                    }
                }
                5 -> {
                    if (isJobReady) {

                        Log.i("guo", "${mJobFirstList[mFirstJobPosition]} : ${mJobIdFirstList[mFirstJobPosition]}")
                        Log.i("guo", "${mJobSecondList[mSecondJobPosition]} : ${mJobIdSecondList[mSecondJobPosition]}")

                        SPStaticUtils.put(Constant.ME_INDUSTRY_CODE,
                            mJobIdFirstList[mFirstJobPosition])
                        SPStaticUtils.put(Constant.ME_INDUSTRY_NAME,
                            mJobFirstList[mFirstJobPosition])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_CODE,
                            mJobIdSecondList[mSecondJobPosition])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_NAME,
                            mJobSecondList[mSecondJobPosition])

                        vf_guide_jump_container.showNext()

                        ll_guide_jump_next.visibility = View.GONE

                    }else{

                        Log.i("guo", "${mJobFirstList[0]} : ${mJobIdFirstList[0]}")
                        Log.i("guo", "${mJobSecondList[0]} : ${mJobIdSecondList[0]}")

                        SPStaticUtils.put(Constant.ME_INDUSTRY_CODE,
                            mJobIdFirstList[0])
                        SPStaticUtils.put(Constant.ME_INDUSTRY_NAME,
                            mJobFirstList[0])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_CODE,
                            mJobIdSecondList[0])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_NAME,
                            mJobSecondList[0])

                        vf_guide_jump_container.showNext()

                        ll_guide_jump_next.visibility = View.GONE

                    }

                }
            }
        }


        // 购车情况
        tv_jump_car_have.setOnClickListener {

            tv_jump_car_have.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_car_have.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()
            SPStaticUtils.put(Constant.ME_CAR, 1)
            ll_guide_jump_next.visibility = View.VISIBLE

        }

        tv_jump_car_non.setOnClickListener {

            tv_jump_car_non.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_car_non.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()
            SPStaticUtils.put(Constant.ME_CAR, 2)
            ll_guide_jump_next.visibility = View.VISIBLE
        }

        // 家乡
        wp_jump_home_one.setOnItemSelectedListener { picker, data, position ->

            isHomeReady = true
            ll_guide_jump_next.setBackgroundResource(R.drawable.shape_bg_common_next)

            mCityFirstPosition = position

            getJobCitySecondList(mCityFirstPosition)

            // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
            if (mCitySecondPosition >= mCitySecondList.size) {
                mCitySecondPosition = mCitySecondList.size - 1
            }

            wp_jump_home_two.data = mCitySecondList

        }

        wp_jump_home_two.setOnItemSelectedListener { picker, data, position ->

            isHomeReady = true
            ll_guide_jump_next.setBackgroundResource(R.drawable.shape_bg_common_next)

            mCitySecondPosition = position

        }

        // 抽烟
        tv_jump_smoke_one.setOnClickListener {

            tv_jump_smoke_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_one.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_SMOKE, 3)

        }

        tv_jump_smoke_two.setOnClickListener {

            tv_jump_smoke_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_two.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_SMOKE, 2)
        }

        tv_jump_smoke_three.setOnClickListener {

            tv_jump_smoke_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_three.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_SMOKE, 1)

        }

        tv_jump_smoke_four.setOnClickListener {

            tv_jump_smoke_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_four.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_SMOKE, 4)
        }

        // 想要孩子吗
        tv_jump_wantchild_one.setOnClickListener {

            tv_jump_wantchild_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_one.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 1)
        }

        tv_jump_wantchild_two.setOnClickListener {

            tv_jump_wantchild_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_two.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 2)
        }

        tv_jump_wantchild_three.setOnClickListener {

            tv_jump_wantchild_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_three.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 3)
        }

        tv_jump_wantchild_four.setOnClickListener {

            tv_jump_wantchild_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_four.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 4)
        }

        // 你有孩子吗
        tv_jump_havechild_one.setOnClickListener {

            tv_jump_havechild_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_one.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()
            ll_guide_jump_next.visibility = View.VISIBLE

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 1)

            ll_guide_jump_next.visibility = View.VISIBLE
        }

        tv_jump_havechild_two.setOnClickListener {

            tv_jump_havechild_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_two.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()
            ll_guide_jump_next.visibility = View.VISIBLE

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 2)

            ll_guide_jump_next.visibility = View.VISIBLE
        }

        tv_jump_havechild_three.setOnClickListener {

            tv_jump_havechild_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_three.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()
            ll_guide_jump_next.visibility = View.VISIBLE

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 3)

            ll_guide_jump_next.visibility = View.VISIBLE
        }

        tv_jump_havechild_four.setOnClickListener {

            tv_jump_havechild_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_four.setTextColor(Color.parseColor("#FF4444"))

            vf_guide_jump_container.showNext()
            ll_guide_jump_next.visibility = View.VISIBLE

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 4)

            ll_guide_jump_next.visibility = View.VISIBLE
        }

        // 工作
        wp_jump_job_one.setOnItemSelectedListener { picker, data, position ->

            isJobReady = true
            ll_guide_jump_next.setBackgroundResource(R.drawable.shape_bg_common_next)

            mFirstJobPosition = position
            getJobSecondList(mFirstJobPosition)

            // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
            if (mSecondJobPosition >= mJobSecondList.size) {
                mSecondJobPosition = mJobSecondList.size - 1
            }

            wp_jump_job_two.data = mJobSecondList

        }

        wp_jump_job_two.setOnItemSelectedListener { picker, data, position ->

            isJobReady = true
            ll_guide_jump_next.setBackgroundResource(R.drawable.shape_bg_common_next)

            mSecondJobPosition = position

        }

        // 住房情况
        tv_jump_house_one.setOnClickListener {

            tv_jump_house_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_one.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 1)

            jumpToMain()

        }

        tv_jump_house_two.setOnClickListener {

            tv_jump_house_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_two.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 2)

            jumpToMain()

        }

        tv_jump_house_three.setOnClickListener {

            tv_jump_house_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_three.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 3)

            jumpToMain()

        }

        tv_jump_house_four.setOnClickListener {

            tv_jump_house_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_four.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 4)

            jumpToMain()

        }

        tv_jump_house_five.setOnClickListener {

            tv_jump_house_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_five.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 5)

            jumpToMain()

        }

    }

    // 初始化各子界面
    private fun initStep(): RegisterStep? {
        when (vf_guide_jump_container.displayedChild) {
            0 -> {
                if (mStepDetailOne == null) {
                    mStepDetailOne = StepDetailOne(this, vf_guide_jump_container.getChildAt(0))
                }

                if (mStepDetailTwo == null) {
                    mStepDetailTwo = StepDetailTwo(this, vf_guide_jump_container.getChildAt(1))
                }

                if (mStepDetailThree == null) {
                    mStepDetailThree = StepDetailThree(this, vf_guide_jump_container.getChildAt(2))
                }

                if (mStepDetailFour == null) {
                    mStepDetailFour = StepDetailFour(this, vf_guide_jump_container.getChildAt(3))
                }

                if (mStepDetailFive == null) {
                    mStepDetailFive = StepDetailFive(this, vf_guide_jump_container.getChildAt(4))
                }

                if (mStepDetailSix == null) {
                    mStepDetailSix = StepDetailSix(this, vf_guide_jump_container.getChildAt(5))
                }

                if (mStepDetailSeven == null) {
                    mStepDetailSeven = StepDetailSeven(this, vf_guide_jump_container.getChildAt(6))
                }
            }
        }
        return null
    }

    // 初始化界面切换动画
    private fun initNextAnimal() {

        vf_guide_jump_container.setInAnimation(this, R.anim.push_left_in)
        vf_guide_jump_container.setOutAnimation(this, R.anim.push_left_out)

    }

    // 跳转至主页面
    private fun jumpToMain() {

        val moreInfoMap: MutableMap<String, String> = TreeMap()
        moreInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        moreInfoMap[Contents.MORE_UPDATE] = getMoreInfo()
        doUpdateMoreInfoPresent.doUpdateMoreInfo(moreInfoMap)


        val baseInfoMap: MutableMap<String, String> = TreeMap()
        baseInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        baseInfoMap[Contents.BASE_UPDATE] = getBaseInfo()
        doUpdateBaseInfoPresent.doUpdateBaseInfo(baseInfoMap)

    }

    // 需要上传的基础信息
    private fun getBaseInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val nick = SPStaticUtils.getString(Constant.ME_NAME, "")
        val age = SPStaticUtils.getInt(Constant.ME_AGE, 0)
        val birthday = SPStaticUtils.getString(Constant.ME_BIRTH, "")
        val height = SPStaticUtils.getInt(Constant.ME_HEIGHT, 0)
        val school = SPStaticUtils.getString(Constant.ME_SCHOOL, "")
        val edu = SPStaticUtils.getInt(Constant.ME_EDU, 0)
        val industryCode = SPStaticUtils.getInt(Constant.ME_INDUSTRY_CODE, 0)
        val industryName = SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "")
        val occupationCode = SPStaticUtils.getInt(Constant.ME_OCCUPATION_CODE, 0)
        val occupationName = SPStaticUtils.getString(Constant.ME_OCCUPATION_NAME, "")
        val province = SPStaticUtils.getInt(Constant.ME_WORK_PROVINCE_CODE, 0)
        val cityCode = SPStaticUtils.getInt(Constant.ME_WORK_CITY_CODE, 0)
        val cityName = SPStaticUtils.getString(Constant.ME_WORK_CITY_NAME, "")
        val home = SPStaticUtils.getString(Constant.ME_HOME, "")
        val income = SPStaticUtils.getInt(Constant.ME_INCOME, 0)
        val marryState = SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0)
        val introduce = SPStaticUtils.getString(Constant.ME_INTRODUCE, "")
        val hobby = SPStaticUtils.getString(Constant.ME_HOBBY, "")
        val ta = SPStaticUtils.getString(Constant.ME_TA, "")

        val baseInfo =
            " {\"user_sex\":                $sex, " +               // 性别
                    "\"nick\":              \"$nick\"," +           // 昵称
                    "\"age\":               $age," +                // 年龄
                    "\"birthday\":          \"$birthday\"," +       // 出生年月日
                    "\"height\":            $height," +             // 身高厘米
                    "\"school_name\":       \"$school\"," +         // 学校名字
                    "\"education\":         $edu," +                // 学历
                    "\"industry_num\":      $industryCode," +       // 行业编码
                    "\"industry_str\":      \"$industryName\"," +       // 行业名字
                    "\"occupation_num\":    $occupationCode," +     // 岗位编码
                    "\"occupation_str\":    \"$occupationName\"," +     // 岗位名字
                    "\"work_province_num\": \"$province\"," +           // 工作省份编码
                    "\"work_city_num\":     \"$cityCode\"," +           // 工作城市编码
                    "\"work_city_str\":     \"$cityName\"," +           // 工作城市名字
                    "\"hometown\":          \"$home\"," +               // 故乡
                    "\"salary_range\":      $income," +             // 月薪范围
                    "\"marry_had\":         $marryState," +         // 当前婚育状况
                    "\"introduce_self\":    \"$introduce\"," +          // 文字自我介绍
                    "\"daily_hobbies\":     \"$hobby\"," +              // 日常兴趣爱好
                    " \"ta_in_my_mind\":    \"$ta\"}"                   // 我心目中的Ta

        return baseInfo

    }

    // 需要上传的更多信息
    private fun getMoreInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val weight = SPStaticUtils.getInt(Constant.ME_WEIGHT, 0)
        val body = SPStaticUtils.getInt(Constant.ME_BODY, 0)
        val blood = SPStaticUtils.getString(Constant.ME_BLOOD, "")
        val constellation = SPStaticUtils.getString(Constant.ME_CONSTELLATION, "")
        val nationality = SPStaticUtils.getString(Constant.ME_NATIONALITY, "")
        val loveTarget = SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0)
        val loveTargetShow = SPStaticUtils.getInt(Constant.ME_LOVE_TARGET_SHOW, 0)
        val car = SPStaticUtils.getInt(Constant.ME_CAR, 0)
        val house = SPStaticUtils.getInt(Constant.ME_HOUSE, 0)
        val smoke = SPStaticUtils.getInt(Constant.ME_SMOKE, 0)
        val drink = SPStaticUtils.getInt(Constant.ME_DRINK, 0)
        val haveChild = SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0)
        val wantChild = SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0)
        val marryTime = SPStaticUtils.getInt(Constant.ME_MARRY_TIME, 0)

        val moreInfo =
            " {\"user_sex\": $sex, " +                        // 性别
                    "\"weight\":       $weight," +            // 体重公斤
                    "\"figure_nan\":       $body," +          // 身材男
                    "\"figure_nv\":       $body," +           // 身材女
                    "\"blood_type\":   \"$blood\"," +         // 血型，不作筛选条
                    "\"constellation\":   \"$constellation\"," +     // 星座，不作筛选条件
                    "\"nationality\":   \"$nationality\"," +       // 民族，不作筛选条件
                    "\"love_target\":       $loveTarget," +       // 恋爱目标
                    "\"target_show\":       $loveTargetShow," +       // 是否展示目标
                    "\"buy_car\":       $car," +           // 是否买车
                    "\"buy_house\":       $house," +         // 是否买房
                    "\"is_smoking\":       $smoke," +        // 是否抽烟
                    "\"is_drinking\":       $drink," +       // 是否喝酒
                    "\"child_had\":       $haveChild," +         // 是否有娃
                    "\"want_child\":       $wantChild," +        // 想要生娃
                    "\"marry_time\":    $marryTime}"  // 想结婚时间

        return moreInfo

    }

    // 工作数据
    private fun getJobSecondList(i: Int) {
        mJobSecondList.clear()
        mJobIdSecondList.clear()
        val jobSecondSize = SPStaticUtils.getInt("job_second_$i _sum", 0)
        for (j in 0.until(jobSecondSize)) {
            mJobSecondList.add(SPStaticUtils.getString("job_second_$i _name_$j", null))
            mJobIdSecondList.add(SPStaticUtils.getInt("job_second_$i _id_$j", 0))
        }
    }

    // 省
    private fun getJobCityFirstList() {
        mCityFirstList.clear()
        mCityIdFirstList.clear()
        for (i in 0.until(cityDate.data.size)) {
            mCityFirstList.add(cityDate.data[i].name)
            mCityIdFirstList.add(cityDate.data[i].id)
        }
    }

    // 市
    private fun getJobCitySecondList(i: Int) {
        mCitySecondList.clear()
        mCityIdSecondList.clear()
        for (j in 0.until(cityDate.data[i].child.size)) {
            mCitySecondList.add(cityDate.data[i].child[j].name)
            mCityIdSecondList.add(cityDate.data[i].child[j].id)
        }
    }

    // 初始化滚轮
    private fun initHomeWheelPicker() {

        wp_jump_home_one.data = mCityFirstList
        wp_jump_home_two.data = mCitySecondList

        wp_jump_job_one.data = mJobFirstList
        wp_jump_job_two.data = mJobSecondList

        // 是否为循环状态
        wp_jump_home_one.isCyclic = false
        // 当前选中的数据项文本颜色
        wp_jump_home_one.selectedItemTextColor = Color.parseColor("#FF4444")
        // 数据项文本颜色
        wp_jump_home_one.itemTextColor = Color.parseColor("#9A9A9A")
        // 设置数据项文本尺寸大小
//        wp_jump_home_one.itemTextSize = ConvertUtils.dp2px(10F)
        // 滚轮选择器数据项之间间距
        wp_jump_home_one.itemSpace = ConvertUtils.dp2px(40F)
        // 是否有指示器
        wp_jump_home_one.setIndicator(true)
        // 滚轮选择器指示器颜色，16位颜色值
        wp_jump_home_one.indicatorColor = Color.parseColor("#FFF5F5")
        // 滚轮选择器是否显示幕布
        wp_jump_home_one.setCurtain(true)
        // 滚轮选择器是否有空气感
        wp_jump_home_one.setAtmospheric(true)
        // 滚轮选择器是否开启卷曲效果
        wp_jump_home_one.isCurved = true
        // 设置滚轮选择器数据项的对齐方式
        wp_jump_home_one.itemAlign = WheelPicker.ALIGN_CENTER

        // 是否为循环状态
        wp_jump_home_two.isCyclic = false
        // 当前选中的数据项文本颜色
        wp_jump_home_two.selectedItemTextColor = Color.parseColor("#FF4444")
        // 数据项文本颜色
        wp_jump_home_two.itemTextColor = Color.parseColor("#9A9A9A")
        // 设置数据项文本尺寸大小
//        wp_jump_home_two.itemTextSize = ConvertUtils.dp2px(10F)
        // 滚轮选择器数据项之间间距
        wp_jump_home_two.itemSpace = ConvertUtils.dp2px(40F)
        // 是否有指示器
        wp_jump_home_two.setIndicator(true)
        // 滚轮选择器指示器颜色，16位颜色值
        wp_jump_home_two.indicatorColor = Color.parseColor("#FFF5F5")
        // 滚轮选择器是否显示幕布
        wp_jump_home_two.setCurtain(true)
        // 滚轮选择器是否有空气感
        wp_jump_home_two.setAtmospheric(true)
        // 滚轮选择器是否开启卷曲效果
        wp_jump_home_two.isCurved = true
        // 设置滚轮选择器数据项的对齐方式
        wp_jump_home_two.itemAlign = WheelPicker.ALIGN_CENTER

        // 是否为循环状态
        wp_jump_job_one.isCyclic = false
        // 当前选中的数据项文本颜色
        wp_jump_job_one.selectedItemTextColor = Color.parseColor("#FF4444")
        // 数据项文本颜色
        wp_jump_job_one.itemTextColor = Color.parseColor("#9A9A9A")
        // 设置数据项文本尺寸大小
//        wp_jump_job_one.itemTextSize = ConvertUtils.dp2px(10F)
        // 滚轮选择器数据项之间间距
        wp_jump_job_one.itemSpace = ConvertUtils.dp2px(40F)
        // 是否有指示器
        wp_jump_job_one.setIndicator(true)
        // 滚轮选择器指示器颜色，16位颜色值
        wp_jump_job_one.indicatorColor = Color.parseColor("#FFF5F5")
        // 滚轮选择器是否显示幕布
        wp_jump_job_one.setCurtain(true)
        // 滚轮选择器是否有空气感
        wp_jump_job_one.setAtmospheric(true)
        // 滚轮选择器是否开启卷曲效果
        wp_jump_job_one.isCurved = true
        // 设置滚轮选择器数据项的对齐方式
        wp_jump_job_one.itemAlign = WheelPicker.ALIGN_CENTER

        // 是否为循环状态
        wp_jump_job_two.isCyclic = false
        // 当前选中的数据项文本颜色
        wp_jump_job_two.selectedItemTextColor = Color.parseColor("#FF4444")
        // 数据项文本颜色
        wp_jump_job_two.itemTextColor = Color.parseColor("#9A9A9A")
        // 设置数据项文本尺寸大小
//        wp_jump_job_two.itemTextSize = ConvertUtils.dp2px(10F)
        // 滚轮选择器数据项之间间距
        wp_jump_job_two.itemSpace = ConvertUtils.dp2px(40F)
        // 是否有指示器
        wp_jump_job_two.setIndicator(true)
        // 滚轮选择器指示器颜色，16位颜色值
        wp_jump_job_two.indicatorColor = Color.parseColor("#FFF5F5")
        // 滚轮选择器是否显示幕布
        wp_jump_job_two.setCurtain(true)
        // 滚轮选择器是否有空气感
        wp_jump_job_two.setAtmospheric(true)
        // 滚轮选择器是否开启卷曲效果
        wp_jump_job_two.isCurved = true
        // 设置滚轮选择器数据项的对齐方式
        wp_jump_job_two.itemAlign = WheelPicker.ALIGN_CENTER

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {

        isCompleteUpdateBaseInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            ToastUtils.showShort("资料上传成功")
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    override fun onDoUpdateBaseInfoError() {

        isCompleteUpdateBaseInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            ToastUtils.showShort("资料上传失败")

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    override fun onDoUpdateMoreInfoSuccess(updateMoreInfoBean: UpdateMoreInfoBean?) {

        isCompleteUpdateMoreInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            ToastUtils.showShort("资料上传成功")

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    override fun onDoUpdateMoreInfoError() {

        isCompleteUpdateMoreInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            ToastUtils.showShort("资料上传失败")

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

}