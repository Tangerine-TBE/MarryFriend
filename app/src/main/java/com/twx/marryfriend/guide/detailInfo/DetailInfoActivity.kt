package com.twx.marryfriend.guide.detailInfo

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AnyRes
import androidx.core.content.FileProvider
import com.aigestudio.wheelpicker.WheelPicker
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
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
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.baseInfo.step.RegisterStep
import com.twx.marryfriend.guide.detailInfo.artificial.IdentityActivity
import com.twx.marryfriend.guide.detailInfo.life.LifeIntroduceActivity
import com.twx.marryfriend.guide.detailInfo.search.SchoolSearchActivity
import com.twx.marryfriend.guide.detailInfo.step.*
import com.twx.marryfriend.guide.jumpInfo.JumpActivity
import com.twx.marryfriend.net.callback.*
import com.twx.marryfriend.net.impl.*
import com.twx.marryfriend.utils.GlideEngine
import com.twx.marryfriend.utils.UnicodeUtils
import com.twx.marryfriend.view.MySeekBar
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_detail_info.*
import kotlinx.android.synthetic.main.layout_guide_step_address.*
import kotlinx.android.synthetic.main.layout_guide_step_edu.*
import kotlinx.android.synthetic.main.layout_guide_step_hobby.*
import kotlinx.android.synthetic.main.layout_guide_step_ideal.*
import kotlinx.android.synthetic.main.layout_guide_step_identity.*
import kotlinx.android.synthetic.main.layout_guide_step_income.*
import kotlinx.android.synthetic.main.layout_guide_step_life.*
import kotlinx.android.synthetic.main.layout_guide_step_mine.*
import kotlinx.android.synthetic.main.layout_guide_step_photo.*
import kotlinx.android.synthetic.main.layout_guide_step_target.*
import java.io.*
import java.util.*


