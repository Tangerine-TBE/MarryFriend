package com.twx.marryfriend.guide.jumpInfo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.aigestudio.wheelpicker.WheelPicker
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.animators.AnimationType
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.style.PictureSelectorStyle
import com.luck.picture.lib.style.PictureWindowAnimationStyle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.step.RegisterStep
import com.twx.marryfriend.guide.detailInfo.step.*
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.mine.greet.GreetEditActivity
import com.twx.marryfriend.net.callback.IDoFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoUpdateBaseInfoCallback
import com.twx.marryfriend.net.callback.IDoUpdateMoreInfoCallback
import com.twx.marryfriend.net.callback.IDoUploadAvatarCallback
import com.twx.marryfriend.net.impl.doFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doUpdateBaseInfoPresentImpl
import com.twx.marryfriend.net.impl.doUpdateMoreInfoPresentImpl
import com.twx.marryfriend.net.impl.doUploadAvatarPresentImpl
import com.twx.marryfriend.utils.BitmapUtil
import com.twx.marryfriend.utils.GlideEngine
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_jump.*
import kotlinx.android.synthetic.main.fragment_data.*
import kotlinx.android.synthetic.main.layout_main_guide_car.*
import kotlinx.android.synthetic.main.layout_main_guide_havechild.*
import kotlinx.android.synthetic.main.layout_main_guide_home.*
import kotlinx.android.synthetic.main.layout_main_guide_house.*
import kotlinx.android.synthetic.main.layout_main_guide_job.*
import kotlinx.android.synthetic.main.layout_main_guide_smoke.*
import kotlinx.android.synthetic.main.layout_main_guide_wantchild.*
import java.io.*
import java.util.*

