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
import android.view.KeyEvent
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
import com.twx.marryfriend.bean.DeletePhotoBean
import com.twx.marryfriend.bean.FaceDetectBean
import com.twx.marryfriend.bean.UploadPhotoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.detailInfo.life.LifeIntroduceActivity
import com.twx.marryfriend.net.callback.IDoDeletePhotoCallback
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback
import com.twx.marryfriend.net.impl.doDeletePhotoPresentImpl
import com.twx.marryfriend.net.impl.doLifeFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doUploadPhotoPresentImpl
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


class LifePhotoActivity : MainBaseViewActivity(), IDoDeletePhotoCallback {

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


    private lateinit var doDeletePhotoPresent: doDeletePhotoPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_life_photo

    override fun initView() {
        super.initView()

        doDeletePhotoPresent = doDeletePhotoPresentImpl.getsInstance()
        doDeletePhotoPresent.registerCallback(this)

        mTempLifePath =
            Environment.getExternalStorageDirectory().toString() + File.separator + "life.jpeg"

        updateExistDate()


        mLifeFirstPath = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE, "")
        mLifeSecondPath = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO, "")
        mLifeThirdPath = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE, "")
        mLifeFourPath = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FOUR, "")
        mLifeFivePath = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FIVE, "")


    }

    override fun initLoadData() {
        super.initLoadData()
    }

    override fun initPresent() {
        super.initPresent()

    }

    override fun initEvent() {
        super.initEvent()

        iv_life_photo_finish.setOnClickListener {

            val intent = intent
            setResult(RESULT_OK, intent)
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

        rl_life_photo_pic_one.setOnClickListener {
            // 第一张图片的描述

            // 这个地方做个判断，要是有url，就传入url，若是没有，就正常流程，包装点进预览有数据

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            Log.i("guo", "lifeChoosePath : $mLifeFirstPath")

            intent.putExtra("path", mLifeFirstPath)
            intent.putExtra("introduce", mLifeFirstText)
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

        rl_life_photo_pic_two.setOnClickListener {
            // 第二张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            Log.i("guo", "lifeChoosePath : $mLifeSecondPath")

            intent.putExtra("path", mLifeSecondPath)
            intent.putExtra("introduce", mLifeSecondText)
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

        rl_life_photo_pic_three.setOnClickListener {
            // 第三张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            Log.i("guo", "lifeChoosePath : $mLifeThirdPath")

            intent.putExtra("path", mLifeThirdPath)
            intent.putExtra("introduce", mLifeThirdText)
            startActivityForResult(intent, 333)

        }



        iv_life_photo_pic_four_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有其他图，若无则直接删除，若有则向上顺移第三张图

            lifeDeleteMode = "four"

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this))
                .show()
        }

        rl_life_photo_pic_four.setOnClickListener {
            // 第四张图片的描述

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            Log.i("guo", "lifeChoosePath : $mLifeFourPath")

            intent.putExtra("path", mLifeFourPath)
            intent.putExtra("introduce", mLifeFourText)
            startActivityForResult(intent, 444)

        }


        iv_life_photo_pic_five_delete.setOnClickListener {
            // 需要判断一下数据，分为是否有其他图，若无则直接删除，若有则向上顺移第三张图

            lifeDeleteMode = "five"

            XPopup.Builder(this)
                .dismissOnTouchOutside(false)
                .dismissOnBackPressed(false)
                .isDestroyOnDismiss(true)
                .popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
                .asCustom(LifeDeleteDialog(this))
                .show()
        }

        rl_life_photo_pic_five.setOnClickListener {
            // 第一张图片的描述

            // 这个地方做个判断，要是有url，就传入url，若是没有，就正常流程，包装点进预览有数据

            val intent = Intent(this, LifeIntroduceActivity::class.java)

            Log.i("guo", "lifeChoosePath : $mLifeFivePath")

            intent.putExtra("path", mLifeFivePath)
            intent.putExtra("introduce", mLifeFiveText)
            startActivityForResult(intent, 555)

        }


    }

    // 删除生活照
    private fun deleteLifePhoto(id: String) {

        ll_life_photo_loading.visibility = View.VISIBLE

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ID] = id
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        doDeletePhotoPresent.doDeletePhoto(map)

    }

    // 判断数据中存储的数据
    private fun updateExistDate() {

        mLifeFirstUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE, "")
        mLifeFirstText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_TEXT, "")
        mLifeFirstId = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_ID, "")
        mLifeFirstState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_AUDIT, "0")

        mLifeSecondUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO, "")
        mLifeSecondText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_TEXT, "")
        mLifeSecondId = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_ID, "")
        mLifeSecondState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_TWO_AUDIT, "0")

        mLifeThirdUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE, "")
        mLifeThirdText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_TEXT, "")
        mLifeThirdId = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_ID, "")
        mLifeThirdState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_THREE_AUDIT, "0")

        mLifeFourUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FOUR, "")
        mLifeFourText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
        mLifeFourId = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
        mLifeFourState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "0")


        mLifeFiveUrl = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FIVE, "")
        mLifeFiveText = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
        mLifeFiveId = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
        mLifeFiveState = SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "0")

        if (mLifeFirstUrl != "") {

            nsv_life_photo_default.visibility = View.GONE
            nsv_life_photo_pic.visibility = View.VISIBLE

            rl_life_photo_pic_one.visibility = View.VISIBLE
            rl_life_photo_pic_more.visibility = View.VISIBLE


            Glide.with(this).load(mLifeFirstUrl).into(iv_life_photo_pic_one)

            haveFirstPic = true

            if (mLifeFirstText != "") {
                tv_life_photo_pic_one.text = mLifeFirstText
                iv_life_photo_pic_one_icon.visibility = View.GONE
            }


            when (mLifeFirstState) {
                "0" -> {
                    tv_life_photo_pic_one_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_one_audit.text = "审核中..."
                }
                "1" -> {
                    tv_life_photo_pic_one_audit.visibility = View.GONE
                }
                "2" -> {
                    tv_life_photo_pic_one_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_one_audit.text = "审核拒绝"
                }
            }

        }

        if (mLifeSecondUrl != "") {

            rl_life_photo_pic_two.visibility = View.VISIBLE

            Glide.with(this).load(mLifeSecondUrl).into(iv_life_photo_pic_two)

            haveSecondPic = true

            if (mLifeSecondText != "") {
                tv_life_photo_pic_two.text = mLifeSecondText
                iv_life_photo_pic_two_icon.visibility = View.GONE
            }

            when (mLifeSecondState) {
                "0" -> {
                    tv_life_photo_pic_two_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_two_audit.text = "审核中..."
                }
                "1" -> {
                    tv_life_photo_pic_two_audit.visibility = View.GONE
                }
                "2" -> {
                    tv_life_photo_pic_two_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_two_audit.text = "审核拒绝"
                }
            }

        }

        if (mLifeThirdUrl != "") {

            rl_life_photo_pic_three.visibility = View.VISIBLE

            Glide.with(this).load(mLifeThirdUrl).into(iv_life_photo_pic_three)

            haveThirdPic = true

            if (mLifeThirdText != "") {
                tv_life_photo_pic_three.text = mLifeThirdText
                iv_life_photo_pic_three_icon.visibility = View.GONE
            }

            when (mLifeThirdState) {
                "0" -> {
                    tv_life_photo_pic_three_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_three_audit.text = "审核中..."
                }
                "1" -> {
                    tv_life_photo_pic_three_audit.visibility = View.GONE
                }
                "2" -> {
                    tv_life_photo_pic_three_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_three_audit.text = "审核拒绝"
                }
            }

        }

        if (mLifeFourUrl != "") {

            rl_life_photo_pic_four.visibility = View.VISIBLE

            Glide.with(this).load(mLifeFourUrl).into(iv_life_photo_pic_four)

            haveFourPic = true

            if (mLifeFourText != "") {
                tv_life_photo_pic_four.text = mLifeFourText
                iv_life_photo_pic_four_icon.visibility = View.GONE
            }

            when (mLifeFourState) {
                "0" -> {
                    tv_life_photo_pic_four_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_four_audit.text = "审核中..."
                }
                "1" -> {
                    tv_life_photo_pic_four_audit.visibility = View.GONE
                }
                "2" -> {
                    tv_life_photo_pic_four_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_four_audit.text = "审核拒绝"
                }
            }


        }

        if (mLifeFiveUrl != "") {

            rl_life_photo_pic_five.visibility = View.VISIBLE
            rl_life_photo_pic_more.visibility = View.GONE

            Glide.with(this).load(mLifeFiveUrl).into(iv_life_photo_pic_five)

            haveFivePic = true

            if (mLifeFiveText != "") {
                tv_life_photo_pic_five.text = mLifeFiveText
                iv_life_photo_pic_five_icon.visibility = View.GONE
            }

            when (mLifeFiveState) {
                "0" -> {
                    tv_life_photo_pic_five_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_five_audit.text = "审核中..."
                }
                "1" -> {
                    tv_life_photo_pic_five_audit.visibility = View.GONE
                }
                "2" -> {
                    tv_life_photo_pic_five_audit.visibility = View.VISIBLE
                    tv_life_photo_pic_five_audit.text = "审核拒绝"
                }

            }

        }


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

                        if (!haveFirstPic) {

                            val intent =
                                Intent(this@LifePhotoActivity, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 111)

                        } else if (!haveSecondPic) {

                            val intent =
                                Intent(this@LifePhotoActivity, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 222)

                        } else if (!haveThirdPic) {

                            val intent =
                                Intent(this@LifePhotoActivity, LifeIntroduceActivity::class.java)
                            intent.putExtra("path", lifeChoosePath)
                            intent.putExtra("introduce", "")
                            startActivityForResult(intent, 333)

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
                        tv_life_photo_pic_one_audit.visibility = View.VISIBLE
                        tv_life_photo_pic_one_audit.text = "审核中..."

                        nsv_life_photo_default.visibility = View.GONE
                        nsv_life_photo_pic.visibility = View.VISIBLE

                        rl_life_photo_pic_one.visibility = View.VISIBLE
                        rl_life_photo_pic_more.visibility = View.VISIBLE


                        Glide.with(this).load(mLifeFirstUrl).into(iv_life_photo_pic_one)

                        haveFirstPic = true

                        if (mLifeFirstText != "") {
                            tv_life_photo_pic_one.text = mLifeFirstText
                            iv_life_photo_pic_one_icon.visibility = View.GONE
                        }

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
                        tv_life_photo_pic_two_audit.visibility = View.VISIBLE
                        tv_life_photo_pic_two_audit.text = "审核中..."

                        rl_life_photo_pic_two.visibility = View.VISIBLE

                        Glide.with(this).load(mLifeSecondUrl).into(iv_life_photo_pic_two)

                        haveSecondPic = true

                        if (mLifeSecondText != "") {
                            tv_life_photo_pic_two.text = mLifeSecondText
                            iv_life_photo_pic_two_icon.visibility = View.GONE
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
                        tv_life_photo_pic_three_audit.visibility = View.VISIBLE
                        tv_life_photo_pic_three_audit.text = "审核中..."

                        rl_life_photo_pic_three.visibility = View.VISIBLE

                        Glide.with(this).load(mLifeThirdUrl).into(iv_life_photo_pic_three)

                        haveThirdPic = true

                        if (mLifeThirdText != "") {
                            tv_life_photo_pic_three.text = mLifeThirdText
                            iv_life_photo_pic_three_icon.visibility = View.GONE
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
                        tv_life_photo_pic_four_audit.visibility = View.VISIBLE
                        tv_life_photo_pic_four_audit.text = "审核中..."

                        rl_life_photo_pic_four.visibility = View.VISIBLE

                        Glide.with(this).load(mLifeFourUrl).into(iv_life_photo_pic_four)

                        haveFourPic = true

                        if (mLifeFourText != "") {
                            tv_life_photo_pic_four.text = mLifeFourText
                            iv_life_photo_pic_four_icon.visibility = View.GONE
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
                        tv_life_photo_pic_five_audit.visibility = View.VISIBLE
                        tv_life_photo_pic_five_audit.text = "审核中..."

                        rl_life_photo_pic_five.visibility = View.VISIBLE
                        rl_life_photo_pic_more.visibility = View.GONE

                        Glide.with(this).load(mLifeFiveUrl).into(iv_life_photo_pic_five)

                        haveFivePic = true

                        if (mLifeFiveText != "") {
                            tv_life_photo_pic_five.text = mLifeFiveText
                            iv_life_photo_pic_five_icon.visibility = View.GONE
                        }

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
                    .permission(Permission.CAMERA)
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>?,
                            all: Boolean,
                        ) {

                            if (all) {
                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 启动系统相机
                                startActivityForResult(intent, 3)
                            } else {
                                ToastUtils.showShort("请授予应用所需相关权限")
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

                            if (!haveFirstPic) {

                                val intent = Intent(this@LifePhotoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 111)

                            } else if (!haveSecondPic) {

                                val intent = Intent(this@LifePhotoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 222)

                            } else if (!haveThirdPic) {

                                val intent = Intent(this@LifePhotoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 333)

                            } else if (!haveFourPic) {

                                val intent = Intent(this@LifePhotoActivity,
                                    LifeIntroduceActivity::class.java)
                                intent.putExtra("path", lifeChoosePath)
                                intent.putExtra("introduce", "")
                                startActivityForResult(intent, 444)

                            } else if (!haveFivePic) {

                                val intent = Intent(this@LifePhotoActivity,
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

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoDeletePhotoSuccess(deletePhotoBean: DeletePhotoBean?) {
        when (lifeDeleteMode) {
            "one" -> {
                if (haveSecondPic) {

                    if (haveThirdPic) {

                        if (haveFourPic) {

                            if (haveFivePic) {
                                // 1、2、3、4、5 删除第五张 第五张、第四张、第三张、第二张依次往前移动（先移动靠前的）

                                rl_life_photo_pic_five.visibility = View.GONE
                                rl_life_photo_pic_more.visibility = View.VISIBLE

                                // 移动第二张

                                mLifeFirstUrl = mLifeSecondUrl
                                mLifeFirstText = mLifeSecondText
                                mLifeFirstId = mLifeSecondId
                                mLifeFirstState = mLifeSecondState

                                Glide.with(applicationContext).load(mLifeFirstUrl)
                                    .into(iv_life_photo_pic_one)
                                tv_life_photo_pic_one.text = mLifeFirstText

                                // 移动第三张

                                mLifeSecondUrl = mLifeThirdUrl
                                mLifeSecondText = mLifeThirdText
                                mLifeSecondId = mLifeThirdId
                                mLifeSecondState = mLifeThirdState

                                Glide.with(applicationContext).load(mLifeSecondUrl)
                                    .into(iv_life_photo_pic_two)
                                tv_life_photo_pic_two.text = mLifeSecondText

                                // 移动第四张

                                mLifeThirdUrl = mLifeFourUrl
                                mLifeThirdText = mLifeFourText
                                mLifeThirdId = mLifeFourId
                                mLifeThirdState = mLifeFourState

                                Glide.with(applicationContext).load(mLifeThirdUrl)
                                    .into(iv_life_photo_pic_three)
                                tv_life_photo_pic_three.text = mLifeThirdText


                                // 移动第五张

                                mLifeFourUrl = mLifeFiveUrl
                                mLifeFourText = mLifeFiveText
                                mLifeFourId = mLifeFiveId
                                mLifeFourState = mLifeFiveState

                                Glide.with(applicationContext).load(mLifeFourUrl)
                                    .into(iv_life_photo_pic_four)
                                tv_life_photo_pic_four.text = mLifeFourText

                                haveFivePic = false
                                mLifeFiveUrl = ""
                                mLifeFiveText = ""

                                iv_life_photo_pic_five_icon.visibility = View.VISIBLE
                                tv_life_photo_pic_five.text = "添加描述"

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

                                rl_life_photo_pic_four.visibility = View.GONE

                                // 移动第二张

                                mLifeFirstUrl = mLifeSecondUrl
                                mLifeFirstText = mLifeSecondText
                                mLifeFirstId = mLifeSecondId
                                mLifeFirstState = mLifeSecondState

                                Glide.with(applicationContext).load(mLifeFirstUrl)
                                    .into(iv_life_photo_pic_one)
                                tv_life_photo_pic_one.text = mLifeFirstText

                                // 移动第三张

                                mLifeSecondUrl = mLifeThirdUrl
                                mLifeSecondText = mLifeThirdText
                                mLifeSecondId = mLifeThirdId
                                mLifeSecondState = mLifeThirdState

                                Glide.with(applicationContext).load(mLifeSecondUrl)
                                    .into(iv_life_photo_pic_two)
                                tv_life_photo_pic_two.text = mLifeSecondText

                                // 移动第四张

                                mLifeThirdUrl = mLifeFourUrl
                                mLifeThirdText = mLifeFourText
                                mLifeThirdId = mLifeFourId
                                mLifeThirdState = mLifeFourState

                                Glide.with(applicationContext).load(mLifeThirdUrl)
                                    .into(iv_life_photo_pic_three)
                                tv_life_photo_pic_three.text = mLifeThirdText

                                haveFourPic = false
                                mLifeFourUrl = ""
                                mLifeFourText = ""

                                iv_life_photo_pic_four_icon.visibility = View.VISIBLE
                                tv_life_photo_pic_four.text = "添加描述"

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
                            rl_life_photo_pic_three.visibility = View.GONE

                            // 移动第二张

                            mLifeFirstUrl = mLifeSecondUrl
                            mLifeFirstText = mLifeSecondText
                            mLifeFirstId = mLifeSecondId
                            mLifeFirstState = mLifeSecondState

                            Glide.with(applicationContext).load(mLifeFirstUrl)
                                .into(iv_life_photo_pic_one)
                            tv_life_photo_pic_one.text = mLifeFirstText

                            // 移动第三张

                            mLifeSecondUrl = mLifeThirdUrl
                            mLifeSecondText = mLifeThirdText
                            mLifeSecondId = mLifeThirdId
                            mLifeSecondState = mLifeThirdState

                            Glide.with(applicationContext).load(mLifeSecondUrl)
                                .into(iv_life_photo_pic_two)
                            tv_life_photo_pic_two.text = mLifeSecondText

                            haveThirdPic = false
                            mLifeThirdUrl = ""
                            mLifeThirdText = ""

                            iv_life_photo_pic_three_icon.visibility = View.VISIBLE
                            tv_life_photo_pic_three.text = "添加描述"

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

                        rl_life_photo_pic_two.visibility = View.GONE

                        mLifeFirstUrl = mLifeSecondUrl
                        mLifeFirstText = mLifeSecondText
                        mLifeFirstId = mLifeSecondId
                        mLifeFirstState = mLifeSecondState

                        Glide.with(applicationContext).load(mLifeFirstUrl)
                            .into(iv_life_photo_pic_one)
                        tv_life_photo_pic_one.text = mLifeFirstText

                        haveSecondPic = false
                        mLifeSecondUrl = ""
                        mLifeSecondText = ""

                        iv_life_photo_pic_two_icon.visibility = View.VISIBLE
                        tv_life_photo_pic_two.text = "添加描述"

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

                    rl_life_photo_pic_one.visibility = View.GONE
                    rl_life_photo_pic_more.visibility = View.GONE
                    nsv_life_photo_pic.visibility = View.GONE

                    nsv_life_photo_default.visibility = View.VISIBLE

                    haveFirstPic = false
                    mLifeFirstUrl = ""
                    mLifeFirstText = ""

                    iv_life_photo_pic_one_icon.visibility = View.VISIBLE
                    tv_life_photo_pic_one.text = "添加描述"

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

                            rl_life_photo_pic_five.visibility = View.GONE
                            rl_life_photo_pic_more.visibility = View.VISIBLE

                            // 移动第三张

                            mLifeSecondUrl = mLifeThirdUrl
                            mLifeSecondText = mLifeThirdText
                            mLifeSecondId = mLifeThirdId
                            mLifeSecondState = mLifeThirdState

                            Glide.with(applicationContext).load(mLifeSecondUrl)
                                .into(iv_life_photo_pic_two)
                            tv_life_photo_pic_two.text = mLifeSecondText

                            // 移动第四张

                            mLifeThirdUrl = mLifeFourUrl
                            mLifeThirdText = mLifeFourText
                            mLifeThirdId = mLifeFourId
                            mLifeThirdState = mLifeFourState

                            Glide.with(applicationContext).load(mLifeThirdUrl)
                                .into(iv_life_photo_pic_three)
                            tv_life_photo_pic_three.text = mLifeThirdText


                            // 移动第五张

                            mLifeFourUrl = mLifeFiveUrl
                            mLifeFourText = mLifeFiveText
                            mLifeFourId = mLifeFiveId
                            mLifeFourState = mLifeFiveState

                            Glide.with(applicationContext).load(mLifeFourUrl)
                                .into(iv_life_photo_pic_four)
                            tv_life_photo_pic_four.text = mLifeFourText

                            haveFivePic = false
                            mLifeFiveUrl = ""
                            mLifeFiveText = ""

                            iv_life_photo_pic_five_icon.visibility = View.VISIBLE
                            tv_life_photo_pic_five.text = "添加描述"

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

                            rl_life_photo_pic_four.visibility = View.GONE

                            // 移动第三张

                            mLifeSecondUrl = mLifeThirdUrl
                            mLifeSecondText = mLifeThirdText
                            mLifeSecondId = mLifeThirdId
                            mLifeSecondState = mLifeThirdState

                            Glide.with(applicationContext).load(mLifeSecondUrl)
                                .into(iv_life_photo_pic_two)
                            tv_life_photo_pic_two.text = mLifeSecondText

                            // 移动第四张

                            mLifeThirdUrl = mLifeFourUrl
                            mLifeThirdText = mLifeFourText
                            mLifeThirdId = mLifeFourId
                            mLifeThirdState = mLifeFourState

                            Glide.with(applicationContext).load(mLifeThirdUrl)
                                .into(iv_life_photo_pic_three)
                            tv_life_photo_pic_three.text = mLifeThirdText

                            haveFourPic = false
                            mLifeFourUrl = ""
                            mLifeFourText = ""

                            iv_life_photo_pic_four_icon.visibility = View.VISIBLE
                            tv_life_photo_pic_four.text = "添加描述"

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
                        rl_life_photo_pic_three.visibility = View.GONE

                        mLifeSecondUrl = mLifeThirdUrl
                        mLifeSecondText = mLifeThirdText
                        mLifeSecondId = mLifeThirdId
                        mLifeSecondState = mLifeThirdState

                        Glide.with(applicationContext).load(mLifeSecondUrl)
                            .into(iv_life_photo_pic_two)
                        tv_life_photo_pic_two.text = mLifeSecondText

                        haveThirdPic = false
                        mLifeThirdUrl = ""
                        mLifeThirdText = ""

                        iv_life_photo_pic_three_icon.visibility = View.VISIBLE
                        tv_life_photo_pic_three.text = "添加描述"

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

                    rl_life_photo_pic_two.visibility = View.GONE

                    haveSecondPic = false
                    mLifeSecondUrl = ""
                    mLifeSecondText = ""

                    iv_life_photo_pic_two_icon.visibility = View.VISIBLE
                    tv_life_photo_pic_two.text = "添加描述"

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

                        rl_life_photo_pic_five.visibility = View.GONE
                        rl_life_photo_pic_more.visibility = View.VISIBLE

                        // 移动第四张

                        mLifeThirdUrl = mLifeFourUrl
                        mLifeThirdText = mLifeFourText
                        mLifeThirdId = mLifeFourId
                        mLifeThirdState = mLifeFourState

                        Glide.with(applicationContext).load(mLifeThirdUrl)
                            .into(iv_life_photo_pic_three)
                        tv_life_photo_pic_three.text = mLifeThirdText

                        // 移动第五张

                        mLifeFourUrl = mLifeFiveUrl
                        mLifeFourText = mLifeFiveText
                        mLifeFourId = mLifeFiveId
                        mLifeFourState = mLifeFiveState

                        Glide.with(applicationContext).load(mLifeFourUrl)
                            .into(iv_life_photo_pic_four)
                        tv_life_photo_pic_four.text = mLifeFourText

                        haveFivePic = false
                        mLifeFiveUrl = ""
                        mLifeFiveText = ""

                        iv_life_photo_pic_five_icon.visibility = View.VISIBLE
                        tv_life_photo_pic_five.text = "添加描述"

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

                        rl_life_photo_pic_four.visibility = View.GONE

                        mLifeThirdUrl = mLifeFourUrl
                        mLifeThirdText = mLifeFourText
                        mLifeThirdId = mLifeFourId
                        mLifeThirdState = mLifeFourState

                        Glide.with(applicationContext).load(mLifeThirdUrl)
                            .into(iv_life_photo_pic_three)
                        tv_life_photo_pic_three.text = mLifeThirdText

                        haveFourPic = false
                        mLifeFourUrl = ""
                        mLifeFourText = ""

                        iv_life_photo_pic_four_icon.visibility = View.VISIBLE
                        tv_life_photo_pic_four.text = "添加描述"

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
                    rl_life_photo_pic_three.visibility = View.GONE

                    haveThirdPic = false
                    mLifeThirdUrl = ""
                    mLifeThirdText = ""

                    iv_life_photo_pic_three_icon.visibility = View.VISIBLE
                    tv_life_photo_pic_three.text = "添加描述"

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

                    rl_life_photo_pic_five.visibility = View.GONE
                    rl_life_photo_pic_more.visibility = View.VISIBLE

                    mLifeFourUrl = mLifeFiveUrl
                    mLifeFourText = mLifeFiveText
                    mLifeFourId = mLifeFiveId
                    mLifeFourState = mLifeFiveState

                    Glide.with(applicationContext).load(mLifeFourUrl).into(iv_life_photo_pic_four)
                    tv_life_photo_pic_four.text = mLifeFourText

                    haveFivePic = false
                    mLifeFiveUrl = ""
                    mLifeFiveText = ""

                    iv_life_photo_pic_five_icon.visibility = View.VISIBLE
                    tv_life_photo_pic_five.text = "添加描述"

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

                    rl_life_photo_pic_four.visibility = View.GONE

                    haveFourPic = false
                    mLifeFourUrl = ""
                    mLifeFourText = ""

                    iv_life_photo_pic_four_icon.visibility = View.VISIBLE
                    tv_life_photo_pic_four.text = "添加描述"

                    // 更新存储数据
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_TEXT, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_ID, "")
                    SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FOUR_AUDIT, "")

                }

            }
            "five" -> {

                rl_life_photo_pic_five.visibility = View.GONE
                rl_life_photo_pic_more.visibility = View.VISIBLE

                haveFivePic = false
                mLifeFiveUrl = ""
                mLifeFiveText = ""

                iv_life_photo_pic_five_icon.visibility = View.VISIBLE
                tv_life_photo_pic_five.text = "添加描述"

                // 更新存储数据
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_TEXT, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_ID, "")
                SPStaticUtils.put(Constant.ME_LIFE_PHOTO_FIVE_AUDIT, "")
            }
        }
        ll_life_photo_loading.visibility = View.GONE
    }

    override fun onDoDeletePhotoError() {
        ll_life_photo_loading.visibility = View.GONE
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = intent
            setResult(RESULT_OK, intent)
            finish()
        }
        return super.onKeyDown(keyCode, event)

    }

    override fun onDestroy() {
        super.onDestroy()

        doDeletePhotoPresent.unregisterCallback(this)

    }

}