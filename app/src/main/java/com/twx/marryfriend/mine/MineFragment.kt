package com.twx.marryfriend.mine

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
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
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.BanBean
import com.twx.marryfriend.bean.FaceDetectBean
import com.twx.marryfriend.bean.PhotoListBean
import com.twx.marryfriend.bean.UpdateGreetInfoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.guide.baseInfo.BaseInfoActivity
import com.twx.marryfriend.mine.life.LifePhotoActivity
import com.twx.marryfriend.mine.record.AudioRecorder
import com.twx.marryfriend.mine.user.UserActivity
import com.twx.marryfriend.mine.verify.VerifyActivity
import com.twx.marryfriend.net.callback.IDoFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoUpdateGreetInfoCallback
import com.twx.marryfriend.net.callback.IGetPhotoListCallback
import com.twx.marryfriend.net.impl.doFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doUpdateGreetInfoPresentImpl
import com.twx.marryfriend.net.impl.getPhotoListPresentImpl
import com.twx.marryfriend.utils.GlideEngine
import com.twx.marryfriend.view.LoadingAnimation.AVLoadingIndicatorView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_mine.*
import java.io.*
import java.util.*

class MineFragment : Fragment(), IGetPhotoListCallback, IDoFaceDetectCallback,
    IDoUpdateGreetInfoCallback {

    // 头像暂存bitmap
    private var mBitmap: Bitmap? = null

    // 临时图片文件路径
    private var mTempPhotoPath = ""

    // 头像上传百度云的url
    private var mPhotoUrl = ""

    // 剪切后图像存放地址
    private var mPhotoPath = ""

    // 录音按钮当前模式
    private var recordMode = "start"

    // 录音文件路径
    private var recordPath = ""

    // 录音工具
    private lateinit var audioRecorder: AudioRecorder

    private lateinit var mediaPlayer: MediaPlayer


    private val dialogInfo: MutableList<String> = arrayListOf()

    // 敏感字
    private var banTextList: MutableList<String> = arrayListOf()

    // 是否具有敏感词
    private var haveBanText = false

    // 剪切后图像文件
    private var mDestination: Uri? = null

    private lateinit var client: BosClient

    private lateinit var getPhotoListPresent: getPhotoListPresentImpl
    private lateinit var doFaceDetectPresent: doFaceDetectPresentImpl
    private lateinit var doUpdateGreetPresent: doUpdateGreetInfoPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {

        SPStaticUtils.put(Constant.IS_IDENTITY_VERIFY, true)

        Glide.with(requireContext()).load(SPStaticUtils.getString(Constant.ME_AVATAR))
            .into(iv_mine_avatar)

        getDialogOrder()

        mTempPhotoPath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"
        mDestination = Uri.fromFile(File(requireActivity().cacheDir, "photoCropImage.jpeg"))

        mPhotoPath = requireActivity().externalCacheDir.toString() + File.separator + "photoPic.png"

        getPhotoListPresent = getPhotoListPresentImpl.getsInstance()
        getPhotoListPresent.registerCallback(this)

        doFaceDetectPresent = doFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        doUpdateGreetPresent = doUpdateGreetInfoPresentImpl.getsInstance()
        doUpdateGreetPresent.registerCallback(this)

        getAvatar()

        tv_mine_nick.text = SPStaticUtils.getString(Constant.ME_NAME, "未填写")

        for (i in 0.until(DataProvider.setInfoDialogData.size)) {
            dialogInfo.add(DataProvider.setInfoDialogData[i])
        }

    }

    private fun initData() {

        recordPath = "/storage/emulated/0/Android/data/com.weilai.marryfriend/cache/record.wav"

        audioRecorder = AudioRecorder.getInstance()
        mediaPlayer = MediaPlayer()

        val banBean: BanBean =
            GsonUtils.fromJson(SPStaticUtils.getString(Constant.BAN_TEXT), BanBean::class.java)

        val x = EncodeUtils.base64Decode(banBean.data.array_string)

        val y = String(x)
        var yy = "{\"data\":$y}"
        var aa =
            com.twx.marryfriend.utils.GsonUtils.parseObject(yy, BaseInfoActivity.Test::class.java)

        for (i in 0.until(aa.data.size)) {
            banTextList.add(aa.data[i])
        }

    }

    private fun initPresent() {

        val config: BosClientConfiguration = BosClientConfiguration()
        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://androidmarryfriend.gz.bcebos.com"

        client = BosClient(config)

    }

    private fun initEvent() {

        iv_mine_dialog_info.setOnClickListener {

            when (SPStaticUtils.getInt(Constant.SET_INFO_DIALOG_SUM, 3)) {
                0 -> {
                    // 上传头像

                    XPopup.Builder(context)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(PhotoGuideDialog(requireContext()))
                        .show()

                }
                1 -> {
                    // 上传生活照,留一个返回值

                    val intent = Intent(context, LifePhotoActivity::class.java)
                    startActivityForResult(intent, 1)

                }
                2 -> {
                    // 实名认证
                    val intent = Intent(context, VerifyActivity::class.java)
                    startActivity(intent)

                }
                3 -> {
                    // 添加爱好

                    XPopup.Builder(context)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(HobbyDialog(requireContext()))
                        .show()

                }
                4 -> {
                    // 添加招呼语

                    XPopup.Builder(context)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(GreetDialog(requireContext()))
                        .show()

                }
                5 -> {
                    // 添加自我介绍
                    XPopup.Builder(context)
                        .dismissOnTouchOutside(false)
                        .dismissOnBackPressed(false)
                        .isDestroyOnDismiss(true)
                        .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                        .asCustom(IntroduceDialog(requireContext()))
                        .show()

                }
                6 -> {
                    // 录语音

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
            }
        }

        iv_mine_nick.setOnClickListener {

            XPopup.Builder(context)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(NameDialog(requireContext()))
                .show()

        }

        iv_mine_avatar.setOnClickListener {
            startActivity(Intent(context, UserActivity::class.java))
        }

        rl_mine_user.setOnClickListener {
            startActivity(Intent(context, UserActivity::class.java))
        }

        ll_mine_set_verify.setOnClickListener {
            startActivity(Intent(context, VerifyActivity::class.java))
        }

    }

    private fun getDialogOrder() {

        // 通过各个文件的存储情况去判断应该跳转至哪个界面

        // 头像未完成则为 0
        // 完成头像 ，后为1 （后续的话为线性条件）
        // 若生活照完成 ， 则为2
        // 若实名认证完成 ， 则为3
        // 若爱好完成 ， 则为4
        // 若招呼语完成 ， 则为5
        // 若介绍完成 ， 则为6
        // 若语音完成 ， 则隐藏所有相关UI

        if (SPStaticUtils.getString(Constant.ME_AVATAR, "") == "") {
            SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 0)
            iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_avatar)
        } else {
            if (SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE, "") == "") {
                SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 1)
                iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_life)
            } else {
                if (!SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)) {
                    SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 2)
                    iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_verify)
                } else {
                    tv_zz.visibility = View.VISIBLE
                    if (SPStaticUtils.getString(Constant.ME_HOBBY, "") == "") {
                        SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 3)
                        iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_hobby)
                    } else {
                        if (SPStaticUtils.getString(Constant.ME_GREET, "") == "") {
                            SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 4)
                            iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_greet)
                        } else {
                            if (SPStaticUtils.getString(Constant.ME_INTRODUCE, "") == "") {
                                SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 5)
                                iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_introduce)
                            } else {
                                if (SPStaticUtils.getString(Constant.ME_VOICE, "") == "") {
                                    SPStaticUtils.put(Constant.SET_INFO_DIALOG_SUM, 6)
                                    iv_mine_dialog_info.setImageResource(R.mipmap.icon_set_dialog_voice)
                                } else {
                                    iv_mine_dialog_info.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAvatar() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = "3"
        map[Contents.KIND] = "1"
        getPhotoListPresent.getPhotoList(map)
    }

    private fun saveBitmap(bitmap: Bitmap, targetPath: String): String {
        ImageUtils.save(bitmap, targetPath, Bitmap.CompressFormat.PNG)
        return targetPath
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

    // 裁剪图片方法实现
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

        Log.i("guo", "startPhotoCropActivity")

        // 是否让用户调整范围(默认false)，如果开启，可能会造成剪切的图片的长宽比不是设定的
        // 如果不开启，用户不能拖动选框，只能缩放图片
//        options.setFreeStyleCropEnabled(true);
        mDestination?.let {
            UCrop.of<Any>(source, it) // 长宽比
                .withAspectRatio(1f, 1f) // 图片大小
                .withMaxResultSize(512, 512) // 配置参数
                .withOptions(options)
                .start(requireActivity())
        }
    }

    // 处理剪切成功的返回值 ( 头像界面 )
    fun handlePhotoCropResult(result: Intent) {

        deleteTempPhotoFile()
        val resultUri = UCrop.getOutput(result)
        if (null != resultUri) {
            var bitmap: Bitmap? = null
            try {
                bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, resultUri)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            mBitmap = bitmap

            val map: MutableMap<String, String> = TreeMap()
            map[Contents.ACCESS_TOKEN] =
                "24.50f0594e1d3ff58ff07ac59e645da8da.2592000.1656229858.282335-26330192"
            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
            map[Contents.IMAGE] = bitmapToBase64(bitmap)
            doFaceDetectPresent.doFaceDetect(map)

            ll_mine_loading.visibility = View.VISIBLE

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

    // 获取招呼语信息
    private fun getGreetInfo(): String {

        val sex = SPStaticUtils.getInt(Constant.ME_SEX, 0)
        val voiceUrl = SPStaticUtils.getString(Constant.ME_VOICE, "")
        val voiceLong = SPStaticUtils.getString(Constant.ME_VOICE_LONG, "")
        val voiceName = SPStaticUtils.getString(Constant.ME_VOICE_NAME, "")
        val greet = SPStaticUtils.getString(Constant.ME_GREET, "")

        val greetInfo =
            " {\"user_sex\":                    $sex, " +
                    "\"voice_url\":           \"$voiceUrl\"," +
                    "\"voice_long\":          \"$voiceLong\"," +
                    "\"voice_name\":          \"$voiceName\"," +
                    " \"zhaohuyu_content\":   \"$greet\"}"

        return greetInfo

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                // 上传生活照
                1 -> {
                    getDialogOrder()
                }
                // 拍照返回至裁切
                2 -> {
                    val temp = File(mTempPhotoPath)
                    startPhotoCropActivity(Uri.fromFile(temp))
                }
            }
        }

    }

    override fun onLoading() {
    }

    override fun onError() {

    }

    override fun onDoUpdateGreetInfoSuccess(updateGreetInfoBean: UpdateGreetInfoBean?) {

    }

    override fun onDoUpdateGreetInfoError() {

    }

    override fun onDoFaceDetectSuccess(faceDetectBean: FaceDetectBean?) {

        ll_mine_loading.visibility = View.GONE
        iv_mine_avatar.setImageBitmap(mBitmap)
        tv_mine_avatar_check.visibility = View.VISIBLE

        FileUtils.delete(mPhotoPath)
        mBitmap?.let { saveBitmap(it, mPhotoPath) }

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

            Log.i("guo", "onDoFaceDetectSuccess")

            SPStaticUtils.put(Constant.ME_AVATAR, mPhotoUrl)

        }.start()

        getDialogOrder()
    }

    override fun onDoFaceDetectError() {

        ll_mine_loading.visibility = View.GONE

    }

    override fun onGetPhotoListSuccess(photoListBean: PhotoListBean) {

//        Glide.with(requireContext()).load(photoListBean.data[1].image_url).into(iv_mine_avatar)

        for (i in 0.until(photoListBean.data.size)) {
            if (photoListBean.data[i].image_url == SPStaticUtils.getString(Constant.ME_AVATAR, "")
            ) {
                when (photoListBean.data[i].status) {
                    0 -> {
                        // 审核中
                        tv_mine_avatar_check.visibility == View.VISIBLE
                    }
                    1 -> {
                        // 通过
                        tv_mine_avatar_check.visibility == View.GONE
                    }
                    2 -> {
                        // 拒绝
                        ToastUtils.showShort("您的头像已违规，请重新更换头像")
                        iv_mine_avatar.setImageResource(R.drawable.ic_mine_male_default)
                        SPStaticUtils.put(Constant.ME_AVATAR, "")
                        getDialogOrder()
                    }
                }
            }
        }
    }

    override fun onGetPhotoListError() {

    }

    //             -----------------  弹窗  -----------------

    // 上传头像
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

                PictureSelector.create(activity)
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

                dismiss()

                XXPermissions.with(context)
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

    // 昵称
    inner class NameDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedUpdate = false

        override fun getImplLayoutId(): Int = R.layout.dialog_user_data_name

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_user_data_name_close)
            val skip = findViewById<TextView>(R.id.tv_user_data_name_skip)

            val name = findViewById<EditText>(R.id.et_user_data_name_name)
            val confirm = findViewById<TextView>(R.id.tv_user_data_name_confirm)

            skip.visibility = View.GONE

            confirm.setOnClickListener {

                for (i in 0.until(banTextList.size)) {
                    val code = banTextList[i]
                    if (name.text.contains(code)) {
                        haveBanText = true
                    }
                }

                if (name.text.isNotEmpty()) {
                    if (haveBanText) {
                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
                        name.setText("")
                    } else {
                        SPStaticUtils.put(Constant.ME_NAME, name.text.toString())
                        isNeedUpdate = true
                        dismiss()
                    }
                } else {
                    ToastUtils.showShort("请输入您需要更改的昵称")
                }

            }

            close.setOnClickListener {
                isNeedUpdate = false
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            if (isNeedUpdate) {
                tv_mine_nick.text = SPStaticUtils.getString(Constant.ME_NAME, "未填写")
            }
        }
    }

    // 添加爱好
    inner class HobbyDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedUpdate = false

        override fun getImplLayoutId(): Int = R.layout.dialog_set_hobby

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_set_hobby_close)
            val content = findViewById<EditText>(R.id.et_dialog_set_hobby_content)
            val sum = findViewById<TextView>(R.id.tv_dialog_set_hobby_sum)
            val confirm = findViewById<TextView>(R.id.tv_dialog_set_hobby_confirm)

            var size = 0
            var text = ""

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

                    for (i in 0.until(banTextList.size)) {
                        val code = banTextList[i]
                        if (text.contains(code)) {
                            haveBanText = true
                        }
                    }

                    if (haveBanText) {
                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
                        text = ""
                        content.setText("")
                    } else {
                        // 保存数据
                        SPStaticUtils.put(Constant.ME_HOBBY, text)
                        isNeedUpdate = true
                        dismiss()
                    }

                } else {
                    ToastUtils.showShort("请输入至少10字内容")
                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            // 更新数据
            if (isNeedUpdate) {
                getDialogOrder()
            }
        }
    }

    // 添加招呼语
    inner class GreetDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedUpdate = false

        override fun getImplLayoutId(): Int = R.layout.dialog_set_greet

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_set_greet_close)
            val content = findViewById<EditText>(R.id.et_dialog_set_greet_content)
            val sum = findViewById<TextView>(R.id.tv_dialog_set_greet_sum)
            val confirm = findViewById<TextView>(R.id.tv_dialog_set_greet_confirm)

            var size = 0
            var text = ""

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

                    for (i in 0.until(banTextList.size)) {
                        val code = banTextList[i]
                        if (text.contains(code)) {
                            haveBanText = true
                        }
                    }

                    if (haveBanText) {
                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
                        text = ""
                        content.setText("")
                    } else {
                        // 保存数据
                        SPStaticUtils.put(Constant.ME_GREET, text)
                        isNeedUpdate = true
                        dismiss()
                    }

                } else {
                    ToastUtils.showShort("请输入至少10字内容")
                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            // 更新数据
            if (isNeedUpdate) {
                getDialogOrder()
            }
        }

    }

    // 添加自我介绍
    inner class IntroduceDialog(context: Context) : FullScreenPopupView(context) {

        private var isNeedUpdate = false

        override fun getImplLayoutId(): Int = R.layout.dialog_set_introduce

        override fun onCreate() {
            super.onCreate()

            val close = findViewById<ImageView>(R.id.iv_dialog_set_introduce_close)
            val content = findViewById<EditText>(R.id.et_dialog_set_introduce_content)
            val sum = findViewById<TextView>(R.id.tv_dialog_set_introduce_sum)
            val confirm = findViewById<TextView>(R.id.tv_dialog_set_introduce_confirm)

            var size = 0
            var text = ""

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

                    for (i in 0.until(banTextList.size)) {
                        val code = banTextList[i]
                        if (text.contains(code)) {
                            haveBanText = true
                        }
                    }

                    if (haveBanText) {
                        ToastUtils.showShort("输入中存在敏感字，请重新输入")
                        text = ""
                        content.setText("")
                    } else {
                        // 保存数据
                        SPStaticUtils.put(Constant.ME_INTRODUCE, text)
                        isNeedUpdate = true
                        dismiss()
                    }

                } else {
                    ToastUtils.showShort("请输入至少10字内容")
                }
            }

        }

        override fun onDismiss() {
            super.onDismiss()
            // 更新数据
            if (isNeedUpdate) {
                getDialogOrder()
            }

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

                getDialogOrder()


            }

        }

    }

}