class DetailInfoActivity : MainBaseViewActivity(), IGetIndustryCallback, IGetJobCallback,
    IDoFaceDetectCallback, IDoIdentityVerifyCallback, IDoUpdateBaseInfoCallback,
    IDoUpdateMoreInfoCallback, IDoUpdateDemandInfoCallback, IDoUploadPhotoCallback {

    // 敏感字
    private var banTextList: MutableList<String> = arrayListOf()

    // 是否具有敏感词
    private var haveBanText = false

    //---------------------- 教育信息 ----------------------

    // 是否完成教育信息接麦你填写
    private var isFinishEdu = false

    // 用户学历
    private var eduPosition = 0
    private var mEduData: MutableList<String> = arrayListOf()

    // 用户学校
    private var eduSchool = ""

    //---------------------- 职业收入情况 ----------------------

    // 是否选择职业
    private var chooseJob = false

    // 是否选择年收入
    private var chooseIncome = false

    // 用户职业
    private var jobFirst = 0
    private var jobSecond = 0

    // 用户年收入
    private var incomePosition = 0
    private var incomeData: MutableList<String> = arrayListOf()

    // 弹窗第一组数据
    private var jobFirstList: MutableList<String> = arrayListOf()
    private var jobIDFirstList: MutableList<Int> = arrayListOf()

    // 弹窗第二组数据
    private var jobSecondList: MutableList<String> = arrayListOf()
    private var jobIDSecondList: MutableList<Int> = arrayListOf()

    private var mIndustryData: MutableList<IndustryData> = arrayListOf()

    private lateinit var getIndustryPresent: getIndustryPresentImpl
    private lateinit var getJobPresent: getJobPresentImpl


    // -------------------  居住地和家乡界面  -----------------


    // 工作是定位模式还是自主选择模式
    private var isJobLocal = false

    // 家乡是定位模式还是自主选择模式
    private var isHomeLocal = false

    private lateinit var myListener: MyLocationListener
    private lateinit var mLocationClient: LocationClient

    // 是否选择工作居住地
    private var chooseJobCity = false

    // 是否选择家乡
    private var chooseHomeCity = false

    // 定位省份
    private var localCityOne = ""

    // 定位省份编码
    private var localCityOneCode = ""

    // 定位城市
    private var localCityTwo = ""

    // 定位城市编码
    private var localCityTwoCode = ""

    // 定位县
    private var localCityThree = ""


    // 用户工作居住地
    private var jobCity = 0
    private var jobCityFirst = 0
    private var jobCitySecond = 0
    private var jobCityThird = 0

    //  用户家乡
    private var homeCity = 0
    private var homeCityFirst = ""
    private var homeCitySecond = ""
    private var homeCityThird = ""

    // 城市json数据
    private var cityJsonDate = ""

    // 城市数据
    private lateinit var cityDate: CityBean

    // 弹窗第一组数据
    private var cityFirstList: MutableList<String> = arrayListOf()
    private var cityIDFirstList: MutableList<String> = arrayListOf()

    // 弹窗第二组数据
    private var citySecondList: MutableList<String> = arrayListOf()
    private var cityIDSecondList: MutableList<String> = arrayListOf()

    // 弹窗第三组数据
    private var cityThirdList: MutableList<String> = arrayListOf()
    private var cityIDThirdList: MutableList<String> = arrayListOf()

    // -------------------  择偶条件界面  -----------------

    // 恋爱目标可见情况
    private var targetVisibilityPosition = 0
    private var targetVisibilityList: MutableList<String> = arrayListOf()

    // 恋爱目标
    private var target = 0

    //恋爱目标是否选择
    private var chooseTarget = false

    // 期望年龄
    private var targetAgeMin = 0
    private var targetAgeMax = 0

    //期望年龄是否选择   先true 验证 后面再改回来
    private var chooseAge = true


    // -------------------  上传头像界面  -----------------

    // 头像暂存的bitmap
    private var photoBitmap: Bitmap? = null


    // 是否完成头像上传
    private var isFinishPhoto = false

    // 是上传图片界面，还是我的生活界面
    private var isPhoto = true

    // 临时图片文件路径
    private var mTempPhotoPath = ""

    // 剪切后图像文件
    private var mDestination: Uri? = null

    // 剪切后图像存放地址
    private var mPhotoPath = ""

    // 图像上传百度云的url
    private var mPhotoUrl = ""

    private lateinit var doFaceDetectPresent: doFaceDetectPresentImpl


    // -------------------  我的生活界面  -----------------

    // 选择器中选中的图片路径
    private var lifeChoosePath: String = ""

    // 生活照暂存的bitmap
    private var lifeBitmap: Bitmap? = null

    // 是否完成生活照选择
    private var isFinishLife = false

    // 临时图片文件路径
    private var mTempLifePath = ""

    // 当前处理的图片
    private var lifeMode = "life"

    // 第一张我的生活照

    private var mLifeFirstPath = ""

    // 第一张我的生活照上传百度云的url
    private var mLifeFirstUrl = ""

    // 是否存在
    private var haveFirstPic = false

    // 存储地址
    private var lifeFirstPic: Uri? = null

    // 介绍
    private var lifeFirstPicText = ""

    private var lifeFirstBitmap: Bitmap? = null

    // 第二张我的生活照

    private var mLifeSecondPath = ""

    // 第二张我的生活照上传百度云的url
    private var mLifeSecondUrl = ""

    // 是否存在
    private var haveSecondPic = false

    // 存储地址
    private var lifeSecondPic: Uri? = null

    // 介绍
    private var lifeSecondPicText = ""

    private var lifeSecondBitmap: Bitmap? = null

    // 第三张我的生活照
    private var mLifeThirdPath = ""

    // 第三张我的生活照上传百度云的url
    private var mLifeThirdUrl = ""

    // 是否存在
    private var haveThirdPic = false

    // 存储地址
    private var lifeThirdPic: Uri? = null

    // 介绍
    private var lifeThirdPicText = ""


    private var lifeThirdBitmap: Bitmap? = null


    // -------------------  关于我界面  -----------------

    // 是否完成自我介绍
    private var isFinishIntroduce = false

    // 自我介绍
    private var introduceText = ""

    // -------------------  我的爱好界面  -----------------

    // 是否完成爱好填写
    private var isFinishHobby = false

    // 爱好
    private var hobbyText = ""

    // -------------------  我心目中的TA界面  -----------------

    // 是否完成心目中的ta填写
    private var isFinishIdeal = false

    // 心目中的TA
    private var idealText = ""

    private lateinit var client: BosClient


    // -------------------  实名认证界面  -----------------

    // 实名认证 姓名
    private var name = ""

    // 实名认证 身份证号
    private var identityCode = ""

    private lateinit var doIdentityVerifyPresent: doIdentityVerifyPresentImpl


    private lateinit var updateBaseInfoPresent: doUpdateBaseInfoPresentImpl

    private lateinit var updateMoreInfoPresent: doUpdateMoreInfoPresentImpl

    private lateinit var updateDemandInfoPresent: doUpdateDemandInfoPresentImpl

    private lateinit var uploadPhotoPresent: doUploadPhotoPresentImpl

    // 各文件是否上传完成
    private var photoCompleteLoad = false
    private var demandCompleteLoad = false
    private var moreInfoCompleteLoad = false
    private var baseInfoCompleteLoad = false

    private var mStepDetailOne: StepDetailOne? = null   //  教育信息
    private var mStepDetailTwo: StepDetailTwo? = null   //  职业收入情况
    private var mStepDetailThree: StepDetailThree? = null   //  居住城市和家乡
    private var mStepDetailFour: StepDetailFour? = null   //  择偶条件
    private var mStepDetailFive: StepDetailFive? = null   //  真实头像
    private var mStepDetailSix: StepDetailSix? = null   //  我的生活
    private var mStepDetailSeven: StepDetailSeven? = null   //  关于我
    private var mStepDetailEight: StepDetailEight? = null   //  我的爱好
    private var mStepDetailNine: StepDetailNine? = null   //  心目中的Ta
    private var mStepDetailTen: StepDetailTen? = null   //  实名认证


    private var xx: BasePopupView? = null   //  实名认证

    override fun getLayoutView(): Int = R.layout.activity_detail_info

    override fun initView() {
        super.initView()

        initStep()

        tsb_guide_detail_guide.isInTouchMode

        getIndustryPresent = getIndustryPresentImpl.getsInstance()
        getIndustryPresent.registerCallback(this)

        getJobPresent = getJobPresentImpl.getsInstance()
        getJobPresent.registerCallback(this)

        doFaceDetectPresent = doFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        doIdentityVerifyPresent = doIdentityVerifyPresentImpl.getsInstance()
        doIdentityVerifyPresent.registerCallback(this)

        updateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        updateBaseInfoPresent.registerCallback(this)

        updateMoreInfoPresent = doUpdateMoreInfoPresentImpl.getsInstance()
        updateMoreInfoPresent.registerCallback(this)

        updateDemandInfoPresent = doUpdateDemandInfoPresentImpl.getsInstance()
        updateDemandInfoPresent.registerCallback(this)

        uploadPhotoPresent = doUploadPhotoPresentImpl.getsInstance()
        uploadPhotoPresent.registerCallback(this)

        myListener = MyLocationListener()
        mLocationClient = LocationClient(this)

        val str1 = "遇到问题？联系客服"
        val stringBuilder1 = SpannableStringBuilder(str1)
        val span1 = TextViewSpan1()
        stringBuilder1.setSpan(span1, 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_guide_detail_service.text = stringBuilder1
        tv_guide_detail_service.movementMethod = LinkMovementMethod.getInstance()


        val str2 = "详见《隐私政策》"
        val stringBuilder2 = SpannableStringBuilder(str2)
        val span2 = TextViewSpan2()
        stringBuilder2.setSpan(span2, 3, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_guide_detail_privacy.text = stringBuilder2
        tv_guide_detail_privacy.movementMethod = LinkMovementMethod.getInstance()


        // 如果是男性，那么为男性默认图，反之，则为女性默认图
        if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
            iv_photo_container.setImageResource(R.mipmap.icon_photo_male)
        } else {
            iv_photo_container.setImageResource(R.mipmap.icon_photo_female)
        }

        xx = XPopup.Builder(this)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(AddressJobCity(this))

    }

    override fun initLoadData() {
        super.initLoadData()

        cityJsonDate = SPStaticUtils.getString(Constant.CITY_JSON_DATE)
        cityDate = GsonUtils.fromJson(cityJsonDate, CityBean::class.java)

        val banText = SPStaticUtils.getString(Constant.BAN_TEXT)
        val array = banText.split(",")

        for (i in 0.until(array.size)) {
            banTextList.add(UnicodeUtils.decode(array[i]))
        }

        mEduData.add("大专以下")
        mEduData.add("大专")
        mEduData.add("本科")
        mEduData.add("硕士")


        incomeData.add("保密")
        incomeData.add("五千以下")
        incomeData.add("五千~一万")
        incomeData.add("一万~两万")
        incomeData.add("两万~四万")
        incomeData.add("四万~七万")
        incomeData.add("七万及以上")

        targetVisibilityList.add("所有人可见")
        targetVisibilityList.add("目标相同的人可见")
        targetVisibilityList.add("不公开")

        mTempPhotoPath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"
        mDestination = Uri.fromFile(File(this.cacheDir, "photoCropImage.jpeg"))

        mPhotoPath = externalCacheDir.toString() + File.separator + "photoPic.png"

        mTempLifePath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"

        mLifeFirstPath = externalCacheDir.toString() + File.separator + "lifeFirstPic.png"
        mLifeSecondPath = externalCacheDir.toString() + File.separator + "lifeSecondPic.png"
        mLifeThirdPath = externalCacheDir.toString() + File.separator + "lifeThirdPic.png"

        lifeFirstPic = Uri.fromFile(File(this.cacheDir, "lifeFirstPic.jpeg"))
        lifeSecondPic = Uri.fromFile(File(this.cacheDir, "lifeSecondPic.jpeg"))
        lifeThirdPic = Uri.fromFile(File(this.cacheDir, "lifeThirdPic.jpeg"))


        FileUtils.delete(mLifeFirstPath)
        FileUtils.delete(mLifeSecondPath)
        FileUtils.delete(mLifeThirdPath)


        if (!SPStaticUtils.getBoolean(Constant.INDUSTRY_HAVE, false)) {
            getIndustry()
        } else {
            val size = SPStaticUtils.getInt(Constant.INDUSTRY_SUM, 0)
            for (i in 0.until(size)) {
                jobFirstList.add(SPStaticUtils.getString("industry_item_name_$i", ""))
                jobIDFirstList.add(SPStaticUtils.getInt("industry_item_id_$i", 0))
            }
        }

        if (!SPStaticUtils.getBoolean(Constant.JOB_HAVE, false)) {
            getJob()
        } else {
            val jobSecondSize = SPStaticUtils.getInt("job_second_0 _sum", 0)
            for (i in 0.until(jobSecondSize)) {
                jobSecondList.add(SPStaticUtils.getString("job_second_0 _name_$i", ""))
                jobIDSecondList.add(SPStaticUtils.getInt("job_second_0 _id_$i", 0))
            }

        }

        val cityFirstSize = SPStaticUtils.getInt("city_province_sum", 0)
        cityFirstList.clear()
        cityIDFirstList.clear()
        for (i in 0.until(cityFirstSize)) {
            cityFirstList.add(SPStaticUtils.getString("city_province_$i _name", ""))
            cityIDFirstList.add(SPStaticUtils.getString("city_province_$i _code", ""))
        }


        getJobCityFirstList()
        getJobCitySecondList(0)
        getJobCityThirdList(0, 0)


        var minAge = SPStaticUtils.getInt(Constant.AGE, 18) - 8
        var maxAge = SPStaticUtils.getInt(Constant.AGE, 18) + 4

        Log.i("guo", SPStaticUtils.getInt(Constant.AGE, 18).toString())

        if (minAge < 18) {
            minAge = 18
        }

        if (maxAge > 60) {
            maxAge = 60
        }


        msb_guide_target_age.setPos(minAge - 18, maxAge - 18)

    }

    override fun initPresent() {
        super.initPresent()

        val config: BosClientConfiguration = BosClientConfiguration()
        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://androidmarryfriend.gz.bcebos.com"
        client = BosClient(config)


    }

    override fun initEvent() {
        super.initEvent()

        tv_guide_detail_skip.setOnClickListener {
            ToastUtils.showShort("跳过整个流程，前往主页")
            startActivity(Intent(this, JumpActivity::class.java))
        }

        tv_guide_detail_previous.setOnClickListener {

            // 滑动效果
            vf_guide_detail_container.setInAnimation(this, R.anim.push_right_in)
            vf_guide_detail_container.setOutAnimation(this, R.anim.push_right_out)

            when (vf_guide_detail_container.displayedChild) {
                0 -> {
                    ToastUtils.showShort("教育信息")
                    ToastUtils.showShort("第一页，就不后退了")
                }
                1 -> {
                    ToastUtils.showShort("职业收入情况")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.08f, "8")

                }
                2 -> {
                    ToastUtils.showShort("居住城市")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.19f, "19")

                }
                3 -> {
                    ToastUtils.showShort("择偶条件")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.26f, "26")
                }
                4 -> {
                    ToastUtils.showShort("头像")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.39f, "39")
                }
                5 -> {
                    ToastUtils.showShort("我的生活")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.46f, "46")
                }
                6 -> {
                    ToastUtils.showShort("关于我")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.49f, "49")
                }
                7 -> {
                    ToastUtils.showShort("我的爱好")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.60f, "60")
                }
                8 -> {
                    ToastUtils.showShort("我心目中的ta")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tsb_guide_detail_guide.setPercent(0.73f, "73")
                }
                9 -> {
                    ToastUtils.showShort("实名认证")
                    vf_guide_detail_container.showPrevious()
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    tv_guide_detail_privacy.visibility = View.INVISIBLE
                    tv_guide_detail_service.visibility = View.INVISIBLE

                    tsb_guide_detail_guide.setPercent(0.88f, "88")
                }
            }

        }

        tv_guide_detail_next.setOnClickListener {

            // 滑动效果
            vf_guide_detail_container.setInAnimation(this, R.anim.push_left_in)
            vf_guide_detail_container.setOutAnimation(this, R.anim.push_left_out)

            when (vf_guide_detail_container.displayedChild) {
                0 -> {

                    if (isFinishEdu) {

                        if (!(chooseJob && chooseIncome)) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.19f, "19")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

//                    vf_guide_detail_container.showNext()
                }
                1 -> {

                    if (chooseJob && chooseIncome) {

                        if (!(chooseJobCity && chooseHomeCity)) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.26f, "26")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

//                    vf_guide_detail_container.showNext()
                }
                2 -> {

                    if (chooseJobCity && chooseHomeCity) {

                        if (!(chooseTarget && chooseAge)) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.39f, "39")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

//                    vf_guide_detail_container.showNext()
                }
                3 -> {
                    if (chooseTarget && chooseAge) {

                        if (!isFinishPhoto) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.46f, "46")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

//                    vf_guide_detail_container.showNext()
                }
                4 -> {
                    if (isFinishPhoto) {

                        if (!isFinishLife) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.49f, "49")

                        // 上传头像
                        Thread {

                            //上传Object
                            val file = File(mPhotoPath)
                            // bucketName 为文件夹名 ，使用用户id来进行命名
                            // key值为保存文件名，试用固定的几种格式来命名

                            val putObjectFromFileResponse =
                                client.putObject("user${
                                    SPStaticUtils.getString(Constant.USER_ID,
                                        "default")
                                }",
                                    FileUtils.getFileName(mPhotoPath), file)


                            mPhotoUrl = client.generatePresignedUrl("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", FileUtils.getFileName(mPhotoPath), -1).toString()


                            Log.i("guo", mPhotoUrl)

                        }.start()

                    } else {
                        ToastUtils.showShort("请上传头像")
                    }

//                    vf_guide_detail_container.showNext()
                }
                5 -> {

                    if (isFinishLife) {

                        if (!isFinishIntroduce) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.60f, "60")

                        // 上传生活照
                        Thread {
                            val file = File(mLifeFirstPath)
                            val putObjectFromFileResponse =
                                client.putObject("user${
                                    SPStaticUtils.getString(Constant.USER_ID, "default")
                                }",
                                    FileUtils.getFileName(mLifeFirstPath), file)

                            mLifeFirstUrl = client.generatePresignedUrl("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", FileUtils.getFileName(mLifeFirstPath), -1).toString()

                        }.start()

                        if (haveSecondPic) {
                            Thread {
                                val file = File(mLifeSecondPath)
                                val putObjectFromFileResponse =
                                    client.putObject("user${
                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                    }",
                                        FileUtils.getFileName(mLifeSecondPath), file)

                                mLifeSecondUrl = client.generatePresignedUrl("user${
                                    SPStaticUtils.getString(Constant.USER_ID, "default")
                                }", FileUtils.getFileName(mLifeSecondPath), -1).toString()

                            }.start()
                        }
                        if (haveThirdPic) {
                            Thread {
                                val file = File(mLifeThirdPath)
                                val putObjectFromFileResponse =
                                    client.putObject("user${
                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                    }",
                                        FileUtils.getFileName(mLifeThirdPath), file)

                                mLifeThirdUrl = client.generatePresignedUrl("user${
                                    SPStaticUtils.getString(Constant.USER_ID, "default")
                                }", FileUtils.getFileName(mLifeThirdPath), -1).toString()

                            }.start()
                        }


                    } else {
                        ToastUtils.showShort("请上传生活照")
                    }

                    vf_guide_detail_container.showNext()

                }
                6 -> {

                    if (isFinishIntroduce) {

                        if (!isFinishHobby) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()

                        tsb_guide_detail_guide.setPercent(0.73f, "73")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

                }
                7 -> {
                    ToastUtils.showShort("我的爱好")

                    if (isFinishHobby) {

                        if (!isFinishIdeal) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()
                        tsb_guide_detail_guide.setPercent(0.88f, "88")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

                }
                8 -> {
                    ToastUtils.showShort("我心目中的ta")

                    if (isFinishIdeal) {

                        if (!(name != "" && identityCode != "")) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        vf_guide_detail_container.showNext()
                        tv_guide_detail_privacy.visibility = View.VISIBLE
                        tv_guide_detail_service.visibility = View.VISIBLE

                        tsb_guide_detail_guide.setPercent(1.0f, "100")

                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }
                }
                9 -> {
                    ToastUtils.showShort("实名认证")

                    if (name != "" && identityCode != "") {

                        if (RegexUtils.isIDCard18Exact(identityCode)) {
                            val map: MutableMap<String, String> = TreeMap()

                            map[Contents.ACCESS_TOKEN] =
                                SPStaticUtils.getString(Constant.ACCESS_TOKEN)
                            map[Contents.CONTENT_TYPE] = "application/json"
                            map[Contents.ID_CARD_NUMBER] = identityCode
                            map[Contents.NAME] = name
                            doIdentityVerifyPresent.doIdentityVerify(map)
                        } else {
                            ToastUtils.showShort("请输入正确的身份证号")
                        }
                    } else {
                        ToastUtils.showShort("请填写详细信息")
                    }

                }
            }

        }

        // -------------------  教育信息界面  -----------------

        rl_guide_edu_edu.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(EduDialog(this))
                .show()

        }

        rl_guide_edu_school.setOnClickListener {
            val intent = Intent(this, SchoolSearchActivity::class.java)
            intent.putExtra("message", "Love")
            startActivityForResult(intent, 1)
        }

        //---------------------- 职业收入情况 ----------------------

        rl_guide_income_job.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(IncomeJobDialog(this))
                .show()

        }

        rl_guide_income_income.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(IncomeIncomeDialog(this))
                .show()

        }

        // -------------------  居住地和家乡界面  -----------------

        rl_guide_address_jobcity.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(AddressJobCity(this))
                .show()
        }

        rl_guide_address_homecity.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(AddressHomeCity(this))
                .show()
        }

        // -------------------  择偶条件界面  -----------------

        ll_guide_target_visibility.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(TargetVisibilityDialog(this))
                .show()

        }

        rl_guide_target_one.setOnClickListener {
            target = 1
            clearTargetChoose()
            rl_guide_target_one.setBackgroundResource(R.drawable.shape_bg_guide_target_choose)
            tv_guide_target_one.setTextColor(Color.parseColor("#FF4444"))
            chooseTarget = true
            verifyTargetNext()
        }

        rl_guide_target_two.setOnClickListener {
            target = 2
            clearTargetChoose()
            rl_guide_target_two.setBackgroundResource(R.drawable.shape_bg_guide_target_choose)
            tv_guide_target_two.setTextColor(Color.parseColor("#FF4444"))
            chooseTarget = true
            verifyTargetNext()
        }

        rl_guide_target_three.setOnClickListener {
            target = 3
            clearTargetChoose()
            rl_guide_target_three.setBackgroundResource(R.drawable.shape_bg_guide_target_choose)
            tv_guide_target_three.setTextColor(Color.parseColor("#FF4444"))
            chooseTarget = true
            verifyTargetNext()
        }

        rl_guide_target_four.setOnClickListener {
            target = 4
            clearTargetChoose()
            rl_guide_target_four.setBackgroundResource(R.drawable.shape_bg_guide_target_choose)
            tv_guide_target_four.setTextColor(Color.parseColor("#FF4444"))
            chooseTarget = true
            verifyTargetNext()
        }

        msb_guide_target_age.setListener { left, right ->
            targetAgeMin = 18 + left.point.mark
            targetAgeMax = 18 + right.point.mark

            tv_guide_target_age.text = "$targetAgeMin ~ $targetAgeMax 岁"
        }


        // -------------------  上传头像界面  -----------------

        iv_photo_delete.setOnClickListener {

            if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
                iv_photo_container.setImageResource(R.mipmap.icon_photo_male)
            } else {
                iv_photo_container.setImageResource(R.mipmap.icon_photo_female)
            }

            iv_photo_container.setOnClickListener {
                XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true)
                    .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(PhotoGuideDialog(this))
                    .show()
            }

            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
            iv_photo_delete.visibility = View.INVISIBLE
            tv_photo_reupload.visibility = View.INVISIBLE
            tv_photo_upload.visibility = View.VISIBLE

            isFinishPhoto = false

        }

        tv_photo_reupload.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(PhotoGuideDialog(this))
                .show()
        }

        iv_photo_container.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(PhotoGuideDialog(this))
                .show()
        }

        tv_photo_upload.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(PhotoGuideDialog(this))
                .show()
        }

        // 设置裁剪图片结果监听

        // -------------------  我的生活界面  -----------------

        ll_guide_life_default.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeGuidDialog(this))
                .show()

        }

        rl_guide_life_pic_more.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeGuidDialog(this))
                .show()

        }

        iv_guide_life_pic_one_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有其他图，若无则直接删除，若有则向上顺移第三张图

            if (haveSecondPic) {

                if (haveThirdPic) {
                    // 有三张图

                    rl_guide_life_pic_three.visibility = View.GONE
                    rl_guide_life_pic_more.visibility = View.VISIBLE

                    FileUtils.delete(mLifeFirstPath)
                    lifeSecondBitmap?.let { it1 -> saveBitmap(it1, mLifeFirstPath) }

                    lifeFirstPicText = lifeSecondPicText
                    lifeFirstBitmap = lifeSecondBitmap
                    Glide.with(this).load(lifeFirstBitmap).into(iv_guide_life_pic_one)
                    tv_guide_life_pic_one.text = lifeFirstPicText


                    FileUtils.delete(mLifeSecondPath)
                    lifeThirdBitmap?.let { it1 -> saveBitmap(it1, mLifeSecondPath) }

                    lifeSecondPicText = lifeThirdPicText
                    lifeSecondBitmap = lifeThirdBitmap
                    Glide.with(this).load(lifeSecondBitmap).into(iv_guide_life_pic_two)
                    tv_guide_life_pic_two.text = lifeSecondPicText

                    haveThirdPic = false
                    lifeThirdPicText = ""
                    lifeThirdBitmap = null
                    iv_guide_life_pic_three_icon.visibility = View.VISIBLE
                    tv_guide_life_pic_three.text = "添加描述"

                    FileUtils.delete(mLifeThirdPath)

                } else {
                    // 有两张图

                    rl_guide_life_pic_two.visibility = View.GONE

                    FileUtils.delete(mLifeFirstPath)
                    lifeSecondBitmap?.let { it1 -> saveBitmap(it1, mLifeFirstPath) }

                    lifeFirstPicText = lifeSecondPicText
                    lifeFirstBitmap = lifeSecondBitmap
                    Glide.with(this).load(lifeFirstBitmap).into(iv_guide_life_pic_one)
                    tv_guide_life_pic_one.text = lifeFirstPicText

                    haveSecondPic = false
                    lifeSecondPicText = ""
                    lifeSecondBitmap = null
                    iv_guide_life_pic_two_icon.visibility = View.VISIBLE
                    tv_guide_life_pic_two.text = "添加描述"

                    FileUtils.delete(mLifeSecondPath)

                }
            } else {
                // 只有一张图

                rl_guide_life_pic_one.visibility = View.GONE
                rl_guide_life_pic_more.visibility = View.GONE
                nsv_guide_life_pic.visibility = View.GONE

                nsv_guide_life_default.visibility = View.VISIBLE

                haveFirstPic = false
                lifeFirstPicText = ""
                lifeFirstBitmap = null
                iv_guide_life_pic_one_icon.visibility = View.VISIBLE
                tv_guide_life_pic_one.text = "添加描述"

                FileUtils.delete(mLifeFirstPath)

                isFinishLife = false
                tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)


            }

        }

        ll_guide_life_pic_one.setOnClickListener {
            // 第一张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeFirstPath)
            startActivityForResult(intent, 111)

        }

        iv_guide_life_pic_two_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有第三张图，若无则直接删除，若有则向上顺移第三张图

            if (haveThirdPic) {
                // 有第三张图

                rl_guide_life_pic_three.visibility = View.GONE
                rl_guide_life_pic_more.visibility = View.VISIBLE

                FileUtils.delete(mLifeSecondPath)
                lifeThirdBitmap?.let { it1 -> saveBitmap(it1, mLifeSecondPath) }

                lifeSecondPicText = lifeThirdPicText
                lifeSecondBitmap = lifeThirdBitmap
                Glide.with(this).load(lifeSecondBitmap).into(iv_guide_life_pic_two)
                tv_guide_life_pic_two.text = lifeSecondPicText

                haveThirdPic = false
                lifeThirdPicText = ""
                lifeThirdBitmap = null
                iv_guide_life_pic_three_icon.visibility = View.VISIBLE
                tv_guide_life_pic_three.text = "添加描述"

                FileUtils.delete(mLifeThirdPath)

            } else {
                // 没有第三张图

                rl_guide_life_pic_two.visibility = View.GONE

                haveSecondPic = false
                lifeSecondPicText = ""
                lifeSecondBitmap = null
                iv_guide_life_pic_two_icon.visibility = View.VISIBLE
                tv_guide_life_pic_two.text = "添加描述"

                FileUtils.delete(mLifeSecondPath)
            }

        }

        ll_guide_life_pic_two.setOnClickListener {
            // 第二张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeSecondPath)
            startActivityForResult(intent, 222)

        }

        iv_guide_life_pic_three_delete.setOnClickListener {

            rl_guide_life_pic_three.visibility = View.GONE
            rl_guide_life_pic_more.visibility = View.VISIBLE

            mLifeThirdPath = ""
            haveThirdPic = false
            lifeThirdPicText = ""
            lifeThirdBitmap = null
            iv_guide_life_pic_three_icon.visibility = View.VISIBLE
            tv_guide_life_pic_three.text = "添加描述"

            FileUtils.delete(mLifeThirdPath)

        }

        ll_guide_life_pic_three.setOnClickListener {
            // 第三张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeThirdPath)
            startActivityForResult(intent, 333)

        }

        // -------------------  关于我界面  -----------------

        et_guide_mine_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                introduceText = s.toString()

                tv_guide_mine_sum.text = s.length.toString()

                for (i in 0.until(banTextList.size)) {
                    haveBanText = introduceText.contains(banTextList[i])
                }

                if (haveBanText) {
                    ToastUtils.showShort("输入中存在敏感字，请重新输入")
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    isFinishIntroduce = false
                } else {
                    if (s.length >= 10) {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                        isFinishIntroduce = true
                    } else {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        isFinishIntroduce = false

                    }
                }

                if (s.length == 1000) {
                    ToastUtils.showShort("已达到输入文字最大数量")
                    KeyboardUtils.hideSoftInput(this@DetailInfoActivity)
                }

            }

        })

        // -------------------  我的爱好界面  -----------------

        et_guide_hobby_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                hobbyText = s.toString()

                tv_guide_hobby_sum.text = s.length.toString()

                for (i in 0.until(banTextList.size)) {
                    haveBanText = hobbyText.contains(banTextList[i])
                }

                if (haveBanText) {
                    ToastUtils.showShort("输入中存在敏感字，请重新输入")
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    isFinishHobby = false
                } else {
                    if (s.length >= 10) {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                        isFinishHobby = true
                    } else {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        isFinishHobby = false
                    }
                }

                if (s.length == 1000) {
                    ToastUtils.showShort("已达到输入文字最大数量")
                    KeyboardUtils.hideSoftInput(this@DetailInfoActivity)
                }

            }

        })


        // -------------------  我心目中的TA界面  -----------------

        et_guide_ideal_content.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                idealText = s.toString()

                tv_guide_ideal_sum.text = s.length.toString()

                for (i in 0.until(banTextList.size)) {
                    haveBanText = idealText.contains(banTextList[i])
                }

                if (haveBanText) {
                    ToastUtils.showShort("输入中存在敏感字，请重新输入")
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    isFinishIdeal = false
                } else {
                    if (s.length >= 10) {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                        isFinishIdeal = true
                    } else {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        isFinishIdeal = false
                    }

                }


                if (s.length == 1000) {
                    ToastUtils.showShort("已达到输入文字最大数量")
                    KeyboardUtils.hideSoftInput(this@DetailInfoActivity)
                }

            }

        })


        // ------------------- 实名认证界面  -----------------

        et_guide_identity_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                name = s.toString()
            }

        })

        et_guide_identity_id.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                identityCode = s.toString()

                if (s.length == 18) {
                    KeyboardUtils.hideSoftInput(this@DetailInfoActivity)

                    if (RegexUtils.isIDCard18Exact(identityCode)) {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    } else {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    }

                }
            }

        })

    }

    private fun initStep(): RegisterStep? {
        when (vf_guide_detail_container.displayedChild) {
            0 -> {
                if (mStepDetailOne == null) {
                    mStepDetailOne = StepDetailOne(this, vf_guide_detail_container.getChildAt(0))
                }

                if (mStepDetailTwo == null) {
                    mStepDetailTwo = StepDetailTwo(this, vf_guide_detail_container.getChildAt(1))
                }

                if (mStepDetailThree == null) {
                    mStepDetailThree =
                        StepDetailThree(this, vf_guide_detail_container.getChildAt(2))
                }

                if (mStepDetailFour == null) {
                    mStepDetailFour = StepDetailFour(this, vf_guide_detail_container.getChildAt(3))
                }

                if (mStepDetailFive == null) {
                    mStepDetailFive = StepDetailFive(this, vf_guide_detail_container.getChildAt(4))
                }

                if (mStepDetailSix == null) {
                    mStepDetailSix = StepDetailSix(this, vf_guide_detail_container.getChildAt(5))
                }

                if (mStepDetailSeven == null) {
                    mStepDetailSeven =
                        StepDetailSeven(this, vf_guide_detail_container.getChildAt(6))
                }

                if (mStepDetailEight == null) {
                    mStepDetailEight =
                        StepDetailEight(this, vf_guide_detail_container.getChildAt(7))
                }

                if (mStepDetailNine == null) {
                    mStepDetailNine =
                        StepDetailNine(this, vf_guide_detail_container.getChildAt(8))
                }

                if (mStepDetailTen == null) {
                    mStepDetailTen =
                        StepDetailTen(this, vf_guide_detail_container.getChildAt(9))
                }
            }
        }
        return null
    }


    //---------------------- 职业收入情况 ----------------------

    private fun getIndustry() {
        val map: MutableMap<String, String> = TreeMap()
        getIndustryPresent.getIndustry(map)
    }

    private fun getJob() {
        val map: MutableMap<String, String> = TreeMap()
        getJobPresent.getJob(map)
    }

    private fun getJobSecondList(i: Int) {
        jobSecondList.clear()
        jobIDSecondList.clear()
        val jobSecondSize = SPStaticUtils.getInt("job_second_$i _sum", 0)
        for (j in 0.until(jobSecondSize)) {
            jobSecondList.add(SPStaticUtils.getString("job_second_$i _name_$j", null))
            jobIDSecondList.add(SPStaticUtils.getInt("job_second_$i _id_$j", 0))
        }
    }

    // -------------------  居住地和家乡界面  -----------------

    // 省
    private fun getJobCityFirstList() {

        cityFirstList.clear()
        cityIDFirstList.clear()

        for (i in 0.until(cityDate.data.size)) {
            cityFirstList.add(cityDate.data[i].name)
            cityIDFirstList.add(cityDate.data[i].code)
        }

    }

    // 市
    private fun getJobCitySecondList(i: Int) {

        citySecondList.clear()
        cityIDSecondList.clear()

        for (j in 0.until(cityDate.data[i].cityList.size)) {
            citySecondList.add(cityDate.data[i].cityList[j].name)
            cityIDSecondList.add(cityDate.data[i].cityList[j].code)
        }

    }

    // 县
    private fun getJobCityThirdList(i: Int, j: Int) {
        cityThirdList.clear()
        cityIDThirdList.clear()

        if (cityDate.data[i].cityList[j].areaList.isNotEmpty()) {
            for (k in 0.until(cityDate.data[i].cityList[j].areaList.size)) {
                cityThirdList.add(cityDate.data[i].cityList[j].areaList[k].name)
                cityIDThirdList.add(cityDate.data[i].cityList[j].areaList[k].code)
            }
        } else {
            cityThirdList.add("")
            cityIDThirdList.add("")
        }

    }

    // 定位初始化
    private fun initLocation() {
        // 定位初始化
        mLocationClient.registerLocationListener(myListener)
        val option = LocationClientOption()
        option.setIsNeedAddress(true);
        option.setNeedNewVersionRgc(true);
        mLocationClient.locOption = option;
        mLocationClient.start()
    }

    // -------------------  择偶条件界面  -----------------

    private fun clearTargetChoose() {
        rl_guide_target_one.setBackgroundResource(R.drawable.shape_bg_common_input)
        rl_guide_target_two.setBackgroundResource(R.drawable.shape_bg_common_input)
        rl_guide_target_three.setBackgroundResource(R.drawable.shape_bg_common_input)
        rl_guide_target_four.setBackgroundResource(R.drawable.shape_bg_common_input)

        tv_guide_target_one.setTextColor(Color.parseColor("#9A9A9A"))
        tv_guide_target_two.setTextColor(Color.parseColor("#9A9A9A"))
        tv_guide_target_three.setTextColor(Color.parseColor("#9A9A9A"))
        tv_guide_target_four.setTextColor(Color.parseColor("#9A9A9A"))
    }

    // 验证 择偶条件界面 下一步按钮 是否可用
    private fun verifyTargetNext() {
        if (chooseTarget && chooseAge) {
            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
        } else {
            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
        }

    }

    // -------------------  上传头像界面  -----------------

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

    // -------------------  我的生活界面  -----------------


    // -------------------  关于我界面  -----------------


    // -------------------  我的爱好界面  -----------------


    // -------------------  我心目中的TA界面  -----------------


    // ------------------- 实名认证界面  -----------------


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    if (data != null) {
                        eduSchool = data.getStringExtra("schoolName").toString()
                        tv_guide_edu_school.text = eduSchool
                        tv_guide_edu_school.setTextColor(Color.parseColor("#0F0F0F"))
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                        isFinishEdu = true
                    }
                }
                // 拍照返回至裁切
                2 -> {
                    val temp = File(mTempPhotoPath)
                    startPhotoCropActivity(Uri.fromFile(temp))
                }
                3 -> {
                    val temp = File(mTempLifePath)

                    if (!haveFirstPic) {

                        val intent =
                            Intent(this@DetailInfoActivity, LifeIntroduceActivity::class.java)
                        intent.putExtra("path", mTempLifePath)
                        startActivityForResult(intent, 111)

                        lifeMode = "one"
                    } else if (!haveSecondPic) {

                        val intent =
                            Intent(this@DetailInfoActivity, LifeIntroduceActivity::class.java)
                        intent.putExtra("path", mTempLifePath)
                        startActivityForResult(intent, 222)

                        lifeMode = "two"
                    } else if (!haveThirdPic) {

                        val intent =
                            Intent(this@DetailInfoActivity, LifeIntroduceActivity::class.java)
                        intent.putExtra("path", mTempLifePath)
                        startActivityForResult(intent, 333)

                        lifeMode = "three"
                    }

                }
                UCrop.REQUEST_CROP -> {

                    // 此处做一个判断，当是头像、我的生活时，分别走不同的回调

                    if (isPhoto) {
                        if (data != null) {
                            ll_guide_detail_loading.visibility = View.VISIBLE

                            handlePhotoCropResult(data)
                        };
                    } else {

                        if (data != null) {

                            ll_guide_detail_loading.visibility = View.VISIBLE

                            handleLifeCropResult(data, lifeMode)
                        }
                    }

                }
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        handlePhotoCropError(data)
                    }
                }

                111 -> {
                    // 生活第一张图的介绍
                    if (data != null) {
                        lifeFirstPicText = data.getStringExtra("introduce").toString()
                        tv_guide_life_pic_one.text = lifeFirstPicText
                        iv_guide_life_pic_one_icon.visibility = View.GONE
                    }

                }
                222 -> {
                    if (data != null) {
                        lifeSecondPicText = data.getStringExtra("introduce").toString()
                        tv_guide_life_pic_two.text = lifeSecondPicText
                        iv_guide_life_pic_two_icon.visibility = View.GONE
                    }
                }
                333 -> {
                    if (data != null) {
                        lifeThirdPicText = data.getStringExtra("introduce").toString()
                        tv_guide_life_pic_three.text = lifeThirdPicText
                        iv_guide_life_pic_three_icon.visibility = View.GONE
                    }
                }

            }
        }

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

    // 裁剪图片方法实现 ( 我的生活界面 )
    private fun startLifeCropActivity(source: Uri, mLifeDestination: Uri) {
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
        mLifeDestination.let {
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

            map[Contents.ACCESS_TOKEN] =
                "24.50f0594e1d3ff58ff07ac59e645da8da.2592000.1656229858.282335-26330192"
            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
            map[Contents.IMAGE] = bitmapToBase64(bitmap)

            doFaceDetectPresent.doFaceDetect(map)


        } else {
            ToastUtils.showShort("无法剪切选择图片")
        }
    }

    // 处理剪切成功的返回值 ( 我的生活界面 )
    private fun handleLifeCropResult(result: Intent, number: String) {

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


            lifeBitmap = bitmap

            val map: MutableMap<String, String> = TreeMap()
            map[Contents.ACCESS_TOKEN] =
                "24.2e3a38df829e258b2df637acc6bf8c1a.2592000.1656229928.282335-26330258"
            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
            map[Contents.IMAGE] = bitmapToBase64(bitmap)

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

    private fun saveBitmap(bitmap: Bitmap, targetPath: String): String {
        ImageUtils.save(bitmap, targetPath, Bitmap.CompressFormat.PNG)
        return targetPath
    }


    private fun judgeLoading() {
        if (photoCompleteLoad && demandCompleteLoad && moreInfoCompleteLoad && baseInfoCompleteLoad) {
            ToastUtils.showShort("资料全部上传完成，跳转至首页")

            SPStaticUtils.put(Constant.DETAIL_INFO_FINISH, true)

        }
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

    override fun onDoUploadPhotoSuccess(uploadPhotoBean: UploadPhotoBean?) {

        photoCompleteLoad = true

        judgeLoading()

    }

    override fun onDoUploadPhotoError() {

    }

    override fun onDoUpdateDemandInfoSuccess(updateDemandInfoBean: UpdateDemandInfoBean?) {

        demandCompleteLoad = true

        judgeLoading()
    }

    override fun onDoUpdateDemandInfoError() {

    }

    override fun onDoUpdateMoreInfoSuccess(updateMoreInfoBean: UpdateMoreInfoBean?) {

        moreInfoCompleteLoad = true

        judgeLoading()

    }

    override fun onDoUpdateMoreInfoError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {

        baseInfoCompleteLoad = true

        judgeLoading()

    }

    override fun onDoUpdateBaseInfoError() {

    }

    override fun onDoIdentityVerifySuccess(accessTokenBean: AccessTokenBean?) {

        ToastUtils.showShort("身份证验证通过，准备开始传递数据")

        var workProvinceNum = "" // 工作省份编码
        var workCityNum = ""        // 工作城市编码

        if (isJobLocal) {
            workProvinceNum = localCityOneCode
            workCityNum = localCityTwoCode

        } else {
            workProvinceNum = cityIDFirstList[jobCityFirst]
            workCityNum = cityIDSecondList[jobCitySecond]
        }

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 1)


        val baseInfo =
            " {\"education\":${eduPosition + 1}, " +
                    "\"school_name\":      \"$eduSchool\"," +
                    " \"industry_str\":    \"${jobFirstList[jobFirst]}\"," +
                    " \"occupation_num\":    ${jobIDSecondList[jobSecond]}, " +
                    "\"occupation_str\":   \"${jobSecondList[jobSecond]}\", " +
                    "\"work_province_num\":\"$workProvinceNum\"" +
                    " \"work_city_num\":   \"$workCityNum\", " +
                    " \"work_city_str\":     ${citySecondList[jobCitySecond]}, " +
                    " \"hometown\":        \"$incomePosition\"," +
                    "\"salary_range\":       $incomePosition," +
                    "\"introduce_self\":   \"$introduceText\"," +
                    "\"daily_hobbies\":    \"$hobbyText\"," +
                    "\"ta_in_my_mind\":    \"$idealText\"}"

        val moreInfo =
            " {\"user_sex\": $sex, " +
                    "\"love_target\":       $target," +
                    " \"target_show\":    $targetVisibilityPosition}"


        val demandInfo =
            " {\"user_sex\": $sex, " +
                    "\"age_min\":     ${targetAgeMin}," +
                    " \"age_max\":    ${targetAgeMax}}"


        val id = SPStaticUtils.getString(Constant.USER_ID)

        val baseInfoMap: MutableMap<String, String> = TreeMap()
        baseInfoMap[Contents.USER_ID] = id
        baseInfoMap[Contents.BASE_UPDATE] = baseInfo
        updateBaseInfoPresent.doUpdateBaseInfo(baseInfoMap)

        val moreInfoMap: MutableMap<String, String> = TreeMap()
        moreInfoMap[Contents.USER_ID] = id
        moreInfoMap[Contents.MORE_UPDATE] = moreInfo
        updateMoreInfoPresent.doUpdateMoreInfo(moreInfoMap)

        val demandInfoMap: MutableMap<String, String> = TreeMap()
        demandInfoMap[Contents.USER_ID] = id
        demandInfoMap[Contents.DEMAND_UPDATE] = demandInfo
        updateDemandInfoPresent.doUpdateDemandInfo(demandInfoMap)

        // 上传头像
        val uploadPhotoMap: MutableMap<String, String> = TreeMap()
        uploadPhotoMap[Contents.USER_ID] = id
        uploadPhotoMap[Contents.IMAGE_URL] = mPhotoUrl
        uploadPhotoMap[Contents.FILE_TYPE] = "png"
        uploadPhotoMap[Contents.FILE_NAME] = "photoPic.png"
        uploadPhotoMap[Contents.CONTENT] = "0"
        uploadPhotoMap[Contents.KIND] = 1.toString()
        uploadPhotoPresent.doUploadPhoto(uploadPhotoMap)

        if (lifeFirstPicText == "") {
            lifeFirstPicText = 0.toString()
        }

        // 上传第一张生活照
        val uploadLifeMap: MutableMap<String, String> = TreeMap()
        uploadLifeMap[Contents.USER_ID] = id
        uploadLifeMap[Contents.IMAGE_URL] = mLifeFirstUrl
        uploadLifeMap[Contents.FILE_TYPE] = "png"
        uploadLifeMap[Contents.FILE_NAME] = "lifeFirstPic.png"
        uploadLifeMap[Contents.CONTENT] = lifeFirstPicText
        uploadLifeMap[Contents.KIND] = 2.toString()
        uploadPhotoPresent.doUploadPhoto(uploadLifeMap)

        if (haveSecondPic) {

            if (lifeSecondPicText == "") {
                lifeSecondPicText = 0.toString()
            }

            val uploadLifeMap: MutableMap<String, String> = TreeMap()
            uploadLifeMap[Contents.USER_ID] = id
            uploadLifeMap[Contents.IMAGE_URL] = mLifeSecondUrl
            uploadLifeMap[Contents.FILE_TYPE] = "png"
            uploadLifeMap[Contents.FILE_NAME] = "lifeSecondPic.png"
            uploadLifeMap[Contents.CONTENT] = lifeSecondPicText
            uploadLifeMap[Contents.KIND] = 2.toString()
            uploadPhotoPresent.doUploadPhoto(uploadLifeMap)
        }

        if (haveThirdPic) {

            if (lifeThirdPicText == "") {
                lifeThirdPicText = 0.toString()
            }

            val uploadLifeMap: MutableMap<String, String> = TreeMap()
            uploadLifeMap[Contents.USER_ID] = id
            uploadLifeMap[Contents.IMAGE_URL] = mLifeThirdUrl
            uploadLifeMap[Contents.FILE_TYPE] = "png"
            uploadLifeMap[Contents.FILE_NAME] = "lifeThirdPic.png"
            uploadLifeMap[Contents.CONTENT] = lifeThirdPicText
            uploadLifeMap[Contents.KIND] = 2.toString()
            uploadPhotoPresent.doUploadPhoto(uploadLifeMap)
        }

    }

    override fun onDoIdentityVerifyError() {

    }

    override fun onDoFaceDetectSuccess(faceDetectBean: FaceDetectBean) {

        ll_guide_detail_loading.visibility = View.GONE

        if (isPhoto) {

            if (faceDetectBean.conclusion == "合规") {

                iv_photo_container.setImageBitmap(photoBitmap)

                iv_photo_container.setOnClickListener(null)


                tv_photo_reupload.visibility = View.VISIBLE
                iv_photo_delete.visibility = View.VISIBLE
                tv_photo_upload.visibility = View.INVISIBLE

                FileUtils.delete(mPhotoPath)

                photoBitmap?.let { saveBitmap(it, mPhotoPath) }

                isFinishPhoto = true

                tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

            } else {
                ToastUtils.showShort(faceDetectBean.data[0].msg + faceDetectBean.data[0].conclusion)
            }

        } else {

            when (lifeMode) {
                "one" -> {


                    nsv_guide_life_default.visibility = View.GONE
                    nsv_guide_life_pic.visibility = View.VISIBLE

                    rl_guide_life_pic_one.visibility = View.VISIBLE
                    rl_guide_life_pic_more.visibility = View.VISIBLE

                    isFinishLife = true

                    Glide.with(this).load(lifeBitmap).into(iv_guide_life_pic_one)

                    haveFirstPic = true

                    lifeFirstBitmap = lifeBitmap

                    FileUtils.delete(mLifeFirstPath)

                    lifeFirstBitmap?.let { saveBitmap(it, mLifeFirstPath) }


                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    if (faceDetectBean.conclusion != "合规") {
                        ToastUtils.showShort(faceDetectBean.data[0].msg)
                    }

                    val intent = Intent(this, LifeIntroduceActivity::class.java)
                    intent.putExtra("path", mLifeFirstPath)
                    startActivityForResult(intent, 111)

                }
                "two" -> {

                    rl_guide_life_pic_two.visibility = View.VISIBLE

                    isFinishLife = true

                    Glide.with(this).load(lifeBitmap).into(iv_guide_life_pic_two)

                    haveSecondPic = true

                    lifeSecondBitmap = lifeBitmap

                    FileUtils.delete(mLifeSecondPath)

                    lifeSecondBitmap?.let { saveBitmap(it, mLifeSecondPath) }

                    if (faceDetectBean.conclusion != "合规") {
                        ToastUtils.showShort(faceDetectBean.data[0].msg)
                    }

                    val intent = Intent(this, LifeIntroduceActivity::class.java)
                    intent.putExtra("path", mLifeSecondPath)
                    startActivityForResult(intent, 222)

                }
                "three" -> {

                    rl_guide_life_pic_three.visibility = View.VISIBLE

                    rl_guide_life_pic_more.visibility = View.GONE

                    isFinishLife = true

                    Glide.with(this).load(lifeBitmap).into(iv_guide_life_pic_three)

                    haveThirdPic = true

                    lifeThirdBitmap = lifeBitmap

                    FileUtils.delete(mLifeThirdPath)

                    lifeThirdBitmap?.let { saveBitmap(it, mLifeThirdPath) }

                    if (faceDetectBean.conclusion != "合规") {
                        ToastUtils.showShort(faceDetectBean.data[0].msg)
                    }

                    val intent = Intent(this, LifeIntroduceActivity::class.java)
                    intent.putExtra("path", mLifeThirdPath)
                    startActivityForResult(intent, 333)

                }
            }

            if (!haveFirstPic) {

//                val intent = Intent(this@DetailInfoActivity,
//                    LifeIntroduceActivity::class.java)
//                intent.putExtra("path", result[0].realPath)
//                startActivityForResult(intent, 111)

                lifeMode = "one"

            } else if (!haveSecondPic) {

//                val intent = Intent(this@DetailInfoActivity,
//                    LifeIntroduceActivity::class.java)
//                intent.putExtra("path", result[0].realPath)
//                startActivityForResult(intent, 222)

                lifeMode = "two"
            } else if (!haveThirdPic) {

//                val intent = Intent(this@DetailInfoActivity,
//                    LifeIntroduceActivity::class.java)
//                intent.putExtra("path", result[0].realPath)
//                startActivityForResult(intent, 333)

                lifeMode = "three"
            }

        }

    }

    override fun onDoFaceDetectError() {

        ll_guide_detail_loading.visibility = View.GONE

    }

    override fun onGetJobSuccess(jobBean: JobBean) {

        for (i in 0.until(jobBean.data.size)) {

            SPStaticUtils.put("job_second_$i _sum", jobBean.data[i].child.size)
            SPStaticUtils.put("job_second_$i _have", true)

            for (j in 0.until(jobBean.data[i].child.size)) {

                if (j == 0) {
                    jobSecondList.add(jobBean.data[0].child[j].name)
                    jobIDSecondList.add(jobBean.data[0].child[j].id)
                }

                SPStaticUtils.put("job_second_$i _id_$j", jobBean.data[i].child[j].id)
                SPStaticUtils.put("job_second_$i _name_$j", jobBean.data[i].child[j].name)

            }

        }

        SPStaticUtils.put(Constant.JOB_HAVE, true)

    }

    override fun onGetJobError() {

    }

    override fun onGetIndustrySuccess(industryBean: IndustryBean) {
        SPStaticUtils.put(Constant.INDUSTRY_SUM, industryBean.data.size)
        for (i in 0.until(industryBean.data.size)) {
            SPStaticUtils.put("industry_item_id_$i", industryBean.data[i].id)
            jobIDFirstList.add(industryBean.data[i].id)
            SPStaticUtils.put("industry_item_name_$i", industryBean.data[i].name)
            jobFirstList.add(industryBean.data[i].name)
            mIndustryData.add(industryBean.data[i])
        }
        SPStaticUtils.put(Constant.INDUSTRY_HAVE, true)
    }

    override fun onGetIndustryError() {

    }

    inner class TextViewSpan1 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            //点击事件
            val intent = Intent(this@DetailInfoActivity, IdentityActivity::class.java)
            startActivity(intent)
        }
    }

    inner class TextViewSpan2 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            //点击事件
            ToastUtils.showShort("此处显示人脸识别的隐私政策")
        }
    }


    inner class LoadingDialog(context: Context) : FullScreenPopupView(context) {

        override fun getInnerLayoutId(): Int = R.layout.dialog_loading

    }

