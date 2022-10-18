package com.twx.marryfriend.guide.detailInfo

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
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import com.aigestudio.wheelpicker.WheelPicker
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.baidu.idl.face.platform.listener.IInitCallback
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
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
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.guide.baseInfo.step.RegisterStep
import com.twx.marryfriend.guide.detailInfo.artificial.IdentityActivity
import com.twx.marryfriend.guide.detailInfo.life.LifeIntroduceActivity
import com.twx.marryfriend.guide.detailInfo.search.SchoolSearchActivity
import com.twx.marryfriend.guide.detailInfo.step.*
import com.twx.marryfriend.login.retrieve.QualityConfig
import com.twx.marryfriend.login.retrieve.QualityConfigManager
import com.twx.marryfriend.login.retrieve.activity.FaceLivenessExpActivity
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.net.callback.*
import com.twx.marryfriend.net.impl.*
import com.twx.marryfriend.set.feedback.FeedbackActivity
import com.twx.marryfriend.set.web.SetWebActivity
import com.twx.marryfriend.utils.BitmapUtil
import com.twx.marryfriend.utils.GlideEngine
import com.twx.marryfriend.view.DoubleSlideSeekBar
import com.umeng.analytics.MobclickAgent
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_detail_info.*
import kotlinx.android.synthetic.main.activity_life_photo.*
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
import kotlin.math.log


