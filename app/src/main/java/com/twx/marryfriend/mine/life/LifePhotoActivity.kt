package com.twx.marryfriend.mine.life

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.FaceDetectBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.detailInfo.life.LifeIntroduceActivity
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback
import com.twx.marryfriend.net.impl.doLifeFaceDetectPresentImpl
import com.twx.marryfriend.utils.GlideEngine
import kotlinx.android.synthetic.main.activity_life_photo.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class LifePhotoActivity : MainBaseViewActivity(), IDoLifeFaceDetectCallback {


    // 选择器中选中的图片路径
    private var lifeChoosePath: String = ""

    // 生活照暂存的bitmap
    private var lifeBitmap: Bitmap? = null

    // 是否完成生活照选择
    private var isFinishLife = false

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

    // 是否存在
    private var haveFirstPic = false

    // 存储地址
    private var lifeFirstPic: Uri? = null

    // 介绍
    private var lifeFirstPicText = ""
    private var lifeFirstBitmap: Bitmap? = null
    private var getFirstBitmap = false

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
    private var getSecondBitmap = false

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
    private var getThirdBitmap = false

    // 图片是否上传成功
    private var isOneUpload = false
    private var isTwoUpload = false
    private var isThreeUpload = false

    // 是否已经回调过
    private var isNeedCallback = false

    // 哪个activity跳转而来
    private var activityName = ""

    private lateinit var doFaceDetectPresent: doLifeFaceDetectPresentImpl

    private lateinit var client: BosClient

    override fun getLayoutView(): Int = R.layout.activity_life_photo

    override fun initView() {
        super.initView()

        doFaceDetectPresent = doLifeFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        activityName = intent.getStringExtra("activity").toString()

        mTempLifePath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "life.jpeg"

        mLifeFirstPath = externalCacheDir.toString() + File.separator + "live1.png"
        mLifeSecondPath = externalCacheDir.toString() + File.separator + "live2.png"
        mLifeThirdPath = externalCacheDir.toString() + File.separator + "live3.png"

        lifeFirstPic = Uri.fromFile(File(this.cacheDir, "lifeFirstPic.jpeg"))
        lifeSecondPic = Uri.fromFile(File(this.cacheDir, "lifeSecondPic.jpeg"))
        lifeThirdPic = Uri.fromFile(File(this.cacheDir, "lifeThirdPic.jpeg"))

        updateExistDate()

    }

    override fun initLoadData() {
        super.initLoadData()
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

        iv_life_photo_finish.setOnClickListener {
            finish()
        }

        ll_life_photo_default.setOnClickListener {
            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeGuidDialog(this))
                .show()

        }

        rl_life_photo_pic_more.setOnClickListener {

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeGuidDialog(this))
                .show()

        }

        iv_life_photo_pic_one_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有其他图，若无则直接删除，若有则向上顺移第三张图

            lifeDeleteMode = "one"

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this))
                .show()
        }

        ll_life_photo_pic_one.setOnClickListener {
            // 第一张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeFirstPath)
            intent.putExtra("introduce", lifeFirstPicText)
            startActivityForResult(intent, 111)
        }

        iv_life_photo_pic_two_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有第三张图，若无则直接删除，若有则向上顺移第三张图

            lifeDeleteMode = "two"

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this))
                .show()

        }

        ll_life_photo_pic_two.setOnClickListener {
            // 第二张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeSecondPath)
            intent.putExtra("introduce", lifeSecondPicText)
            startActivityForResult(intent, 222)

        }

        iv_life_photo_pic_three_delete.setOnClickListener {

            lifeDeleteMode = "three"

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this))
                .show()

        }

        ll_life_photo_pic_three.setOnClickListener {
            // 第三张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)
            intent.putExtra("path", mLifeThirdPath)
            intent.putExtra("introduce", lifeThirdPicText)
            startActivityForResult(intent, 333)

        }

        tv_life_photo_next.setOnClickListener {

            if (isFinishLife) {

                // 上传生活照
                Thread {
                    val file = File(mLifeFirstPath)

                    val putObjectFromFileResponse = client.putObject("user${
                        SPStaticUtils.getString(Constant.USER_ID,
                            "default")
                    }", FileUtils.getFileName(mLifeFirstPath), file)

                    mLifeFirstUrl = client.generatePresignedUrl("user${
                        SPStaticUtils.getString(Constant.USER_ID, "default")
                    }", FileUtils.getFileName(mLifeFirstPath), -1).toString()

                    Log.i("guo", "mLifeFirstUrl :$mLifeFirstUrl")

                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, mLifeFirstUrl)
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, lifeFirstPicText)

                    isOneUpload = true

                    if (isOneUpload && isTwoUpload && isThreeUpload) {
                        val intent = intent
                        setResult(RESULT_OK, intent)
                        finish()

                    }
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

                        Log.i("guo", "mLifeSecondUrl :$mLifeSecondUrl")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, mLifeSecondUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, lifeSecondPicText)



                        isTwoUpload = true

                        if (isOneUpload && isTwoUpload && isThreeUpload) {
                            val intent = intent
                            setResult(RESULT_OK, intent)
                            finish()
                        }


                    }.start()
                } else {
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
                    isTwoUpload = true

                    if (isOneUpload && isTwoUpload && isThreeUpload) {
                        val intent = intent
                        setResult(RESULT_OK, intent)
                        finish()
                    }
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

                        Log.i("guo", "mLifeThirdUrl :$mLifeThirdUrl")

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, mLifeThirdUrl)
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, lifeThirdPicText)


                        isThreeUpload = true

                        if (isOneUpload && isTwoUpload && isThreeUpload) {
                            val intent = intent
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                    }.start()
                } else {
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")

                    isThreeUpload = true

                    if (isOneUpload && isTwoUpload && isThreeUpload) {
                        val intent = intent
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }

            } else {

                if (activityName == "data") {
                    val intent = intent
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    ToastUtils.showShort("您还未选择生活照信息")
                }
            }

        }

    }

    // 判断数据中存储的数据
    private fun updateExistDate() {

        val lifePhotoOne = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE, "")
        val lifePhotoTwo = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO, "")
        val lifePhotoThree = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE, "")

        val lifePhotoOneText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")
        val lifePhotoTwoText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
        val lifePhotoThreeText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")

        if (lifePhotoOne != "") {

            nsv_life_photo_default.visibility = View.GONE
            nsv_life_photo_pic.visibility = View.VISIBLE

            rl_life_photo_pic_one.visibility = View.VISIBLE
            rl_life_photo_pic_more.visibility = View.VISIBLE

            Thread {
                lifeFirstBitmap = decodeUriAsBitmapFromNet(lifePhotoOne)

                lifeFirstBitmap?.let { saveBitmap(it, mLifeFirstPath) }



                ThreadUtils.runOnUiThread {
                    getFirstBitmap = true
                    hideLoading()
                    Glide.with(this).load(lifeFirstBitmap).into(iv_life_photo_pic_one)
                }

            }.start()



            isFinishLife = true
            haveFirstPic = true

            if (lifePhotoOneText != "") {
                lifeFirstPicText = lifePhotoOneText
                tv_life_photo_pic_one.text = lifeFirstPicText
                iv_life_photo_pic_one_icon.visibility = View.GONE
            }

        } else {
            getFirstBitmap = true
        }

        if (lifePhotoTwo != "") {

            rl_life_photo_pic_two.visibility = View.VISIBLE

            Thread {
                lifeSecondBitmap = decodeUriAsBitmapFromNet(lifePhotoTwo)

                lifeSecondBitmap?.let { saveBitmap(it, mLifeSecondPath) }

                ThreadUtils.runOnUiThread {
                    getSecondBitmap = true
                    hideLoading()
                    Glide.with(this).load(lifeSecondBitmap).into(iv_life_photo_pic_two)
                }

            }.start()

            isFinishLife = true
            haveSecondPic = true

            if (lifePhotoTwoText != "") {
                lifeSecondPicText = lifePhotoTwoText
                tv_life_photo_pic_two.text = lifeSecondPicText
                iv_life_photo_pic_two_icon.visibility = View.GONE
            }

        } else {
            getSecondBitmap = true
        }

        if (lifePhotoThree != "") {

            rl_life_photo_pic_three.visibility = View.VISIBLE
            rl_life_photo_pic_more.visibility = View.GONE

            Thread {
                lifeThirdBitmap = decodeUriAsBitmapFromNet(lifePhotoThree)

                lifeThirdBitmap?.let { saveBitmap(it, mLifeThirdPath) }

                ThreadUtils.runOnUiThread {
                    getThirdBitmap = true
                    hideLoading()
                    Glide.with(this).load(lifeThirdBitmap).into(iv_life_photo_pic_three)
                }

            }.start()

            isFinishLife = true
            haveThirdPic = true

            if (lifePhotoThreeText != "") {
                lifeThirdPicText = lifePhotoThreeText
                tv_life_photo_pic_three.text = lifeThirdPicText
                iv_life_photo_pic_three_icon.visibility = View.GONE
            }
        } else {
            getThirdBitmap = true
            hideLoading()
        }

    }

    // 判断数据操作是否读取完成,隐藏加载弹窗
    private fun hideLoading() {
        if (getFirstBitmap && getSecondBitmap && getThirdBitmap) {
            ll_life_photo_loading.visibility = View.GONE
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

    private fun decodeUriAsBitmapFromNet(url: String): Bitmap? {
        var fileUrl: URL? = null
        var bitmap: Bitmap? = null
        try {
            fileUrl = URL(url)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        try {
            val conn: HttpURLConnection = fileUrl?.openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            val `is`: InputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                3 -> {
                    if (data != null) {
                        val bundle = data.extras
                        val bitmap: Bitmap = bundle?.get("data") as Bitmap

                        lifeBitmap = bitmap

                        ImageUtils.save(bitmap, mTempLifePath, Bitmap.CompressFormat.PNG)

                        lifeChoosePath = mTempLifePath

                        val map: MutableMap<String, String> = TreeMap()
                        map[Contents.ACCESS_TOKEN] = "24.13603dd5bb5800c98718b088f24c804d.2592000.1658887826.282335-26330258"
                        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                        map[Contents.IMAGE] = bitmapToBase64(lifeBitmap)

                        isNeedCallback = true
                        doFaceDetectPresent.doLifeFaceDetect(map)

//                        // 显示加载动画
                        ll_life_photo_loading.visibility = View.VISIBLE

                    }
                }
                111 -> {
                    // 生活第一张图的介绍
                    if (data != null) {

                        nsv_life_photo_default.visibility = View.GONE
                        nsv_life_photo_pic.visibility = View.VISIBLE

                        rl_life_photo_pic_one.visibility = View.VISIBLE
                        rl_life_photo_pic_more.visibility = View.VISIBLE

                        isFinishLife = true

                        if (lifeBitmap == null) {
                            lifeBitmap = lifeFirstBitmap
                        }

                        Glide.with(this).load(lifeBitmap).into(iv_life_photo_pic_one)

                        haveFirstPic = true

                        lifeFirstBitmap = lifeBitmap

                        FileUtils.delete(mLifeFirstPath)

                        lifeFirstBitmap?.let { saveBitmap(it, mLifeFirstPath) }

                        lifeFirstPicText = data.getStringExtra("introduce").toString()
                        if (lifeFirstPicText != "") {
                            tv_life_photo_pic_one.text = lifeFirstPicText
                            iv_life_photo_pic_one_icon.visibility = View.GONE
                        }
                        lifeBitmap = null

                    }
                }
                222 -> {
                    if (data != null) {

                        rl_life_photo_pic_two.visibility = View.VISIBLE

                        isFinishLife = true

                        if (lifeBitmap == null) {
                            lifeBitmap = lifeSecondBitmap
                        }

                        Glide.with(this).load(lifeBitmap).into(iv_life_photo_pic_two)

                        haveSecondPic = true

                        lifeSecondBitmap = lifeBitmap

                        FileUtils.delete(mLifeSecondPath)

                        lifeSecondBitmap?.let { saveBitmap(it, mLifeSecondPath) }

                        lifeSecondPicText = data.getStringExtra("introduce").toString()
                        if (lifeSecondPicText != "") {
                            tv_life_photo_pic_two.text = lifeSecondPicText
                            iv_life_photo_pic_two_icon.visibility = View.GONE
                        }
                        lifeBitmap = null
                    }
                }
                333 -> {
                    if (data != null) {

                        rl_life_photo_pic_three.visibility = View.VISIBLE
                        rl_life_photo_pic_more.visibility = View.GONE

                        isFinishLife = true

                        if (lifeBitmap == null) {
                            lifeBitmap = lifeThirdBitmap
                        }

                        Glide.with(this).load(lifeBitmap).into(iv_life_photo_pic_three)

                        haveThirdPic = true

                        lifeThirdBitmap = lifeBitmap

                        FileUtils.delete(mLifeThirdPath)

                        lifeThirdBitmap?.let { saveBitmap(it, mLifeThirdPath) }

                        lifeThirdPicText = data.getStringExtra("introduce").toString()
                        if (lifeThirdPicText != "") {
                            tv_life_photo_pic_three.text = lifeThirdPicText
                            iv_life_photo_pic_three_icon.visibility = View.GONE
                        }

                        lifeBitmap = null
                    }
                }
            }
        }
    }

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

                XXPermissions.with(this@LifePhotoActivity)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 启动系统相机
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
                isCamera = false
                dismiss()

                val selectorStyle = PictureSelectorStyle()
                val animationStyle = PictureWindowAnimationStyle()
                animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
                animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
                selectorStyle.windowAnimationStyle = animationStyle

                PictureSelector.create(this@LifePhotoActivity)
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

                            lifeBitmap = ImageUtils.getBitmap(result[0].realPath)

                            val map: MutableMap<String, String> = TreeMap()
                            map[Contents.ACCESS_TOKEN] = "24.13603dd5bb5800c98718b088f24c804d.2592000.1658887826.282335-26330258"
                            map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                            map[Contents.IMAGE] = bitmapToBase64(ImageUtils.getBitmap(result[0].realPath))

                            isNeedCallback = true
                            doFaceDetectPresent.doLifeFaceDetect(map)

//                            // 显示加载动画
                            ll_life_photo_loading.visibility = View.VISIBLE

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
                        if (haveSecondPic) {

                            if (haveThirdPic) {
                                // 有三张图

                                rl_life_photo_pic_three.visibility = View.GONE
                                rl_life_photo_pic_more.visibility = View.VISIBLE

                                FileUtils.delete(mLifeFirstPath)
                                lifeSecondBitmap?.let { it1 -> saveBitmap(it1, mLifeFirstPath) }

                                lifeFirstPicText = lifeSecondPicText
                                lifeFirstBitmap = lifeSecondBitmap
                                Glide.with(this).load(lifeFirstBitmap).into(iv_life_photo_pic_one)
                                tv_life_photo_pic_one.text = lifeFirstPicText


                                FileUtils.delete(mLifeSecondPath)
                                lifeThirdBitmap?.let { it1 -> saveBitmap(it1, mLifeSecondPath) }

                                lifeSecondPicText = lifeThirdPicText
                                lifeSecondBitmap = lifeThirdBitmap
                                Glide.with(this).load(lifeSecondBitmap).into(iv_life_photo_pic_two)
                                tv_life_photo_pic_two.text = lifeSecondPicText

                                haveThirdPic = false
                                lifeThirdPicText = ""
                                lifeThirdBitmap = null
                                iv_life_photo_pic_three_icon.visibility = View.VISIBLE
                                tv_life_photo_pic_three.text = "添加描述"

                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")

                                FileUtils.delete(mLifeThirdPath)

                            } else {
                                // 有两张图

                                rl_life_photo_pic_two.visibility = View.GONE

                                FileUtils.delete(mLifeFirstPath)
                                lifeSecondBitmap?.let { it1 -> saveBitmap(it1, mLifeFirstPath) }

                                lifeFirstPicText = lifeSecondPicText
                                lifeFirstBitmap = lifeSecondBitmap
                                Glide.with(this).load(lifeFirstBitmap).into(iv_life_photo_pic_one)
                                tv_life_photo_pic_one.text = lifeFirstPicText

                                haveSecondPic = false
                                lifeSecondPicText = ""
                                lifeSecondBitmap = null
                                iv_life_photo_pic_two_icon.visibility = View.VISIBLE
                                tv_life_photo_pic_two.text = "添加描述"

                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")

                                FileUtils.delete(mLifeSecondPath)

                            }
                        } else {
                            // 只有一张图

                            rl_life_photo_pic_one.visibility = View.GONE
                            rl_life_photo_pic_more.visibility = View.GONE
                            nsv_life_photo_pic.visibility = View.GONE

                            nsv_life_photo_default.visibility = View.VISIBLE

                            haveFirstPic = false
                            lifeFirstPicText = ""
                            lifeFirstBitmap = null
                            iv_life_photo_pic_one_icon.visibility = View.VISIBLE
                            tv_life_photo_pic_one.text = "添加描述"

                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")

                            FileUtils.delete(mLifeFirstPath)
                            isFinishLife = false
                        }
                    }
                    "two" -> {
                        if (haveThirdPic) {
                            // 有第三张图

                            rl_life_photo_pic_three.visibility = View.GONE
                            rl_life_photo_pic_more.visibility = View.VISIBLE

                            FileUtils.delete(mLifeSecondPath)
                            lifeThirdBitmap?.let { it1 -> saveBitmap(it1, mLifeSecondPath) }

                            lifeSecondPicText = lifeThirdPicText
                            lifeSecondBitmap = lifeThirdBitmap
                            Glide.with(this).load(lifeSecondBitmap).into(iv_life_photo_pic_two)
                            tv_life_photo_pic_two.text = lifeSecondPicText

                            haveThirdPic = false
                            lifeThirdPicText = ""
                            lifeThirdBitmap = null
                            iv_life_photo_pic_three_icon.visibility = View.VISIBLE
                            tv_life_photo_pic_three.text = "添加描述"

                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")

                            FileUtils.delete(mLifeThirdPath)

                        } else {
                            // 没有第三张图

                            rl_life_photo_pic_two.visibility = View.GONE

                            haveSecondPic = false
                            lifeSecondPicText = ""
                            lifeSecondBitmap = null
                            iv_life_photo_pic_two_icon.visibility = View.VISIBLE
                            tv_life_photo_pic_two.text = "添加描述"

                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO, "")
                            SPStaticUtils.put(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")

                            FileUtils.delete(mLifeSecondPath)
                        }
                    }
                    "three" -> {

                        rl_life_photo_pic_three.visibility = View.GONE
                        rl_life_photo_pic_more.visibility = View.VISIBLE

                        mLifeThirdPath = ""
                        haveThirdPic = false
                        lifeThirdPicText = ""
                        lifeThirdBitmap = null
                        iv_life_photo_pic_three_icon.visibility = View.VISIBLE
                        tv_life_photo_pic_three.text = "添加描述"

                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE, "")
                        SPStaticUtils.put(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")

                        FileUtils.delete(mLifeThirdPath)

                    }
                }
                dismiss()
            }

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoLifeFaceDetectSuccess(faceDetectBean: FaceDetectBean) {
        ll_life_photo_loading.visibility = View.GONE

        if (isNeedCallback) {
            isNeedCallback = false
            if (faceDetectBean.conclusion != "合规") {
                ToastUtils.showShort(faceDetectBean.data[0].msg)
            } else {
                if (!haveFirstPic) {

                    val intent = Intent(this, LifeIntroduceActivity::class.java)

                    intent.putExtra("path", lifeChoosePath)
                    intent.putExtra("introduce", "")
                    startActivityForResult(intent, 111)

                } else if (!haveSecondPic) {

                    val intent = Intent(this, LifeIntroduceActivity::class.java)
                    intent.putExtra("path", lifeChoosePath)
                    intent.putExtra("introduce", "")
                    startActivityForResult(intent, 222)

                } else if (!haveThirdPic) {

                    val intent = Intent(this, LifeIntroduceActivity::class.java)
                    intent.putExtra("path", lifeChoosePath)
                    intent.putExtra("introduce", "")
                    startActivityForResult(intent, 333)

                }
            }
        }
    }

    override fun onDoLifeFaceDetectError() {
        ll_life_photo_loading.visibility = View.GONE
    }


}