//---------------------- 教育信息 ----------------------

    inner class EduDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_info_edu

        override fun onCreate() {
            super.onCreate()

            findViewById<ImageView>(R.id.iv_edu_edu_close).setOnClickListener {
                dismiss()
            }

            val x = findViewById<WheelPicker>(R.id.wp_edu_edu_container)

            eduPosition = 0

            x.selectedItemPosition = eduPosition

            x.data = mEduData
            x.setOnItemSelectedListener { picker, data, position ->
                eduPosition = position
            }

            // 是否为循环状态
            x.isCyclic = false

            // 当前选中的数据项文本颜色
            x.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            x.itemTextColor = Color.parseColor("#9A9A9A")
//            // 设置数据项文本尺寸大小
//            x.itemTextSize = ConvertUtils.dp2px(17F)
            // 滚轮选择器数据项之间间距
            x.itemSpace = ConvertUtils.dp2px(40F)

            // 是否有指示器
            x.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            x.indicatorColor = Color.parseColor("#FFF5F5")

            // 滚轮选择器是否显示幕布
            x.setCurtain(true)

            // 滚轮选择器是否有空气感
            x.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            x.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            x.itemAlign = WheelPicker.ALIGN_CENTER


            findViewById<TextView>(R.id.tv_edu_edu_confirm).setOnClickListener {
                dismiss()
                tv_guide_edu_edu.text = mEduData[eduPosition]
                tv_guide_edu_edu.setTextColor(Color.parseColor("#0F0F0F"))

                if (eduPosition != 0) {
                    tv_guide_edu_school_text.visibility = View.VISIBLE
                    rl_guide_edu_school.visibility = View.VISIBLE

                    if (eduSchool == "") {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        isFinishEdu = false
                    }

                } else {
                    tv_guide_edu_school_text.visibility = View.GONE
                    rl_guide_edu_school.visibility = View.GONE
                    eduSchool = ""
                    isFinishEdu = true

                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                }

            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

//---------------------- 职业收入情况 ----------------------

    inner class IncomeJobDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_info_income_job

        override fun onCreate() {
            super.onCreate()

            val one = findViewById<WheelPicker>(R.id.wp_income_job_first_container)
            val two = findViewById<WheelPicker>(R.id.wp_income_job_second_container)

            one.data = jobFirstList
            two.data = jobSecondList

            jobFirst = 0
            jobSecond = 0

            // 是否为循环状态
            one.isCyclic = false
            // 当前选中的数据项文本颜色
            one.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            one.itemTextColor = Color.parseColor("#9A9A9A")
//            // 设置数据项文本尺寸大小
//            one.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            one.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            one.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            one.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            one.setCurtain(true)
            // 滚轮选择器是否有空气感
            one.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            one.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            one.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            two.isCyclic = false
            // 当前选中的数据项文本颜色
            two.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            two.itemTextColor = Color.parseColor("#9A9A9A")
//            // 设置数据项文本尺寸大小
//            two.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            two.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            two.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            two.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            two.setCurtain(true)
            // 滚轮选择器是否有空气感
            two.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            two.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            two.itemAlign = WheelPicker.ALIGN_CENTER

            one.setOnItemSelectedListener { picker, data, position ->
                jobFirst = position

                getJobSecondList(jobFirst)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (jobSecond >= jobSecondList.size) {
                    jobSecond = jobSecondList.size - 1
                }

                two.data = jobSecondList

            }

            two.setOnItemSelectedListener { picker, data, position ->

                jobSecond = position

            }


            findViewById<ImageView>(R.id.iv_income_job_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_income_job_confirm).setOnClickListener {

                chooseJob = true

                tv_guide_income_job.text =
                    "${jobFirstList[jobFirst]}-${jobSecondList[jobSecond]}"

                tv_guide_income_job.setTextColor(Color.parseColor("#0F0F0F"))

                if (chooseJob && chooseIncome) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                }

                dismiss()

            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    inner class IncomeIncomeDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_info_income_income

        override fun onCreate() {
            super.onCreate()

            val incomePicker = findViewById<WheelPicker>(R.id.wp_income_job_income_container)

            incomePicker.data = incomeData

            // 是否为循环状态
            incomePicker.isCyclic = false
            // 当前选中的数据项文本颜色
            incomePicker.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            incomePicker.itemTextColor = Color.parseColor("#9A9A9A")
//            // 设置数据项文本尺寸大小
//            incomePicker.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            incomePicker.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            incomePicker.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            incomePicker.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            incomePicker.setCurtain(true)
            // 滚轮选择器是否有空气感
            incomePicker.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            incomePicker.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            incomePicker.itemAlign = WheelPicker.ALIGN_CENTER

            incomePicker.setOnItemSelectedListener { picker, data, position ->
                incomePosition = position
            }

            findViewById<ImageView>(R.id.iv_income_income_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_income_income_confirm).setOnClickListener {

                tv_guide_income_income.text = incomeData[incomePosition]

                tv_guide_income_income.setTextColor(Color.parseColor("#0F0F0F"))

                chooseIncome = true

                if (chooseJob && chooseIncome) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                }

                dismiss()

            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

// -------------------  居住地和家乡界面  -----------------

    inner class AddressJobCity(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_info_address_jobcity

        override fun onCreate() {
            super.onCreate()

            val one = findViewById<WheelPicker>(R.id.wp_address_jobcity_first_container)
            val two = findViewById<WheelPicker>(R.id.wp_address_jobcity_second_container)
            val three = findViewById<WheelPicker>(R.id.wp_address_jobcity_third_container)

            one.data = cityFirstList
            two.data = citySecondList
            three.data = cityThirdList

            jobCityFirst = 0
            jobCitySecond = 0
            jobCityThird = 0

            getJobCitySecondList(0)
            getJobCityThirdList(0, 0)

            // 是否为循环状态
            one.isCyclic = false
            // 当前选中的数据项文本颜色
            one.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            one.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
//            one.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            one.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            one.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            one.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            one.setCurtain(true)
            // 滚轮选择器是否有空气感
            one.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            one.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            one.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            two.isCyclic = false
            // 当前选中的数据项文本颜色
            two.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            two.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
//            two.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            two.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            two.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            two.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            two.setCurtain(true)
            // 滚轮选择器是否有空气感
            two.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            two.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            two.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            three.isCyclic = false
            // 当前选中的数据项文本颜色
            three.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            three.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
//            three.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            three.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            three.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            three.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            three.setCurtain(true)
            // 滚轮选择器是否有空气感
            three.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            three.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            three.itemAlign = WheelPicker.ALIGN_CENTER

            one.setOnItemSelectedListener { picker, data, position ->
                jobCityFirst = position

                getJobCitySecondList(jobCityFirst)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (jobCitySecond >= citySecondList.size) {
                    jobCitySecond = citySecondList.size - 1
                }

                getJobCityThirdList(jobCityFirst, jobCitySecond)

                two.data = citySecondList
                three.data = cityThirdList

            }

            two.setOnItemSelectedListener { picker, data, position ->

                jobCitySecond = position

                getJobCityThirdList(jobCityFirst, jobCitySecond)

                three.data = cityThirdList

            }

            three.setOnItemSelectedListener { picker, data, position ->

                jobCityThird = position

            }


            findViewById<ImageView>(R.id.iv_address_jobcity_close).setOnClickListener {
                dismiss()
            }

            val localtion = findViewById<LinearLayout>(R.id.ll_address_jobcity_location)
            val detail = findViewById<LinearLayout>(R.id.ll_address_jobcity_location_detail)
            val city = findViewById<TextView>(R.id.tv_address_jobcity_location_detail)

            findViewById<LinearLayout>(R.id.ll_address_jobcity_location).setOnClickListener {

                XXPermissions.with(this@DetailInfoActivity)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                            val mLocationClient = LocationClient(context)

                            mLocationClient.registerLocationListener(object :
                                BDAbstractLocationListener() {
                                override fun onReceiveLocation(location: BDLocation) {
                                    Log.i("guo", "定位开始")
                                    localCityOne = location.province
                                    localCityTwo = location.city
                                    localCityThree = location.district

                                    for (i in 0.until(cityFirstList.size)) {
                                        if (cityFirstList[i] == location.province) {
                                            localCityOneCode = cityIDFirstList[i]
                                        }
                                        getJobCitySecondList(i)
                                        for (j in 0.until(citySecondList.size)) {
                                            if (citySecondList[j] == location.city) {
                                                localCityTwoCode = cityIDSecondList[j]
                                            }
                                        }
                                    }


                                    if (localtion != null) {
                                        localtion.visibility = View.GONE
                                    }
                                    if (detail != null) {
                                        detail.visibility = View.VISIBLE
                                    }

                                    if (city != null) {
                                        city.text = localCityTwo
                                    }

                                    chooseJobCity = true

                                    isJobLocal = true

                                    tv_guide_address_jobcity.text = localCityTwo

                                    tv_guide_address_jobcity.setTextColor(Color.parseColor("#0F0F0F"))

                                    if (chooseJobCity && chooseHomeCity) {
                                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                                    } else {
                                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                                    }

                                    dismiss()
                                }
                            })
                            val option = LocationClientOption()
                            option.setIsNeedAddress(true);
                            option.setNeedNewVersionRgc(true);
                            mLocationClient.locOption = option;
                            mLocationClient.start()


                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予用户所需权限")
                        }

                    })

            }

            detail.setOnClickListener {
                localtion.visibility = View.VISIBLE
                detail.visibility = View.GONE

                chooseJobCity = false

                isJobLocal = false

            }


            findViewById<TextView>(R.id.tv_address_jobcity_confirm).setOnClickListener {

                chooseJobCity = true

                isJobLocal = false

                if (cityThirdList[jobCityThird] == "") {
                    tv_guide_address_jobcity.text =
                        "${cityFirstList[jobCityFirst]}-${citySecondList[jobCitySecond]}"
                } else {
                    tv_guide_address_jobcity.text =
                        "${cityFirstList[jobCityFirst]}-${citySecondList[jobCitySecond]}-${cityThirdList[jobCityThird]}"
                }

                tv_guide_address_jobcity.setTextColor(Color.parseColor("#0F0F0F"))

                if (chooseJobCity && chooseHomeCity) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                }

                dismiss()

            }

        }

    }

    inner class AddressHomeCity(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_info_address_jobcity

        override fun onCreate() {
            super.onCreate()

            val one = findViewById<WheelPicker>(R.id.wp_address_jobcity_first_container)
            val two = findViewById<WheelPicker>(R.id.wp_address_jobcity_second_container)
            val three = findViewById<WheelPicker>(R.id.wp_address_jobcity_third_container)

            one.data = cityFirstList
            two.data = citySecondList
            three.data = cityThirdList

            jobCityFirst = 0
            jobCitySecond = 0
            jobCityThird = 0

            getJobCitySecondList(0)
            getJobCityThirdList(0, 0)

            // 是否为循环状态
            one.isCyclic = false
            // 当前选中的数据项文本颜色
            one.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            one.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
//            one.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            one.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            one.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            one.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            one.setCurtain(true)
            // 滚轮选择器是否有空气感
            one.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            one.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            one.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            two.isCyclic = false
            // 当前选中的数据项文本颜色
            two.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            two.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
//            two.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            two.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            two.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            two.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            two.setCurtain(true)
            // 滚轮选择器是否有空气感
            two.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            two.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            two.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            three.isCyclic = false
            // 当前选中的数据项文本颜色
            three.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            three.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
//            three.itemTextSize = ConvertUtils.dp2px(10F)
            // 滚轮选择器数据项之间间距
            three.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            three.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            three.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            three.setCurtain(true)
            // 滚轮选择器是否有空气感
            three.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            three.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            three.itemAlign = WheelPicker.ALIGN_CENTER

            one.setOnItemSelectedListener { picker, data, position ->
                jobCityFirst = position

                getJobCitySecondList(jobCityFirst)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (jobCitySecond >= citySecondList.size) {
                    jobCitySecond = citySecondList.size - 1
                }

                getJobCityThirdList(jobCityFirst, jobCitySecond)

                two.data = citySecondList
                three.data = cityThirdList

            }

            two.setOnItemSelectedListener { picker, data, position ->

                jobCitySecond = position

                getJobCityThirdList(jobCityFirst, jobCitySecond)

                three.data = cityThirdList

            }

            three.setOnItemSelectedListener { picker, data, position ->

                jobCityThird = position

            }


            findViewById<ImageView>(R.id.iv_address_jobcity_close).setOnClickListener {
                dismiss()
            }


            val localtion = findViewById<LinearLayout>(R.id.ll_address_jobcity_location)
            val detail = findViewById<LinearLayout>(R.id.ll_address_jobcity_location_detail)
            val city = findViewById<TextView>(R.id.tv_address_jobcity_location_detail)

            findViewById<LinearLayout>(R.id.ll_address_jobcity_location).setOnClickListener {

                XXPermissions.with(this@DetailInfoActivity)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(permissions: MutableList<String>?, all: Boolean) {

                            val mLocationClient = LocationClient(context)

                            mLocationClient.registerLocationListener(object :
                                BDAbstractLocationListener() {
                                override fun onReceiveLocation(location: BDLocation) {
                                    Log.i("guo", "定位开始")
                                    localCityOne = location.province
                                    localCityTwo = location.city
                                    localCityThree = location.district

                                    for (i in 0.until(cityFirstList.size)) {
                                        if (cityFirstList[i] == location.province) {
                                            localCityOneCode = cityIDFirstList[i]
                                        }
                                        getJobCitySecondList(i)
                                        for (j in 0.until(citySecondList.size)) {
                                            if (citySecondList[j] == location.city) {
                                                localCityTwoCode = cityIDSecondList[j]
                                            }
                                        }
                                    }


                                    localtion.visibility = View.GONE
                                    detail.visibility = View.VISIBLE

                                    city.text = localCityTwo

                                    chooseHomeCity = true

                                    isHomeLocal = true

                                    tv_guide_address_homecity.text = localCityTwo

                                    tv_guide_address_homecity.setTextColor(Color.parseColor("#0F0F0F"))

                                    if (chooseJobCity && chooseHomeCity) {
                                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                                    } else {
                                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                                    }

                                    dismiss()

                                }
                            })
                            val option = LocationClientOption()
                            option.setIsNeedAddress(true);
                            option.setNeedNewVersionRgc(true);
                            mLocationClient.locOption = option;
                            mLocationClient.start()

                        }

                        override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                            super.onDenied(permissions, never)
                            ToastUtils.showShort("请授予用户所需权限")
                        }

                    })

            }

            detail.setOnClickListener {
                localtion.visibility = View.VISIBLE
                detail.visibility = View.GONE

                chooseHomeCity = false

                isHomeLocal = false

            }

            findViewById<TextView>(R.id.tv_address_jobcity_confirm).setOnClickListener {

                chooseHomeCity = true

                isHomeLocal = false


                if (cityThirdList[jobCityThird] == "") {
                    tv_guide_address_homecity.text =
                        "${cityFirstList[jobCityFirst]}-${citySecondList[jobCitySecond]}"
                } else {
                    tv_guide_address_homecity.text =
                        "${cityFirstList[jobCityFirst]}-${citySecondList[jobCitySecond]}-${cityThirdList[jobCityThird]}"
                }

                tv_guide_address_homecity.setTextColor(Color.parseColor("#0F0F0F"))

                if (chooseJobCity && chooseHomeCity) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                }

                dismiss()

            }

        }

    }

    inner class MyLocationListener : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            val addr = location.addrStr //获取详细地址信息
            val country = location.country //获取国家
            val province = location.province //获取省份
            val city = location.city //获取城市
            val district = location.district //获取区县
            val street = location.street //获取街道信息
            val adcode = location.adCode //获取adcode
            val town = location.town //获取乡镇信息

            Log.i("guo", location.city)

            localCityOne = location.province
            localCityTwo = location.city
            localCityThree = location.district

            for (i in 0.until(cityFirstList.size)) {

                if (cityFirstList[i] == location.province) {
                    localCityOneCode = cityIDFirstList[i]
                }

                getJobCitySecondList(i)

                for (j in 0.until(citySecondList.size)) {
                    if (citySecondList[j] == location.city) {
                        localCityTwoCode = cityIDSecondList[j]
                    }
                }
            }

        }
    }


