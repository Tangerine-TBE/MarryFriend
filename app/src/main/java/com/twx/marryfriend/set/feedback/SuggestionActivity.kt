package com.twx.marryfriend.set.feedback

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.baidubce.BceClientException
import com.baidubce.BceServiceException
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
import java.net.UnknownHostException
import java.util.*

class SuggestionActivity : MainBaseViewActivity(), IDoUploadFeedbackCallback,
    SuggestionAdapter.OnItemClickListener {


    private var mChoosePath = ""

    private var mImageUrl1 = ""

    private var mImageUrl2 = ""

    private var mImageUrl3 = ""

    private var suggestion = ""

    private var contact = ""

    private var mChooseList: MutableList<String> = arrayListOf()
    private var mList: MutableList<String> = arrayListOf()

    private lateinit var adapter: SuggestionAdapter

    private lateinit var client: BosClient

    private lateinit var doUploadFeedbackPresent: doUploadFeedbackPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_suggestion

    override fun initView() {
        super.initView()

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

        // 设置HTTP最大连接数为10
        config.maxConnections = 10
        // 设置TCP连接超时为5000毫秒
        config.connectionTimeoutInMillis = 15000
        // 设置Socket传输数据超时的时间为2000毫秒
        config.socketTimeoutInMillis = 15000

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

                mList.remove("add")

                Log.i("guo", "true : ${suggestion != "" || mList.isNotEmpty()}")

                if (suggestion != "" || mList.isNotEmpty()) {
                    iv_suggestion_commit.setImageResource(R.mipmap.icon_report_commit)
                } else {
                    iv_suggestion_commit.setImageResource(R.mipmap.icon_report_commit_non)
                }

                mList.add("add")

            }

        })

        et_suggestion_contact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                contact = s.toString()

            }

        })


        iv_suggestion_commit.setOnClickListener {

            mList.remove("add")

            var mUrl = ""
            for (i in 0.until(mList.size)) {
                mUrl = "$mUrl,${mList[i]}"
            }


            if (suggestion != "" || mList.isNotEmpty()) {
                ll_suggestion_load.visibility = View.VISIBLE
                doUploadFeedback(suggestion, contact, mUrl)
            } else {
                ToastUtils.showShort("请填写完整信息")
            }
            mList.add("add")

        }

    }


    // 上传意见反馈
    private fun doUploadFeedback(
        suggestion: String,
        contact1: String,
        image: String,
    ) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.CONTENT_TEXT] = suggestion
        map[Contents.PLATFORM] = "360"
        map[Contents.VERSION] = "1.0"
        map[Contents.CONTACT_1] = contact1
        map[Contents.IMAGES_URL] = image
        doUploadFeedbackPresent.doUploadFeedback(map)
    }


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUploadFeedbackSuccess(uploadFeedbackBean: UploadFeedbackBean?) {
        ll_suggestion_load.visibility = View.GONE
        if (uploadFeedbackBean != null) {
            if (uploadFeedbackBean.code == 200) {
                ToastUtils.showShort("反馈成功，请耐心等待反馈")
                finish()
            } else {
                ToastUtils.showShort("反馈失败，请等到网络稳定后重新尝试")
            }
        }
    }

    override fun onDoUploadFeedbackError() {
        ll_suggestion_load.visibility = View.GONE
        ToastUtils.showShort("举报失败，请等到网络稳定后重新尝试")
    }

    override fun onItemClick(v: View?, position: Int) {

        if (position == mList.size - 1 && mList.contains("add")) {
            ToastUtils.showShort("文件点击事件")


            val selectorStyle = PictureSelectorStyle()
            val animationStyle = PictureWindowAnimationStyle()
            animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
            animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
            selectorStyle.windowAnimationStyle = animationStyle

            val maxSize = 10 - mList.size

            PictureSelector.create(this).openGallery(SelectMimeType.TYPE_IMAGE)
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectionMode(SelectModeConfig.MULTIPLE)
                .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION).setImageSpanCount(3)
                .setMaxSelectNum(maxSize).setMinSelectNum(1).isDisplayCamera(true)
                .isPreviewImage(true).isEmptyResultReturn(true).setLanguage(LanguageConfig.CHINESE)
                .setSelectorUIStyle(selectorStyle)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {

                        if (result != null) {

                            ll_suggestion_load.visibility = View.VISIBLE

                            mChooseList.clear()

                            mList.remove("add")

                            for (i in 0.until(result.size)) {
                                mChooseList.add(result[i].realPath)
                            }

                            Thread {

                                try {


                                    for (i in 0.until(mChooseList.size)) {

                                        val span = TimeUtils.getNowMills()
                                        val path =
                                            "${FileUtils.getFileNameNoExtension(mChooseList[i])}_${span}.jpg"

                                        val putObjectFromFileResponse = client.putObject("user${
                                            SPStaticUtils.getString(Constant.USER_ID, "default")
                                        }", path, File(mChooseList[i]))

                                        mList.add(0, client.generatePresignedUrl("user${
                                            SPStaticUtils.getString(Constant.USER_ID, "default")
                                        }", path, -1).toString())

                                    }

                                    ThreadUtils.runOnUiThread {

                                        Log.i("guo", "size : ${mList.size}")

                                        tv_suggestion_photo_size.text = "${mList.size}/9张"

                                        if (suggestion != "" || mList.isNotEmpty()) {
                                            iv_suggestion_commit.setImageResource(R.mipmap.icon_report_commit)
                                        } else {
                                            iv_suggestion_commit.setImageResource(R.mipmap.icon_report_commit_non)
                                        }

                                        if (mList.size < 9) {
                                            mList.add("add")
                                        }

                                        ll_suggestion_load.visibility = View.GONE

                                        adapter.notifyDataSetChanged()

                                    }

                                } catch (e: BceClientException) {
                                    e.printStackTrace()
                                    ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                } catch (e: BceServiceException) {
                                    Log.i("guo", "Error ErrorCode: " + e.errorCode);
                                    Log.i("guo", "Error RequestId: " + e.requestId);
                                    Log.i("guo", "Error StatusCode: " + e.statusCode);
                                    Log.i("guo", "Error ErrorType: " + e.errorType);
                                } catch (e: UnknownHostException) {
                                    Log.i("guo","网络请求错误，请检查网络后稍后重试")
                                    ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                    e.printStackTrace()
                                }

                            }.start()

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

        if (mList.size == 1 || mList.contains("add")) {
            iv_suggestion_commit.setImageResource(R.mipmap.icon_report_commit_non)
        }

        adapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()

        doUploadFeedbackPresent.unregisterCallback(this)

    }

}