class DetailInfoActivity : MainBaseViewActivity(), IGetIndustryCallback, IGetJobCallback,
    IDoFaceDetectCallback, IDoIdentityVerifyCallback, IDoUpdateBaseInfoCallback,
    IDoUpdateMoreInfoCallback, IDoUpdateDemandInfoCallback, IDoUploadPhotoCallback,
    IDoTextVerifyCallback, IDoDeletePhotoCallback, IDoUploadAvatarCallback {

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

    private lateinit var mLocationClient: LocationClient

    // 是否选择工作居住地
    private var chooseJobCity = false

    // 是否选择家乡
    private var chooseHomeCity = false

    // 定位省份
    private var localCityOne = ""

    // 定位省份编码
    private var localCityOneCode = 0

    // 定位城市
    private var localCityTwo = ""

    // 定位城市编码
    private var localCityTwoCode = 0

    // 定位县
    private var localCityThree = ""


    // 工作省份编码
    private var jobProvinceCode = 0

    // 工作省份名字
    private var jobProvinceName = ""

    // 工作城市编码
    private var jobCityCode = 0

    // 工作城市名字
    private var jobCityName = ""

    // 家乡省份编码
    private var homeProvinceCode = 0

    // 家乡省份名字
    private var homeProvinceName = ""

    // 家乡城市编码
    private var homeCityCode = 0

    // 家乡城市名字
    private var homeCityName = ""

    // 用户工作居住地
    private var jobCityFirst = 0
    private var jobCitySecond = 0
    private var jobCityThird = 0

    //  用户家乡
    private var home = ""

    // 用户家乡
    private var homeCityFirst = 0
    private var homeCitySecond = 0
    private var homeCityThird = 0


    // 城市json数据
    private var cityJsonDate = ""

    // 城市数据
    private lateinit var cityDate: CityBean

    // 弹窗第一组数据
    private var cityFirstList: MutableList<String> = arrayListOf()
    private var cityIDFirstList: MutableList<Int> = arrayListOf()

    // 弹窗第二组数据
    private var citySecondList: MutableList<String> = arrayListOf()
    private var cityIDSecondList: MutableList<Int> = arrayListOf()

    // -------------------  择偶条件界面  -----------------

    // 恋爱目标可见情况
    private var targetVisibilityPosition = 0
    private var targetVisibilityList: MutableList<String> = arrayListOf()

    // 恋爱目标
    private var target = 0

    //恋爱目标是否选择
    private var chooseTarget = false

    // 期望年龄
    private var targetAgeMin = 18
    private var targetAgeMax = 60

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

    // 选择的删除键是第几个删除
    private var lifeDeleteMode = "one"

    // 是通过相机选择生活照，还是通过相册选择
    private var isCamera = true

    // 临时图片文件路径
    private var mTempLifePath = ""

    // 第一张我的生活照
    private var mLifeFirstPath = ""

    // 第一张我的生活照上传百度云的url
    private var mLifeFirstUrl = ""

    // 第一张我的生活照Id
    private var mLifeFirstId = ""

    // 第一张我的生活照介绍
    private var mLifeFirstText = ""

    // 第一张我的生活照审核状态
    private var mLifeFirstState = ""

    // 是否存在
    private var haveFirstPic = false


    // 第二张我的生活照
    private var mLifeSecondPath = ""

    // 第二张我的生活照上传百度云的url
    private var mLifeSecondUrl = ""

    // 第二张我的生活照Id
    private var mLifeSecondId = ""

    // 第二张我的生活照介绍
    private var mLifeSecondText = ""

    // 第二张我的生活照审核状态
    private var mLifeSecondState = ""

    // 是否存在
    private var haveSecondPic = false


    // 第三张我的生活照
    private var mLifeThirdPath = ""

    // 第三张我的生活照上传百度云的url
    private var mLifeThirdUrl = ""

    // 第三张我的生活照Id
    private var mLifeThirdId = ""

    // 第三张我的生活照介绍
    private var mLifeThirdText = ""

    // 第三张我的生活照审核状态
    private var mLifeThirdState = ""

    // 是否存在
    private var haveThirdPic = false


    // 第四张我的生活照
    private var mLifeFourPath = ""

    // 第四张我的生活照上传百度云的url
    private var mLifeFourUrl = ""

    // 第四张我的生活照Id
    private var mLifeFourId = ""

    // 第四张我的生活照介绍
    private var mLifeFourText = ""

    // 第四张我的生活照审核状态
    private var mLifeFourState = ""

    // 是否存在
    private var haveFourPic = false


    // 第五张我的生活照
    private var mLifeFivePath = ""

    // 第五张我的生活照上传百度云的url
    private var mLifeFiveUrl = ""

    // 第五张我的生活照Id
    private var mLifeFiveId = ""

    // 第五张我的生活照介绍
    private var mLifeFiveText = ""

    // 第五张我的生活照审核状态
    private var mLifeFiveState = ""

    // 是否存在
    private var haveFivePic = false


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

    // 是否完成文字校验
    private var isCompleteIntroduce = false
    private var isCompleteHobby = false
    private var isCompleteIdeal = false

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

    private lateinit var doUploadAvatarPresent: doUploadAvatarPresentImpl

    private lateinit var uploadPhotoPresent: doUploadPhotoPresentImpl

    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

    private lateinit var doDeletePhotoPresent: doDeletePhotoPresentImpl

    // 百度人脸识别动作
    private var livenessList: MutableList<LivenessTypeEnum> = ArrayList()

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

        doUploadAvatarPresent = doUploadAvatarPresentImpl.getsInstance()
        doUploadAvatarPresent.registerCallback(this)

        uploadPhotoPresent = doUploadPhotoPresentImpl.getsInstance()
        uploadPhotoPresent.registerCallback(this)

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

        doDeletePhotoPresent = doDeletePhotoPresentImpl.getsInstance()
        doDeletePhotoPresent.registerCallback(this)


        //进入教育信息页
        MobclickAgent.onEvent(this, "10018_education");


        mLocationClient = LocationClient(this)

        val str1 = "遇到问题？联系客服"
        val stringBuilder1 = SpannableStringBuilder(str1)
        val span1 = TextViewSpan1()
        stringBuilder1.setSpan(span1, 5, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tv_guide_detail_service.text = stringBuilder1
        tv_guide_detail_service.movementMethod = LinkMovementMethod.getInstance()


        val str2 = "详见《服务协议》"
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

        xx = XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
            .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(AddressJobCity(this))

    }

    override fun initLoadData() {
        super.initLoadData()

        cityJsonDate = SPStaticUtils.getString(Constant.CITY_JSON_DATE)
        cityDate = GsonUtils.fromJson(cityJsonDate, CityBean::class.java)

        Log.i("guo", "cityDate :$cityDate")

        updateLifePhoto()

        mEduData.add("大专以下")
        mEduData.add("大专")
        mEduData.add("本科")
        mEduData.add("硕士")
        mEduData.add("博士")
        mEduData.add("博士以上")

        targetVisibilityList.add("所有人可见")
        targetVisibilityList.add("目标相同的人可见")
        targetVisibilityList.add("不公开")

        mTempPhotoPath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"
        mDestination = Uri.fromFile(File(this.cacheDir, "photoCropImage.jpeg"))

        mPhotoPath = externalCacheDir.toString() + File.separator + "head.png"

        mTempLifePath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "life.jpeg"

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

        getJobCityFirstList()
        getJobCitySecondList(0)


        var minAge = if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
            SPStaticUtils.getInt(Constant.ME_AGE, 18) - 8
        } else {
            SPStaticUtils.getInt(Constant.ME_AGE, 18) - 4
        }

        var maxAge = if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
            SPStaticUtils.getInt(Constant.ME_AGE, 18) + 4
        } else {
            SPStaticUtils.getInt(Constant.ME_AGE, 18) + 8
        }

        if (minAge < 18) {
            minAge = 18
        }

        if (maxAge > 60) {
            maxAge = 60
        }

        // 设置默认目标年龄
        targetAgeMin = minAge
        targetAgeMax = maxAge

        tv_guide_target_age.text = "${targetAgeMin}~${targetAgeMax}岁"

        SPStaticUtils.put(Constant.TA_AGE_MIN, targetAgeMin)
        SPStaticUtils.put(Constant.TA_AGE_MAX, targetAgeMax)

    }

    override fun initPresent() {
        super.initPresent()

        val config: BosClientConfiguration = BosClientConfiguration()
        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://adrmf.gz.bcebos.com"
        client = BosClient(config)

        initLicense()

    }

    override fun initEvent() {
        super.initEvent()

        tv_guide_detail_skip.setOnClickListener {
            ToastUtils.showShort("跳过整个流程，前往主页")


            when (vf_guide_detail_container.displayedChild) {
                0 -> {
                    //教育信息页点击跳过
                    MobclickAgent.onEvent(this, "10019_education_skip");
                }
                1 -> {
                    //职业收入情况页面点击跳过
                    MobclickAgent.onEvent(this, "10024_occupation_skip");
                }
                2 -> {
                    //居住城市和家乡页面点击跳过
                    MobclickAgent.onEvent(this, "10027_hometown_skip");
                }
                3 -> {
                    //我的恋爱目标页面点击跳过
                    MobclickAgent.onEvent(this, "10030_love_goal_skip");
                }
                4 -> {
                    //上传头像页面点击跳过
                    MobclickAgent.onEvent(this, "10033_upload_avatar_skip");
                }
                5 -> {
                    //我的生活页面点击跳过
                    MobclickAgent.onEvent(this, "10036_my_life_skip");
                }
                6 -> {
                    //关于我页面点击跳过
                    MobclickAgent.onEvent(this, "10039_about_me_skip");
                }
                7 -> {
                    //我的爱好页面点击跳过
                    MobclickAgent.onEvent(this, "10042_hobby_skip");
                }
                8 -> {
                    //我心目中的ta页面点击跳过
                    MobclickAgent.onEvent(this, "10045_hobby_skip");
                }
                9 -> {
                    //实名认证页面点击跳过
                    MobclickAgent.onEvent(this, "10048_real_name_skip");
                }
            }

            SPStaticUtils.put(Constant.DETAIL_INFO_FINISH, true)

            startActivity(Intent(this, MainActivity::class.java))
            this.finish()
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

                        //填写完教育信息点击下一步
                        MobclickAgent.onEvent(this, "10022_education_next");

                        vf_guide_detail_container.showNext()

                        //进入职业收入情况页面
                        MobclickAgent.onEvent(this, "10023_occupation");

                        tsb_guide_detail_guide.setPercent(0.19f, "19")

                        SPStaticUtils.put(Constant.ME_EDU, eduPosition + 1)
                        SPStaticUtils.put(Constant.ME_SCHOOL, eduSchool)

                    } else {
                        ToastUtils.showShort("请完善您的学历信息")
                    }

                }
                1 -> {

                    if (chooseJob && chooseIncome) {

                        if (!(chooseJobCity && chooseHomeCity)) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //职业收入情况页面填写数据点击下一步
                        MobclickAgent.onEvent(this, "10025_occupation_next");

                        vf_guide_detail_container.showNext()

                        //进入居住城市和家乡页面
                        MobclickAgent.onEvent(this, "10026_hometown");

                        tsb_guide_detail_guide.setPercent(0.26f, "26")

                        SPStaticUtils.put(Constant.ME_INDUSTRY_CODE, jobIDFirstList[jobFirst])
                        SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, jobFirstList[jobFirst])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_CODE, jobIDSecondList[jobSecond])
                        SPStaticUtils.put(Constant.ME_OCCUPATION_NAME, jobSecondList[jobSecond])

                        SPStaticUtils.put(Constant.ME_INCOME, incomePosition + 1)

                    } else {
                        if (!chooseJob) {
                            ToastUtils.showShort("请填写您的职业名称")
                        } else {
                            if (!chooseIncome) {
                                ToastUtils.showShort("请填写您的月收入")
                            }
                        }
                    }

                }
                2 -> {

                    if (chooseJobCity && chooseHomeCity) {

                        if (!(chooseTarget && chooseAge)) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //居住城市和家乡页面填写资料点击下一步
                        MobclickAgent.onEvent(this, "10028_hometown_next");

                        vf_guide_detail_container.showNext()

                        //进入我的恋爱目标页面
                        MobclickAgent.onEvent(this, "10029_love_goal");


                        tsb_guide_detail_guide.setPercent(0.39f, "39")

                        if (isJobLocal) {
                            // 定位数据
                            SPStaticUtils.put(Constant.ME_WORK_PROVINCE_CODE, localCityOneCode)
                            SPStaticUtils.put(Constant.ME_WORK_CITY_CODE, localCityTwoCode)
                            SPStaticUtils.put(Constant.ME_WORK_CITY_NAME, localCityTwo)

                        } else {
                            // 选择数据
                            SPStaticUtils.put(Constant.ME_WORK_PROVINCE_CODE, jobProvinceCode)
                            SPStaticUtils.put(Constant.ME_WORK_PROVINCE_NAME, jobProvinceName)
                            SPStaticUtils.put(Constant.ME_WORK_CITY_CODE, jobCityCode)
                            SPStaticUtils.put(Constant.ME_WORK_CITY_NAME, jobCityName)
                        }

                        SPStaticUtils.put(Constant.ME_HOME_PROVINCE_CODE, homeProvinceCode)
                        SPStaticUtils.put(Constant.ME_HOME_PROVINCE_NAME, homeProvinceName)
                        SPStaticUtils.put(Constant.ME_HOME_CITY_CODE, homeCityCode)
                        SPStaticUtils.put(Constant.ME_HOME_CITY_NAME, homeCityName)

                        val task: TimerTask = object : TimerTask() {
                            override fun run() {
                                // 18 设置为 0开始点
                                dssb_guide_target_age.setMin(targetAgeMin - 18)
                                dssb_guide_target_age.setMax(targetAgeMax - 18)
                            }
                        }
                        val timer = Timer()
                        timer.schedule(task, 100)

                    } else {

                        if (!chooseJobCity) {
                            ToastUtils.showShort("请填写您的工作居住地信息")
                        } else {
                            if (!chooseHomeCity) {
                                ToastUtils.showShort("请填写您的家乡信息")
                            }
                        }

                    }

                }
                3 -> {
                    if (chooseTarget && chooseAge) {

                        if (!isFinishPhoto) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //填写我的恋爱目标点击下一步
                        MobclickAgent.onEvent(this, "10031_love_goal_next");

                        vf_guide_detail_container.showNext()

                        //进入上传头像页面
                        MobclickAgent.onEvent(this, "10032_upload_avatar");

                        tsb_guide_detail_guide.setPercent(0.46f, "46")

                        SPStaticUtils.put(Constant.ME_LOVE_TARGET_SHOW, targetVisibilityPosition)
                        SPStaticUtils.put(Constant.ME_LOVE_TARGET, target)


                    } else {

                        if (!chooseTarget) {
                            ToastUtils.showShort("请填写您的恋爱目标")
                        }
                    }

                }
                4 -> {

                    if (isFinishPhoto) {

                        if (mLifeFirstUrl == "") {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        } else {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                        }

                        //上传头像成功,点击下一步
                        MobclickAgent.onEvent(this, "10034_upload_avatar_success");


                        vf_guide_detail_container.showNext()

                        //进入我的生活页面
                        MobclickAgent.onEvent(this, "10035_my_life");

                        tsb_guide_detail_guide.setPercent(0.49f, "49")

                        // 上传头像
                        Thread {

                            //上传Object
                            val file = File(mPhotoPath)
                            // bucketName 为文件夹名 ，使用用户id来进行命名
                            // key值为保存文件名，试用固定的几种格式来命名

                            val avatarName = TimeUtils.getNowMills()

                            val putObjectFromFileResponse = client.putObject("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", "${avatarName}.jpg", file)


                            mPhotoUrl = client.generatePresignedUrl("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", "${avatarName}.jpg", -1).toString()

                            SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, mPhotoUrl)

                        }.start()

                    } else {
                        ToastUtils.showShort("请上传您的真实头像")
                    }

                }
                5 -> {

                    if (mLifeFirstUrl != "") {

                        if (!isFinishIntroduce) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //我的生活上传成功,点击下一步
                        MobclickAgent.onEvent(this, "10037_my_life_next");

                        vf_guide_detail_container.showNext()

                        //进入关于我页面
                        MobclickAgent.onEvent(this, "10038_about_me");

                        tsb_guide_detail_guide.setPercent(0.60f, "60")

                    } else {
                        ToastUtils.showShort("请上传您的生活照")
                    }

                }
                6 -> {

                    if (isFinishIntroduce) {

                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ACCESS_TOKEN] =
                            SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                        map[Contents.TEXT] = introduceText
                        isCompleteIntroduce = true
                        doTextVerifyPresent.doTextVerify(map)

                    } else {
                        ToastUtils.showShort("请添加一份30字左右的自我介绍")
                    }

                }
                7 -> {
                    if (isFinishHobby) {


                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ACCESS_TOKEN] =
                            SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                        map[Contents.TEXT] = hobbyText
                        isCompleteHobby = true
                        doTextVerifyPresent.doTextVerify(map)

                    } else {
                        ToastUtils.showShort("请添加一些您的日常爱好")
                    }

                }
                8 -> {

                    if (isFinishIdeal) {

//                        for (i in 0.until(banTextList.size)) {
//                            val code = banTextList[i]
//                            if (idealText.contains(code)) {
//                                haveBanText = true
//                            }
//                        }
//                        if (haveBanText) {
//
//                            ToastUtils.showShort("输入中存在敏感字，请重新输入")
//                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
//
//                            isFinishIdeal = false
//                            haveBanText = false
//
//                            et_guide_ideal_content.setText("")
//
//                        } else {
//
//                            if (!(name != "" && identityCode != "")) {
//                                tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
//                            }
//
//                            vf_guide_detail_container.showNext()
//                            tv_guide_detail_privacy.visibility = View.VISIBLE
//                            tv_guide_detail_service.visibility = View.VISIBLE
//
//                            tsb_guide_detail_guide.setPercent(1.0f, "100")
//
//                            SPStaticUtils.put(Constant.ME_TA, idealText)
//
//                        }

                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ACCESS_TOKEN] =
                            SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                        map[Contents.TEXT] = idealText
                        isCompleteIdeal = true
                        doTextVerifyPresent.doTextVerify(map)

                    } else {
                        ToastUtils.showShort("请描述一下您心中理想的对象")
                    }

                }
                9 -> {

                    if (name != "" && identityCode != "") {

                        if (RegexUtils.isIDCard18Exact(identityCode)) {
                            val map: MutableMap<String, String> = TreeMap()

                            map[Contents.ACCESS_TOKEN] =
                                SPStaticUtils.getString(Constant.ID_ACCESS_TOKEN, "")
                            map[Contents.CONTENT_TYPE] = "application/json"
                            map[Contents.ID_CARD_NUMBER] = identityCode
                            map[Contents.NAME] = name
                            doIdentityVerifyPresent.doIdentityVerify(map)

                            // 返回mbody为空，以后人脸识别可以了直接

                        } else {
                            ToastUtils.showShort("请输入正确的身份证号")
                        }
                    } else {
                        ToastUtils.showShort("请填写您的真实姓名与身份证号，以用于实名认证")
                    }

                }
            }

        }

        // -------------------  教育信息界面  -----------------

        rl_guide_edu_edu.setOnClickListener {

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(EduDialog(this)).show()

        }

        rl_guide_edu_school.setOnClickListener {
            val intent = Intent(this, SchoolSearchActivity::class.java)
            intent.putExtra("message", "Love")
            startActivityForResult(intent, 1)
        }

        //---------------------- 职业收入情况 ----------------------

        rl_guide_income_job.setOnClickListener {

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(IncomeJobDialog(this)).show()

        }

        rl_guide_income_income.setOnClickListener {

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(IncomeIncomeDialog(this)).show()

        }

        // -------------------  居住地和家乡界面  -----------------

        rl_guide_address_jobcity.setOnClickListener {
            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(AddressJobCity(this)).show()
        }

        rl_guide_address_homecity.setOnClickListener {

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(AddressHomeCity(this)).show()
        }

        // -------------------  择偶条件界面  -----------------

        ll_guide_target_visibility.setOnClickListener {

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(TargetVisibilityDialog(this)).show()

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

        dssb_guide_target_age.setOnRangeListener { low, big ->
            targetAgeMin = low.toInt()
            targetAgeMax = big.toInt()

            tv_guide_target_age.text = "$targetAgeMin ~ ${targetAgeMax}岁"
        }

        // -------------------  上传头像界面  -----------------

        iv_photo_delete.setOnClickListener {

            if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
                iv_photo_container.setImageResource(R.mipmap.icon_photo_male)
            } else {
                iv_photo_container.setImageResource(R.mipmap.icon_photo_female)
            }

            iv_photo_container.setOnClickListener {
                XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                    .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                    .asCustom(PhotoGuideDialog(this)).show()
            }

            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
            iv_photo_delete.visibility = View.INVISIBLE
            tv_photo_reupload.visibility = View.INVISIBLE

            isFinishPhoto = false

        }

        tv_photo_reupload.setOnClickListener {
            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(PhotoGuideDialog(this)).show()
        }

        iv_photo_container.setOnClickListener {
            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(PhotoGuideDialog(this)).show()
        }


        // 设置裁剪图片结果监听


        // -------------------  我的生活界面  -----------------

        ll_guide_life_default.setOnClickListener {
            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeGuidDialog(this)).show()
        }

        rl_guide_life_pic_more.setOnClickListener {
            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeGuidDialog(this)).show()
        }

        rl_guide_life_pic_one.setOnClickListener {
            // 第一张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeFirstUrl)


            Log.i("guo", "mLifeFirstUrl : $mLifeFirstUrl")

            intent.putExtra("introduce", mLifeFirstText)
            startActivityForResult(intent, 111)
        }

        iv_guide_life_pic_one_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有其他图，若无则直接删除，若有则向上顺移第三张图

            lifeDeleteMode = "one"

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this)).show()
        }

        rl_guide_life_pic_two.setOnClickListener {
            // 第二张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeSecondPath)
            intent.putExtra("introduce", mLifeSecondText)
            startActivityForResult(intent, 222)
        }

        iv_guide_life_pic_two_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有第三张图，若无则直接删除，若有则向上顺移第三张图

            lifeDeleteMode = "two"

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this)).show()
        }

        rl_guide_life_pic_three.setOnClickListener {
            // 第三张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeThirdPath)
            intent.putExtra("introduce", mLifeThirdText)
            startActivityForResult(intent, 333)
        }

        iv_guide_life_pic_three_delete.setOnClickListener {
            lifeDeleteMode = "three"

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this)).show()
        }

        rl_guide_life_pic_four.setOnClickListener {
            // 第四张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            intent.putExtra("path", mLifeFourPath)
            intent.putExtra("introduce", mLifeFourText)
            startActivityForResult(intent, 444)

        }

        iv_guide_life_pic_four_delete.setOnClickListener {
            lifeDeleteMode = "four"

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this)).show()

        }

        rl_guide_life_pic_five.setOnClickListener {
            // 第五张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            intent.putExtra("path", mLifeFivePath)
            intent.putExtra("introduce", mLifeFiveText)
            startActivityForResult(intent, 555)

        }

        iv_guide_life_pic_five_delete.setOnClickListener {
            lifeDeleteMode = "five"

            XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
                .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this)).show()

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

                if (s.length >= 10) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    isFinishIntroduce = true
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    isFinishIntroduce = false

                }

                if (s.length == 100) {
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


                if (s.length >= 10) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    isFinishHobby = true
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    isFinishHobby = false
                }


                if (s.length == 100) {
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



                if (s.length >= 10) {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    isFinishIdeal = true
                } else {
                    tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    isFinishIdeal = false
                }


                if (s.length == 100) {
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
                    mStepDetailNine = StepDetailNine(this, vf_guide_detail_container.getChildAt(8))
                }

                if (mStepDetailTen == null) {
                    mStepDetailTen = StepDetailTen(this, vf_guide_detail_container.getChildAt(9))
                }
            }
        }
        return null
    }

    // 百度人脸采集sdk设置
    private fun initLicense() {
        val success = setFaceConfig()
        if (!success) {
            ToastUtils.showShort("初始化失败 = json配置文件解析出错")
            return
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(this,
            "jiaou-hunlian-face-android",
            "idl-license.face-android",
            object : IInitCallback {
                override fun initSuccess() {
                    runOnUiThread {
                        Log.e("guo", "初始化成功")
                        ToastUtils.showShort("初始化成功")
//                        mIsInitSuccess = true
                    }
                }

                override fun initFailure(errCode: Int, errMsg: String) {
                    runOnUiThread {
                        Log.i("guo", "初始化失败 = $errCode $errMsg")
                        ToastUtils.showShort("初始化失败 = $errCode, $errMsg")
//                        mIsInitSuccess = false
                    }
                }
            })
    }

    /**
     * 参数配置方法
     */
    private fun setFaceConfig(): Boolean {
        val config = FaceSDKManager.getInstance().faceConfig
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
        // 获取保存的质量等级

        val qualityLevel = 0

        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        val manager: QualityConfigManager = QualityConfigManager.getInstance()
        manager.readQualityFile(this.applicationContext, qualityLevel)
        val qualityConfig: QualityConfig = manager.config ?: return false
        // 设置模糊度阈值
        config.blurnessValue = qualityConfig.blur
        // 设置最小光照阈值（范围0-255）
        config.brightnessValue = qualityConfig.minIllum
        // 设置最大光照阈值（范围0-255）
        config.brightnessMaxValue = qualityConfig.maxIllum
        // 设置左眼遮挡阈值
        config.occlusionLeftEyeValue = qualityConfig.leftEyeOcclusion
        // 设置右眼遮挡阈值
        config.occlusionRightEyeValue = qualityConfig.rightEyeOcclusion
        // 设置鼻子遮挡阈值
        config.occlusionNoseValue = qualityConfig.noseOcclusion
        // 设置嘴巴遮挡阈值
        config.occlusionMouthValue = qualityConfig.mouseOcclusion
        // 设置左脸颊遮挡阈值
        config.occlusionLeftContourValue = qualityConfig.leftContourOcclusion
        // 设置右脸颊遮挡阈值
        config.occlusionRightContourValue = qualityConfig.rightContourOcclusion
        // 设置下巴遮挡阈值
        config.occlusionChinValue = qualityConfig.chinOcclusion
        // 设置人脸姿态角阈值
        config.headPitchValue = qualityConfig.pitch
        config.headYawValue = qualityConfig.yaw
        config.headRollValue = qualityConfig.roll
        // 设置可检测的最小人脸阈值
        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
        // 设置可检测到人脸的阈值
        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
        // 设置闭眼阈值
        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
        // 设置图片缓存数量
        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.livenessTypeList = livenessList
        // 设置动作活体是否随机
        config.isLivenessRandom = true
        // 设置开启提示音
        config.isSound = false
        // 原图缩放系数
        config.scale = FaceEnvironment.VALUE_SCALE
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
        config.cropWidth = FaceEnvironment.VALUE_CROP_WIDTH
        // 抠图人脸框与背景比例
        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
        // 检测超时设置
        config.timeDetectModule = FaceEnvironment.TIME_DETECT_MODULE
        // 检测框远近比率
        config.faceFarRatio = FaceEnvironment.VALUE_FAR_RATIO
        config.faceClosedRatio = FaceEnvironment.VALUE_CLOSED_RATIO
        FaceSDKManager.getInstance().faceConfig = config
        return true
    }

    // 更新生活照数据视图
    private fun updateLifePhoto() {

        mLifeFirstUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE, "")
        mLifeFirstText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")

        mLifeSecondUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO, "")
        mLifeSecondText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")

        mLifeThirdUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE, "")
        mLifeThirdText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")

        if (mLifeFirstUrl != "") {
            nsv_guide_life_default.visibility = View.GONE
            nsv_guide_life_pic.visibility = View.VISIBLE
            rl_guide_life_pic_one.visibility = View.VISIBLE
            rl_guide_life_pic_more.visibility = View.VISIBLE

            Glide.with(this).load(mLifeFirstUrl).into(iv_guide_life_pic_one)
            tv_guide_life_pic_one.text = mLifeFirstText

            if (mLifeSecondUrl != "") {
                Glide.with(this).load(mLifeSecondUrl).into(iv_guide_life_pic_two)
                tv_guide_life_pic_two.text = mLifeSecondText
                rl_guide_life_pic_two.visibility = View.VISIBLE

                if (mLifeThirdUrl != "") {


                    Glide.with(this).load(mLifeThirdUrl).into(iv_guide_life_pic_three)
                    tv_guide_life_pic_three.text = mLifeSecondText
                    rl_guide_life_pic_three.visibility = View.VISIBLE
                    rl_guide_life_pic_more.visibility = View.GONE

                } else {
                    rl_guide_life_pic_three.visibility = View.GONE
                }
            } else {
                rl_guide_life_pic_two.visibility = View.GONE
            }
        } else {
            nsv_guide_life_default.visibility = View.VISIBLE
            nsv_guide_life_pic.visibility = View.GONE
            rl_guide_life_pic_one.visibility = View.GONE
            rl_guide_life_pic_two.visibility = View.GONE
            rl_guide_life_pic_three.visibility = View.GONE
            rl_guide_life_pic_more.visibility = View.GONE
        }
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
            cityIDFirstList.add(cityDate.data[i].id)
        }

    }

    // 市
    private fun getJobCitySecondList(i: Int) {

        citySecondList.clear()
        cityIDSecondList.clear()

        for (j in 0.until(cityDate.data[i].child.size)) {
            citySecondList.add(cityDate.data[i].child[j].name)
            cityIDSecondList.add(cityDate.data[i].child[j].id)
        }

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

    // 上传头像
    private fun updateAvatar(photoUrl: String, type: String, name: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.IMAGE_URL] = photoUrl
        map[Contents.FILE_TYPE] = type
        map[Contents.FILE_NAME] = name
        map[Contents.CONTENT] = "0"
        doUploadAvatarPresent.doUploadAvatar(map)

    }

    // -------------------  我的生活界面  -----------------


    // 删除生活照
    private fun deleteLifePhoto(id: String) {

        ll_guide_life_loading.visibility = View.VISIBLE

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ID] = id
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        doDeletePhotoPresent.doDeletePhoto(map)


    }

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
                    if (data != null) {
                        val bundle = data.extras
                        val bitmap: Bitmap = bundle?.get("data") as Bitmap

                        lifeBitmap = bitmap

                        ImageUtils.save(bitmap, mTempLifePath, Bitmap.CompressFormat.PNG)

                        lifeChoosePath = mTempLifePath

                        if (mLifeFirstUrl == "") {

                            val intent = Intent(this, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 111)

                        } else if (mLifeSecondUrl == "") {

                            val intent = Intent(this, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 222)

                        } else if (mLifeThirdUrl == "") {

                            val intent = Intent(this, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 333)
                        } else if (mLifeFourUrl == "") {

                            val intent = Intent(this, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 444)
                        } else if (mLifeFiveUrl == "") {

                            val intent = Intent(this, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 555)
                        }
                    }
                }
                111 -> {
                    // 生活第一张图的介绍
                    if (data != null) {

                        mLifeFirstPath = data.getStringExtra("path").toString()
                        mLifeFirstUrl = data.getStringExtra("url").toString()
                        mLifeFirstId = data.getStringExtra("id").toString()
                        mLifeFirstText = data.getStringExtra("text").toString()

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, mLifeFirstUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, mLifeFirstText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, mLifeFirstId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "0")

                        mLifeFirstState = "0"
                        tv_guide_life_pic_one_audit?.visibility = View.VISIBLE

                        nsv_guide_life_default?.visibility = View.GONE
                        nsv_guide_life_pic?.visibility = View.VISIBLE

                        rl_guide_life_pic_one?.visibility = View.VISIBLE
                        rl_guide_life_pic_more?.visibility = View.VISIBLE


                        if (iv_guide_life_pic_one != null) {
                            Glide.with(this).load(mLifeFirstUrl).into(iv_guide_life_pic_one)
                        }

                        haveFirstPic = true

                        if (mLifeFirstText != "") {
                            tv_guide_life_pic_one?.text = mLifeFirstText
                            iv_guide_life_pic_one_icon?.visibility = View.GONE
                        }

                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

                    }
                }
                222 -> {
                    if (data != null) {

                        mLifeSecondPath = data.getStringExtra("path").toString()
                        mLifeSecondUrl = data.getStringExtra("url").toString()
                        mLifeSecondId = data.getStringExtra("id").toString()
                        mLifeSecondText = data.getStringExtra("text").toString()

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

                        mLifeSecondState = "0"
                        tv_guide_life_pic_two_audit?.visibility = View.VISIBLE

                        rl_guide_life_pic_two?.visibility = View.VISIBLE

                        if (iv_guide_life_pic_two != null) {
                            Glide.with(this).load(mLifeSecondUrl).into(iv_guide_life_pic_two)
                        }


                        haveSecondPic = true


                        if (mLifeSecondText != "") {
                            tv_guide_life_pic_two?.text = mLifeSecondText
                            iv_guide_life_pic_two_icon?.visibility = View.GONE
                        }


                    }
                }
                333 -> {
                    if (data != null) {

                        mLifeThirdPath = data.getStringExtra("path").toString()
                        mLifeThirdUrl = data.getStringExtra("url").toString()
                        mLifeThirdId = data.getStringExtra("id").toString()
                        mLifeThirdText = data.getStringExtra("text").toString()

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

                        mLifeThirdState = "0"
                        tv_guide_life_pic_three_audit?.visibility = View.VISIBLE

                        rl_guide_life_pic_three?.visibility = View.VISIBLE

                        if (iv_guide_life_pic_three != null) {
                            Glide.with(this).load(mLifeThirdUrl).into(iv_guide_life_pic_three)


                        }


                        haveThirdPic = true


                        if (mLifeThirdText != "") {
                            tv_guide_life_pic_three?.text = mLifeThirdText
                            iv_guide_life_pic_three_icon?.visibility = View.GONE
                        }


                    }
                }
                444 -> {
                    if (data != null) {

                        mLifeFourPath = data.getStringExtra("path").toString()
                        mLifeFourUrl = data.getStringExtra("url").toString()
                        mLifeFourId = data.getStringExtra("id").toString()
                        mLifeFourText = data.getStringExtra("text").toString()

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, mLifeFourUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, mLifeFourText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, mLifeFourId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")

                        mLifeFourState = "0"
                        tv_guide_life_pic_four_audit?.visibility = View.VISIBLE

                        rl_guide_life_pic_four?.visibility = View.VISIBLE

                        if (iv_guide_life_pic_four != null) {
                            Glide.with(this).load(mLifeFourUrl).into(iv_guide_life_pic_four)
                        }

                        haveFourPic = true

                        if (mLifeFourText != "") {
                            tv_guide_life_pic_four?.text = mLifeFourText
                            iv_guide_life_pic_four_icon?.visibility = View.GONE
                        }

                    }
                }
                555 -> {
                    if (data != null) {

                        mLifeFivePath = data.getStringExtra("path").toString()
                        mLifeFiveUrl = data.getStringExtra("url").toString()
                        mLifeFiveId = data.getStringExtra("id").toString()
                        mLifeFiveText = data.getStringExtra("text").toString()

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, mLifeFiveUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, mLifeFiveText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, mLifeFiveId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")

                        mLifeFiveState = "0"
                        tv_guide_life_pic_five_audit?.visibility = View.VISIBLE

                        rl_guide_life_pic_five?.visibility = View.VISIBLE
                        rl_guide_life_pic_more?.visibility = View.GONE

                        if (iv_guide_life_pic_five != null) {
                            Glide.with(this).load(mLifeFiveUrl).into(iv_guide_life_pic_five)
                        }


                        haveFivePic = true

                        if (mLifeFiveText != "") {
                            tv_guide_life_pic_five?.text = mLifeFiveText
                            iv_guide_life_pic_five_icon?.visibility = View.GONE
                        }

                    }
                }
                UCrop.REQUEST_CROP -> {

                    // 只走头像的回调
                    if (data != null) {
                        ll_guide_detail_loading.visibility = View.VISIBLE

                        handlePhotoCropResult(data)
                    };

                }
                UCrop.RESULT_ERROR -> {
                    if (data != null) {
                        handlePhotoCropError(data)
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
                .withOptions(options).start(this)
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

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()

        }
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

    override fun onDoUploadAvatarSuccess(uploadAvatarBean: UploadAvatarBean?) {

        ll_guide_detail_loading.visibility = View.GONE

        if (uploadAvatarBean != null) {
            if (uploadAvatarBean.code == 200) {

                ToastUtils.showShort("头像上传成功")
                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, mPhotoUrl)


                iv_photo_container.setImageBitmap(photoBitmap)

                iv_photo_container.setOnClickListener(null)

                tv_photo_reupload.visibility = View.VISIBLE
                iv_photo_delete.visibility = View.VISIBLE

                isFinishPhoto = true
                tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)

            } else {
                ToastUtils.showShort("头像上传失败")
            }
        }
    }

    override fun onDoUploadAvatarError() {
        Log.i("guo", "error")
        ll_guide_detail_loading.visibility = View.GONE
    }

    override fun onDoDeletePhotoSuccess(deletePhotoBean: DeletePhotoBean?) {
        when (lifeDeleteMode) {
            "one" -> {
                if (haveSecondPic) {

                    if (haveThirdPic) {

                        if (haveFourPic) {

                            if (haveFivePic) {
                                // 1、2、3、4、5 删除第五张 第五张、第四张、第三张、第二张依次往前移动（先移动靠前的）

                                rl_guide_life_pic_five?.visibility = View.GONE
                                rl_guide_life_pic_more?.visibility = View.VISIBLE

                                // 移动第二张

                                mLifeFirstUrl = mLifeSecondUrl
                                mLifeFirstText = mLifeSecondText
                                mLifeFirstId = mLifeSecondId
                                mLifeFirstState = mLifeSecondState

                                Glide.with(applicationContext).load(mLifeFirstUrl)
                                    .into(iv_guide_life_pic_one)
                                tv_guide_life_pic_one?.text = mLifeFirstText

                                // 移动第三张

                                mLifeSecondUrl = mLifeThirdUrl
                                mLifeSecondText = mLifeThirdText
                                mLifeSecondId = mLifeThirdId
                                mLifeSecondState = mLifeThirdState

                                Glide.with(applicationContext).load(mLifeSecondUrl)
                                    .into(iv_guide_life_pic_two)
                                tv_guide_life_pic_two?.text = mLifeSecondText

                                // 移动第四张

                                mLifeThirdUrl = mLifeFourUrl
                                mLifeThirdText = mLifeFourText
                                mLifeThirdId = mLifeFourId
                                mLifeThirdState = mLifeFourState

                                Glide.with(applicationContext).load(mLifeThirdUrl)
                                    .into(iv_guide_life_pic_three)
                                tv_guide_life_pic_three?.text = mLifeThirdText


                                // 移动第五张

                                mLifeFourUrl = mLifeFiveUrl
                                mLifeFourText = mLifeFiveText
                                mLifeFourId = mLifeFiveId
                                mLifeFourState = mLifeFiveState

                                Glide.with(applicationContext).load(mLifeFourUrl)
                                    .into(iv_guide_life_pic_four)
                                tv_guide_life_pic_four?.text = mLifeFourText

                                haveFivePic = false
                                mLifeFiveUrl = ""
                                mLifeFiveText = ""

                                iv_guide_life_pic_five_icon?.visibility = View.VISIBLE
                                tv_guide_life_pic_five?.text = "添加描述"

                                // 更新存储数据

                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, mLifeFirstUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, mLifeFirstText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, mLifeFirstId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, mLifeFirstState)

                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                                    mLifeSecondState)


                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                                    mLifeThirdState)


                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, mLifeFourUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, mLifeFourText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, mLifeFourId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, mLifeFourState)


                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "")


                            } else {
                                // 1、2、3、4 删除第四张  第三张、第三张、第四张顺延


                                rl_guide_life_pic_four?.visibility = View.GONE


                                // 移动第二张

                                mLifeFirstUrl = mLifeSecondUrl
                                mLifeFirstText = mLifeSecondText
                                mLifeFirstId = mLifeSecondId
                                mLifeFirstState = mLifeSecondState

                                Glide.with(applicationContext).load(mLifeFirstUrl)
                                    .into(iv_guide_life_pic_one)
                                tv_guide_life_pic_one?.text = mLifeFirstText

                                // 移动第三张

                                mLifeSecondUrl = mLifeThirdUrl
                                mLifeSecondText = mLifeThirdText
                                mLifeSecondId = mLifeThirdId
                                mLifeSecondState = mLifeThirdState

                                Glide.with(applicationContext).load(mLifeSecondUrl)
                                    .into(iv_guide_life_pic_two)
                                tv_guide_life_pic_two?.text = mLifeSecondText

                                // 移动第四张

                                mLifeThirdUrl = mLifeFourUrl
                                mLifeThirdText = mLifeFourText
                                mLifeThirdId = mLifeFourId
                                mLifeThirdState = mLifeFourState

                                Glide.with(applicationContext).load(mLifeThirdUrl)
                                    .into(iv_guide_life_pic_three)
                                tv_guide_life_pic_three?.text = mLifeThirdText

                                haveFourPic = false
                                mLifeFourUrl = ""
                                mLifeFourText = ""

                                iv_guide_life_pic_four_icon?.visibility = View.VISIBLE
                                tv_guide_life_pic_four?.text = "添加描述"

                                // 更新存储数据

                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, mLifeFirstUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, mLifeFirstText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, mLifeFirstId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, mLifeFirstState)

                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT,
                                    mLifeSecondState)


                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT,
                                    mLifeThirdState)


                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "")


                            }

                        } else {
                            // 1、2、3，删除第3张 第3张 第二张 顺延

                            rl_guide_life_pic_three?.visibility = View.GONE


                            // 移动第二张

                            mLifeFirstUrl = mLifeSecondUrl
                            mLifeFirstText = mLifeSecondText
                            mLifeFirstId = mLifeSecondId
                            mLifeFirstState = mLifeSecondState

                            Glide.with(applicationContext).load(mLifeFirstUrl)
                                .into(iv_guide_life_pic_one)
                            tv_guide_life_pic_one?.text = mLifeFirstText

                            // 移动第三张

                            mLifeSecondUrl = mLifeThirdUrl
                            mLifeSecondText = mLifeThirdText
                            mLifeSecondId = mLifeThirdId
                            mLifeSecondState = mLifeThirdState

                            Glide.with(applicationContext).load(mLifeSecondUrl)
                                .into(iv_guide_life_pic_two)
                            tv_guide_life_pic_two?.text = mLifeSecondText

                            haveThirdPic = false
                            mLifeThirdUrl = ""
                            mLifeThirdText = ""

                            iv_guide_life_pic_three_icon?.visibility = View.VISIBLE
                            tv_guide_life_pic_three?.text = "添加描述"

                            // 更新存储数据

                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, mLifeFirstUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, mLifeFirstText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, mLifeFirstId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, mLifeFirstState)

                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, mLifeSecondState)

                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "")

                        }

                    } else {

                        // 1、2 删除第二张 第二张顺延到第一张


                        rl_guide_life_pic_two?.visibility = View.GONE



                        mLifeFirstUrl = mLifeSecondUrl
                        mLifeFirstText = mLifeSecondText
                        mLifeFirstId = mLifeSecondId
                        mLifeFirstState = mLifeSecondState

                        Glide.with(applicationContext).load(mLifeFirstUrl)
                            .into(iv_guide_life_pic_one)
                        tv_guide_life_pic_one?.text = mLifeFirstText

                        haveSecondPic = false
                        mLifeSecondUrl = ""
                        mLifeSecondText = ""

                        iv_guide_life_pic_two_icon?.visibility = View.VISIBLE
                        tv_guide_life_pic_two?.text = "添加描述"

                        // 更新存储数据
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, mLifeFirstUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, mLifeFirstText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, mLifeFirstId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, mLifeFirstState)

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "")

                    }

                } else {
                    // 只有一张图 直接删除

                    rl_guide_life_pic_one?.visibility = View.GONE
                    rl_guide_life_pic_more?.visibility = View.GONE
                    nsv_life_photo_pic?.visibility = View.GONE

                    nsv_life_photo_default?.visibility = View.VISIBLE

                    haveFirstPic = false
                    mLifeFirstUrl = ""
                    mLifeFirstText = ""

                    iv_guide_life_pic_one_icon?.visibility = View.VISIBLE
                    tv_guide_life_pic_one?.text = "添加描述"

                    // 更新存储数据
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_ID, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "")

                }
            }
            "two" -> {
                if (haveThirdPic) {
                    // 有第三张图

                    if (haveFourPic) {

                        if (haveFivePic) {
                            // 1、2、3、4、5 删除第五张 第五张、第四张、第三张依次往前移动（先移动靠前的）

                            rl_guide_life_pic_five?.visibility = View.GONE
                            rl_guide_life_pic_more?.visibility = View.VISIBLE

                            // 移动第三张

                            mLifeSecondUrl = mLifeThirdUrl
                            mLifeSecondText = mLifeThirdText
                            mLifeSecondId = mLifeThirdId
                            mLifeSecondState = mLifeThirdState

                            Glide.with(applicationContext).load(mLifeSecondUrl)
                                .into(iv_guide_life_pic_two)
                            tv_guide_life_pic_two?.text = mLifeSecondText

                            // 移动第四张

                            mLifeThirdUrl = mLifeFourUrl
                            mLifeThirdText = mLifeFourText
                            mLifeThirdId = mLifeFourId
                            mLifeThirdState = mLifeFourState

                            Glide.with(applicationContext).load(mLifeThirdUrl)
                                .into(iv_guide_life_pic_three)
                            tv_guide_life_pic_three?.text = mLifeThirdText


                            // 移动第五张

                            mLifeFourUrl = mLifeFiveUrl
                            mLifeFourText = mLifeFiveText
                            mLifeFourId = mLifeFiveId
                            mLifeFourState = mLifeFiveState

                            Glide.with(applicationContext).load(mLifeFourUrl)
                                .into(iv_guide_life_pic_four)
                            tv_guide_life_pic_four?.text = mLifeFourText

                            haveFivePic = false
                            mLifeFiveUrl = ""
                            mLifeFiveText = ""

                            iv_guide_life_pic_five_icon?.visibility = View.VISIBLE
                            tv_guide_life_pic_five?.text = "添加描述"

                            // 更新存储数据
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, mLifeSecondState)


                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, mLifeThirdState)


                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, mLifeFourUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, mLifeFourText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, mLifeFourId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, mLifeFourState)


                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "")


                        } else {
                            // 1、2、3、4 删除第四张  第三张、第四张顺延

                            rl_guide_life_pic_four?.visibility = View.GONE

                            // 移动第三张

                            mLifeSecondUrl = mLifeThirdUrl
                            mLifeSecondText = mLifeThirdText
                            mLifeSecondId = mLifeThirdId
                            mLifeSecondState = mLifeThirdState

                            Glide.with(applicationContext).load(mLifeSecondUrl)
                                .into(iv_guide_life_pic_two)
                            tv_guide_life_pic_two?.text = mLifeSecondText

                            // 移动第四张

                            mLifeThirdUrl = mLifeFourUrl
                            mLifeThirdText = mLifeFourText
                            mLifeThirdId = mLifeFourId
                            mLifeThirdState = mLifeFourState

                            Glide.with(applicationContext).load(mLifeThirdUrl)
                                .into(iv_guide_life_pic_three)
                            tv_guide_life_pic_three?.text = mLifeThirdText

                            haveFourPic = false
                            mLifeFourUrl = ""
                            mLifeFourText = ""

                            iv_guide_life_pic_four_icon?.visibility = View.VISIBLE
                            tv_guide_life_pic_four?.text = "添加描述"

                            // 更新存储数据
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, mLifeSecondState)


                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, mLifeThirdState)


                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "")


                        }

                    } else {
                        // 1、2、3，删除第3张 第3张顺延到第二张
                        rl_guide_life_pic_three?.visibility = View.GONE

                        mLifeSecondUrl = mLifeThirdUrl
                        mLifeSecondText = mLifeThirdText
                        mLifeSecondId = mLifeThirdId
                        mLifeSecondState = mLifeThirdState

                        Glide.with(applicationContext).load(mLifeSecondUrl)
                            .into(iv_guide_life_pic_two)
                        tv_guide_life_pic_two?.text = mLifeSecondText

                        haveThirdPic = false
                        mLifeThirdUrl = ""
                        mLifeThirdText = ""

                        iv_guide_life_pic_three_icon?.visibility = View.VISIBLE
                        tv_guide_life_pic_three?.text = "添加描述"

                        // 更新存储数据
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, mLifeSecondText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, mLifeSecondId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, mLifeSecondState)

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "")

                    }

                } else {
                    // 1、2 直接删除即可

                    rl_guide_life_pic_two?.visibility = View.GONE

                    haveSecondPic = false
                    mLifeSecondUrl = ""
                    mLifeSecondText = ""

                    iv_guide_life_pic_two_icon?.visibility = View.VISIBLE
                    tv_guide_life_pic_two?.text = "添加描述"

                    // 更新存储数据
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_ID, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "")

                }
            }
            "three" -> {
                if (haveFourPic) {

                    if (haveFivePic) {
                        // 1、2、3、4、5 删除第五张 第五张、第四章依次往前移动（先移动靠前的）

                        rl_guide_life_pic_five?.visibility = View.GONE
                        rl_guide_life_pic_more?.visibility = View.VISIBLE

                        // 移动第四张

                        mLifeThirdUrl = mLifeFourUrl
                        mLifeThirdText = mLifeFourText
                        mLifeThirdId = mLifeFourId
                        mLifeThirdState = mLifeFourState

                        Glide.with(applicationContext).load(mLifeThirdUrl)
                            .into(iv_guide_life_pic_three)
                        tv_guide_life_pic_three?.text = mLifeThirdText

                        // 移动第五张

                        mLifeFourUrl = mLifeFiveUrl
                        mLifeFourText = mLifeFiveText
                        mLifeFourId = mLifeFiveId
                        mLifeFourState = mLifeFiveState

                        Glide.with(applicationContext).load(mLifeFourUrl)
                            .into(iv_guide_life_pic_four)
                        tv_guide_life_pic_four?.text = mLifeFourText

                        haveFivePic = false
                        mLifeFiveUrl = ""
                        mLifeFiveText = ""

                        iv_guide_life_pic_five_icon?.visibility = View.VISIBLE
                        tv_guide_life_pic_five?.text = "添加描述"

                        // 更新存储数据
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, mLifeThirdState)


                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, mLifeFourUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, mLifeFourText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, mLifeFourId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, mLifeFourState)


                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "")

                    } else {
                        // 1、2、3、4 删除第四张

                        rl_guide_life_pic_four?.visibility = View.GONE

                        mLifeThirdUrl = mLifeFourUrl
                        mLifeThirdText = mLifeFourText
                        mLifeThirdId = mLifeFourId
                        mLifeThirdState = mLifeFourState

                        Glide.with(applicationContext).load(mLifeThirdUrl)
                            .into(iv_guide_life_pic_three)
                        tv_guide_life_pic_three?.text = mLifeThirdText

                        haveFourPic = false
                        mLifeFourUrl = ""
                        mLifeFourText = ""

                        iv_guide_life_pic_four_icon?.visibility = View.VISIBLE
                        tv_guide_life_pic_four?.text = "添加描述"

                        // 更新存储数据
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, mLifeThirdText)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, mLifeThirdId)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, mLifeThirdState)

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "")

                    }

                } else {
                    // 1、2、3，直接删除第三张
                    rl_guide_life_pic_three?.visibility = View.GONE

                    haveThirdPic = false
                    mLifeThirdUrl = ""
                    mLifeThirdText = ""

                    iv_guide_life_pic_three_icon?.visibility = View.VISIBLE
                    tv_guide_life_pic_three?.text = "添加描述"

                    // 更新存储数据
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_ID, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "")

                }
            }
            "four" -> {
                if (haveFivePic) {
                    // 有第五张图

                    rl_guide_life_pic_five?.visibility = View.GONE
                    rl_guide_life_pic_more?.visibility = View.VISIBLE

                    mLifeFourUrl = mLifeFiveUrl
                    mLifeFourText = mLifeFiveText
                    mLifeFourId = mLifeFiveId
                    mLifeFourState = mLifeFiveState

                    Glide.with(applicationContext).load(mLifeFourUrl).into(iv_guide_life_pic_four)
                    tv_guide_life_pic_four?.text = mLifeFourText

                    haveFivePic = false
                    mLifeFiveUrl = ""
                    mLifeFiveText = ""

                    iv_guide_life_pic_five_icon?.visibility = View.VISIBLE
                    tv_guide_life_pic_five?.text = "添加描述"

                    // 更新存储数据
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, mLifeFourUrl)
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, mLifeFourText)
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, mLifeFourId)
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, mLifeFourState)

                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "")

                } else {
                    // 没有第五张图

                    rl_guide_life_pic_four?.visibility = View.GONE

                    haveFourPic = false
                    mLifeFourUrl = ""
                    mLifeFourText = ""

                    iv_guide_life_pic_four_icon?.visibility = View.VISIBLE
                    tv_guide_life_pic_four?.text = "添加描述"

                    // 更新存储数据
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "")

                }

            }
            "five" -> {

                rl_guide_life_pic_five?.visibility = View.GONE
                rl_guide_life_pic_more?.visibility = View.VISIBLE

                haveFivePic = false
                mLifeFiveUrl = ""
                mLifeFiveText = ""

                iv_guide_life_pic_five_icon?.visibility = View.VISIBLE
                tv_guide_life_pic_five?.text = "添加描述"

                // 更新存储数据
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "")
            }
        }
        ll_guide_life_loading?.visibility = View.GONE
    }

    override fun onDoDeletePhotoError() {
        ll_guide_life_loading.visibility = View.GONE
    }

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

        when (vf_guide_detail_container.displayedChild) {

            6 -> {

                if (isCompleteIntroduce) {
                    if (textVerifyBean.conclusion == "合规") {

                        if (!isFinishHobby) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //填写关于我_点击下一步
                        MobclickAgent.onEvent(this, "10040_about_me_next");

                        vf_guide_detail_container.showNext()

                        //进入我的爱好页面
                        MobclickAgent.onEvent(this, "10041_hobby");


                        tsb_guide_detail_guide.setPercent(0.73f, "73")

                        SPStaticUtils.put(Constant.ME_INTRODUCE, introduceText)

                    } else {
                        if (textVerifyBean.error_msg != null) {
                            ToastUtils.showShort(textVerifyBean.error_msg)
                        } else {
                            ToastUtils.showShort(textVerifyBean.data[0].msg)
                        }
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        isFinishIntroduce = false
                        haveBanText = false
                        et_guide_mine_content.setText("")
                    }
                    isCompleteIntroduce = false
                }

            }

            7 -> {
                if (isCompleteHobby) {
                    if (textVerifyBean.conclusion == "合规") {
                        if (!isFinishIdeal) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //填写我的爱好_点击下一步
                        MobclickAgent.onEvent(this, "10043_hobby_next");

                        vf_guide_detail_container.showNext()

                        //进入我心目中的ta页面
                        MobclickAgent.onEvent(this, "10044_hobby");

                        tsb_guide_detail_guide.setPercent(0.88f, "88")
                        SPStaticUtils.put(Constant.ME_HOBBY, hobbyText)
                    } else {
                        if (textVerifyBean.error_msg != null) {
                            ToastUtils.showShort(textVerifyBean.error_msg)
                        } else {
                            ToastUtils.showShort(textVerifyBean.data[0].msg)
                        }
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        isFinishHobby = false
                        haveBanText = false
                        et_guide_hobby_content.setText("")
                    }
                    isCompleteHobby = false
                }

            }
            8 -> {

                if (isCompleteIdeal) {
                    if (textVerifyBean.conclusion == "合规") {

                        if (!(name != "" && identityCode != "")) {
                            tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                        }

                        //填写我心目中的ta_点击下一步
                        MobclickAgent.onEvent(this, "10046_hobby_next");

                        vf_guide_detail_container.showNext()

                        //进入实名认证页面
                        MobclickAgent.onEvent(this, "10047_real_name");

                        tv_guide_detail_privacy.visibility = View.VISIBLE
                        tv_guide_detail_service.visibility = View.VISIBLE

                        tsb_guide_detail_guide.setPercent(1.0f, "100")

                        SPStaticUtils.put(Constant.ME_TA, idealText)

                    } else {

                        if (textVerifyBean.error_msg != null) {
                            ToastUtils.showShort(textVerifyBean.error_msg)
                        } else {
                            ToastUtils.showShort(textVerifyBean.data[0].msg)
                        }
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)

                        isFinishIdeal = false
                        haveBanText = false

                        et_guide_ideal_content.setText("")
                    }
                    isCompleteIdeal = false
                }
            }

        }


    }

    override fun onDoTextVerifyError() {

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

    override fun onDoIdentityVerifySuccess(identityVerifyBean: IdentityVerifyBean) {

        if (identityVerifyBean.error_msg == "SUCCESS") {

            SPStaticUtils.put(Constant.TRUE_NAME, name)
            SPStaticUtils.put(Constant.TRUE_ID, identityCode)

            // 这里需要


            XXPermissions.with(this).permission(Permission.CAMERA)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                        val intent =
                            Intent(this@DetailInfoActivity, FaceLivenessExpActivity::class.java)
                        startActivity(intent)
                        this@DetailInfoActivity.finish()
                    }

                    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                        super.onDenied(permissions, never)
                        ToastUtils.showShort("请授予应用拍照权限，否则应用无法进行人脸识别认证")
                    }
                })

        } else {

            if (identityVerifyBean.error_code == 222351) {
                ToastUtils.showShort("身份证号与姓名不匹配或身份证号不存在")
            } else {
                ToastUtils.showShort(identityVerifyBean.error_msg)
            }


        }

    }

    override fun onDoIdentityVerifyError() {

        ToastUtils.showShort("身份验证接口暂不可用")

    }

    override fun onDoFaceDetectSuccess(faceDetectBean: FaceDetectBean) {

        if (isPhoto) {
            if (faceDetectBean.conclusion == "合规") {


                val bitmap = BitmapUtil.generateBitmap("佳偶婚恋交友", 16f, Color.WHITE)?.let {
                    BitmapUtil.createWaterMarkBitmap(photoBitmap, it)
                }

                FileUtils.delete(mPhotoPath)

                bitmap?.let { saveBitmap(it, mPhotoPath) }

                Thread {

                    //上传Object
                    val file = File(mPhotoPath)
                    // bucketName 为文件夹名 ，使用用户id来进行命名
                    // key值为保存文件名，试用固定的几种格式来命名


                    val faceName = TimeUtils.getNowMills()

                    val putObjectFromFileResponse = client.putObject("user${
                        SPStaticUtils.getString(Constant.USER_ID, "default")
                    }", "${faceName}.jpg", file)

                    Log.i("guo", FileUtils.getFileName(mPhotoPath))

                    mPhotoUrl = client.generatePresignedUrl("user${
                        SPStaticUtils.getString(Constant.USER_ID, "default")
                    }", "${faceName}.jpg", -1).toString()


                    Log.i("guo", mPhotoUrl)

                    updateAvatar(mPhotoUrl,
                        FileUtils.getFileExtension(mPhotoPath),
                        FileUtils.getFileNameNoExtension(mPhotoPath))

                }.start()

            } else {

                ll_guide_detail_loading.visibility = View.GONE

                if (faceDetectBean.error_msg != null) {
                    ToastUtils.showShort(faceDetectBean.error_msg)
                } else {
                    ToastUtils.showShort(faceDetectBean.data[0].msg)
                }

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
//            startActivity(SetWebActivity.getIntent(this@DetailInfoActivity, "在线客服", DataProvider.WebUrlData[0].url))
            startActivity(Intent(this@DetailInfoActivity, FeedbackActivity::class.java))
        }
    }

    inner class TextViewSpan2 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            //点击事件
            startActivity(SetWebActivity.getIntent(this@DetailInfoActivity,
                "人脸认证服务协议",
                DataProvider.WebUrlData[10].url))
        }
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

                tv_guide_income_job.text = "${jobFirstList[jobFirst]}-${jobSecondList[jobSecond]}"

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

            incomePicker.data = DataProvider.IncomeData

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
                Log.i("guo", position.toString())
                incomePosition = position
            }

            findViewById<ImageView>(R.id.iv_income_income_close).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_income_income_confirm).setOnClickListener {

                tv_guide_income_income.text = DataProvider.IncomeData[incomePosition]

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

            one.data = cityFirstList
            two.data = citySecondList

            jobCityFirst = 0
            jobCitySecond = 0
            jobCityThird = 0

            getJobCitySecondList(0)

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



            one.setOnItemSelectedListener { picker, data, position ->
                jobCityFirst = position

                getJobCitySecondList(jobCityFirst)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (jobCitySecond >= citySecondList.size) {
                    jobCitySecond = citySecondList.size - 1
                }

                two.data = citySecondList

            }

            two.setOnItemSelectedListener { picker, data, position ->

                jobCitySecond = position

            }

            findViewById<ImageView>(R.id.iv_address_jobcity_close).setOnClickListener {
                dismiss()
            }

            val city = findViewById<TextView>(R.id.tv_address_jobcity_location)

            if (localCityTwo != "") {
                city.text = localCityTwo
            }

            findViewById<LinearLayout>(R.id.ll_address_jobcity_location).setOnClickListener {

                if (localCityTwo != "") {

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

                } else {

                    XXPermissions.with(this@DetailInfoActivity)
                        .permission(Permission.ACCESS_COARSE_LOCATION)
                        .permission(Permission.ACCESS_FINE_LOCATION)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean,
                            ) {

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

                                        city.text = localCityTwo

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

                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean,
                            ) {
                                super.onDenied(permissions, never)
                                ToastUtils.showShort("请授予用户所需权限")
                            }

                        })

                }

            }


            findViewById<TextView>(R.id.tv_address_jobcity_confirm).setOnClickListener {

                chooseJobCity = true

                isJobLocal = false


                tv_guide_address_jobcity.text =
                    "${cityFirstList[jobCityFirst]}-${citySecondList[jobCitySecond]}"

                jobProvinceName = cityFirstList[jobCityFirst]
                jobProvinceCode = cityIDFirstList[jobCityFirst]
                jobCityName = citySecondList[jobCitySecond]
                jobCityCode = cityIDSecondList[jobCitySecond]


                Log.i("guo", "$jobProvinceName --- $jobProvinceCode")
                Log.i("guo", "$jobCityName --- $jobCityCode")

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

            one.data = cityFirstList
            two.data = citySecondList

            homeCityFirst = 0
            homeCitySecond = 0
            homeCityThird = 0

            getJobCitySecondList(0)

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

            one.setOnItemSelectedListener { picker, data, position ->
                homeCityFirst = position

                getJobCitySecondList(homeCityFirst)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (homeCitySecond >= citySecondList.size) {
                    homeCitySecond = citySecondList.size - 1
                }

                two.data = citySecondList

            }

            two.setOnItemSelectedListener { picker, data, position ->

                homeCitySecond = position

            }


            findViewById<ImageView>(R.id.iv_address_jobcity_close).setOnClickListener {
                dismiss()
            }

            val city = findViewById<TextView>(R.id.tv_address_jobcity_location)

            if (localCityTwo != "") {
                city.text = localCityTwo
            }

            findViewById<LinearLayout>(R.id.ll_address_jobcity_location).setOnClickListener {

                if (localCityTwo != "") {

                    chooseHomeCity = true

                    isHomeLocal = true

                    tv_guide_address_homecity.text = localCityTwo

                    home = "$localCityOne -$localCityTwo - $localCityThree"

                    tv_guide_address_homecity.setTextColor(Color.parseColor("#0F0F0F"))

                    if (chooseJobCity && chooseHomeCity) {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next)
                    } else {
                        tv_guide_detail_next.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    }

                    dismiss()

                } else {

                    XXPermissions.with(this@DetailInfoActivity)
                        .permission(Permission.ACCESS_COARSE_LOCATION)
                        .permission(Permission.ACCESS_FINE_LOCATION)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean,
                            ) {

                                val mLocationClient = LocationClient(context)

                                mLocationClient.registerLocationListener(object :
                                    BDAbstractLocationListener() {
                                    override fun onReceiveLocation(location: BDLocation) {
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


                                        chooseHomeCity = true

                                        isHomeLocal = true

                                        tv_guide_address_homecity.text = localCityTwo

                                        home = "$localCityOne -$localCityTwo - $localCityThree"

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

                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean,
                            ) {
                                super.onDenied(permissions, never)
                                ToastUtils.showShort("请授予用户所需权限")
                            }

                        })

                }

            }

            findViewById<TextView>(R.id.tv_address_jobcity_confirm).setOnClickListener {

                chooseHomeCity = true

                isHomeLocal = false

                home = "${cityFirstList[homeCityFirst]}-${citySecondList[homeCitySecond]}"

                homeProvinceName = cityFirstList[homeCityFirst]
                homeProvinceCode = cityIDFirstList[homeCityFirst]
                homeCityName = citySecondList[homeCitySecond]
                homeCityCode = cityIDSecondList[homeCitySecond]

                Log.i("guo", "$homeProvinceName --- $homeProvinceCode")
                Log.i("guo", "$homeCityName --- $homeCityCode")

                tv_guide_address_homecity.text = home

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
                    .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION).setImageSpanCount(3)
                    .isDisplayCamera(true).isPreviewImage(true).isEmptyResultReturn(true)
                    .setLanguage(LanguageConfig.CHINESE).setSelectorUIStyle(selectorStyle)
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

                XXPermissions.with(this@DetailInfoActivity).permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            if (all) {

                                val tempPhotoFile: File = File(mTempPhotoPath)
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                // 如果在Android7.0以上,使用FileProvider获取Uri
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    val authority = context.packageName.toString() + ".fileProvider"
                                    val contentUri: Uri = FileProvider.getUriForFile(context,
                                        authority,
                                        tempPhotoFile)
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
                                } else {
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(tempPhotoFile))
                                }
                                startActivityForResult(intent, 2)

                            } else {
                                ToastUtils.showShort("请授予应用相关权限")
                            }

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
                isCamera = true
                dismiss()

                XXPermissions.with(this@DetailInfoActivity).permission(Permission.CAMERA)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            if (all) {

                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 启动系统相机
                                startActivityForResult(intent, 3)

                            } else {
                                ToastUtils.showShort("请授予应用相关权限")
                            }

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
                isCamera = false
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
                    .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION).setImageSpanCount(3)
                    .isDisplayCamera(true).isPreviewImage(true).isEmptyResultReturn(true)
                    .setLanguage(LanguageConfig.CHINESE).setSelectorUIStyle(selectorStyle)
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>) {

                            lifeChoosePath = result[0].realPath
                            lifeBitmap = ImageUtils.getBitmap(result[0].realPath)

                            if (mLifeFirstUrl == "") {

                                val intent = Intent(this@DetailInfoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 111)

                            } else if (mLifeSecondUrl == "") {

                                val intent = Intent(this@DetailInfoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 222)

                            } else if (mLifeThirdUrl == "") {

                                val intent = Intent(this@DetailInfoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 333)

                            } else if (mLifeFourUrl == "") {

                                val intent = Intent(this@DetailInfoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 444)

                            } else if (mLifeFiveUrl == "") {

                                val intent = Intent(this@DetailInfoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 555)

                            }

                        }

                        override fun onCancel() {
                        }

                    })
            }
        }

    }

    inner class LifeDeleteDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_life_delete

        override fun onCreate() {
            super.onCreate()

            findViewById<TextView>(R.id.tv_dialog_life_delete_cancel).setOnClickListener {
                dismiss()
            }

            findViewById<TextView>(R.id.tv_dialog_life_delete_confirm).setOnClickListener {

                when (lifeDeleteMode) {
                    "one" -> {
                        deleteLifePhoto(SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_ID))
                    }
                    "two" -> {
                        deleteLifePhoto(SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_ID))
                    }
                    "three" -> {
                        deleteLifePhoto(SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_ID))
                    }
                    "four" -> {
                        deleteLifePhoto(SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FOUR_ID))
                    }
                    "five" -> {
                        deleteLifePhoto(SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FIVE_ID))
                    }
                }
                dismiss()
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


    override fun onDestroy() {
        super.onDestroy()


        getIndustryPresent.unregisterCallback(this)

        getJobPresent.unregisterCallback(this)

        doFaceDetectPresent.unregisterCallback(this)

        doIdentityVerifyPresent.unregisterCallback(this)

        updateBaseInfoPresent.unregisterCallback(this)

        updateMoreInfoPresent.unregisterCallback(this)

        updateDemandInfoPresent.unregisterCallback(this)

        doUploadAvatarPresent.unregisterCallback(this)

        uploadPhotoPresent.unregisterCallback(this)

        doTextVerifyPresent.unregisterCallback(this)

        doDeletePhotoPresent.unregisterCallback(this)

    }


}


