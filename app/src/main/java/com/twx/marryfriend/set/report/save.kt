package com.twx.marryfriend.set.report

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import com.twx.marryfriend.bean.vip.ReportOtherBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.vip.IDoReportOtherCallback
import com.twx.marryfriend.net.impl.doLifeFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.vip.doReportOtherPresentImpl
import com.twx.marryfriend.set.adapter.ReportDataAdapter
import com.twx.marryfriend.utils.GlideEngine
import kotlinx.android.synthetic.main.activity_report.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/9/19
 */
class save {


    class ReportActivity : MainBaseViewActivity(), IDoTextVerifyCallback, IDoLifeFaceDetectCallback,
        IDoReportOtherCallback, ReportDataAdapter.OnItemClickListener {

        private var hostId = ""

        private var guestId = ""

        private var mode = 0

        private var reportText = ""

        private var mList: MutableList<String> = arrayListOf()

        private var mChooseList: MutableList<String> = arrayListOf()
        private var mDetectList: MutableList<String> = arrayListOf()

        private lateinit var adapter: ReportDataAdapter

        private lateinit var client: BosClient

        private lateinit var doTextVerifyPresent: doTextVerifyPresentImpl
        private lateinit var doFaceDetectPresent: doLifeFaceDetectPresentImpl
        private lateinit var doReportOtherPresent: doReportOtherPresentImpl

        companion object {

            private const val HOST_ID = "host_Id"
            private const val GUEST_ID = "guest_Id"
            private const val MODE = "mode"
            fun getIntent(context: Context, hostId: String, guestId: String, mode: Int): Intent {
                val intent = Intent(context, ReportActivity::class.java)
                intent.putExtra(HOST_ID, hostId)
                intent.putExtra(GUEST_ID, guestId)
                intent.putExtra(MODE, mode)
                return intent
            }
        }

        override fun getLayoutView(): Int = R.layout.activity_report

        override fun initView() {
            super.initView()

            hostId = intent.getStringExtra("host_Id").toString()
            guestId = intent.getStringExtra("guest_Id").toString()
            mode = intent.getIntExtra("mode", 0)

            Log.i("guo", mode.toString())

            doTextVerifyPresent = doTextVerifyPresentImpl.getsInstance()
            doTextVerifyPresent.registerCallback(this)

            doFaceDetectPresent = doLifeFaceDetectPresentImpl.getsInstance()
            doFaceDetectPresent.registerCallback(this)

            doReportOtherPresent = doReportOtherPresentImpl.getsInstance()
            doReportOtherPresent.registerCallback(this)

            adapter = ReportDataAdapter(mList)
            adapter.setOnItemClickListener(this)

            rv_report_container.layoutManager = GridLayoutManager(this, 3)
            rv_report_container.adapter = adapter

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

            iv_report_reason_finish.setOnClickListener {
                finish()
            }


            et_report_edit_container.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    reportText = s.toString()

                    tv_report_edit_text_sum.text = "${reportText.length.toString()}/500"

                    if (reportText != "" && mList.size != 1) {
                        iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit)
                    } else {
                        iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit_non)
                    }

                }

            })


            iv_report_reason_commit.setOnClickListener {

                var mUrl = ""
                for (i in 0.until(mList.size)) {
                    mUrl = "$mUrl,${mList[i]}"
                }

                if (reportText != "" && mList.size != 1) {
                    ll_report_load.visibility = View.VISIBLE
                    doTextDetect(reportText)
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

        // 上传举报信息
        private fun doReport(text: String, url: String) {
            val map: MutableMap<String, String> = TreeMap()
            map[Contents.HOST_UID] = hostId
            map[Contents.GUEST_UID] = guestId
            map[Contents.REASON_CODE] = mode.toString()
            map[Contents.REASON_TEXT] = text
            map[Contents.MARK_NOTICE] = "0"
            map[Contents.IMAGE_URL] = url

            Log.i("guo", map.toString())

            doReportOtherPresent.doReportOther(map)
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

        override fun onItemClick(v: View?, position: Int) {
            if (position == mList.size - 1) {
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
                    .isPreviewImage(true).isEmptyResultReturn(true)
                    .setLanguage(LanguageConfig.CHINESE).setSelectorUIStyle(selectorStyle)
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {

                            if (result != null) {

                                ll_report_load.visibility = View.VISIBLE

                                for (i in 0.until(result.size)) {
                                    mChooseList.add(result[i].realPath)
                                }

                                for (i in 0.until(result.size)) {
                                    doFaceDetect(result[i].realPath)
                                }

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

            if (mList.size != 9) {
                tv_report_photo_size.text = "${mList.size - 1}/9张"
            } else {
                tv_report_photo_size.text = "${mList.size}/9张"
            }

            adapter.notifyDataSetChanged()
        }

        override fun onLoading() {

        }

        override fun onError() {

        }

        override fun onDoTextVerifySuccess(textVerifyBean: TextVerifyBean?) {
            if (textVerifyBean != null) {
                if (textVerifyBean.conclusion == "合规") {

                    var mUrl = ""
                    for (i in 0.until(mList.size)) {
                        mUrl = "$mUrl,${mList[i]}"
                    }

                    doReport(reportText, mUrl)

                } else {

                    if (textVerifyBean.error_msg != null) {
                        ToastUtils.showShort(textVerifyBean.error_msg)
                    } else {
                        ToastUtils.showShort(textVerifyBean.data[0].msg)
                    }
                    ll_report_load.visibility = View.GONE
                }
            }
        }

        override fun onDoTextVerifyError() {
            ll_report_load.visibility = View.GONE
        }

        override fun onDoReportOtherSuccess(reportOtherBean: ReportOtherBean?) {
            ll_report_load.visibility = View.GONE
            if (reportOtherBean != null) {
                if (reportOtherBean.code == 200) {
                    ToastUtils.showShort("举报成功，请耐心等待反馈")
                } else {
                    ToastUtils.showShort("举报失败，请等到网络稳定后重新尝试")
                }
            }
        }

        override fun onDoReportOtherError() {
            ll_report_load.visibility = View.GONE
            ToastUtils.showShort("举报失败，请等到网络稳定后重新尝试")
        }

        override fun onDoLifeFaceDetectSuccess(faceDetectBean: FaceDetectBean?) {

            if (faceDetectBean != null) {
                mDetectList.add(faceDetectBean.conclusion)
            }

            if (mDetectList.size == mChooseList.size) {

                Log.i("guo", mDetectList.toString())

                val mErrorList = mutableListOf<Int>()

                for (i in 0.until(mDetectList.size)) {
                    if (mDetectList[i] == "合规") {

                        Thread {

                            val span = TimeUtils.getNowMills()
                            val path =
                                "${FileUtils.getFileNameNoExtension(mChooseList[i])}_${span}.jpg"

                            val putObjectFromFileResponse = client.putObject("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", path, File(mChooseList[i]))

                            mList.add(0, client.generatePresignedUrl("user${
                                SPStaticUtils.getString(Constant.USER_ID, "default")
                            }", path, -1).toString())

                            ThreadUtils.runOnUiThread {

                                if (mList.size != 9) {
                                    tv_report_photo_size.text = "${mList.size - 1}/9张"
                                } else {
                                    tv_report_photo_size.text = "${mList.size}/9张"
                                }

                                adapter.notifyDataSetChanged()
                                if (mErrorList.size != 0) {
                                    ToastUtils.showShort("共有${mErrorList.size}张图片违规")
                                }

                                if (reportText != "" && mList.size != 1) {
                                    iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit)
                                } else {
                                    iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit_non)
                                }
                            }

                        }.start()

                    } else {
                        mErrorList.add(i)
                    }
                }

                if (mList.size == 10) {
                    mList.removeAt(9)
                }

                ll_report_load.visibility = View.GONE


            }

        }

        override fun onDoLifeFaceDetectError() {

            ToastUtils.showShort("图片审核失败，请稍后重新选择上传")
            mDetectList.add("null")

            if (mDetectList.size == mChooseList.size) {
                ll_report_load.visibility = View.GONE
            }
        }

        override fun onDestroy() {
            super.onDestroy()

            doTextVerifyPresent.unregisterCallback(this)
            doFaceDetectPresent.unregisterCallback(this)
            doReportOtherPresent.unregisterCallback(this)

        }

    }

}