class JumpActivity : MainBaseViewActivity(), IDoUpdateMoreInfoCallback, IDoUpdateBaseInfoCallback,
    IDoFaceDetectCallback, IDoUploadAvatarCallback {

    // 基础数据是否上传完成
    private var isCompleteUpdateBaseInfo = false

    // 更多数据是否上传完成
    private var isCompleteUpdateMoreInfo = false

    // 是否允许跳转 (逻辑是,点击一次之后继续展示下一界面,再点击一次则跳过,跳转至主界面)
    private var isReadyJump = false


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


    // 头像暂存的bitmap
    private var photoBitmap: Bitmap? = null

    // 临时图片文件路径
    private var mTempPhotoPath = ""

    // 剪切后图像文件
    private var mDestination: Uri? = null

    // 剪切后图像存放地址
    private var mPhotoPath = ""

    // 图像上传百度云的url
    private var mPhotoUrl = ""


    private var mStepDetailOne: StepDetailOne? = null   //  购车情况
    private var mStepDetailTwo: StepDetailTwo? = null   //  家乡
    private var mStepDetailThree: StepDetailThree? = null   //  抽烟
    private var mStepDetailFour: StepDetailFour? = null   //  是否想要孩子
    private var mStepDetailFive: StepDetailFive? = null   //  是否有孩子
    private var mStepDetailSix: StepDetailSix? = null   //  工作
    private var mStepDetailSeven: StepDetailSeven? = null   //  住房情况
    private var mStepDetailEight: StepDetailEight? = null   //  招呼语
    private var mStepDetailNine: StepDetailNine? = null   //  头像

    private lateinit var client: BosClient

    private lateinit var doUpdateMoreInfoPresent: doUpdateMoreInfoPresentImpl
    private lateinit var doUpdateBaseInfoPresent: doUpdateBaseInfoPresentImpl
    private lateinit var doFaceDetectPresent: doFaceDetectPresentImpl
    private lateinit var doUploadAvatarPresent: doUploadAvatarPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_jump

    override fun initView() {
        super.initView()

        initStep()
        initNextAnimal()

        doUpdateMoreInfoPresent = doUpdateMoreInfoPresentImpl.getsInstance()
        doUpdateMoreInfoPresent.registerCallback(this)

        doUpdateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        doUpdateBaseInfoPresent.registerCallback(this)

        doFaceDetectPresent = doFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        doUploadAvatarPresent = doUploadAvatarPresentImpl.getsInstance()
        doUploadAvatarPresent.registerCallback(this)

        mTempPhotoPath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"
        mDestination = Uri.fromFile(File(this.cacheDir, "photoCropImage.jpeg"))

        mPhotoPath = this.externalCacheDir.toString() + File.separator + "head.png"

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

        showBeginUI()

    }

    override fun initPresent() {
        super.initPresent()

        val config: BosClientConfiguration = BosClientConfiguration()
        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://adrmf.gz.bcebos.com"
        client = BosClient(config)

    }

    override fun initEvent() {
        super.initEvent()

        ll_guide_jump_skip.setOnClickListener {

            if (isReadyJump || vf_guide_jump_container.displayedChild == 8) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {


                when (vf_guide_jump_container.displayedChild) {
                    0 -> ll_guide_jump_next.visibility = View.VISIBLE
                    4 -> ll_guide_jump_next.visibility = View.VISIBLE
                    6 -> {
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                    }
                    7 -> {
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                    }
                    else -> ll_guide_jump_next.visibility = View.GONE
                }

                vf_guide_jump_container.showNext()
                isReadyJump = true
            }

        }

        ll_guide_jump_next.setOnClickListener {
            when (vf_guide_jump_container.displayedChild) {
                1 -> {
                    if (isHomeReady) {

                        SPStaticUtils.put(Constant.ME_HOME_PROVINCE_CODE,
                            mCityIdFirstList[mCityFirstPosition])
                        SPStaticUtils.put(Constant.ME_HOME_PROVINCE_NAME,
                            mCityFirstList[mCityFirstPosition])
                        SPStaticUtils.put(Constant.ME_HOME_CITY_CODE,
                            mCityIdSecondList[mCitySecondPosition])
                        SPStaticUtils.put(Constant.ME_HOME_CITY_NAME,
                            mCitySecondList[mCitySecondPosition])

                        showNextUI()

                    } else {

                        ToastUtils.showShort("请确定您的家乡")

                        isHomeReady = true

                    }
                }
                5 -> {
                    if (isJobReady) {

                        SPStaticUtils.put(Constant.ME_INDUSTRY_CODE,
                            mJobIdFirstList[mFirstJobPosition])
                        SPStaticUtils.put(Constant.ME_INDUSTRY_NAME,
                            mJobFirstList[mFirstJobPosition])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_CODE,
                            mJobIdSecondList[mSecondJobPosition])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_NAME,
                            mJobSecondList[mSecondJobPosition])

                        showNextUI()

                    } else {

                        ToastUtils.showShort("请确定您的工作")

                        isJobReady = true

                    }

                }
                7 -> {

                    // 需要跳转到介绍语填写界面
                    startActivityForResult(Intent(this, GreetEditActivity::class.java), 0)

                }
                8 -> {

                    // 需要弹出头像上传弹窗
                    XPopup.Builder(this)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(PhotoGuideDialog(this))
                        .show()

                }
            }
        }


        // 购车情况
        tv_jump_car_have.setOnClickListener {

            tv_jump_car_have.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_car_have.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()
            SPStaticUtils.put(Constant.ME_CAR, 1)


        }

        tv_jump_car_non.setOnClickListener {

            tv_jump_car_non.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_car_non.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()
            SPStaticUtils.put(Constant.ME_CAR, 2)
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

            showNextUI()

            SPStaticUtils.put(Constant.ME_SMOKE, 3)

        }

        tv_jump_smoke_two.setOnClickListener {

            tv_jump_smoke_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_two.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_SMOKE, 2)
        }

        tv_jump_smoke_three.setOnClickListener {

            tv_jump_smoke_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_three.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_SMOKE, 1)

        }

        tv_jump_smoke_four.setOnClickListener {

            tv_jump_smoke_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_smoke_four.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_SMOKE, 4)
        }

        // 想要孩子吗
        tv_jump_wantchild_one.setOnClickListener {

            tv_jump_wantchild_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_one.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 1)
        }

        tv_jump_wantchild_two.setOnClickListener {

            tv_jump_wantchild_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_two.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 2)
        }

        tv_jump_wantchild_three.setOnClickListener {

            tv_jump_wantchild_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_three.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 3)
        }

        tv_jump_wantchild_four.setOnClickListener {

            tv_jump_wantchild_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_wantchild_four.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_WANT_CHILD, 4)
        }

        // 你有孩子吗
        tv_jump_havechild_one.setOnClickListener {

            tv_jump_havechild_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_one.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 1)

        }

        tv_jump_havechild_two.setOnClickListener {

            tv_jump_havechild_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_two.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 2)

        }

        tv_jump_havechild_three.setOnClickListener {

            tv_jump_havechild_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_three.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 3)

        }

        tv_jump_havechild_four.setOnClickListener {

            tv_jump_havechild_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_havechild_four.setTextColor(Color.parseColor("#FF4444"))

            showNextUI()

            SPStaticUtils.put(Constant.ME_HAVE_CHILD, 4)

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

            showNextUI()

        }

        tv_jump_house_two.setOnClickListener {

            tv_jump_house_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_two.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 2)

            showNextUI()

        }

        tv_jump_house_three.setOnClickListener {

            tv_jump_house_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_three.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 3)

            showNextUI()

        }

        tv_jump_house_four.setOnClickListener {

            tv_jump_house_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_four.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 4)

            showNextUI()

        }

        tv_jump_house_five.setOnClickListener {

            tv_jump_house_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
            tv_jump_house_five.setTextColor(Color.parseColor("#FF4444"))

            SPStaticUtils.put(Constant.ME_HOUSE, 5)

            showNextUI()


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

                if (mStepDetailEight == null) {
                    mStepDetailEight = StepDetailEight(this, vf_guide_jump_container.getChildAt(7))
                }

                if (mStepDetailNine == null) {
                    mStepDetailNine = StepDetailNine(this, vf_guide_jump_container.getChildAt(8))
                }

            }
        }
        return null
    }


    private fun showBeginUI() {
        // 判断各个界面是否有数据

        // 购车情况
        val car = SPStaticUtils.getInt(Constant.ME_CAR, 0)
        // 家乡
        val home = SPStaticUtils.getString(Constant.ME_HOME_PROVINCE_NAME, "")
        // 抽烟
        val smoke = SPStaticUtils.getInt(Constant.ME_SMOKE, 0)
        // 想要孩子吗
        val wantChild = SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0)
        // 你有孩子吗
        val haveChild = SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0)
        // 工作
        val work = SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "")
        // 住房情况
        val house = SPStaticUtils.getInt(Constant.ME_HOUSE, 0)
        // 招呼语
        val greet = SPStaticUtils.getString(Constant.ME_GREET, "")
        // 头像
        val avatar = SPStaticUtils.getString(Constant.ME_AVATAR, "")


        Log.i("guo", "car : $car")
        Log.i("guo", "home : $home")
        Log.i("guo", "smoke : $smoke")
        Log.i("guo", "wantChild : $wantChild")
        Log.i("guo", "haveChild : $haveChild")
        Log.i("guo", "work : $work")
        Log.i("guo", "house : $house")
        Log.i("guo", "greet : $greet")
        Log.i("guo", "avatar : $avatar")


        when {
            car == 0 -> {
                // home 没有数据
                show(0)
                ll_guide_jump_next.visibility = View.GONE
                ll_guide_jump_tips.visibility = View.GONE
            }
            home == "" -> {
                // home 没有数据
                show(1)
                ll_guide_jump_next.visibility = View.VISIBLE
                ll_guide_jump_tips.visibility = View.GONE
            }
            smoke == 0 -> {
                show(2)
                ll_guide_jump_next.visibility = View.GONE
                ll_guide_jump_tips.visibility = View.GONE
            }
            wantChild == 0 -> {
                show(3)
                ll_guide_jump_next.visibility = View.GONE
                ll_guide_jump_tips.visibility = View.GONE
            }
            haveChild == 0 -> {
                show(4)
                ll_guide_jump_next.visibility = View.GONE
                ll_guide_jump_tips.visibility = View.GONE
            }
            work == "" -> {
                show(5)
                ll_guide_jump_next.visibility = View.VISIBLE
                ll_guide_jump_tips.visibility = View.GONE
            }
            house == 0 -> {
                show(6)
                ll_guide_jump_next.visibility = View.GONE
                ll_guide_jump_tips.visibility = View.GONE
            }
            greet == "" -> {
                show(7)
                ll_guide_jump_next.visibility = View.VISIBLE
                ll_guide_jump_next.text = "立即填写"
                ll_guide_jump_tips.visibility = View.VISIBLE
            }
            avatar == "" -> {
                show(8)
                ll_guide_jump_next.visibility = View.VISIBLE
                ll_guide_jump_next.text = "上传头像"
                ll_guide_jump_tips.visibility = View.GONE
            }
            else -> {
                // 跳转到首页
                jumpToMain()
            }
        }


    }

    // 显示下一个弹窗
    private fun showNextUI() {
        // 判断各个界面是否有数据

        // 购车情况
        val car = SPStaticUtils.getInt(Constant.ME_CAR, 0)
        // 家乡
        val home = SPStaticUtils.getString(Constant.ME_HOME_PROVINCE_NAME, "")
        // 抽烟
        val smoke = SPStaticUtils.getInt(Constant.ME_SMOKE, 0)
        // 想要孩子吗
        val wantChild = SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0)
        // 你有孩子吗
        val haveChild = SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0)
        // 工作
        val work = SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "")
        // 住房情况
        val house = SPStaticUtils.getInt(Constant.ME_HOUSE, 0)
        // 招呼语
        val greet = SPStaticUtils.getString(Constant.ME_GREET, "")
        // 头像
        val avatar = SPStaticUtils.getString(Constant.ME_AVATAR, "")


        Log.i("guo", "car : $car")
        Log.i("guo", "home : $home")
        Log.i("guo", "smoke : $smoke")
        Log.i("guo", "wantChild : $wantChild")
        Log.i("guo", "haveChild : $haveChild")
        Log.i("guo", "work : $work")
        Log.i("guo", "house : $house")
        Log.i("guo", "greet : $greet")
        Log.i("guo", "avatar : $avatar")


        when (vf_guide_jump_container.displayedChild) {
            0 -> {
                when {
                    home == "" -> {
                        // home 没有数据
                        show(1)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    smoke == 0 -> {
                        show(2)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    wantChild == 0 -> {
                        show(3)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    haveChild == 0 -> {
                        show(4)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    work == "" -> {
                        show(5)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    house == 0 -> {
                        show(6)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            1 -> {
                when {
                    smoke == 0 -> {
                        show(2)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    wantChild == 0 -> {
                        show(3)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    haveChild == 0 -> {
                        show(4)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    work == "" -> {
                        show(5)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    house == 0 -> {
                        show(6)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            2 -> {
                when {
                    wantChild == 0 -> {
                        show(3)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    haveChild == 0 -> {
                        show(4)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    work == "" -> {
                        show(5)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    house == 0 -> {
                        show(6)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            3 -> {
                when {
                    haveChild == 0 -> {
                        show(4)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    work == "" -> {
                        show(5)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    house == 0 -> {
                        show(6)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            4 -> {
                when {
                    work == "" -> {
                        show(5)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    house == 0 -> {
                        show(6)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            5 -> {
                when {
                    house == 0 -> {
                        show(6)
                        ll_guide_jump_next.visibility = View.GONE
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            6 -> {
                when {
                    greet == "" -> {
                        show(7)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "立即填写"
                        ll_guide_jump_tips.visibility = View.VISIBLE
                    }
                    avatar == "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }
            7 -> {
                when (avatar) {
                    "" -> {
                        show(8)
                        ll_guide_jump_next.visibility = View.VISIBLE
                        ll_guide_jump_next.text = "上传头像"
                        ll_guide_jump_tips.visibility = View.GONE
                    }
                    else -> {
                        // 跳转到首页
                        jumpToMain()
                    }
                }
            }

        }

    }

    private fun show(page: Int) {
        vf_guide_jump_container.displayedChild = page
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
    private fun updateAvatar(photoUrl: String, type: String, name: String) {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.IMAGE_URL] = photoUrl
        map[Contents.FILE_TYPE] = type
        map[Contents.FILE_NAME] = name
        map[Contents.CONTENT] = "0"
        doUploadAvatarPresent.doUploadAvatar(map)

    }

    // 需要上传的基础信息
    private fun getBaseInfo(): String {

        val industryCode = SPStaticUtils.getInt(Constant.ME_INDUSTRY_CODE, 0)
        val industryName = SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "")
        val occupationCode = SPStaticUtils.getInt(Constant.ME_OCCUPATION_CODE, 0)
        val occupationName = SPStaticUtils.getString(Constant.ME_OCCUPATION_NAME, "")

        val homeProvinceCode = SPStaticUtils.getInt(Constant.ME_HOME_PROVINCE_CODE, 0)
        val homeProvinceName = SPStaticUtils.getString(Constant.ME_HOME_PROVINCE_NAME, "")
        val homeCityCode = SPStaticUtils.getInt(Constant.ME_HOME_CITY_CODE, 0)
        val homeCityName = SPStaticUtils.getString(Constant.ME_HOME_CITY_NAME, "")

        val baseInfo =
            " {\"industry_num\":      $industryCode," +       // 行业编码
                    "\"industry_str\":      \"$industryName\"," +       // 行业名字
                    "\"occupation_num\":    $occupationCode," +     // 岗位编码
                    "\"occupation_str\":    \"$occupationName\"," +     // 岗位名字
                    "\"hometown_province_num\": \"$homeProvinceCode\"," +           // 家乡省份编码
                    "\"hometown_province_str\": \"$homeProvinceName\"," +           // 家乡省份名字
                    "\"hometown_city_num\":     \"$homeCityCode\"," +           // 家乡城市编码
                    "\"hometown_city_str\":     \"$homeCityName\"}"                   // 家乡城市名字

        return baseInfo

    }

    // 需要上传的更多信息
    private fun getMoreInfo(): String {

        val car = SPStaticUtils.getInt(Constant.ME_CAR, 0)
        val house = SPStaticUtils.getInt(Constant.ME_HOUSE, 0)
        val smoke = SPStaticUtils.getInt(Constant.ME_SMOKE, 0)
        val haveChild = SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0)
        val wantChild = SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0)

        val moreInfo =
            " {\"buy_car\":       $car," +           // 是否买车
                    "\"buy_house\":       $house," +         // 是否买房
                    "\"is_smoking\":       $smoke," +        // 是否抽烟
                    "\"child_had\":       $haveChild," +         // 是否有娃
                    "\"want_child\":       $wantChild}"  // 想结婚时间

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

    // 裁剪图片方法实现 ( 头像界面 )
    private fun startPhotoCropActivity(source: Uri) {
        val options = UCrop.Options()
        // 修改标题栏颜色
        options.setToolbarColor(Color.parseColor("#FFFFFF"))
        // 修改状态栏颜色
        options.setStatusBarColor(Color.parseColor("#0F0F0F"))
        // 隐藏底部工具
        options.setHideBottomControls(true)
        // 图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
        // 设置图片压缩质量
        options.setCompressionQuality(100)


        // 是否让用户调整范围(默认false)，如果开启，可能会造成剪切的图片的长宽比不是设定的
        // 如果不开启，用户不能拖动选框，只能缩放图片
//        options.setFreeStyleCropEnabled(true);
        mDestination?.let {
            UCrop.of<Any>(source, it) // 长宽比
                .withAspectRatio(1f, 1f) // 图片大小
                .withMaxResultSize(512, 512) // 配置参数
                .withOptions(options)
                .start(this)
        }

    }

    // 处理剪切成功的返回值 ( 头像界面 )
    private fun handlePhotoCropResult(result: Intent) {
        deleteTempPhotoFile()
        val resultUri = UCrop.getOutput(result)
        if (null != resultUri) {
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            photoBitmap = bitmap

            val map: MutableMap<String, String> = TreeMap()

            map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
            map[Contents.IMAGE] = bitmapToBase64(bitmap)

            ll_jump_loading.visibility = View.VISIBLE

            doFaceDetectPresent.doFaceDetect(map)

        } else {
            ToastUtils.showShort("无法剪切选择图片")
        }
    }

    // 处理剪切失败的返回值
    private fun handlePhotoCropError(result: Intent) {
        deleteTempPhotoFile()
        val cropError = UCrop.getError(result)
        if (cropError != null) {
            ToastUtils.showShort(cropError.message)
        } else {
            ToastUtils.showShort("无法剪切选择图片")
        }
    }

    // 删除拍照临时文件
    private fun deleteTempPhotoFile() {
        val tempFile = File(mTempPhotoPath)
        if (tempFile.exists() && tempFile.isFile) {
            tempFile.delete()
        }
    }

    // 将图片转换成Base64编码的字符串
    private fun bitmapToBase64(bitmap: Bitmap?): String {
        var result: String = ""
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes = baos.toByteArray()
                result =
                    android.util.Base64.encodeToString(bitmapBytes, android.util.Base64.NO_WRAP)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    private fun saveBitmap(bitmap: Bitmap, targetPath: String): String {
        ImageUtils.save(bitmap, targetPath, Bitmap.CompressFormat.PNG)
        return targetPath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {

                    showNextUI()

                }
                // 拍照返回至裁切
                1 -> {
                    val temp = File(mTempPhotoPath)
                    startPhotoCropActivity(Uri.fromFile(temp))
                }
                UCrop.REQUEST_CROP -> {

                    // 只走头像的回调
                    if (data != null) {
                        handlePhotoCropResult(data)
                    }

                }
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        handlePhotoCropError(data)
                    }
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUploadAvatarSuccess(uploadAvatarBean: UploadAvatarBean?) {

        Log.i("guo","onDoUploadAvatarSuccess")
        jumpToMain()

        if (uploadAvatarBean != null) {
            if (uploadAvatarBean.code == 200) {
                jumpToMain()
            } else {
                ToastUtils.showShort("头像上传失败")
            }
        }
    }

    override fun onDoUploadAvatarError() {
        ll_jump_loading.visibility = View.GONE
    }

    override fun onDoFaceDetectSuccess(faceDetectBean: FaceDetectBean?) {
        if (faceDetectBean != null) {
            if (faceDetectBean.conclusion == "合规") {


                val bitmap = BitmapUtil.generateBitmap("佳偶婚恋交友", 16f, Color.WHITE)?.let {
                    BitmapUtil.createWaterMarkBitmap(photoBitmap, it)
                }


                FileUtils.delete(mPhotoPath)

                if (bitmap != null) {
                    saveBitmap(bitmap, mPhotoPath)
                }

                Thread {

                    //上传Object
                    val file = File(mPhotoPath)
                    // bucketName 为文件夹名 ，使用用户id来进行命名
                    // key值为保存文件名，试用固定的几种格式来命名

                    val putObjectFromFileResponse = client.putObject("user${
                        SPStaticUtils.getString(Constant.USER_ID,
                            "default")
                    }",
                        FileUtils.getFileName(mPhotoPath), file)

                    Log.i("guo", FileUtils.getFileName(mPhotoPath))

                    mPhotoUrl = client.generatePresignedUrl("user${
                        SPStaticUtils.getString(Constant.USER_ID, "default")
                    }", FileUtils.getFileName(mPhotoPath), -1).toString()

                    SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, mPhotoUrl)

                    Log.i("guo", mPhotoUrl)

                    updateAvatar(mPhotoUrl,
                        FileUtils.getFileExtension(mPhotoPath),
                        FileUtils.getFileNameNoExtension(mPhotoPath))

                }.start()

            } else {
                ToastUtils.showShort(faceDetectBean.error_msg)
                ll_jump_loading.visibility = View.GONE
            }
        }
    }

    override fun onDoFaceDetectError() {
        ll_jump_loading.visibility = View.GONE
    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {

        ll_jump_loading.visibility = View.GONE

        isCompleteUpdateBaseInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {

            if (baseInfoUpdateBean != null) {
                if (baseInfoUpdateBean.code == 200) {
                    ToastUtils.showShort("资料上传成功")
                } else {
                    ToastUtils.showShort("资料上传失败")
                }
            }

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    override fun onDoUpdateBaseInfoError() {

        ll_jump_loading.visibility = View.GONE

        isCompleteUpdateBaseInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            ToastUtils.showShort("资料上传失败")

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    override fun onDoUpdateMoreInfoSuccess(updateMoreInfoBean: UpdateMoreInfoBean?) {

        ll_jump_loading.visibility = View.GONE

        isCompleteUpdateMoreInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            if (updateMoreInfoBean != null) {
                if (updateMoreInfoBean.code == 200) {
                    ToastUtils.showShort("资料上传成功")
                } else {
                    ToastUtils.showShort("资料上传失败")
                }
            }

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    override fun onDoUpdateMoreInfoError() {
        ll_jump_loading.visibility = View.GONE

        isCompleteUpdateMoreInfo = true

        if (isCompleteUpdateBaseInfo && isCompleteUpdateMoreInfo) {
            ToastUtils.showShort("资料上传失败")

//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }

    }

    // -------------------  上传头像界面  -----------------

    inner class PhotoGuideDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_photo_guide

        override fun onCreate() {
            super.onCreate()

            val assetManager = context.assets

            val goodOne: InputStream = assetManager.open("pic/pic_guide_photo_good_one.png")
            val goodTwo: InputStream = assetManager.open("pic/pic_guide_photo_good_two.png")
            val goodThree: InputStream = assetManager.open("pic/pic_guide_photo_good_three.png")

            val badOne: InputStream = assetManager.open("pic/pic_guide_photo_bad_one.png")
            val badTwo: InputStream = assetManager.open("pic/pic_guide_photo_bad_two.png")
            val badThree: InputStream = assetManager.open("pic/pic_guide_photo_bad_three.png")
            val badFour: InputStream = assetManager.open("pic/pic_guide_photo_bad_four.png")
            val badFive: InputStream = assetManager.open("pic/pic_guide_photo_bad_five.png")

            findViewById<ImageView>(R.id.iv_dialog_photo_good_one).background =
                BitmapDrawable(BitmapFactory.decodeStream(goodOne))
            findViewById<ImageView>(R.id.iv_dialog_photo_good_two).background =
                BitmapDrawable(BitmapFactory.decodeStream(goodTwo))
            findViewById<ImageView>(R.id.iv_dialog_photo_good_three).background =
                BitmapDrawable(BitmapFactory.decodeStream(goodThree))
            findViewById<ImageView>(R.id.iv_dialog_photo_bad_one).background =
                BitmapDrawable(BitmapFactory.decodeStream(badOne))
            findViewById<ImageView>(R.id.iv_dialog_photo_bad_two).background =
                BitmapDrawable(BitmapFactory.decodeStream(badTwo))
            findViewById<ImageView>(R.id.iv_dialog_photo_bad_three).background =
                BitmapDrawable(BitmapFactory.decodeStream(badThree))
            findViewById<ImageView>(R.id.iv_dialog_photo_bad_four).background =
                BitmapDrawable(BitmapFactory.decodeStream(badFour))
            findViewById<ImageView>(R.id.iv_dialog_photo_bad_five).background =
                BitmapDrawable(BitmapFactory.decodeStream(badFive))

            findViewById<ImageView>(R.id.iv_dialog_photo_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_photo_confirm).setOnClickListener {
                ToastUtils.showShort("打开相册")

                dismiss()

                val selectorStyle = PictureSelectorStyle()
                val animationStyle = PictureWindowAnimationStyle()
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
                selectorStyle.windowAnimationStyle = animationStyle

                PictureSelector.create(this@JumpActivity)
                    .openGallery(SelectMimeType.TYPE_IMAGE)
                    .setImageEngine(GlideEngine.createGlideEngine())
                    .setSelectionMode(SelectModeConfig.SINGLE)
                    .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                    .setImageSpanCount(3)
                    .isDisplayCamera(true)
                    .isPreviewImage(true)
                    .isEmptyResultReturn(true)
                    .setLanguage(LanguageConfig.CHINESE)
                    .setSelectorUIStyle(selectorStyle)
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {

                            val temp = File(result?.get(0)?.realPath)
                            startPhotoCropActivity(Uri.fromFile(temp))

                        }

                        override fun onCancel() {

                        }

                    })


            }

            findViewById<TextView>(R.id.tv_dialog_photo_camera).setOnClickListener {
                ToastUtils.showShort("打开相机")

                dismiss()

                XXPermissions.with(this@JumpActivity)
                    .permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            val tempPhotoFile: File = File(mTempPhotoPath)
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            // 如果在Android7.0以上,使用FileProvider获取Uri
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                val authority = context.packageName.toString() + ".fileProvider"
                                val contentUri: Uri =
                                    FileProvider.getUriForFile(context, authority, tempPhotoFile)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                            } else {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(tempPhotoFile))
                            }
                            startActivityForResult(intent, 1)
                        }

                        override fun onDenied(
                            permissions: MutableList<String>?,
                            never: Boolean,
                        ) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予应用相关权限")
                        }

                    })
            }

        }

    }

}