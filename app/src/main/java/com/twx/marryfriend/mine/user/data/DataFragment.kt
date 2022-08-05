package com.twx.marryfriend.mine.user.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aigestudio.wheelpicker.WheelPicker
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
import com.blankj.utilcode.util.FileUtils
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.util.NalUnitUtil
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
import com.twx.marryfriend.bean.*
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.guide.baseInfo.BaseInfoActivity
import com.twx.marryfriend.mine.life.LifePhotoActivity
import com.twx.marryfriend.mine.record.AudioRecorder
import com.twx.marryfriend.mine.user.data.adapter.DataBaseAdapter
import com.twx.marryfriend.mine.user.data.adapter.DataMoreAdapter
import com.twx.marryfriend.net.callback.*
import com.twx.marryfriend.net.impl.*
import com.twx.marryfriend.utils.GlideEngine
import com.twx.marryfriend.view.LoadingAnimation.AVLoadingIndicatorView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_data.*
import java.io.*
import java.util.*

class DataFragment : Fragment(), IDoUpdateMoreInfoCallback, IDoUpdateBaseInfoCallback,
    IDoFaceDetectCallback, IDoUploadPhotoCallback, IDoUpdateProportionCallback,
    IDoUpdateGreetInfoCallback {

    // 敏感字
    private var banTextList: MutableList<String> = arrayListOf()

    // 是否具有敏感词
    private var haveBanText = false

    // 资料完成度
    private var proportion = 0

    // 头像数据
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


    // 是否正在播放
    private var isPlaying = false

    private var isCurrentDown = false
    private var mCountDownTimer: CountDownTimer? = null

    // 录音按钮当前模式
    private var recordMode = "start"

    // 录音文件路径
    private var recordPath = ""

    // 录音工具
    private lateinit var audioRecorder: AudioRecorder

    private lateinit var mediaPlayer: MediaPlayer


    // 时间选择器数据
    // 年
    var year = 0
    private var mYearPosition = 0
    private var mYearList: MutableList<Int> = arrayListOf()

    // 月
    var month = 0
    private var mMonthPosition = 0
    private var mMonthList: MutableList<Int> = arrayListOf()

    // 日
    var day = 0
    private var mDayPosition = 0
    private var mDayList: MutableList<Int> = arrayListOf()

    private var mHeightList: MutableList<Int> = arrayListOf()
    private var mIncomeList: MutableList<String> = arrayListOf()

    private var mJobFirstList: MutableList<String> = arrayListOf()
    private var mJobIdFirstList: MutableList<Int> = arrayListOf()
    private var mJobSecondList: MutableList<String> = arrayListOf()
    private var mJobIdSecondList: MutableList<Int> = arrayListOf()

    // 城市数据
    private lateinit var cityDate: CityBean

    private var mCityFirstList: MutableList<String> = arrayListOf()
    private var mCityIdFirstList: MutableList<String> = arrayListOf()
    private var mCitySecondList: MutableList<String> = arrayListOf()
    private var mCityIdSecondList: MutableList<String> = arrayListOf()
    private var mCityThirdList: MutableList<String> = arrayListOf()
    private var mCityIdThirdList: MutableList<String> = arrayListOf()


    private var mWeightList: MutableList<String> = arrayListOf()
    private var mBodyList: MutableList<String> = arrayListOf()

    private var baseInfoList: MutableList<String> = arrayListOf()
    private var moreInfoList: MutableList<String> = arrayListOf()

    private lateinit var baseAdapter: DataBaseAdapter
    private lateinit var moreAdapter: DataMoreAdapter

    private lateinit var client: BosClient

    private lateinit var doUpdateMoreInfoPresent: doUpdateMoreInfoPresentImpl
    private lateinit var doUpdateBaseInfoPresent: doUpdateBaseInfoPresentImpl

    private lateinit var doFaceDetectPresent: doFaceDetectPresentImpl
    private lateinit var uploadPhotoPresent: doUploadPhotoPresentImpl
    private lateinit var updateProportionPresent: doUpdateProportionPresentImpl
    private lateinit var doUpdateGreetPresent: doUpdateGreetInfoPresentImpl


    private var lastBaseInfo = ""
    private var lastMoreInfo = ""

    private var xx = false

    private var introduceDialog: BasePopupView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {

        doUpdateMoreInfoPresent = doUpdateMoreInfoPresentImpl.getsInstance()
        doUpdateMoreInfoPresent.registerCallback(this)

        doUpdateBaseInfoPresent = doUpdateBaseInfoPresentImpl.getsInstance()
        doUpdateBaseInfoPresent.registerCallback(this)

        doFaceDetectPresent = doFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        uploadPhotoPresent = doUploadPhotoPresentImpl.getsInstance()
        uploadPhotoPresent.registerCallback(this)

        updateProportionPresent = doUpdateProportionPresentImpl.getsInstance()
        updateProportionPresent.registerCallback(this)

        doUpdateGreetPresent = doUpdateGreetInfoPresentImpl.getsInstance()
        doUpdateGreetPresent.registerCallback(this)

        mTempPhotoPath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"
        mDestination = Uri.fromFile(File(requireActivity().cacheDir, "photoCropImage.jpeg"))

        mPhotoPath = requireActivity().externalCacheDir.toString() + File.separator + "head.png"

        recordPath = "/storage/emulated/0/Android/data/com.jiaou.love/cache/record.wav"

        audioRecorder = AudioRecorder.getInstance()
        mediaPlayer = MediaPlayer()

        initTimeData()

        initInfo()

        baseAdapter = DataBaseAdapter(DataProvider.dataBaseData, baseInfoList)
        moreAdapter = DataMoreAdapter(DataProvider.dataMoreData, moreInfoList)

        val baseScrollManager: LinearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        val moreScrollManager: LinearLayoutManager = object : LinearLayoutManager(context) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        rv_user_data_base.layoutManager = baseScrollManager
        rv_user_data_base.adapter = baseAdapter

        rv_user_data_more.layoutManager = moreScrollManager
        rv_user_data_more.adapter = moreAdapter



        sb_user_data_progress.setOnTouchListener(View.OnTouchListener { v, event -> true })


        if (SPStaticUtils.getString(Constant.ME_AVATAR, "") == "") {
            // 先判断性别
            if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
                iv_user_data_avatar.setImageResource(R.mipmap.icon_avatar_upload_male)
            } else {
                iv_user_data_avatar.setImageResource(R.mipmap.icon_avatar_upload_female)
            }
        } else {
            Glide.with(requireContext()).load(SPStaticUtils.getString(Constant.ME_AVATAR))
                .into(iv_user_data_avatar)
        }

        if (SPStaticUtils.getString(Constant.ME_INTRODUCE, "") != "") {
            iv_user_data_introduce.visibility = View.GONE
            tv_user_data_introduce.text = SPStaticUtils.getString(Constant.ME_INTRODUCE, "")
        }

        if (SPStaticUtils.getString(Constant.ME_TA, "") != "") {
            iv_user_data_ideal.visibility = View.GONE
            tv_user_data_ideal.text = SPStaticUtils.getString(Constant.ME_TA, "")
        }

        updateLife()

    }

    private fun initData() {

        for (i in 0..80) {
            mHeightList.add(140 + i)
        }

        mIncomeList.add("保密")
        mIncomeList.add("五千及以下")
        mIncomeList.add("五千~一万")
        mIncomeList.add("一万~两万")
        mIncomeList.add("两万~四万")
        mIncomeList.add("四万~七万")
        mIncomeList.add("七万及以上")


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

        cityDate = GsonUtils.fromJson(SPStaticUtils.getString(Constant.CITY_JSON_DATE),
            CityBean::class.java)

        getJobCityFirstList()
        getJobCitySecondList(0)
        getJobCityThirdList(0, 0)

        for (i in 0..60) {
            mWeightList.add("${40 + i}kg")
        }

        // 先判断性别
        if (SPStaticUtils.getInt(Constant.ME_SEX, 1) == 1) {
            mBodyList.add("保密")
            mBodyList.add("一般")
            mBodyList.add("瘦长")
            mBodyList.add("运动员型")
            mBodyList.add("比较魁梧")
            mBodyList.add("壮实")
        } else {
            mBodyList.add("保密")
            mBodyList.add("一般")
            mBodyList.add("瘦长")
            mBodyList.add("苗条")
            mBodyList.add("高大美丽")
            mBodyList.add("丰满")
            mBodyList.add("富线条美")
        }

        if (SPStaticUtils.getString(Constant.ME_VOICE_LONG, "") != "") {
            rl_user_data_voice_non.visibility = View.GONE
            rl_user_data_voice.visibility = View.VISIBLE

            val time = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "0").toLong()
            var formatTime = if (time.div(1000) / 60 >= 10) {
                if (time.div(1000) % 60 >= 10) {
                    "${time.div(1000) / 60} : ${time.div(1000) % 60}"
                } else {
                    "${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                }
            } else {
                if (time.div(1000) % 60 >= 10) {
                    "0${time.div(1000) / 60} : ${time.div(1000) % 60}"
                } else {
                    "0${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                }
            }

            tv_user_data_voice.text = formatTime

        } else {
            rl_user_data_voice_non.visibility = View.VISIBLE
            rl_user_data_voice.visibility = View.GONE
        }


//        val banBean: BanBean =
//            GsonUtils.fromJson(SPStaticUtils.getString(Constant.BAN_TEXT), BanBean::class.java)
//
//        val x = EncodeUtils.base64Decode(banBean.data.array_string)
//
//        val y = String(x)
//        var yy = "{\"data\":$y}"
//        var aa =
//            com.twx.marryfriend.utils.GsonUtils.parseObject(yy, BaseInfoActivity.Test::class.java)
//
//        for (i in 0.until(aa.data.size)) {
//            banTextList.add(aa.data[i])
//        }

    }

    private fun initPresent() {

        lastBaseInfo = getBaseInfo()
        lastMoreInfo = getMoreInfo()

        updateDateUI()

        xx = false

        val config: BosClientConfiguration = BosClientConfiguration()
        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://adrmf.gz.bcebos.com"
        client = BosClient(config)


        introduceDialog = XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(IntroduceDialog(requireContext()))

    }


    // 验证码倒计时
    private fun startCurrentDownTimer(mBeginTime: Long) {
        isCurrentDown = true
        mCountDownTimer = object : CountDownTimer(mBeginTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                var text = ""
                if (millisUntilFinished.div(1000) / 60 >= 10) {

                    if (millisUntilFinished.div(1000) % 60 >= 10) {
                        text =
                            "${millisUntilFinished.div(1000) / 60} : ${millisUntilFinished.div(1000) % 60}"
                    } else {
                        text =
                            "${millisUntilFinished.div(1000) / 60} : 0${millisUntilFinished.div(1000) % 60}"
                    }

                } else {
                    if (millisUntilFinished.div(1000) % 60 >= 10) {
                        text =
                            "0${millisUntilFinished.div(1000) / 60} : ${millisUntilFinished.div(1000) % 60}"
                    } else {
                        text = "0${millisUntilFinished.div(1000) / 60} : 0${
                            millisUntilFinished.div(1000) % 60
                        }"
                    }
                }
                tv_user_data_voice.text = text
            }

            override fun onFinish() {
                isCurrentDown = false

                // 暂停播放
                iv_user_data_voice.setImageResource(R.drawable.ic_data_voice_play)

                val time = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "0").toLong()
                var formatTime = if (time.div(1000) / 60 >= 10) {
                    if (time.div(1000) % 60 >= 10) {
                        "${time.div(1000) / 60} : ${time.div(1000) % 60}"
                    } else {
                        "${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                    }
                } else {
                    if (time.div(1000) % 60 >= 10) {
                        "0${time.div(1000) / 60} : ${time.div(1000) % 60}"
                    } else {
                        "0${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                    }
                }

                tv_user_data_voice.text = formatTime

                isPlaying = !isPlaying

            }
        }.start()
    }

    private fun initEvent() {

        showFirstDialog()

        iv_user_data_avatar.setOnClickListener {
            ToastUtils.showShort("上传头像")

            XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(PhotoGuideDialog(requireContext()))
                .show()

        }

        rl_user_data_voice_non.setOnClickListener {

            XXPermissions.with(context)
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>?,
                        all: Boolean,
                    ) {
                        XPopup.Builder(context)
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isDestroyOnDismiss(true)
                            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                            .asCustom(VoiceDialog(requireContext()))
                            .show()
                    }

                    override fun onDenied(
                        permissions: MutableList<String>?,
                        never: Boolean,
                    ) {
                        ToastUtils.showShort("请授予应用所需权限。")
                    }
                })

        }

        rl_user_data_voice.setOnClickListener {

            XXPermissions.with(context)
                .permission(Permission.RECORD_AUDIO)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>?,
                        all: Boolean,
                    ) {
                        XPopup.Builder(context)
                            .dismissOnTouchOutside(false)
                            .dismissOnBackPressed(false)
                            .isDestroyOnDismiss(true)
                            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                            .asCustom(VoiceDialog(requireContext()))
                            .show()
                    }

                    override fun onDenied(
                        permissions: MutableList<String>?,
                        never: Boolean,
                    ) {
                        ToastUtils.showShort("请授予应用所需权限。")
                    }
                })

        }

        iv_user_data_voice.setOnClickListener {

            if (!isPlaying) {
                // 开始播放
                iv_user_data_voice.setImageResource(R.drawable.ic_data_voice_pause)

                startCurrentDownTimer((SPStaticUtils.getString(Constant.ME_VOICE_LONG, "0")
                    .toLong()))

                mediaPlayer.reset()
                mediaPlayer.setDataSource(recordPath);
                mediaPlayer.prepare();
                mediaPlayer.start();

                isPlaying = !isPlaying
            } else {
                // 暂停播放
                iv_user_data_voice.setImageResource(R.drawable.ic_data_voice_play)
                mediaPlayer.stop();

                mCountDownTimer?.cancel()

                val time = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "0").toLong()
                var formatTime = if (time.div(1000) / 60 >= 10) {
                    if (time.div(1000) % 60 >= 10) {
                        "${time.div(1000) / 60} : ${time.div(1000) % 60}"
                    } else {
                        "${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                    }
                } else {
                    if (time.div(1000) % 60 >= 10) {
                        "0${time.div(1000) / 60} : ${time.div(1000) % 60}"
                    } else {
                        "0${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                    }
                }

                tv_user_data_voice.text = formatTime

                isPlaying = !isPlaying
            }

        }

        ll_user_data_introduce.setOnClickListener {
            introduceDialog!!.show()
        }

        ll_user_data_ideal.setOnClickListener {

            XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(IdealDialog(requireContext()))
                .show()

        }

        tv_user_data_life_next.setOnClickListener {
            val intent = Intent(context, LifePhotoActivity::class.java)
            intent.putExtra("activity", "data")
            startActivityForResult(intent, 0)
        }

        iv_user_data_life_next.setOnClickListener {
            val intent = Intent(context, LifePhotoActivity::class.java)
            intent.putExtra("activity", "data")
            startActivityForResult(intent, 0)
        }

        ll_user_data_life.setOnClickListener {
            val intent = Intent(context, LifePhotoActivity::class.java)
            intent.putExtra("activity", "data")
            startActivityForResult(intent, 0)
        }

        baseAdapter.setOnItemClickListener(object : DataBaseAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> showNameDialog()
                    1 -> showSexDialog()
                    2 -> {
                        if (SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {
                            ToastUtils.showShort("已经实名认证, 生日无法修改")
                        } else {
                            showBirthDialog()
                        }
                    }
                    3 -> showHeightDialog()
                    4 -> showIncomeDialog()
                    5 -> showWorkPlaceDialog()
                    6 -> showEduDialog()
                    7 -> showMarryStateDialog()
                    8 -> showTargetDialog()
                }
            }
        })

        moreAdapter.setOnItemClickListener(object : DataMoreAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                when (position) {
                    0 -> {
                        showHaveChildDialog()
                    }
                    1 -> {
                        showWantChildDialog()
                    }
                    2 -> {
                        showJobDialog()
                    }
                    3 -> {
                        showHouseDialog()
                    }
                    4 -> {
                        showCarDialog()
                    }
                    5 -> {
                        showHomeDialog()
                    }
                    6 -> {
                        showWeightDialog()
                    }
                    7 -> {
                        showBodyDialog()
                    }
                }
            }
        })

    }

    // 判断是否为闰年
    private fun injustLeapYear(s: Int): Boolean {
        var isLeapYear = false
        isLeapYear = (s % 4 == 0 && s % 400 != 0) || (s % 400 == 0)
        return isLeapYear
    }

    // 进入界面时显示一个弹窗
    private fun showFirstDialog() {

        if (SPStaticUtils.getString(Constant.ME_NAME, "") == "") {
            // 昵称
            showNameDialog()
        } else {
            if (SPStaticUtils.getInt(Constant.ME_SEX, 0) == 0) {
                // 性别
                showSexDialog()
            } else {
                if (SPStaticUtils.getString(Constant.ME_BIRTH, "") == "") {
                    // 生日
                    showBirthDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_HEIGHT, 0) == 0) {
                        // 身高
                        showHeightDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_INCOME, 7) == 7) {
                            // 月收入
                            showIncomeDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                                // 有没有孩子
                                showHaveChildDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                    // 想不想要孩子
                                    showWantChildDialog()
                                } else {
                                    if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                            "") == ""
                                    ) {
                                        // 职业
                                        showJobDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                            // 购房情况
                                            showHouseDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                                // 购车情况
                                                showCarDialog()
                                            } else {
                                                if (SPStaticUtils.getString(Constant.ME_HOME,
                                                        "") == ""
                                                ) {
                                                    // 籍贯
                                                    showHomeDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                            0) == 0
                                                    ) {
                                                        // 体重
                                                        showWeightDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                                10) == 10
                                                        ) {
                                                            // 体型
                                                            showBodyDialog()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 点击选项之后显示下一个弹窗
    private fun showNextDialog(position: Int) {
        when (position) {
            0 -> {
                if (SPStaticUtils.getInt(Constant.ME_SEX, 0) == 0) {
                    // 性别
                    showSexDialog()
                } else {
                    if (SPStaticUtils.getString(Constant.ME_BIRTH, "") == "") {
                        // 生日
                        showBirthDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_HEIGHT, 0) == 0) {
                            // 身高
                            showHeightDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_INCOME, 7) == 7) {
                                // 月收入
                                showIncomeDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                                    // 有没有孩子
                                    showHaveChildDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                        // 想不想要孩子
                                        showWantChildDialog()
                                    } else {
                                        if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                                "") == ""
                                        ) {
                                            // 职业
                                            showJobDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                                // 购房情况
                                                showHouseDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                                    // 购车情况
                                                    showCarDialog()
                                                } else {
                                                    if (SPStaticUtils.getString(Constant.ME_HOME,
                                                            "") == ""
                                                    ) {
                                                        // 籍贯
                                                        showHomeDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                                0) == 0
                                                        ) {
                                                            // 体重
                                                            showWeightDialog()
                                                        } else {
                                                            if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                                    10) == 10
                                                            ) {
                                                                // 体型
                                                                showBodyDialog()
                                                            } else {
                                                                updateDateUI()
                                                                ToastUtils.showShort("刷新界面")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                if (SPStaticUtils.getString(Constant.ME_BIRTH, "") == "") {
                    // 生日
                    showBirthDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_HEIGHT, 0) == 0) {
                        // 身高
                        showHeightDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_INCOME, 7) == 7) {
                            // 月收入
                            showIncomeDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                                // 有没有孩子
                                showHaveChildDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                    // 想不想要孩子
                                    showWantChildDialog()
                                } else {
                                    if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                            "") == ""
                                    ) {
                                        // 职业
                                        showJobDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                            // 购房情况
                                            showHouseDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                                // 购车情况
                                                showCarDialog()
                                            } else {
                                                if (SPStaticUtils.getString(Constant.ME_HOME,
                                                        "") == ""
                                                ) {
                                                    // 籍贯
                                                    showHomeDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                            0) == 0
                                                    ) {
                                                        // 体重
                                                        showWeightDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                                10) == 10
                                                        ) {
                                                            // 体型
                                                            showBodyDialog()
                                                        } else {
                                                            updateDateUI()
                                                            ToastUtils.showShort("刷新界面")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                if (SPStaticUtils.getInt(Constant.ME_HEIGHT, 0) == 0) {
                    // 身高
                    showHeightDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_INCOME, 7) == 7) {
                        // 月收入
                        showIncomeDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                            // 有没有孩子
                            showHaveChildDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                // 想不想要孩子
                                showWantChildDialog()
                            } else {
                                if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                        "") == ""
                                ) {
                                    // 职业
                                    showJobDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                        // 购房情况
                                        showHouseDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                            // 购车情况
                                            showCarDialog()
                                        } else {
                                            if (SPStaticUtils.getString(Constant.ME_HOME,
                                                    "") == ""
                                            ) {
                                                // 籍贯
                                                showHomeDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                        0) == 0
                                                ) {
                                                    // 体重
                                                    showWeightDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                            10) == 10
                                                    ) {
                                                        // 体型
                                                        showBodyDialog()
                                                    } else {
                                                        updateDateUI()
                                                        ToastUtils.showShort("刷新界面")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                if (SPStaticUtils.getInt(Constant.ME_INCOME, 7) == 7) {
                    // 月收入
                    showIncomeDialog()
                } else {


                    if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                        // 有没有孩子
                        showHaveChildDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                            // 想不想要孩子
                            showWantChildDialog()
                        } else {
                            if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                    "") == ""
                            ) {
                                // 职业
                                showJobDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                    // 购房情况
                                    showHouseDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                        // 购车情况
                                        showCarDialog()
                                    } else {
                                        if (SPStaticUtils.getString(Constant.ME_HOME,
                                                "") == ""
                                        ) {
                                            // 籍贯
                                            showHomeDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                    0) == 0
                                            ) {
                                                // 体重
                                                showWeightDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                        10) == 10
                                                ) {
                                                    // 体型
                                                    showBodyDialog()
                                                } else {
                                                    updateDateUI()
                                                    ToastUtils.showShort("刷新界面")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            4 -> {
                if (SPStaticUtils.getString(Constant.ME_WORK, "") == "") {
                    //工作地区
                    showWorkPlaceDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_EDU, 0) == 0) {
                        // 学历
                        showEduDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0) == 0) {
                            // 婚姻
                            showMarryStateDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0) == 0) {
                                // 恋爱目标
                                showTargetDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                                    // 有没有孩子
                                    showHaveChildDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                        // 想不想要孩子
                                        showWantChildDialog()
                                    } else {
                                        if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                                "") == ""
                                        ) {
                                            // 职业
                                            showJobDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                                // 购房情况
                                                showHouseDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                                    // 购车情况
                                                    showCarDialog()
                                                } else {
                                                    if (SPStaticUtils.getString(Constant.ME_HOME,
                                                            "") == ""
                                                    ) {
                                                        // 籍贯
                                                        showHomeDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                                0) == 0
                                                        ) {
                                                            // 体重
                                                            showWeightDialog()
                                                        } else {
                                                            if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                                    10) == 10
                                                            ) {
                                                                // 体型
                                                                showBodyDialog()
                                                            } else {
                                                                updateDateUI()
                                                                ToastUtils.showShort("刷新界面")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            5 -> {
                if (SPStaticUtils.getInt(Constant.ME_EDU, 0) == 0) {
                    // 学历
                    showEduDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0) == 0) {
                        // 婚姻
                        showMarryStateDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0) == 0) {
                            // 恋爱目标
                            showTargetDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                                // 有没有孩子
                                showHaveChildDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                    // 想不想要孩子
                                    showWantChildDialog()
                                } else {
                                    if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                            "") == ""
                                    ) {
                                        // 职业
                                        showJobDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                            // 购房情况
                                            showHouseDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                                // 购车情况
                                                showCarDialog()
                                            } else {
                                                if (SPStaticUtils.getString(Constant.ME_HOME,
                                                        "") == ""
                                                ) {
                                                    // 籍贯
                                                    showHomeDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                            0) == 0
                                                    ) {
                                                        // 体重
                                                        showWeightDialog()
                                                    } else {
                                                        if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                                10) == 10
                                                        ) {
                                                            // 体型
                                                            showBodyDialog()
                                                        } else {
                                                            updateDateUI()
                                                            ToastUtils.showShort("刷新界面")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            6 -> {
                if (SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0) == 0) {
                    // 婚姻
                    showMarryStateDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0) == 0) {
                        // 恋爱目标
                        showTargetDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                            // 有没有孩子
                            showHaveChildDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                                // 想不想要孩子
                                showWantChildDialog()
                            } else {
                                if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                        "") == ""
                                ) {
                                    // 职业
                                    showJobDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                        // 购房情况
                                        showHouseDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                            // 购车情况
                                            showCarDialog()
                                        } else {
                                            if (SPStaticUtils.getString(Constant.ME_HOME,
                                                    "") == ""
                                            ) {
                                                // 籍贯
                                                showHomeDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                        0) == 0
                                                ) {
                                                    // 体重
                                                    showWeightDialog()
                                                } else {
                                                    if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                            10) == 10
                                                    ) {
                                                        // 体型
                                                        showBodyDialog()
                                                    } else {
                                                        updateDateUI()
                                                        ToastUtils.showShort("刷新界面")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            7 -> {
                if (SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0) == 0) {
                    // 恋爱目标
                    showTargetDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                        // 有没有孩子
                        showHaveChildDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                            // 想不想要孩子
                            showWantChildDialog()
                        } else {
                            if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                    "") == ""
                            ) {
                                // 职业
                                showJobDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                    // 购房情况
                                    showHouseDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                        // 购车情况
                                        showCarDialog()
                                    } else {
                                        if (SPStaticUtils.getString(Constant.ME_HOME,
                                                "") == ""
                                        ) {
                                            // 籍贯
                                            showHomeDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                    0) == 0
                                            ) {
                                                // 体重
                                                showWeightDialog()
                                            } else {
                                                if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                        10) == 10
                                                ) {
                                                    // 体型
                                                    showBodyDialog()
                                                } else {
                                                    updateDateUI()
                                                    ToastUtils.showShort("刷新界面")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            8 -> {
                if (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 0) == 0) {
                    // 有没有孩子
                    showHaveChildDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                        // 想不想要孩子
                        showWantChildDialog()
                    } else {
                        if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                                "") == ""
                        ) {
                            // 职业
                            showJobDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                                // 购房情况
                                showHouseDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                    // 购车情况
                                    showCarDialog()
                                } else {
                                    if (SPStaticUtils.getString(Constant.ME_HOME,
                                            "") == ""
                                    ) {
                                        // 籍贯
                                        showHomeDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                                0) == 0
                                        ) {
                                            // 体重
                                            showWeightDialog()
                                        } else {
                                            if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                    10) == 10
                                            ) {
                                                // 体型
                                                showBodyDialog()
                                            } else {
                                                updateDateUI()
                                                ToastUtils.showShort("刷新界面")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            9 -> {
                if (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 0) == 0) {
                    // 想不想要孩子
                    showWantChildDialog()
                } else {
                    if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                            "") == ""
                    ) {
                        // 职业
                        showJobDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                            // 购房情况
                            showHouseDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                                // 购车情况
                                showCarDialog()
                            } else {
                                if (SPStaticUtils.getString(Constant.ME_HOME,
                                        "") == ""
                                ) {
                                    // 籍贯
                                    showHomeDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                            0) == 0
                                    ) {
                                        // 体重
                                        showWeightDialog()
                                    } else {
                                        if (SPStaticUtils.getInt(Constant.ME_BODY,
                                                10) == 10
                                        ) {
                                            // 体型
                                            showBodyDialog()
                                        } else {
                                            updateDateUI()
                                            ToastUtils.showShort("刷新界面")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            10 -> {
                if (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "") == "") {
                    // 职业
                    showJobDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                        // 购房情况
                        showHouseDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                            // 购车情况
                            showCarDialog()
                        } else {
                            if (SPStaticUtils.getString(Constant.ME_HOME,
                                    "") == ""
                            ) {
                                // 籍贯
                                showHomeDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                        0) == 0
                                ) {
                                    // 体重
                                    showWeightDialog()
                                } else {
                                    if (SPStaticUtils.getInt(Constant.ME_BODY,
                                            10) == 10
                                    ) {
                                        // 体型
                                        showBodyDialog()
                                    } else {
                                        updateDateUI()
                                        ToastUtils.showShort("刷新界面")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            11 -> {
                if (SPStaticUtils.getInt(Constant.ME_HOUSE, 0) == 0) {
                    // 购房情况
                    showHouseDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                        // 购车情况
                        showCarDialog()
                    } else {
                        if (SPStaticUtils.getString(Constant.ME_HOME,
                                "") == ""
                        ) {
                            // 籍贯
                            showHomeDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                    0) == 0
                            ) {
                                // 体重
                                showWeightDialog()
                            } else {
                                if (SPStaticUtils.getInt(Constant.ME_BODY,
                                        10) == 10
                                ) {
                                    // 体型
                                    showBodyDialog()
                                } else {
                                    updateDateUI()
                                    ToastUtils.showShort("刷新界面")
                                }
                            }
                        }
                    }
                }
            }
            12 -> {
                if (SPStaticUtils.getInt(Constant.ME_CAR, 0) == 0) {
                    // 购车情况
                    showCarDialog()
                } else {
                    if (SPStaticUtils.getString(Constant.ME_HOME,
                            "") == ""
                    ) {
                        // 籍贯
                        showHomeDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                                0) == 0
                        ) {
                            // 体重
                            showWeightDialog()
                        } else {
                            if (SPStaticUtils.getInt(Constant.ME_BODY,
                                    10) == 10
                            ) {
                                // 体型
                                showBodyDialog()
                            } else {
                                updateDateUI()
                                ToastUtils.showShort("刷新界面")
                            }
                        }
                    }
                }
            }
            13 -> {
                if (SPStaticUtils.getString(Constant.ME_HOME, "") == "") {
                    // 籍贯
                    showHomeDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_WEIGHT,
                            0) == 0
                    ) {
                        // 体重
                        showWeightDialog()
                    } else {
                        if (SPStaticUtils.getInt(Constant.ME_BODY,
                                10) == 10
                        ) {
                            // 体型
                            showBodyDialog()
                        } else {
                            updateDateUI()
                            ToastUtils.showShort("刷新界面")
                        }
                    }
                }
            }
            14 -> {
                if (SPStaticUtils.getInt(Constant.ME_WEIGHT, 0) == 0) {
                    // 体重
                    showWeightDialog()
                } else {
                    if (SPStaticUtils.getInt(Constant.ME_BODY,
                            10) == 10
                    ) {
                        // 体型
                        showBodyDialog()
                    } else {
                        updateDateUI()
                        ToastUtils.showShort("刷新界面")
                    }
                }
            }
            15 -> {
                if (SPStaticUtils.getInt(Constant.ME_BODY, 10) == 10) {
                    // 体型
                    showBodyDialog()
                } else {
                    updateDateUI()
                    ToastUtils.showShort("刷新界面")
                }
            }
        }

    }


    // 获取、更新生活照
    private fun updateLife() {
        val lifePhotoOne = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE, "")
        val lifePhotoOneState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "0")

        val lifePhotoTwo = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO, "")
        val lifePhotoTwoState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

        val lifePhotoThree = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE, "")
        val lifePhotoThreeState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

        if (lifePhotoOne != "") {
            if (lifePhotoTwo != "") {
                if (lifePhotoThree != "") {
                    // 有三张图片,全部正常显示
                    ThreadUtils.runOnUiThread {
                        Glide.with(this).load(lifePhotoOne).into(iv_user_data_life_one)
                        if (lifePhotoOneState == "0") {
                            tv_user_data_life_one.visibility = View.VISIBLE
                        } else {
                            tv_user_data_life_one.visibility = View.GONE
                        }

                        rl_user_data_life_two.visibility = View.VISIBLE
                        Glide.with(this).load(lifePhotoTwo).into(iv_user_data_life_two)
                        if (lifePhotoTwoState == "0") {
                            tv_user_data_life_two.visibility = View.VISIBLE
                        } else {
                            tv_user_data_life_two.visibility = View.GONE
                        }

                        rl_user_data_life_three.visibility = View.VISIBLE
                        Glide.with(this).load(lifePhotoThree).into(iv_user_data_life_three)
                        if (lifePhotoThreeState == "0") {
                            tv_user_data_life_three.visibility = View.VISIBLE
                        } else {
                            tv_user_data_life_three.visibility = View.GONE
                        }

                    }
                } else {
                    // 有两张图片，,第一、二张正常显示，第三张显示为添加
                    ThreadUtils.runOnUiThread {
                        Glide.with(this).load(lifePhotoOne).into(iv_user_data_life_one)
                        if (lifePhotoOneState == "0") {
                            tv_user_data_life_one.visibility = View.VISIBLE
                        } else {
                            tv_user_data_life_one.visibility = View.GONE
                        }

                        rl_user_data_life_two.visibility = View.VISIBLE
                        Glide.with(this).load(lifePhotoTwo).into(iv_user_data_life_two)
                        if (lifePhotoTwoState == "0") {
                            tv_user_data_life_two.visibility = View.VISIBLE
                        } else {
                            tv_user_data_life_two.visibility = View.GONE
                        }

                        rl_user_data_life_three.visibility = View.VISIBLE
                        Glide.with(this).load(R.drawable.ic_data_life_add).into(iv_user_data_life_three)
                    }
                }
            } else {
                // 有一张图片,第一张正常显示，第二张显示为添加
                ThreadUtils.runOnUiThread {
                    Glide.with(this).load(lifePhotoOne).into(iv_user_data_life_one)
                    if (lifePhotoOneState == "0") {
                        tv_user_data_life_one.visibility = View.VISIBLE
                    } else {
                        tv_user_data_life_one.visibility = View.GONE
                    }

                    rl_user_data_life_two.visibility = View.VISIBLE
                    Glide.with(this).load(R.drawable.ic_data_life_add).into(iv_user_data_life_two)
                    rl_user_data_life_three.visibility = View.GONE
                }
            }
        } else {
            ThreadUtils.runOnUiThread {
                Glide.with(this).load(R.drawable.ic_data_life_add).into(iv_user_data_life_one)
                rl_user_data_life_two.visibility = View.GONE
                rl_user_data_life_three.visibility = View.GONE
            }
        }

    }

    // 获取所有数据更新视图
    private fun updateDateUI() {

        var nick = ""
        var sex = ""
        var birth = ""
        var height = ""
        var income = ""
        var workPlace = ""
        var edu = ""
        var marryState = ""
        var target = ""

        var haveChild = ""
        var wantChild = ""
        var job = ""
        var house = ""
        var car = ""
        var home = ""
        var weight = ""
        var body = ""

        nick = when (SPStaticUtils.getString(Constant.ME_NAME, "")) {
            "" -> "未填写"
            else -> SPStaticUtils.getString(Constant.ME_NAME, "")
        }

        when (SPStaticUtils.getInt(Constant.ME_SEX, 3)) {
            0 -> sex = "保密"
            1 -> sex = "男"
            2 -> sex = "女"
            3 -> sex = "未填写"
        }

        birth = when (SPStaticUtils.getString(Constant.ME_BIRTH, "")) {
            "" -> "未填写"
            else -> SPStaticUtils.getString(Constant.ME_BIRTH, "").toString()
        }

        height = when (SPStaticUtils.getInt(Constant.ME_HEIGHT, 0)) {
            0 -> "未填写"
            else -> SPStaticUtils.getInt(Constant.ME_HEIGHT, 0).toString()
        }

        job = when (SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME, "")) {
            "" -> "未填写"
            else -> "${
                SPStaticUtils.getString(Constant.ME_INDUSTRY_NAME,
                    "")
            }-${SPStaticUtils.getString(Constant.ME_OCCUPATION_NAME, "")}"
        }

        when (SPStaticUtils.getInt(Constant.ME_INCOME, 7)) {
            0 -> income = "保密"
            1 -> income = "五千及以下"
            2 -> income = "五千~一万"
            3 -> income = "一万~两万"
            4 -> income = "两万~四万"
            5 -> income = "四万~七万"
            6 -> income = "七万及以上"
            7 -> income = "未填写"
        }

        workPlace = when (SPStaticUtils.getString(Constant.ME_WORK, "")) {
            "" -> "未填写"
            else -> SPStaticUtils.getString(Constant.ME_WORK, "")
        }

        when (SPStaticUtils.getInt(Constant.ME_EDU, 0)) {
            0 -> edu = "未填写"
            1 -> edu = "大专以下"
            2 -> edu = "大专"
            3 -> edu = "本科"
            4 -> edu = "硕士"
            5 -> edu = "博士"
        }

        when (SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0)) {
            0 -> marryState = "未填写"
            1 -> marryState = "未婚"
            2 -> marryState = "离异"
            3 -> marryState = "丧偶"
        }

        when (SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0)) {
            0 -> target = "未填写"
            1 -> target = "短期内想结婚"
            2 -> target = "合适就结婚"
            3 -> target = "先认真谈恋爱再说"
            4 -> target = "没考虑清楚"
        }

        when (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 4)) {
            0 -> haveChild = "没有孩子"
            1 -> haveChild = "有孩子且住在一起"
            2 -> haveChild = "有孩子偶尔会住一起"
            3 -> haveChild = "有但不在身边"
            4 -> haveChild = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 4)) {
            0 -> wantChild = "视情况而定"
            1 -> wantChild = "想要孩子"
            2 -> wantChild = "不想要孩子"
            3 -> wantChild = "以后再告诉你"
            4 -> wantChild = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.ME_HOUSE, 5)) {
            0 -> house = "和家人同住"
            1 -> house = "已购房"
            2 -> house = "租房"
            3 -> house = "打算婚后购房"
            4 -> house = "住在单位宿舍"
            5 -> house = "未填写"
        }

        when (SPStaticUtils.getInt(Constant.ME_CAR, 0)) {
            0 -> car = "未填写"
            1 -> car = "买了"
            2 -> car = "没买"
        }

        home = when (SPStaticUtils.getString(Constant.ME_HOME, "")) {
            "" -> "未填写"
            else -> SPStaticUtils.getString(Constant.ME_HOME, "")
        }

        weight = when (SPStaticUtils.getInt(Constant.ME_WEIGHT, 0)) {
            0 -> "未填写"
            else -> "${SPStaticUtils.getInt(Constant.ME_WEIGHT, 0)}kg"
        }

        body = when (SPStaticUtils.getInt(Constant.ME_BODY, 10)) {
            10 -> "未填写"
            else -> mBodyList[SPStaticUtils.getInt(Constant.ME_BODY, 10)]
        }

        baseInfoList.clear()
        moreInfoList.clear()

        baseInfoList.add(nick)
        baseInfoList.add(sex)
        baseInfoList.add(birth)
        baseInfoList.add(height)
        baseInfoList.add(income)
        baseInfoList.add(workPlace)
        baseInfoList.add(edu)
        baseInfoList.add(marryState)
        baseInfoList.add(target)

        moreInfoList.add(haveChild)
        moreInfoList.add(wantChild)
        moreInfoList.add(job)
        moreInfoList.add(house)
        moreInfoList.add(car)
        moreInfoList.add(home)
        moreInfoList.add(weight)
        moreInfoList.add(body)

        baseAdapter.notifyDataSetChanged()
        moreAdapter.notifyDataSetChanged()

        getDataCompletion()


        // 更新数据
        if (xx) {
            Log.i("guo", "数据改变")
            update()
        } else {
            xx = true
            Log.i("guo", "数据未变，不用更新")
        }

    }

    // 加载数据
    private fun initInfo() {
        baseInfoList.clear()
        moreInfoList.clear()

        for (i in 0.until(DataProvider.targetBaseData.size)) {
            baseInfoList.add("未填写")
        }

        for (j in 0.until(DataProvider.targetMoreData.size)) {
            moreInfoList.add("未填写")
        }

    }

    // 通过数据中“未填写”的数量来判断资料完成度
    private fun getDataCompletion() {

        var baseSize = 0
        var moreSize = 0

        for (i in 0.until(baseInfoList.size)) {
            if (baseInfoList[i] == "未填写") {
                baseSize++
            }
        }

        for (i in 0.until(moreInfoList.size)) {
            if (moreInfoList[i] == "未填写") {
                moreSize++
            }
        }

        var progress = 0

        when (baseSize + moreSize) {
            0 -> {
                progress = 100
            }
            2 -> {
                progress = 90
            }
            3 -> {
                progress = 80
            }
            4 -> {
                progress = 72
            }
            5 -> {
                progress = 68
            }
            6 -> {
                progress = 60
            }
            7 -> {
                progress = 53
            }
            8 -> {
                progress = 47
            }
            9 -> {
                progress = 39
            }
            10 -> {
                progress = 30
            }
            11 -> {
                progress = 20
            }
            12 -> {
                progress = 10
            }
            13 -> {
                progress = 0
            }
        }

        tv_user_data_progress.text = "( 完成$progress% )"
        sb_user_data_progress.progress = progress

    }

    // 加载时间数据，提供给时间选择器
    private fun initTimeData() {

        // 获取年月日
        year = TimeUtils.date2String(TimeUtils.getNowDate(), "yyyy").toInt()
        month = TimeUtils.date2String(TimeUtils.getNowDate(), "MM").toInt()
        day = TimeUtils.date2String(TimeUtils.getNowDate(), "dd").toInt()

        mYearList.clear()
        mMonthList.clear()
        mDayList.clear()


        for (i in 0..(100)) {
            mYearList.add(year - 100 + i)
        }

        for (i in 0.until(12)) {
            mMonthList.add(1 + i)
        }

        for (i in 0.until(31)) {
            mDayList.add(1 + i)
        }

        if (SPStaticUtils.getInt(Constant.ME_BIRTH_YEAR, 0) != 0) {

            val year = mYearList[SPStaticUtils.getInt(Constant.ME_BIRTH_YEAR, 0)]
            val month = mMonthList[SPStaticUtils.getInt(Constant.ME_BIRTH_MONTH, 0)]
            val day = mDayList[SPStaticUtils.getInt(Constant.ME_BIRTH_DAY, 0)]

            SPStaticUtils.put(Constant.ME_BIRTH, "$year-$month-$day")
        }

    }

    // 获取月份数据
    private fun getMonthData(yearPosition: Int) {
        mMonthList.clear()
        if (mYearList[yearPosition] == year) {
            for (i in 0.until(month)) {
                mMonthList.add(1 + i)
            }
        } else {
            for (i in 0.until(12)) {
                mMonthList.add(1 + i)
            }
        }
    }

    // 获取日数据
    private fun getDayData(yearPosition: Int, MonthPosition: Int) {
        mDayList.clear()
        if (mYearList[yearPosition] == year && mMonthList[MonthPosition] == month) {
            for (i in 0.until(day)) {
                mDayList.add(1 + i)
            }
        } else {
            var maxDay = 31

            when (MonthPosition + 1) {
                1 -> {
                    maxDay = 31
                }
                2 -> {
                    maxDay = if (injustLeapYear(mYearList[yearPosition])) {
                        29
                    } else {
                        28
                    }
                }
                3 -> {
                    maxDay = 31
                }
                4 -> {
                    maxDay = 30
                }
                5 -> {
                    maxDay = 31
                }
                6 -> {
                    maxDay = 30
                }
                7 -> {
                    maxDay = 31
                }
                8 -> {
                    maxDay = 31
                }
                9 -> {
                    maxDay = 30
                }
                10 -> {
                    maxDay = 31
                }
                11 -> {
                    maxDay = 30
                }
                12 -> {
                    maxDay = 31
                }
            }

            for (i in 0.until(maxDay)) {
                mDayList.add(1 + i)
            }

        }
    }

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
            mCityIdFirstList.add(cityDate.data[i].code)
        }
    }

    // 市
    private fun getJobCitySecondList(i: Int) {
        mCitySecondList.clear()
        mCityIdSecondList.clear()
        for (j in 0.until(cityDate.data[i].cityList.size)) {
            mCitySecondList.add(cityDate.data[i].cityList[j].name)
            mCityIdSecondList.add(cityDate.data[i].cityList[j].code)
        }
    }

    // 县
    private fun getJobCityThirdList(i: Int, j: Int) {
        mCityThirdList.clear()
        mCityIdThirdList.clear()
        if (cityDate.data[i].cityList[j].areaList.isNotEmpty()) {
            for (k in 0.until(cityDate.data[i].cityList[j].areaList.size)) {
                mCityThirdList.add(cityDate.data[i].cityList[j].areaList[k].name)
                mCityIdThirdList.add(cityDate.data[i].cityList[j].areaList[k].code)
            }
        } else {
            mCityThirdList.add("")
            mCityIdThirdList.add("")
        }
    }

    // 昵称
    private fun showNameDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(NameDialog(requireContext()))
            .show()
    }

    // 性别
    private fun showSexDialog() {

//        XPopup.Builder(context)
//            .dismissOnTouchOutside(false)
//            .dismissOnBackPressed(false)
//            .isDestroyOnDismiss(true)
//            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
//            .asCustom(SexDialog(requireContext()))
//            .show()

        if (SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {
            XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(SexVerifyDialog(requireContext()))
                .show()
        } else {
            XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(SexNonVerifyDialog(requireContext()))
                .show()
        }

    }

    // 生日
    private fun showBirthDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(BirthDialog(requireContext()))
            .show()
    }

    // 身高
    private fun showHeightDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HeightDialog(requireContext()))
            .show()
    }

    // 月收入
    private fun showIncomeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(IncomeDialog(requireContext()))
            .show()
    }

    // 工作地区
    private fun showWorkPlaceDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(WorkPlaceDialog(requireContext()))
            .show()
    }

    // 学历
    private fun showEduDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(EduDialog(requireContext()))
            .show()
    }

    // 婚况
    private fun showMarryStateDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(MarryStateDialog(requireContext()))
            .show()
    }

    // 恋爱目标
    private fun showTargetDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(TargetDialog(requireContext()))
            .show()
    }

    // 有没有孩子
    private fun showHaveChildDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HaveChildDialog(requireContext()))
            .show()
    }

    // 想不想要孩子
    private fun showWantChildDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(WantChildDialog(requireContext()))
            .show()
    }

    // 职业
    private fun showJobDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(JobDialog(requireContext()))
            .show()
    }

    // 购房情况
    private fun showHouseDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HouseDialog(requireContext()))
            .show()
    }

    // 购车情况
    private fun showCarDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(CarDialog(requireContext()))
            .show()
    }

    // 籍贯
    private fun showHomeDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(HomeDialog(requireContext()))
            .show()
    }

    // 体重
    private fun showWeightDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(WeightDialog(requireContext()))
            .show()
    }

    // 体型
    private fun showBodyDialog() {
        XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(false)
            .isDestroyOnDismiss(true)
            .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(BodyDialog(requireContext()))
            .show()
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
            this.activity?.let { it1 ->
                UCrop.of<Any>(source, it) // 长宽比
                    .withAspectRatio(1f, 1f) // 图片大小
                    .withMaxResultSize(512, 512) // 配置参数
                    .withOptions(options)
                    .start(it1)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    // 更新生活照
                    updateLife()
                }
                // 拍照返回至裁切
                2 -> {
                    val temp = File(mTempPhotoPath)
                    startPhotoCropActivity(Uri.fromFile(temp))
                }
            }
        }
    }

    fun handlePhotoCropResult(result: Intent) {

        ll_user_data_loading.visibility = View.VISIBLE

        deleteTempPhotoFile()
        val resultUri = UCrop.getOutput(result)
        if (null != resultUri) {
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,
                    resultUri)
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
    fun handlePhotoCropError(result: Intent) {
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


    // ---------------------------------- 上传信息 ----------------------------------

    // 开始上传信息
    private fun update() {


        Log.i("guo", "getMoreInfo")
        val moreInfoMap: MutableMap<String, String> = TreeMap()
        moreInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        moreInfoMap[Contents.MORE_UPDATE] = getMoreInfo()
        doUpdateMoreInfoPresent.doUpdateMoreInfo(moreInfoMap)



        Log.i("guo", "getBaseInfo")
        val baseInfoMap: MutableMap<String, String> = TreeMap()
        baseInfoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        baseInfoMap[Contents.BASE_UPDATE] = getBaseInfo()
        doUpdateBaseInfoPresent.doUpdateBaseInfo(baseInfoMap)


        // 上传头像
        if (mPhotoUrl != "") {
            val uploadPhotoMap: MutableMap<String, String> = TreeMap()
            uploadPhotoMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
            uploadPhotoMap[Contents.IMAGE_URL] = mPhotoUrl
            uploadPhotoMap[Contents.FILE_TYPE] = "png"
            uploadPhotoMap[Contents.FILE_NAME] = "head.png"
            uploadPhotoMap[Contents.CONTENT] = "0"
            uploadPhotoMap[Contents.KIND] = 1.toString()
            uploadPhotoPresent.doUploadPhoto(uploadPhotoMap)
        } else {
            Log.i("guo", "getBaseInfo")
        }

        // 更新资料完善度
        val proportionMap: MutableMap<String, String> = TreeMap()
        proportionMap[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
        proportionMap[Contents.PROPORTION] = proportion.toString()
        updateProportionPresent.doUpdateProportion(proportionMap)

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
        val province = SPStaticUtils.getString(Constant.ME_WORK_PROVINCE_CODE, "")
        val cityCode = SPStaticUtils.getString(Constant.ME_WORK_CITY_CODE, "")
        val cityName = SPStaticUtils.getString(Constant.ME_WORK_CITY_NAME, "")
        val home = SPStaticUtils.getString(Constant.ME_HOME, "")
        val income = SPStaticUtils.getInt(Constant.ME_INCOME, 7)
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
        val body = SPStaticUtils.getInt(Constant.ME_BODY, 10)
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

    // 获取招呼语信息
    private fun getGreetInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val voiceUrl = SPStaticUtils.getString(Constant.ME_VOICE, "")
        val voiceLong = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "")
        val voiceName = SPStaticUtils.getString(Constant.ME_VOICE_NAME, "")
        val greet = SPStaticUtils.getString(Constant.ME_GREET, "")

        Log.i("guo", "voice_long : $voiceLong")

        val greetInfo =
            " {\"user_sex\":                    $sex, " +
                    "\"voice_url\":           \"$voiceUrl\"," +
                    "\"voice_long\":          \"$voiceLong\"," +
                    "\"voice_name\":          \"$voiceName\"," +
                    " \"zhaohuyu_content\":   \"$greet\"}"

        return greetInfo

    }


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateGreetInfoSuccess(updateGreetInfoBean: UpdateGreetInfoBean?) {
    }

    override fun onDoUpdateGreetInfoError() {

    }

    override fun onDoUpdateProportionSuccess(updateProportionInfoBean: UpdateProportionInfoBean?) {
        lastBaseInfo = getBaseInfo()
        lastMoreInfo = getMoreInfo()
    }

    override fun onDoUpdateProportionError() {

    }

    override fun onDoFaceDetectSuccess(faceDetectBean: FaceDetectBean) {

        if (ll_user_data_loading != null) {
            ll_user_data_loading.visibility = View.GONE
        }


        if (faceDetectBean.conclusion == "合规") {

            if (iv_user_data_avatar != null) {
                iv_user_data_avatar.setImageDrawable(null)
            }

            iv_user_data_avatar.setImageBitmap(photoBitmap)

            FileUtils.delete(mPhotoPath)

            photoBitmap?.let { saveBitmap(it, mPhotoPath) }

            Log.i("guo", "path : $mPhotoPath")

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


                SPStaticUtils.put(Constant.ME_AVATAR, mPhotoUrl)

                Log.i("guo", mPhotoUrl)

            }.start()


        } else {
            ToastUtils.showShort(faceDetectBean.data[0].msg)
        }

    }

    override fun onDoFaceDetectError() {
        ll_user_data_loading.visibility = View.GONE
    }

    private fun saveBitmap(bitmap: Bitmap, targetPath: String): String {
        ImageUtils.save(bitmap, targetPath, Bitmap.CompressFormat.PNG)
        return targetPath
    }

    override fun onDoUploadPhotoSuccess(uploadPhotoBean: UploadPhotoBean?) {
        lastBaseInfo = getBaseInfo()
        lastMoreInfo = getMoreInfo()
    }

    override fun onDoUploadPhotoError() {

    }

    override fun onDoUpdateMoreInfoSuccess(updateMoreInfoBean: UpdateMoreInfoBean?) {
        lastBaseInfo = getBaseInfo()
        lastMoreInfo = getMoreInfo()
    }

    override fun onDoUpdateMoreInfoError() {

    }

    override fun onDoUpdateBaseInfoSuccess(baseInfoUpdateBean: BaseInfoUpdateBean?) {
        lastBaseInfo = getBaseInfo()
        lastMoreInfo = getMoreInfo()
    }

    override fun onDoUpdateBaseInfoError() {

    }

    // ---------------------------------- 基础弹窗 ----------------------------------

    // 上传头像界面

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

                PictureSelector.create(context)
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

                            Log.i("guo", temp.path)
                            startPhotoCropActivity(Uri.fromFile(temp))

                        }

                        override fun onCancel() {

                            ToastUtils.showShort("取消")

                        }

                    })


            }

            findViewById<TextView>(R.id.tv_dialog_photo_camera).setOnClickListener {
                ToastUtils.showShort("打开相机")

                dismiss()

                XXPermissions.with(context)
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
                                val contentUri: Uri = FileProvider.getUriForFile(context,
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

    // 添加自我介绍
    inner class IntroduceDialog(context: Context) : FullScreenPopupView(context),
        IDoTextVerifyCallback {

        private var isNeedUpdate = false

        private var size = 0
        private var text = ""

        private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl


        override fun getImplLayoutId(): Int = R.layout.dialog_set_introduce

        override fun onCreate() {
            super.onCreate()

            doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
            doTextVerifyPresent.registerCallback(this)

            val close = findViewById<ImageView>(R.id.iv_dialog_set_introduce_close)
            val content = findViewById<EditText>(R.id.et_dialog_set_introduce_content)
            val sum = findViewById<TextView>(R.id.tv_dialog_set_introduce_sum)
            val confirm = findViewById<TextView>(R.id.tv_dialog_set_introduce_confirm)

            content.setText(SPStaticUtils.getString(Constant.ME_INTRODUCE, ""))
            text = SPStaticUtils.getString(Constant.ME_INTRODUCE, "")
            size = SPStaticUtils.getString(Constant.ME_INTRODUCE, "").length
            sum.text = SPStaticUtils.getString(Constant.ME_INTRODUCE, "").length.toString()

            if (size >= 10) {
                confirm.setBackgroundResource(R.drawable.shape_bg_common_next)
            } else {
                confirm.setBackgroundResource(R.drawable.shape_bg_common_next_non)
            }

            close.setOnClickListener {
                isNeedUpdate = false
                dismiss()
            }

            content.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {

                }

                override fun afterTextChanged(s: Editable) {

                    size = s.length
                    text = s.toString()

                    sum.text = s.length.toString()

                    if (s.length >= 10) {
                        confirm.setBackgroundResource(R.drawable.shape_bg_common_next)
                    } else {
                        confirm.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    }

                    if (s.length >= 1000) {
                        ToastUtils.showShort("已达到输入文字最大数量")
                        KeyboardUtils.hideSoftInput(requireActivity())
                    }
                }
            })

            confirm.setOnClickListener {
                if (size >= 10) {

//                    for (i in 0.until(banTextList.size)) {
//                        val code = banTextList[i]
//                        if (text.contains(code)) {
//                            haveBanText = true
//                        }
//                    }
//                    if (haveBanText) {
//                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
//                        text = ""
//                        content.setText("")
//                        haveBanText = false
//                    } else {
//                        // 保存数据
//                        SPStaticUtils.put(Constant.ME_INTRODUCE, text)
//                        isNeedUpdate = true
//                        dismiss()
//                    }

                    val map: MutableMap<String, String> = TreeMap()
                    map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                    map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                    map[Contents.TEXT] = text
                    doTextVerifyPresent.doTextVerify(map)

                } else {
                    ToastUtils.showShort("请输入至少10字内容")
                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            // 更新数据
            if (isNeedUpdate) {
                iv_user_data_introduce.visibility = View.GONE
                tv_user_data_introduce.text = SPStaticUtils.getString(Constant.ME_INTRODUCE, "")
            }
        }

        override fun onLoading() {}

        override fun onError() {}

        override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

            if (textVerifyBean.conclusion == "合规") {
                // 保存数据
                SPStaticUtils.put(Constant.ME_INTRODUCE, text)
                isNeedUpdate = true
                dismiss()
            } else {
                ToastUtils.showShort(textVerifyBean.data[0].msg)
                text = ""
                findViewById<EditText>(R.id.et_dialog_set_introduce_content).setText("")
                haveBanText = false
            }
        }

        override fun onDoTextVerifyError() {
            ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
        }

    }

    // 添加语音介绍
    inner class VoiceDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedUpdate = false

        override fun getImplLayoutId(): Int = R.layout.dialog_set_voice

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_set_voice_close)
            val delete = findViewById<LinearLayout>(R.id.ll_dialog_set_voice_delete)
            val button = findViewById<LinearLayout>(R.id.ll_dialog_set_voice_button)
            val confirm = findViewById<LinearLayout>(R.id.ll_dialog_set_voice_confirm)

            val state = findViewById<ImageView>(R.id.iv_dialog_set_voice_state)
            val animation = findViewById<AVLoadingIndicatorView>(R.id.avv_dialog_set_voice_state)

            val timer = findViewById<Chronometer>(R.id.timer)
            val mode = findViewById<TextView>(R.id.tv_dialog_set_voice_button)

            mode.text = "点击开始录音"
            recordMode = "start"

            close.setOnClickListener {
                isNeedUpdate = false
                dismiss()
            }

            recordMode = "start"

            button.setOnClickListener {

                when (recordMode) {
                    "start" -> {
                        mode.text = "点击结束"
                        recordMode = "stop"

                        state.setImageResource(R.drawable.ic_record_start)
                        state.visibility = View.GONE
                        animation.visibility = View.VISIBLE

                        FileUtils.delete(recordPath)

                        // 计时器
                        timer.base = SystemClock.elapsedRealtime() //计时器清零
                        val hour = (SystemClock.elapsedRealtime() - timer.base) / 1000 / 3600
                        timer.format = "0$hour:%s"
                        timer.start()

                        // 录音
                        audioRecorder.createDefaultAudio("record", context)
                        audioRecorder.startRecord(null)
                    }
                    "stop" -> {

                        Log.i("guo",
                            "time :${(SystemClock.elapsedRealtime() - timer.base).toString()}")

                        // 存储录音文件的长度
                        SPStaticUtils.put(Constant.ME_VOICE_LONG,
                            (SystemClock.elapsedRealtime() - timer.base).toString())
                        SPStaticUtils.put(Constant.ME_VOICE_NAME, "Greet")

                        mode.text = "点击播放"
                        delete.visibility = View.VISIBLE
                        confirm.visibility = View.VISIBLE
                        recordMode = "listen"
                        state.visibility = View.VISIBLE
                        animation.visibility = View.GONE
                        state.setImageResource(R.drawable.ic_record_play)

                        timer.stop()

                        audioRecorder.stopRecord()
                    }
                    "listen" -> {
                        ToastUtils.showShort("播放录音")
                        mode.text = "播放结束"
                        state.visibility = View.GONE
                        animation.visibility = View.VISIBLE
                        recordMode = "listenStop"

                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(recordPath);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                    "listenStop" -> {
                        ToastUtils.showShort("结束播放录音")
                        mode.text = "点击播放"
                        recordMode = "listen"
                        state.visibility = View.VISIBLE
                        animation.visibility = View.GONE

                        mediaPlayer.stop()
                    }

                }

            }

            delete.setOnClickListener {
                delete.visibility = View.GONE
                confirm.visibility = View.GONE
                state.setImageResource(R.drawable.ic_record_start)
                state.visibility = View.VISIBLE
                animation.visibility = View.GONE

                mode.text = "点击开始录音"
                recordMode = "start"

            }

            confirm.setOnClickListener {

                ToastUtils.showShort("录音选择完成，开始上传")

                Thread {

                    //上传Object
                    val file = File(recordPath)
                    // bucketName 为文件夹名 ，使用用户id来进行命名
                    // key值为保存文件名，试用固定的几种格式来命名

                    val putObjectFromFileResponse = client.putObject("user${
                        SPStaticUtils.getString(Constant.USER_ID,
                            "default")
                    }", FileUtils.getFileName(recordPath), file)

                    val mVoiceUrl = client.generatePresignedUrl("user${
                        SPStaticUtils.getString(Constant.USER_ID,
                            "default")
                    }", FileUtils.getFileName(recordPath), -1).toString()

                    Log.i("guo", mVoiceUrl)

                    SPStaticUtils.put(Constant.ME_VOICE, mVoiceUrl)

                    val map: MutableMap<String, String> = TreeMap()
                    map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID)
                    map[Contents.GREET_UPDATE] = getGreetInfo()
                    doUpdateGreetPresent.doUpdateGreetInfo(map)

                }.start()

                isNeedUpdate = true
                dismiss()

            }

        }

        override fun onDismiss() {
            super.onDismiss()
            // 更新数据的同时还需要上传数据
            if (isNeedUpdate) {
                rl_user_data_voice_non.visibility = View.GONE
                rl_user_data_voice.visibility = View.VISIBLE

                val time = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "0").toLong()
                var formatTime = ""

                formatTime = if (time.div(1000) / 60 >= 10) {
                    if (time.div(1000) % 60 >= 10) {
                        "${time.div(1000) / 60} : ${time.div(1000) % 60}"
                    } else {
                        "${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                    }
                } else {
                    if (time.div(1000) % 60 >= 10) {
                        "0${time.div(1000) / 60} : ${time.div(1000) % 60}"
                    } else {
                        "0${time.div(1000) / 60} : 0${time.div(1000) % 60}"
                    }
                }

                tv_user_data_voice.text = formatTime

            }
        }

    }

    // 添加心目中的TA
    inner class IdealDialog(context: Context) : FullScreenPopupView(context),
        IDoTextVerifyCallback {

        private var isNeedUpdate = false

        private var size = 0
        private var text = ""

        private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

        override fun getImplLayoutId(): Int = R.layout.dialog_set_ideal

        override fun onCreate() {
            super.onCreate()

            doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
            doTextVerifyPresent.registerCallback(this)

            val close = findViewById<ImageView>(R.id.iv_dialog_set_ideal_close)
            val content = findViewById<EditText>(R.id.et_dialog_set_ideal_content)
            val sum = findViewById<TextView>(R.id.tv_dialog_set_ideal_sum)
            val confirm = findViewById<TextView>(R.id.tv_dialog_set_ideal_confirm)

            content.setText(SPStaticUtils.getString(Constant.ME_TA, ""))
            text = SPStaticUtils.getString(Constant.ME_TA, "")
            size = SPStaticUtils.getString(Constant.ME_TA, "").length
            sum.text = SPStaticUtils.getString(Constant.ME_TA, "").length.toString()

            if (size >= 10) {
                confirm.setBackgroundResource(R.drawable.shape_bg_common_next)
            } else {
                confirm.setBackgroundResource(R.drawable.shape_bg_common_next_non)
            }

            close.setOnClickListener {
                isNeedUpdate = false
                dismiss()
            }

            content.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {

                }

                override fun afterTextChanged(s: Editable) {

                    size = s.length
                    text = s.toString()

                    sum.text = s.length.toString()

                    if (s.length >= 10) {
                        confirm.setBackgroundResource(R.drawable.shape_bg_common_next)
                    } else {
                        confirm.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                    }

                    if (s.length >= 1000) {
                        ToastUtils.showShort("已达到输入文字最大数量")
                        KeyboardUtils.hideSoftInput(requireActivity())
                    }
                }
            })

            confirm.setOnClickListener {
                if (size >= 10) {

//                    for (i in 0.until(banTextList.size)) {
//                        val code = banTextList[i]
//                        if (text.contains(code)) {
//                            haveBanText = true
//                        }
//                    }
//
//                    if (haveBanText) {
//                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
//                        text = ""
//                        content.setText("")
//                        haveBanText = false
//                    } else {
//                        // 保存数据
//                        SPStaticUtils.put(Constant.ME_TA, text)
//                        isNeedUpdate = true
//                        dismiss()
//                    }

                    val map: MutableMap<String, String> = TreeMap()
                    map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                    map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                    map[Contents.TEXT] = text
                    doTextVerifyPresent.doTextVerify(map)

                } else {
                    ToastUtils.showShort("请输入至少10字内容")
                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            // 更新数据
            if (isNeedUpdate) {
                iv_user_data_ideal.visibility = View.GONE
                tv_user_data_ideal.text = SPStaticUtils.getString(Constant.ME_TA, "")
            }
        }

        override fun onLoading() {}

        override fun onError() {}

        override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

            if (textVerifyBean.conclusion == "合规") {
                // 保存数据
                SPStaticUtils.put(Constant.ME_TA, text)
                isNeedUpdate = true
                dismiss()
            } else {
                ToastUtils.showShort(textVerifyBean.data[0].msg)
                text = ""
                findViewById<EditText>(R.id.et_dialog_set_ideal_content).setText("")
                haveBanText = false
            }

        }

        override fun onDoTextVerifyError() {
            ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
        }

    }

    // 昵称
    inner class NameDialog(context: Context) : FullScreenPopupView(context), IDoTextVerifyCallback {
        private var isNeedJump = false // 是否需要跳转

        private var name1 = ""

        private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_name

        override fun onCreate() {
            super.onCreate()

            doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
            doTextVerifyPresent.registerCallback(this)

            val close = findViewById<ImageView>(R.id.iv_user_data_name_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_name_skip)

            val name = findViewById<EditText>(R.id.et_user_data_name_name)
            val confirm = findViewById<TextView>(R.id.tv_user_data_name_confirm)

            confirm.setOnClickListener {
                name1 = name.text.toString()

                if (name1.isNotEmpty()) {

//                    for (i in 0.until(banTextList.size)) {
//                        val code = banTextList[i]
//                        if (name1.contains(code)) {
//                            haveBanText = true
//                        }
//                    }
//
//                    if (haveBanText) {
//                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
//                        name1 = ""
//                        name.setText("")
//                        haveBanText = false
//                    } else {
//                        // 保存数据
//                        SPStaticUtils.put(Constant.ME_NAME, name1)
//                        isNeedJump = true
//                        dismiss()
//                    }

                    val map: MutableMap<String, String> = TreeMap()
                    map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                    map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                    map[Contents.TEXT] = name1
                    doTextVerifyPresent.doTextVerify(map)

                } else {
                    ToastUtils.showShort("请输入新昵称")
                }

            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(0)
            } else {
                ToastUtils.showShort("刷新界面")
                updateDateUI()
            }
        }

        override fun onLoading() {

        }

        override fun onError() {

        }

        override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

            if (textVerifyBean.conclusion == "合规") {
                // 保存数据
                SPStaticUtils.put(Constant.ME_NAME, name1)
                isNeedJump = true
                dismiss()
            } else {
                ToastUtils.showShort(textVerifyBean.data[0].msg)
                name1 = ""
                findViewById<EditText>(R.id.et_user_data_name_name).setText("")
                haveBanText = false
            }

        }

        override fun onDoTextVerifyError() {
            ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
        }

    }

    // 性别
    inner class SexDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_sex

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_sex_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_sex_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_sex_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_sex_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_sex_three)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SEX, 0)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SEX, 1)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_SEX, 2)
                isNeedJump = true
                dismiss()
            }



            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_SEX, 5)) {
                5 -> {
                }
                0 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(1)
            } else {
                ToastUtils.showShort("刷新界面")
                updateDateUI()
            }
        }

    }

    // 性别(已认证)
    inner class SexVerifyDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var confirm: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_sex_verify

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_sex_verify_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_sex_verify_skip)

            confirm = findViewById<TextView>(R.id.tv_user_data_sex_verify_confirm)

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            confirm.setOnClickListener {
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(1)
            } else {
                ToastUtils.showShort("刷新界面")
                updateDateUI()
            }
        }

    }

    // 性别(未认证)
    inner class SexNonVerifyDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var confirm: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_sex_verify_non

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_sex_verify_non_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_sex_verify_non_skip)

            confirm = findViewById<TextView>(R.id.tv_user_data_sex_verify_non_confirm)

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            confirm.setOnClickListener {
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(1)
            } else {
                ToastUtils.showShort("刷新界面")
                updateDateUI()
            }
        }

    }

    // 生日
    inner class BirthDialog(context: Context) : FullScreenPopupView(context) {
        // 三个转盘

        private var isNeedJump = false // 是否需要跳转


        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_birth

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_birth_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_birth_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_data_birth_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_data_birth_two)
            val wheelThree = findViewById<WheelPicker>(R.id.wp_user_data_birth_three)
            val confirm = findViewById<TextView>(R.id.tv_user_data_birth_confirm)

            wheelOne.data = mYearList
            wheelTwo.data = mMonthList
            wheelThree.data = mDayList

            mYearPosition = SPStaticUtils.getInt(Constant.ME_BIRTH_YEAR, 0)
            mMonthPosition = SPStaticUtils.getInt(Constant.ME_BIRTH_MONTH, 0)
            mDayPosition = SPStaticUtils.getInt(Constant.ME_BIRTH_DAY, 0)

            wheelOne.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_BIRTH_YEAR, 0), false)
            getMonthData(SPStaticUtils.getInt(Constant.ME_BIRTH_YEAR, 0))
            wheelTwo.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_BIRTH_MONTH, 0),
                false)
            getDayData(SPStaticUtils.getInt(Constant.ME_BIRTH_YEAR, 0),
                SPStaticUtils.getInt(Constant.ME_BIRTH_MONTH, 0))
            wheelThree.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_BIRTH_DAY, 0),
                false)

            // 是否为循环状态
            wheelOne.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelOne.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelOne.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelOne.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelOne.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelOne.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelOne.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelOne.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelOne.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelOne.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            wheelTwo.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelTwo.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelTwo.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelTwo.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelTwo.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelTwo.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelTwo.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelTwo.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelTwo.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelTwo.itemAlign = WheelPicker.ALIGN_CENTER


            // 是否为循环状态
            wheelThree.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelThree.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelThree.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelThree.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelThree.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelThree.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelThree.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelThree.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelThree.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelThree.itemAlign = WheelPicker.ALIGN_CENTER

            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mYearPosition = position

                getMonthData(mYearPosition)


                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mMonthPosition >= mMonthList.size) {
                    mMonthPosition = mMonthList.size - 1
                }

                getDayData(mYearPosition, mMonthPosition)

                // 当三级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ，
                if (mDayPosition >= mDayList.size) {
                    mDayPosition = mDayList.size - 1
                }

                wheelTwo.data = mMonthList
                wheelThree.data = mDayList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mMonthPosition = position

                getDayData(mYearPosition, mMonthPosition)

                // 当三级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位
                if (mDayPosition >= mDayList.size) {
                    mDayPosition = mDayList.size - 1
                }

                wheelThree.data = mDayList

            }

            wheelThree.setOnItemSelectedListener { picker, data, position ->

                mDayPosition = position

            }

            confirm.setOnClickListener {

                val birth =
                    "${mYearList[mYearPosition]}-${mMonthList[mMonthPosition]}-${mDayList[mDayPosition]}"

                SPStaticUtils.put(Constant.ME_BIRTH_YEAR, mYearPosition)
                SPStaticUtils.put(Constant.ME_BIRTH_MONTH, mMonthPosition)
                SPStaticUtils.put(Constant.ME_BIRTH_DAY, mDayPosition)

                SPStaticUtils.put(Constant.ME_BIRTH, birth)

                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(2)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }
    }

    // 身高
    inner class HeightDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mHeight = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_height


        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_height_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_height_skip)

            val wheel = findViewById<WheelPicker>(R.id.wp_user_data_height_container)
            val confirm = findViewById<TextView>(R.id.tv_user_data_height_confirm)

            wheel.data = mHeightList

            wheel.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_HEIGHT) - 140, false)

            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER

            wheel.setOnItemSelectedListener { picker, data, position ->
                mHeight = mHeightList[position]
            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mHeight)
                SPStaticUtils.put(Constant.ME_HEIGHT, mHeight)
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(3)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 月收入
    inner class IncomeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mIncome = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_income

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_income_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_income_skip)

            val wheel = findViewById<WheelPicker>(R.id.wp_user_data_income_container)
            val confirm = findViewById<TextView>(R.id.tv_user_data_income_confirm)

            wheel.data = mIncomeList
            wheel.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_INCOME, 0), false)


            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER


            wheel.setOnItemSelectedListener { picker, data, position ->
                mIncome = position
            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mIncome)
                SPStaticUtils.put(Constant.ME_INCOME, mIncome)
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(4)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 工作地区
    inner class WorkPlaceDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mCityFirstPosition = 0
        private var mCitySecondPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_address

        override fun onCreate() {
            super.onCreate()

            findViewById<TextView>(R.id.info).text = "工作地区"

            val close = findViewById<ImageView>(R.id.iv_user_target_address_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_address_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_target_address_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_target_address_two)

            val confirm = findViewById<TextView>(R.id.tv_user_target_address_confirm)

            wheelOne.data = mCityFirstList
            wheelTwo.data = mCitySecondList

            mCityFirstPosition = SPStaticUtils.getInt(Constant.ME_WORK_PROVINCE_PICK, 0)
            mCitySecondPosition = SPStaticUtils.getInt(Constant.ME_WORK_CITY_PICK, 0)

            wheelOne.setSelectedItemPosition(mCityFirstPosition, false)
            getJobCitySecondList(mCityFirstPosition)
            wheelTwo.setSelectedItemPosition(mCitySecondPosition, false)

            // 是否为循环状态
            wheelOne.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelOne.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelOne.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelOne.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelOne.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelOne.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelOne.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelOne.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelOne.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelOne.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            wheelTwo.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelTwo.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelTwo.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelTwo.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelTwo.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelTwo.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelTwo.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelTwo.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelTwo.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelTwo.itemAlign = WheelPicker.ALIGN_CENTER


            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mCityFirstPosition = position

                getJobCitySecondList(mCityFirstPosition)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mCitySecondPosition >= mCitySecondList.size) {
                    mCitySecondPosition = mCitySecondList.size - 1
                }

                wheelTwo.data = mCitySecondList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->
                mCitySecondPosition = position

            }

            confirm.setOnClickListener {

                val work =
                    " ${mCityFirstList[mCityFirstPosition]}-${mCitySecondList[mCitySecondPosition]}"

                ToastUtils.showShort(work)

                SPStaticUtils.put(Constant.ME_WORK, work)

                SPStaticUtils.put(Constant.ME_WORK_PROVINCE_NAME,
                    mCityFirstList[mCityFirstPosition])
                SPStaticUtils.put(Constant.ME_WORK_PROVINCE_CODE,
                    mCityIdFirstList[mCityFirstPosition])
                SPStaticUtils.put(Constant.ME_WORK_PROVINCE_PICK, mCityFirstPosition)
                SPStaticUtils.put(Constant.ME_WORK_CITY_NAME, mCitySecondList[mCitySecondPosition])
                SPStaticUtils.put(Constant.ME_WORK_CITY_CODE,
                    mCityIdSecondList[mCitySecondPosition])
                SPStaticUtils.put(Constant.ME_WORK_CITY_PICK, mCitySecondPosition)

                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(5)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 学历
    inner class EduDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView
        private lateinit var tv_five: TextView


        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_edu

        override fun onCreate() {
            super.onCreate()

            findViewById<TextView>(R.id.info).text = "学历"

            val close = findViewById<ImageView>(R.id.iv_user_data_edu_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_edu_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_edu_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_edu_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_edu_three)
            tv_four = findViewById<TextView>(R.id.tv_user_data_edu_four)
            tv_five = findViewById<TextView>(R.id.tv_user_data_edu_five)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_EDU, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_EDU, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_EDU, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_EDU, 4)
                isNeedJump = true
                dismiss()
            }

            tv_five.setOnClickListener {
                clearChoose()
                tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_five.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_EDU, 5)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_EDU, 0)) {
                0 -> {

                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
                5 -> {
                    tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_five.setTextColor(Color.parseColor("#FF4444"))
                }

            }

        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

            tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_five.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(6)
            } else {
                updateDateUI()
            }

        }

    }

    // 婚况
    inner class MarryStateDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_marrystate

        override fun onCreate() {
            super.onCreate()
            val close = findViewById<ImageView>(R.id.iv_user_data_marrystate_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_marrystate_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_marrystate_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_marrystate_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_marrystate_three)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_MARRY_STATE, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_MARRY_STATE, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_MARRY_STATE, 3)
                isNeedJump = true
                dismiss()
            }


            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_MARRY_STATE, 0)) {
                0 -> {
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
            }

        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(7)
            } else {
                updateDateUI()
            }
        }
    }

    // 恋爱目标
    inner class TargetDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_target

        override fun onCreate() {
            super.onCreate()
            val close = findViewById<ImageView>(R.id.iv_user_data_target_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_target_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_target_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_target_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_target_three)
            tv_four = findViewById<TextView>(R.id.tv_user_data_target_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_LOVE_TARGET, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_LOVE_TARGET, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_LOVE_TARGET, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_LOVE_TARGET, 4)
                isNeedJump = true
                dismiss()
            }


            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_LOVE_TARGET, 0)) {
                0 -> {
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
            }

        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(8)
            } else {
                updateDateUI()
            }

        }

    }

    // ---------------------------------- 更多弹窗 ----------------------------------

    // 有没有孩子
    inner class HaveChildDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_havechild

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_havechild_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_havechild_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_havechild_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_havechild_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_havechild_three)
            tv_four = findViewById<TextView>(R.id.tv_user_data_havechild_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HAVE_CHILD, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_HAVE_CHILD, 5)) {
                5 -> {
                }
                0 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {
            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(9)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 是否想要孩子
    inner class WantChildDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_wantchild

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_wantchild_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_wantchild_skip)


            tv_one = findViewById<TextView>(R.id.tv_user_data_wantchild_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_wantchild_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_wantchild_three)
            tv_four = findViewById<TextView>(R.id.tv_user_data_wantchild_four)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_WANT_CHILD, 4)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_WANT_CHILD, 5)) {
                5 -> {
                }
                0 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(10)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }
    }

    // 职业
    inner class JobDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mFirstJobPosition = 0
        private var mSecondJobPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_job

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_job_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_job_skip)

            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_data_job_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_data_job_two)
            val confirm = findViewById<TextView>(R.id.tv_user_data_job_confirm)

            wheelOne.data = mJobFirstList
            wheelTwo.data = mJobSecondList

            mFirstJobPosition = SPStaticUtils.getInt(Constant.ME_INDUSTRY_PICK, 0)
            mSecondJobPosition = SPStaticUtils.getInt(Constant.ME_OCCUPATION_PICK, 0)

            wheelOne.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_INDUSTRY_PICK, 0),
                false)
            getJobSecondList(SPStaticUtils.getInt(Constant.ME_INDUSTRY_PICK, 0))
            wheelTwo.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_OCCUPATION_PICK,
                0),
                false)

            // 是否为循环状态
            wheelOne.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelOne.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelOne.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelOne.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelOne.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelOne.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelOne.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelOne.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelOne.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelOne.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            wheelTwo.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelTwo.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelTwo.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelTwo.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelTwo.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelTwo.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelTwo.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelTwo.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelTwo.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelTwo.itemAlign = WheelPicker.ALIGN_CENTER

            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mFirstJobPosition = position

                getJobSecondList(mFirstJobPosition)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mSecondJobPosition >= mJobSecondList.size) {
                    mSecondJobPosition = mJobSecondList.size - 1
                }

                wheelTwo.data = mJobSecondList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->

                mSecondJobPosition = position

            }

            confirm.setOnClickListener {
                ToastUtils.showShort("${mJobFirstList[mFirstJobPosition]} - ${mJobIdFirstList[mFirstJobPosition]} ," + " ${mJobSecondList[mSecondJobPosition]} - ${mJobIdSecondList[mSecondJobPosition]}")
                SPStaticUtils.put(Constant.ME_INDUSTRY_NAME, mJobFirstList[mFirstJobPosition])
                SPStaticUtils.put(Constant.ME_INDUSTRY_CODE, mJobIdFirstList[mFirstJobPosition])
                SPStaticUtils.put(Constant.ME_INDUSTRY_PICK, mFirstJobPosition)

                SPStaticUtils.put(Constant.ME_OCCUPATION_NAME,
                    mJobSecondList[mSecondJobPosition])
                SPStaticUtils.put(Constant.ME_OCCUPATION_CODE,
                    mJobIdSecondList[mSecondJobPosition])
                SPStaticUtils.put(Constant.ME_OCCUPATION_PICK, mSecondJobPosition)
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(11)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 购房情况
    inner class HouseDialog(context: Context) : FullScreenPopupView(context) {
        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView
        private lateinit var tv_three: TextView
        private lateinit var tv_four: TextView
        private lateinit var tv_five: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_house

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_house_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_house_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_house_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_house_two)
            tv_three = findViewById<TextView>(R.id.tv_user_data_house_three)
            tv_four = findViewById<TextView>(R.id.tv_user_data_house_four)
            tv_five = findViewById<TextView>(R.id.tv_user_data_house_five)


            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 2)
                isNeedJump = true
                dismiss()
            }

            tv_three.setOnClickListener {
                clearChoose()
                tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_three.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 3)
                isNeedJump = true
                dismiss()
            }

            tv_four.setOnClickListener {
                clearChoose()
                tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_four.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 4)
                isNeedJump = true
                dismiss()
            }

            tv_five.setOnClickListener {
                clearChoose()
                tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_five.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_HOUSE, 5)
                isNeedJump = true
                dismiss()
            }


            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_HOUSE, 5)) {
                5 -> {
                }
                0 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                1 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_three.setTextColor(Color.parseColor("#FF4444"))
                }
                3 -> {
                    tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_four.setTextColor(Color.parseColor("#FF4444"))
                }
                4 -> {
                    tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_five.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

            tv_three.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_three.setTextColor(Color.parseColor("#101010"))

            tv_four.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_four.setTextColor(Color.parseColor("#101010"))

            tv_five.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_five.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(12)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }
    }

    // 购车情况
    inner class CarDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private lateinit var tv_one: TextView
        private lateinit var tv_two: TextView

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_car

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_car_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_car_skip)

            tv_one = findViewById<TextView>(R.id.tv_user_data_car_one)
            tv_two = findViewById<TextView>(R.id.tv_user_data_car_two)

            clearChoose()
            initChoose()

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            tv_one.setOnClickListener {
                clearChoose()
                tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_one.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_CAR, 1)
                isNeedJump = true
                dismiss()
            }

            tv_two.setOnClickListener {
                clearChoose()
                tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                tv_two.setTextColor(Color.parseColor("#FF4444"))
                SPStaticUtils.put(Constant.ME_CAR, 2)
                isNeedJump = true
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

        }

        private fun initChoose() {
            when (SPStaticUtils.getInt(Constant.ME_CAR, 0)) {
                0 -> {
                }
                1 -> {
                    tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_one.setTextColor(Color.parseColor("#FF4444"))
                }
                2 -> {
                    tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_check)
                    tv_two.setTextColor(Color.parseColor("#FF4444"))
                }
            }
        }

        private fun clearChoose() {

            tv_one.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_one.setTextColor(Color.parseColor("#101010"))

            tv_two.setBackgroundResource(R.drawable.shape_bg_dialog_choose_uncheck)
            tv_two.setTextColor(Color.parseColor("#101010"))

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(13)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }
    }

    // 籍贯
    inner class HomeDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mCityFirstPosition = 0
        private var mCitySecondPosition = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_target_address

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_target_address_close)
            val skip = findViewById<TextView>(R.id.tv_user_target_address_skip)


            val wheelOne = findViewById<WheelPicker>(R.id.wp_user_target_address_one)
            val wheelTwo = findViewById<WheelPicker>(R.id.wp_user_target_address_two)

            val confirm = findViewById<TextView>(R.id.tv_user_target_address_confirm)

            wheelOne.data = mCityFirstList
            wheelTwo.data = mCitySecondList

            mCityFirstPosition = SPStaticUtils.getInt(Constant.ME_HOME_PROVINCE_PICK, 0)
            mCitySecondPosition = SPStaticUtils.getInt(Constant.ME_HOME_CITY_PICK, 0)

            wheelOne.setSelectedItemPosition(mCityFirstPosition, false)
            getJobCitySecondList(mCityFirstPosition)
            wheelTwo.setSelectedItemPosition(mCitySecondPosition, false)

            // 是否为循环状态
            wheelOne.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelOne.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelOne.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelOne.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelOne.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelOne.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelOne.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelOne.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelOne.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelOne.itemAlign = WheelPicker.ALIGN_CENTER

            // 是否为循环状态
            wheelTwo.isCyclic = false
            // 当前选中的数据项文本颜色
            wheelTwo.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheelTwo.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheelTwo.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheelTwo.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheelTwo.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheelTwo.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheelTwo.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheelTwo.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheelTwo.itemAlign = WheelPicker.ALIGN_CENTER


            wheelOne.setOnItemSelectedListener { picker, data, position ->
                mCityFirstPosition = position

                getJobCitySecondList(mCityFirstPosition)

                // 当二级条目多的向少的移动时 ， 默认使选择的选项调整为最后一位 ， 不至于出现没有数据的情况
                if (mCitySecondPosition >= mCitySecondList.size) {
                    mCitySecondPosition = mCitySecondList.size - 1
                }

                wheelTwo.data = mCitySecondList

            }

            wheelTwo.setOnItemSelectedListener { picker, data, position ->
                mCitySecondPosition = position

            }

            confirm.setOnClickListener {

                val home =
                    " ${mCityFirstList[mCityFirstPosition]}-${mCitySecondList[mCitySecondPosition]}"

                ToastUtils.showShort(home)

                SPStaticUtils.put(Constant.ME_HOME, home)

                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_NAME,
                    mCityFirstList[mCityFirstPosition])
                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_CODE,
                    mCityIdFirstList[mCityFirstPosition])
                SPStaticUtils.put(Constant.ME_HOME_PROVINCE_PICK, mCityFirstPosition)
                SPStaticUtils.put(Constant.ME_HOME_CITY_NAME,
                    mCitySecondList[mCitySecondPosition])
                SPStaticUtils.put(Constant.ME_HOME_CITY_CODE,
                    mCityIdSecondList[mCitySecondPosition])
                SPStaticUtils.put(Constant.ME_HOME_CITY_PICK, mCitySecondPosition)

                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(14)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 体重
    inner class WeightDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mWeight = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_weight

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_weight_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_weight_skip)

            val wheel = findViewById<WheelPicker>(R.id.wp_user_data_weight_container)
            val confirm = findViewById<TextView>(R.id.tv_user_data_weight_confirm)

            wheel.data = mWeightList

            wheel.setSelectedItemPosition((SPStaticUtils.getInt(Constant.ME_WEIGHT) - 40),
                false)

            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER


            wheel.setOnItemSelectedListener { picker, data, position ->
                mWeight = position + 40
            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mWeight)
                SPStaticUtils.put(Constant.ME_WEIGHT, mWeight)
                isNeedJump = true
                dismiss()
            }

            close.setOnClickListener {
                isNeedJump = false
                dismiss()
            }

            skip.setOnClickListener {
                isNeedJump = false
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedJump) {
                showNextDialog(15)
            } else {
                updateDateUI()
                ToastUtils.showShort("刷新界面")
            }
        }

    }

    // 体型
    inner class BodyDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedJump = false // 是否需要跳转

        private var mBody = 0

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_body

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_body_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_body_skip)

            val wheel = findViewById<WheelPicker>(R.id.wp_user_data_body_container)
            val confirm = findViewById<TextView>(R.id.tv_user_data_body_confirm)

            wheel.data = mBodyList
            wheel.setSelectedItemPosition(SPStaticUtils.getInt(Constant.ME_BODY, 0), false)

            // 是否为循环状态
            wheel.isCyclic = false
            // 当前选中的数据项文本颜色
            wheel.selectedItemTextColor = Color.parseColor("#FF4444")
            // 数据项文本颜色
            wheel.itemTextColor = Color.parseColor("#9A9A9A")
            // 滚轮选择器数据项之间间距
            wheel.itemSpace = ConvertUtils.dp2px(40F)
            // 是否有指示器
            wheel.setIndicator(true)
            // 滚轮选择器指示器颜色，16位颜色值
            wheel.indicatorColor = Color.parseColor("#FFF5F5")
            // 滚轮选择器是否显示幕布
            wheel.setCurtain(true)
            // 滚轮选择器是否有空气感
            wheel.setAtmospheric(true)
            // 滚轮选择器是否开启卷曲效果
            wheel.isCurved = true
            // 设置滚轮选择器数据项的对齐方式
            wheel.itemAlign = WheelPicker.ALIGN_CENTER


            wheel.setOnItemSelectedListener { picker, data, position ->
                mBody = position
            }

            confirm.setOnClickListener {
                ToastUtils.showShort(mBody)
                SPStaticUtils.put(Constant.ME_BODY, mBody)
                dismiss()
            }

            close.setOnClickListener {
                dismiss()
            }

            skip.setOnClickListener {
                dismiss()
            }
        }

        override fun onDismiss() {
            super.onDismiss()
            updateDateUI()
            ToastUtils.showShort("刷新界面")
        }

    }

}