package com.twx.marryfriend.set.report

import android.content.Context
import android.content.Intent
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
import com.twx.marryfriend.bean.vip.ReportOtherBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.vip.IDoReportOtherCallback
import com.twx.marryfriend.net.impl.vip.doReportOtherPresentImpl
import com.twx.marryfriend.set.adapter.ReportDataAdapter
import com.twx.marryfriend.utils.GlideEngine
import kotlinx.android.synthetic.main.activity_report.*
import java.io.File
import java.util.*

class ReportActivity : MainBaseViewActivity(), IDoReportOtherCallback,
    ReportDataAdapter.OnItemClickListener {

    private var hostId = ""

    private var guestId = ""

    private var mode = 0

    private var reportText = ""

    private var mChooseList: MutableList<String> = arrayListOf()
    private var mList: MutableList<String> = arrayListOf()

    private lateinit var adapter: ReportDataAdapter

    private lateinit var client: BosClient

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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                reportText = s.toString()

                tv_report_edit_text_sum.text = "${reportText.length.toString()}/500"

                mList.remove("add")

                if (reportText != "" || mList.isNotEmpty()) {
                    iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit)
                } else {
                    iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit_non)
                }

                mList.add("add")
            }
        })

        iv_report_reason_commit.setOnClickListener {

            mList.remove("add")

            var mUrl = ""
            for (i in 0.until(mList.size)) {
                mUrl = "$mUrl,${mList[i]}"
            }

            if (reportText != "" && mList.isNotEmpty()) {
                ll_report_load.visibility = View.VISIBLE

                doReport(reportText, mUrl)

            } else {
                ToastUtils.showShort("请填写完整信息")
            }

            mList.add("add")
        }
    }

    // 上传举报信息
    private fun doReport(text: String, url: String) {

        val reasonText = when (mode) {
            0 -> {
                "其它"
            }
            1 -> {
                "恶语辱骂"
            }
            2 -> {
                "广告骚扰"
            }
            3 -> {
                "投资诈骗"
            }
            4 -> {
                "涉黄涉赌"
            }
            5 -> {
                "资料作假"
            }
            6 -> {
                "不当言论"
            }
            7 -> {
                "其他平台违规"
            }
            else -> {
                "其它"
            }
        }

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.HOST_UID] = hostId
        map[Contents.GUEST_UID] = guestId
        map[Contents.REASON_CODE] = (mode + 1).toString()
        map[Contents.REASON_TEXT] = reasonText
        map[Contents.MARK_NOTICE] = text
        map[Contents.IMAGE_URL] = url
        doReportOtherPresent.doReportOther(map)
    }

    override fun onItemClick(v: View?, position: Int) {
        if (position == mList.size - 1) {

            val selectorStyle = PictureSelectorStyle()
            val animationStyle = PictureWindowAnimationStyle()
            animationStyle.setActivityEnterAnimation(R.anim.ps_anim_up_in)
            animationStyle.setActivityExitAnimation(R.anim.ps_anim_down_out)
            selectorStyle.windowAnimationStyle = animationStyle

            val maxSize = 10 - mList.size

            PictureSelector.create(this)
                .openGallery(SelectMimeType.TYPE_IMAGE)
                .setImageEngine(GlideEngine.createGlideEngine())
                .setSelectionMode(SelectModeConfig.MULTIPLE)
                .setRecyclerAnimationMode(AnimationType.ALPHA_IN_ANIMATION)
                .setImageSpanCount(3)
                .setMaxSelectNum(maxSize)
                .setMinSelectNum(1)
                .isDisplayCamera(true)
                .isPreviewImage(true)
                .isEmptyResultReturn(true)
                .setLanguage(LanguageConfig.CHINESE)
                .setSelectorUIStyle(selectorStyle)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {

                        if (result != null) {

                            ll_report_load.visibility = View.VISIBLE

                            mChooseList.clear()

                            mList.remove("add")

                            for (i in 0.until(result.size)) {
                                mChooseList.add(result[i].realPath)
                            }

                            Thread {

                                for (i in 0.until(mChooseList.size)) {

                                    val coverName = TimeUtils.getNowMills()

                                    val putObjectFromFileResponse = client.putObject("user${
                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                    }",
                                        "${coverName}.jpg",
                                        File(mChooseList[i]))

                                    mList.add(0, client.generatePresignedUrl("user${
                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                    }",
                                        "${coverName}.jpg",
                                        -1)
                                        .toString())

                                }

                                ThreadUtils.runOnUiThread {

                                    Log.i("guo", "size : ${mList.size}")

                                    tv_report_photo_size.text = "${mList.size}/9张"

                                    if (reportText != "" && mList.isNotEmpty()) {
                                        iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit)
                                    } else {
                                        iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit_non)
                                    }

                                    if (mList.size < 9) {
                                        mList.add("add")
                                    }

                                    ll_report_load.visibility = View.GONE

                                    adapter.notifyDataSetChanged()

                                }

                            }.start()

                        }
                    }

                    override fun onCancel() {}
                })

        } else {
            ToastUtils.showShort("展示图片")
        }
    }

    override fun onItemCloseClick(v: View?, position: Int) {
        mList.removeAt(position)

        mList.remove("add")

        tv_report_photo_size.text = "${mList.size}/9张"

        if (reportText != "" && mList.isNotEmpty()) {
            iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit)
        } else {
            iv_report_reason_commit.setImageResource(R.mipmap.icon_report_commit_non)
        }

        if (mList.size < 9) {
            mList.add("add")
        }

        adapter.notifyDataSetChanged()
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoReportOtherSuccess(reportOtherBean: ReportOtherBean?) {
        ll_report_load.visibility = View.GONE
        if (reportOtherBean != null) {
            if (reportOtherBean.code == 200) {
                ToastUtils.showShort("举报成功，请耐心等待反馈")

                val intent = intent
                setResult(RESULT_OK, intent)
                finish()

            } else {
                ToastUtils.showShort("举报失败，请等到网络稳定后重新尝试")
            }
        }
    }

    override fun onDoReportOtherError() {
        ll_report_load.visibility = View.GONE
        ToastUtils.showShort("举报失败，请等到网络稳定后重新尝试")
    }

    override fun onDestroy() {
        super.onDestroy()
        doReportOtherPresent.unregisterCallback(this)
    }

}