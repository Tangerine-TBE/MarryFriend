package com.twx.marryfriend.tools.avatar

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
import androidx.core.content.FileProvider
import com.baidubce.BceClientException
import com.baidubce.BceServiceException
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
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.FaceDetectBean
import com.twx.marryfriend.bean.UploadAvatarBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoUploadAvatarCallback
import com.twx.marryfriend.net.impl.doFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doUploadAvatarPresentImpl
import com.twx.marryfriend.utils.BitmapUtil
import com.twx.marryfriend.utils.GlideEngine
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_avatar_tool.*
import kotlinx.android.synthetic.main.activity_detail_info.*
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.layout_guide_step_edu.*
import kotlinx.android.synthetic.main.layout_guide_step_life.*
import java.io.*
import java.net.UnknownHostException
import java.util.*

class AvatarToolActivity : MainBaseViewActivity(), IDoFaceDetectCallback, IDoUploadAvatarCallback {

    // 临时图片文件路径
    private var mTempPhotoPath = ""

    // 剪切后图像文件
    private var mDestination: Uri? = null

    // 头像暂存的bitmap
    private var photoBitmap: Bitmap? = null

    // 剪切后图像存放地址
    private var mPhotoPath = ""

    // 头像上传百度云的url
    private var mPhotoUrl = ""

    private lateinit var doFaceDetectPresent: doFaceDetectPresentImpl
    private lateinit var doUploadAvatarPresent: doUploadAvatarPresentImpl

    private lateinit var avatarClient: BosClient

    override fun getLayoutView(): Int = R.layout.activity_avatar_tool

    override fun initView() {
        super.initView()

        doFaceDetectPresent = doFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        doUploadAvatarPresent = doUploadAvatarPresentImpl.getsInstance()
        doUploadAvatarPresent.registerCallback(this)

        val assetManager = this.assets

        val goodOne: InputStream = assetManager.open("pic/pic_guide_photo_good_one.png")
        val goodTwo: InputStream = assetManager.open("pic/pic_guide_photo_good_two.png")
        val goodThree: InputStream = assetManager.open("pic/pic_guide_photo_good_three.png")

        val badOne: InputStream = assetManager.open("pic/pic_guide_photo_bad_one.png")
        val badTwo: InputStream = assetManager.open("pic/pic_guide_photo_bad_two.png")
        val badThree: InputStream = assetManager.open("pic/pic_guide_photo_bad_three.png")
        val badFour: InputStream = assetManager.open("pic/pic_guide_photo_bad_four.png")
        val badFive: InputStream = assetManager.open("pic/pic_guide_photo_bad_five.png")


        iv_avatar_good_one.background = BitmapDrawable(BitmapFactory.decodeStream(goodOne))
        iv_avatar_good_two.background = BitmapDrawable(BitmapFactory.decodeStream(goodTwo))
        iv_avatar_good_three.background = BitmapDrawable(BitmapFactory.decodeStream(goodThree))
        iv_avatar_bad_one.background = BitmapDrawable(BitmapFactory.decodeStream(badOne))
        iv_avatar_bad_two.background = BitmapDrawable(BitmapFactory.decodeStream(badTwo))
        iv_avatar_bad_three.background = BitmapDrawable(BitmapFactory.decodeStream(badThree))
        iv_avatar_bad_four.background = BitmapDrawable(BitmapFactory.decodeStream(badFour))
        iv_avatar_bad_five.background = BitmapDrawable(BitmapFactory.decodeStream(badFive))

        mTempPhotoPath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "photo.jpeg"
        mDestination = Uri.fromFile(File(this.cacheDir, "photoCropImage.jpeg"))

        mPhotoPath = this.externalCacheDir.toString() + File.separator + "head.png"

    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()

        val config: BosClientConfiguration = BosClientConfiguration()

        // 设置HTTP最大连接数为10
        config.maxConnections = 10
        // 设置TCP连接超时为5000毫秒
        config.connectionTimeoutInMillis = 15000
        // 设置Socket传输数据超时的时间为2000毫秒
        config.socketTimeoutInMillis = 15000

        config.credentials = DefaultBceCredentials("545c965a81ba49889f9d070a1e147a7b",
            "1b430f2517d0460ebdbecfd910c572f8")
        config.endpoint = "http://adrmf.gz.bcebos.com"

        avatarClient = BosClient(config)

    }