// -------------------  择偶条件界面  -----------------

    inner class TargetVisibilityDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_info_target_visibility

        override fun onCreate() {
            super.onCreate()

            val incomePicker = findViewById<WheelPicker>(R.id.wp_target_visibility_container)

            incomePicker.data = targetVisibilityList

            // 是否为循环状态
            incomePicker.isCyclic = false
            // 当前选中的数据项文本颜色
            incomePicker.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            incomePicker.itemTextColor = Color.parseColor("#9A9A9A")
            // 设置数据项文本尺寸大小
            incomePicker.itemTextSize = ConvertUtils.dp2px(17F)
            // 滚轮选择器数据项之间间距
            incomePicker.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            incomePicker.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            incomePicker.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            incomePicker.setCurtain(true)
            // 滚轮选择器是否有空气感
            incomePicker.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            incomePicker.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            incomePicker.itemAlign = WheelPicker.ALIGN_CENTER


            incomePicker.setOnItemSelectedListener { picker, data, position ->
                targetVisibilityPosition = position
            }

            findViewById<ImageView>(R.id.iv_target_visibility_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_target_visibility_confirm).setOnClickListener {

                tv_guide_target_visibility.text = targetVisibilityList[targetVisibilityPosition]
                dismiss()

            }
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

                isPhoto = true

                dismiss()

                val selectorStyle = PictureSelectorStyle()
                val animationStyle = PictureWindowAnimationStyle()
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
                selectorStyle.windowAnimationStyle = animationStyle

                PictureSelector.create(this@DetailInfoActivity)
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

                isPhoto = true

                dismiss()

                XXPermissions.with(this@DetailInfoActivity)
                    .permission(Permission.CAMERA)
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
                                    FileProvider.getUriForFile(context,
                                        authority,
                                        tempPhotoFile)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                            } else {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(tempPhotoFile))
                            }
                            startActivityForResult(intent, 2)
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

