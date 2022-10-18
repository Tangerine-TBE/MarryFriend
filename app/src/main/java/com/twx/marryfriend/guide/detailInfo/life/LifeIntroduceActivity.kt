package com.twx.marryfriend.guide.detailInfo.life

import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.FaceDetectBean
import com.twx.marryfriend.bean.TextVerifyBean
import com.twx.marryfriend.bean.UploadPhotoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback
import com.twx.marryfriend.net.impl.doLifeFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.doUploadPhotoPresentImpl
import com.twx.marryfriend.utils.BitmapUtil
import com.twx.marryfriend.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_base_info.*
import kotlinx.android.synthetic.main.activity_life_introduce.*
import kotlinx.android.synthetic.main.layout_guide_step_name.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class LifeIntroduceActivity : MainBaseViewActivity(),
    IDoTextVerifyCallback, IDoLifeFaceDetectCallback, IDoUploadPhotoCallback {

    private var picPath = ""
    private var introduce = ""
    private var picUrl = ""

    private var haveUpload = false

    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl
    private lateinit var doFaceDetectPresent: doLifeFaceDetectPresentImpl
    private lateinit var doUploadPhotoPresent: doUploadPhotoPresentImpl

    private lateinit var client: BosClient

    override fun getLayoutView(): Int = R.layout.activity_life_introduce

    override fun initView() {
        super.initView()

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

        doFaceDetectPresent = doLifeFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        doUploadPhotoPresent = doUploadPhotoPresentImpl.getsInstance()
        doUploadPhotoPresent.registerCallback(this)

        picPath = intent.getStringExtra("path").toString()
        introduce = intent.getStringExtra("introduce").toString()

        Log.i("guo", "picPath : $picPath")

        Glide.with(this)
            .load(picPath)
//            .apply(requestOptions)
            .into(iv_life_introduce_container)

        et_life_introduce_introduce.setText(introduce)

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

        iv_life_introduce_finish.setOnClickListener {
            finish()
            this.finish()
        }

        tv_life_introduce_confirm.setOnClickListener {

            ll_life_introduce_loading.visibility = View.VISIBLE

            // 文字审核
            introduce = et_life_introduce_introduce.text.toString().trim { it <= ' ' }

            Log.i("guo", "doTextVerify")

            if (introduce.isNotEmpty()) {
                val map: MutableMap<String, String> = TreeMap()
                map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
                map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
                map[Contents.TEXT] = introduce
                doTextVerifyPresent.doTextVerify(map)

            } else {

                Log.i("guo", "doFaceDetect")

                haveUpload = false
                doFaceDetect()
            }

        }

        ll_life_introduce_loading.setOnClickListener {

        }

    }

    // 生活照 百度云审核
    private fun doFaceDetect() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.LIFE_ACCESS_TOKEN, "")
        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
        map[Contents.IMAGE] = bitmapToBase64(ImageUtils.getBitmap(picPath))
        doFaceDetectPresent.doLifeFaceDetect(map)
    }

    // 上传生活照
    private fun uploadPhoto(imageUrl: String, fileType: String, fileName: String, content: String) {

        val map: MutableMap<String, String> = TreeMap()

        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.IMAGE_URL] = imageUrl
        map[Contents.FILE_TYPE] = fileType
        map[Contents.FILE_NAME] = fileName
        map[Contents.CONTENT] = content

        doUploadPhotoPresent.doUploadPhoto(map)
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


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoLifeFaceDetectSuccess(faceDetectBean: FaceDetectBean?) {

        if (faceDetectBean != null) {
            if (faceDetectBean.conclusion != "合规") {
                ll_life_introduce_loading.visibility = View.GONE
                ToastUtils.showShort("图片审核失败，请稍后再试")
            } else {
                // 图片合规，开始进行上传

                Log.i("guo", "图片合规，开始进行上传")

                if (!haveUpload) {

                    Log.i("guo", "haveUpload")

                    haveUpload = true

                    Thread {

                        val file = File(picPath)

                        val bitmap =
                            BitmapUtil.generateBitmap("佳偶婚恋交友", 16f, Color.WHITE)
                                ?.let {
                                    BitmapUtil.createWaterMarkBitmap(ImageUtils.getBitmap(
                                        file), it)
                                }

                        val mPhotoPath =
                            this.externalCacheDir.toString() + File.separator + "${
                                FileUtils.getFileNameNoExtension(picPath)
                            }.png"

                        if (bitmap != null) {
                            BitmapUtil.saveBitmap(bitmap, mPhotoPath)
                        }


                        val name =
                            if (SPStaticUtils.getString(Constant.USER_ID, "default").length == 1) {
                                "0${SPStaticUtils.getString(Constant.USER_ID, "default")}"
                            } else {
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }

                        // 时间戳，防止重复上传时传不上去
                        val span = TimeUtils.getNowMills()
                        val path = "${FileUtils.getFileNameNoExtension(picPath)}_${span}.jpg"

                        val putObjectFromFileResponse = client.putObject("user${name}",
                            path,
                            File(mPhotoPath))

                        picUrl = client.generatePresignedUrl("user${name}",
                            path, -1).toString()

                        Log.i("guo", "mLifeSecondUrl :$picUrl")

                        uploadPhoto(picUrl,
                            FileUtils.getFileNameNoExtension(picPath),
                            FileUtils.getFileExtension(picPath),
                            introduce)

                    }.start()

                }

            }
        }

    }

    override fun onDoLifeFaceDetectError() {
        ll_life_introduce_loading.visibility = View.GONE
    }

    override fun onDoUploadPhotoSuccess(uploadPhotoBean: UploadPhotoBean?) {
        ll_life_introduce_loading.visibility = View.GONE
        // 上传成功,带数据返回上一页
        if (uploadPhotoBean != null) {
            if (uploadPhotoBean.code == 200) {
                ToastUtils.showShort("上传成功")

                // 需要返回给前一个界面的值
                val intent = intent
                intent.putExtra("path", picPath)
                intent.putExtra("url", picUrl)
                intent.putExtra("id", uploadPhotoBean.data[0].toString())
                intent.putExtra("text", introduce)
                setResult(RESULT_OK, intent)
                finish()

            } else {
                ll_life_introduce_loading.visibility = View.GONE
                ToastUtils.showShort(uploadPhotoBean.msg)
            }
        }

    }

    override fun onDoUploadPhotoError() {
        ll_life_introduce_loading.visibility = View.GONE
    }

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean) {

        if (textVerifyBean.conclusion == "合规") {
            // 进行图片审核
            haveUpload = false
            doFaceDetect()
        } else {
            if (textVerifyBean.error_msg != null) {
                ToastUtils.showShort(textVerifyBean.error_msg)
            } else {
                ToastUtils.showShort(textVerifyBean.data[0].msg)
            }
            et_life_introduce_introduce.setText("")
        }

    }


    override fun onDoTextVerifyError() {
        ll_life_introduce_loading.visibility = View.GONE
        ToastUtils.showShort("网络出现故障，无法完成文字校验，请稍后再试")
    }

    override fun onDestroy() {
        super.onDestroy()

        doTextVerifyPresent.unregisterCallback(this)
        doFaceDetectPresent.unregisterCallback(this)
        doUploadPhotoPresent.unregisterCallback(this)

    }

}