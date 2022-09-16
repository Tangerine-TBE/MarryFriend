package com.twx.marryfriend.set.feedback

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.baidubce.auth.DefaultBceCredentials
import com.baidubce.services.bos.BosClient
import com.baidubce.services.bos.BosClientConfiguration
import com.blankj.utilcode.util.*
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
import com.twx.marryfriend.bean.TextVerifyBean
import com.twx.marryfriend.bean.vip.UploadFeedbackBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.vip.IDoUploadFeedbackCallback
import com.twx.marryfriend.net.impl.doLifeFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.vip.doUploadFeedbackPresentImpl
import com.twx.marryfriend.set.adapter.ReportDataAdapter
import com.twx.marryfriend.set.adapter.SuggestionAdapter
import com.twx.marryfriend.utils.GlideEngine
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_suggestion.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class SuggestionActivity : MainBaseViewActivity(), IDoTextVerifyCallback, IDoLifeFaceDetectCallback,
    IDoUploadFeedbackCallback,
    SuggestionAdapter.OnItemClickListener {


    private var mChoosePath = ""

    private var mImageUrl1 = ""

    private var mImageUrl2 = ""

    private var mImageUrl3 = ""

    private var suggestion = ""

    private var mList: MutableList<String> = arrayListOf()

    private lateinit var adapter: SuggestionAdapter

    private lateinit var client: BosClient

    private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl
    private lateinit var doFaceDetectPresent: doLifeFaceDetectPresentImpl
    private lateinit var doUploadFeedbackPresent: doUploadFeedbackPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_suggestion

    override fun initView() {
        super.initView()

        doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
        doTextVerifyPresent.registerCallback(this)

        doFaceDetectPresent = doLifeFaceDetectPresentImpl.getsInstance()
        doFaceDetectPresent.registerCallback(this)

        doUploadFeedbackPresent = doUploadFeedbackPresentImpl.getsInstance()
        doUploadFeedbackPresent.registerCallback(this)

        adapter = SuggestionAdapter(mList)
        adapter.setOnItemClickListener(this)

        rv_suggestion_container.layoutManager = GridLayoutManager(this, 3)
        rv_suggestion_container.adapter = adapter

        mList.add("add")

        adapter.notifyDataSetChanged()

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

        iv_suggestion_finish.setOnClickListener {
            finish()
        }

        et_suggestion_feedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                suggestion = s.toString()

                tv_suggestion_text_sum.text = "${suggestion.length.toString()}/500"

                val havePhoto = !(mList.size == 1 && mList.contains("add"))

                if (suggestion != "" && havePhoto) {
                    iv_suggestion_commit.setBackgroundResource(R.drawable.shape_bg_commit)
                } else {
                    iv_suggestion_commit.setBackgroundResource(R.drawable.shape_bg_commit_non)
                }

            }

        })

        iv_suggestion_commit.setOnClickListener {

            var mUrl = ""
            for (i in 0.until(mList.size)) {
                mUrl = "$mUrl,${mList[i]}"
            }

            val havePhoto = !(mList.size == 1 && mList.contains("add"))

            if (suggestion != "" && havePhoto) {
                ll_suggestion_load.visibility = View.VISIBLE
                doTextDetect(suggestion)
            } else {
                ToastUtils.showShort("请填写完整信息")
            }

        }

    }

    // 生活照 百度云审核
    private fun doFaceDetect(picPath: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.LIFE_ACCESS_TOKEN, "")
        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
        map[Contents.IMAGE] = bitmapToBase64(ImageUtils.getBitmap(picPath))
        doFaceDetectPresent.doLifeFaceDetect(map)
    }

    // 文字审核
    private fun doTextDetect(text: String) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.ACCESS_TOKEN] = SPStaticUtils.getString(Constant.ACCESS_TOKEN, "")
        map[Contents.CONTENT_TYPE] = "application/x-www-form-urlencoded"
        map[Contents.TEXT] = text
        doTextVerifyPresent.doTextVerify(map)
    }


    // 上传意见反馈
    private fun doUploadFeedback(
        suggestion: String,
        contact1: String,
        contact2: String,
        image1: String,
        image2: String,
        image3: String,
    ) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.CONTENT_TEXT] = suggestion
        map[Contents.PLATFORM] = "360"
        map[Contents.VERSION] = "1.0"
        map[Contents.CONTACT_1] = contact1
        map[Contents.CONTACT_2] = contact2
        map[Contents.IMAGE_1] = image1
        map[Contents.IMAGE_2] = image2
        map[Contents.IMAGE_3] = image3
        doUploadFeedbackPresent.doUploadFeedback(map)
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

    override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean?) {
        if (textVerifyBean != null) {
            if (textVerifyBean.conclusion == "合规") {

                if (mList.contains("add")) {
                    mList.remove("add")
                }

                when (mList.size) {
                    1 -> {
                        mImageUrl1 = mList[0]
                        mImageUrl2 = ""
                        mImageUrl3 = ""
                    }
                    2 -> {
                        mImageUrl1 = mList[0]
                        mImageUrl2 = mList[1]
                        mImageUrl3 = ""
                    }
                    3 -> {
                        mImageUrl1 = mList[0]
                        mImageUrl2 = mList[1]
                        mImageUrl3 = mList[2]
                    }
                }

                doUploadFeedback(suggestion, "", "", mImageUrl1, mImageUrl2, mImageUrl3)

            } else {

                if (textVerifyBean.error_msg != null) {
                    ToastUtils.showShort(textVerifyBean.error_msg)
                } else {
                    ToastUtils.showShort(textVerifyBean.data[0].msg)
                }
                ll_suggestion_load.visibility = View.GONE
            }
        }
    }

    override fun onDoTextVerifyError() {
        ll_suggestion_load.visibility = View.GONE
    }

    override fun onDoLifeFaceDetectSuccess(faceDetectBean: FaceDetectBean?) {
        if (faceDetectBean != null) {
            if (faceDetectBean.conclusion == "合规") {

                Thread {

                    val putObjectFromFileResponse = client.putObject("user${
                        SPStaticUtils.getString(Constant.USER_ID, "default")
                    }",
                        "${FileUtils.getFileNameNoExtension(mChoosePath)}.jpg",
                        File(mChoosePath))

                    mList.add(0, client.generatePresignedUrl("user${
                        SPStaticUtils.getString(Constant.USER_ID, "default")
                    }", "${FileUtils.getFileNameNoExtension(mChoosePath)}.jpg", -1)
                        .toString())

                    ThreadUtils.runOnUiThread {

                        if (mList.size == 4) {
                            mList.removeAt(3)
                        }

                        if (mList.size != 3) {
                            tv_suggestion_photo_size.text = "${mList.size - 1}/3张"
                        } else {
                            tv_suggestion_photo_size.text = "${mList.size}/3张"
                        }

                        adapter.notifyDataSetChanged()

                        val havePhoto = !(mList.size == 1 && mList.contains("add"))

                        if (suggestion != "" && havePhoto) {
                            iv_suggestion_commit.setBackgroundResource(R.drawable.shape_bg_commit)
                        } else {
                            iv_suggestion_commit.setBackgroundResource(R.drawable.shape_bg_commit_non)
                        }

                    }

                }.start()

            } else {

                if (faceDetectBean.error_msg != null) {
                    ToastUtils.showShort(faceDetectBean.error_msg)
                } else {
                    ToastUtils.showShort(faceDetectBean.data[0].msg)
                }
            }
        }

        ll_suggestion_load.visibility = View.GONE
    }

    override fun onDoLifeFaceDetectError() {
        ToastUtils.showShort("图片审核失败，请稍后重新选择上传")
        ll_suggestion_load.visibility = View.GONE
    }

    override fun onDoUploadFeedbackSuccess(uploadFeedbackBean: UploadFeedbackBean?) {
        ll_suggestion_load.visibility = View.GONE
        if (uploadFeedbackBean != null) {
            if (uploadFeedbackBean.code == 200) {
                ToastUtils.showShort("反馈成功，请耐心等待反馈")
            } else {
                ToastUtils.showShort("反馈失败，请等到网络稳定后重新尝试")
            }
        }
    }

    override fun onDoUploadFeedbackError() {
        ll_suggestion_load.visibility = View.GONE
    }

    override fun onItemClick(v: View?, position: Int) {
        if (position == mList.size - 1 && mList.contains("add")) {
            ToastUtils.showShort("文件点击事件")


            val selectorStyle = PictureSelectorStyle()
            val animationStyle = PictureWindowAnimationStyle()
            animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
            animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
            selectorStyle.windowAnimationStyle = animationStyle

            PictureSelector.create(this)
                .openGallery(SelectMimeType.TYPE_IMAGE)
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectionMode(SelectModeConfig.MULTIPLE)
                .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                .setImageSpanCount(3)
                .setMaxSelectNum(1)
                .setMinSelectNum(1)
                .isDisplayCamera(true)
                .isPreviewImage(true)
                .isEmptyResultReturn(true)
                .setLanguage(LanguageConfig.CHINESE)
                .setSelectorUIStyle(selectorStyle)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {

                        if (result != null) {

                            ll_suggestion_load.visibility = View.VISIBLE

                            mChoosePath = result[0].realPath

                            doFaceDetect(result[0].realPath)


                        }

                    }

                    override fun onCancel() {

                    }

                })

        } else {
            ToastUtils.showShort("展示图片")
        }
    }

    override fun onItemCloseClick(v: View?, position: Int) {

        mList.removeAt(position)

        if (!mList.contains("add")) {
            mList.add(mList.size, "add")
        }

        tv_suggestion_photo_size.text = "${mList.size - 1}/3张"

        if (mList.size == 1 && mList.contains("add")) {
            iv_suggestion_commit.setBackgroundResource(R.drawable.shape_bg_commit_non)
        }

        adapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()

        doTextVerifyPresent.unregisterCallback(this)
        doFaceDetectPresent.unregisterCallback(this)
        doUploadFeedbackPresent.unregisterCallback(this)

    }

}