    override fun initEvent() {
        super.initEvent()

        iv_avatar_finish.setOnClickListener {
            finish()
        }

        tv_avatar_confirm.setOnClickListener {
            ToastUtils.showShort("打开相册")

            val selectorStyle = PictureSelectorStyle()
            val animationStyle = PictureWindowAnimationStyle()
            animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
            animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
            selectorStyle.windowAnimationStyle = animationStyle

            PictureSelector.create(this).openGallery(SelectMimeType.TYPE_IMAGE)
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

        tv_avatar_camera.setOnClickListener {
            ToastUtils.showShort("打开相机")

            XXPermissions.with(this).permission(Permission.CAMERA)
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
                                val authority =
                                    this@AvatarToolActivity.packageName.toString() + ".fileProvider"
                                val contentUri: Uri =
                                    FileProvider.getUriForFile(this@AvatarToolActivity,
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

            doFaceDetect(bitmap)
            ll_avatar_loading.visibility = View.VISIBLE

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


    // 百度云图片审核
    private fun doFaceDetect(bitmap: Bitmap?) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
        map[Contents.IMAGE] = bitmapToBase64(bitmap)
        doFaceDetectPresent.doFaceDetect(map)
    }

    // 头像上传
    private fun doUploadAvatar(url: String, type: String, name: String) {


        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.IMAGE_URL] = mPhotoUrl
        map[Contents.FILE_TYPE] = type
        map[Contents.FILE_NAME] = name
        map[Contents.CONTENT] = "0"
        doUploadAvatarPresent.doUploadAvatar(map)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                // 拍照返回至裁切
                2 -> {
                    val temp = File(mTempPhotoPath)
                    startPhotoCropActivity(Uri.fromFile(temp))
                }
                UCrop.REQUEST_CROP -> {
                    // 只走头像的回调
                    if (data != null) {
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

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUploadAvatarSuccess(uploadAvatarBean: UploadAvatarBean?) {
        ll_avatar_loading.visibility = View.GONE
        if (uploadAvatarBean != null) {
            if (uploadAvatarBean.code == 200) {
                SPStaticUtils.put(Constant.ME_AVATAR_AUDIT, mPhotoUrl)
                this.finish()
            } else {
                ToastUtils.showShort(uploadAvatarBean.msg)
            }
        }

    }

    override fun onDoUploadAvatarError() {
        ll_avatar_loading.visibility = View.GONE
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDoFaceDetectSuccess(faceDetectBean: FaceDetectBean?) {

        if (faceDetectBean != null) {
            if (faceDetectBean.conclusion == "合规") {


                val bitmap = BitmapUtil.generateBitmap("佳偶婚恋交友", 16f, Color.WHITE)?.let {
                    BitmapUtil.createWaterMarkBitmap(photoBitmap, it)
                }

                FileUtils.delete(mPhotoPath)
                bitmap?.let { saveBitmap(it, mPhotoPath) }

                Thread {

                    try {

                        //上传Object
                        val file = File(mPhotoPath)
                        // bucketName 为文件夹名 ，使用用户id来进行命名
                        // key值为保存文件名，试用固定的几种格式来命名

                        val span = TimeUtils.getNowMills()
                        val path = "${FileUtils.getFileNameNoExtension(mPhotoPath)}_${span}.jpg"

                        val putObjectFromFileResponse =
                            avatarClient.putObject("user" + SPStaticUtils.getString(Constant.USER_ID,
                                "default"), path, file)

                        mPhotoUrl = avatarClient.generatePresignedUrl("user${
                            SPStaticUtils.getString(Constant.USER_ID, "default")
                        }", path, -1).toString()

                        // 这个时候应该上传
                        doUploadAvatar(mPhotoUrl,
                            FileUtils.getFileExtension(mPhotoPath),
                            FileUtils.getFileNameNoExtension(mPhotoPath))

                    } catch (e: BceClientException) {
                        e.printStackTrace()

                        ThreadUtils.runOnUiThread {
                            ll_avatar_loading.visibility = View.GONE
                            ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                        }
                    } catch (e: BceServiceException) {

                        ThreadUtils.runOnUiThread {
                            ll_avatar_loading.visibility = View.GONE
                            ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                        }

                        Log.i("guo", "Error ErrorCode: " + e.errorCode);
                        Log.i("guo", "Error RequestId: " + e.requestId);
                        Log.i("guo", "Error StatusCode: " + e.statusCode);
                        Log.i("guo", "Error ErrorType: " + e.errorType);
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                        ThreadUtils.runOnUiThread {
                            ll_avatar_loading.visibility = View.GONE
                            ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                        }
                    }


                }.start()

            } else {

                ll_avatar_loading.visibility = View.GONE

                if (faceDetectBean.error_msg != null) {
                    ToastUtils.showShort(faceDetectBean.error_msg)
                } else {
                    ToastUtils.showShort(faceDetectBean.data[0].msg)
                }

            }
        }
    }

    override fun onDoFaceDetectError() {
        ll_avatar_loading.visibility = View.GONE
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()

        doFaceDetectPresent.unregisterCallback(this)
        doUploadAvatarPresent.unregisterCallback(this)
    }

}