// -------------------  我的生活界面  -----------------

    inner class LifeGuidDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_life_guide

        override fun onCreate() {
            super.onCreate()

            val assetManager = context.assets

            val lifeOne: InputStream = assetManager.open("pic/pic_guide_life_one.png")
            val lifeTwo: InputStream = assetManager.open("pic/pic_guide_life_two.png")
            val lifeThree: InputStream = assetManager.open("pic/pic_guide_life_three.png")

            findViewById<ImageView>(R.id.iv_dialog_life_pic_one).background =
                BitmapDrawable(BitmapFactory.decodeStream(lifeOne))
            findViewById<ImageView>(R.id.iv_dialog_life_pic_two).background =
                BitmapDrawable(BitmapFactory.decodeStream(lifeTwo))
            findViewById<ImageView>(R.id.iv_dialog_life_pic_three).background =
                BitmapDrawable(BitmapFactory.decodeStream(lifeThree))

            findViewById<ImageView>(R.id.iv_dialog_life_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_life_camera).setOnClickListener {
                ToastUtils.showShort("直接打开相机")
                isPhoto = false

                dismiss()

                XXPermissions.with(this@DetailInfoActivity)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            val tempPhotoFile: File = File(mTempLifePath)
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            // 如果在Android7.0以上,使用FileProvider获取Uri
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                val authority = context.packageName.toString() + ".fileProvider"
                                val contentUri: Uri =
                                    FileProvider.getUriForFile(context,
                                        authority,
                                        tempPhotoFile)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                            } else {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(tempPhotoFile))
                            }

                            startActivityForResult(intent, 3)
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

            findViewById<TextView>(R.id.tv_dialog_life_album).setOnClickListener {
                ToastUtils.showShort("打开相册")
                isPhoto = false

                dismiss()

                val selectorStyle = PictureSelectorStyle()
                val animationStyle = PictureWindowAnimationStyle()
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
                selectorStyle.windowAnimationStyle = animationStyle

                PictureSelector.create(this@DetailInfoActivity)
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
                        override fun onResult(result: ArrayList<LocalMedia>) {

                            lifeChoosePath = result[0].realPath

                            val map: MutableMap<String, String> = TreeMap()
                            map[Contents.ACCESS_TOKEN] =
                                "24.2e3a38df829e258b2df637acc6bf8c1a.2592000.1656229928.282335-26330258"
                            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                            map[Contents.IMAGE] = bitmapToBase64(ImageUtils.getBitmap(result[0].realPath))

                            doFaceDetectPresent.doFaceDetect(map)


                        }

                        override fun onCancel() {
                        }

                    })

            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

// -------------------  关于我界面  -----------------


// -------------------  我的爱好界面  -----------------


// -------------------  我心目中的TA界面  -----------------


// ------------------- 实名认证界面  -----------------


}


