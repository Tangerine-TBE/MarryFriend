package com.twx.marryfriend.guide.detailInfo.life

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.baidubce.BceClientException
import com.baidubce.BceServiceException
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
import com.twx.marryfriend.bean.vip.UpdateDescribeBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.mine.voice.VoiceActivity
import com.twx.marryfriend.net.callback.IDoLifeFaceDetectCallback
import com.twx.marryfriend.net.callback.IDoTextVerifyCallback
import com.twx.marryfriend.net.callback.IDoUpdateDescribeCallback
import com.twx.marryfriend.net.callback.IDoUploadPhotoCallback
import com.twx.marryfriend.net.impl.doLifeFaceDetectPresentImpl
import com.twx.marryfriend.net.impl.doTextVerifyPresentImpl
import com.twx.marryfriend.net.impl.doUpdateDescribePresentImpl
import com.twx.marryfriend.net.impl.doUploadPhotoPresentImpl
import com.twx.marryfriend.utils.BitmapUtil
import com.twx.marryfriend.utils.TimeUtil
import kotlinx.android.synthetic.main.activity_base_info.*
import kotlinx.android.synthetic.main.activity_life_introduce.*
import kotlinx.android.synthetic.main.layout_guide_step_name.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.UnknownHostException
import java.util.*
import top.zibin.luban.OnCompressListener

import top.zibin.luban.CompressionPredicate

import top.zibin.luban.Luban


class LifeIntroduceActivity : MainBaseViewActivity(), IDoUploadPhotoCallback,
    IDoUpdateDescribeCallback {

    private var picPath = ""
    private var introduce = ""
    private var picUrl = ""


    private var mode = 0
    private var photoId = ""


    private lateinit var doUploadPhotoPresent: doUploadPhotoPresentImpl
    private lateinit var doUpdateDescribePresent: doUpdateDescribePresentImpl

    private lateinit var client: BosClient

    /**
     * path ：图片url
     * introduce：图片描述
     * mode ：模式选择，0：上传新的生活照；1：更新生活照的描述
     * photoId ： 生活照id
     * */
    companion object {
        private const val PATH = "path"
        private const val INTRODUCE = "introduce"
        private const val MODE = "mode"
        private const val PHOTO_ID = "photo_Id"
        private const val URL = "url"

        fun getIntent(
            context: Context,
            path: String,
            introduce: String,
            mode: Int? = 0,
            photoId: String? = "",
            url: String? = "",
        ): Intent {
            val intent = Intent(context, LifeIntroduceActivity::class.java)
            intent.putExtra(PATH, path)
            intent.putExtra(INTRODUCE, introduce)
            intent.putExtra(MODE, mode)
            intent.putExtra(PHOTO_ID, photoId)
            intent.putExtra(URL, url)
            return intent
        }

    }

    override fun getLayoutView(): Int = R.layout.activity_life_introduce

    override fun initView() {
        super.initView()

        picPath = intent.getStringExtra("path").toString()
        introduce = intent.getStringExtra("introduce").toString()

        mode = intent.getIntExtra("mode", 0)
        photoId = intent.getStringExtra("photo_Id").toString()

        picUrl = intent.getStringExtra("url").toString()

        doUploadPhotoPresent = doUploadPhotoPresentImpl.getsInstance()
        doUploadPhotoPresent.registerCallback(this)

        doUpdateDescribePresent = doUpdateDescribePresentImpl.getsInstance()
        doUpdateDescribePresent.registerCallback(this)



        Log.i("guo", "picPath : $picPath")

        Glide.with(this).load(picPath)
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

        iv_life_introduce_finish.setOnClickListener {
            finish()
            this.finish()
        }

        tv_life_introduce_confirm.setOnClickListener {


            Log.i("guo","显示loading弹窗")
            ll_life_introduce_loading.visibility = View.VISIBLE


            // 文字审核
            introduce = et_life_introduce_introduce.text.toString().trim { it <= ' ' }

            when (mode) {
                0 -> {
                    // 上传新的生活照

                    Log.i("guo", "图片合规，开始进行上传")


                    Thread{


                        val file = File(picPath)

                        val bitmap = BitmapUtil.generateBitmap("佳偶婚恋交友", 16f, Color.WHITE)?.let {
                            BitmapUtil.createWaterMarkBitmap(ImageUtils.getBitmap(file), it)
                        }

                        val mPhotoPath = this.externalCacheDir.toString() + File.separator + "${
                            FileUtils.getFileNameNoExtension(picPath)
                        }.png"

                        if (bitmap != null) {
                            BitmapUtil.saveBitmap(bitmap, mPhotoPath)
                        }

                        Luban.with(this).load(mPhotoPath).ignoreBy(100)
                            .setTargetDir(this.externalCacheDir.toString())
                            .setCompressListener(object : OnCompressListener {
                                override fun onStart() {
                                    Log.i("guo", "开始压缩")
                                }

                                override fun onSuccess(file: File) {

                                    ThreadUtils.runOnUiThread {
                                        Log.i("guo", "压缩完成")

                                        Thread {

                                            try {

                                                val name = if (SPStaticUtils.getString(Constant.USER_ID,
                                                        "default").length == 1
                                                ) {
                                                    "0${
                                                        SPStaticUtils.getString(Constant.USER_ID, "default")
                                                    }"
                                                } else {
                                                    SPStaticUtils.getString(Constant.USER_ID, "default")
                                                }

                                                // 时间戳，防止重复上传时传不上去
                                                val span = TimeUtils.getNowMills()
                                                val path =
                                                    "${FileUtils.getFileNameNoExtension(picPath)}_${span}.jpg"

                                                val putObjectFromFileResponse =
                                                    client.putObject("user${name}", path, file)

                                                picUrl =
                                                    client.generatePresignedUrl("user${name}", path, -1)
                                                        .toString()

                                                Log.i("guo", "mLifeSecondUrl :$picUrl")

                                                uploadPhoto(picUrl,
                                                    FileUtils.getFileNameNoExtension(picPath),
                                                    FileUtils.getFileExtension(picPath),
                                                    introduce)

                                            } catch (e: BceClientException) {
                                                e.printStackTrace()

                                                ThreadUtils.runOnUiThread {
                                                    ll_life_introduce_loading.visibility = View.GONE
                                                    ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                                }

                                            } catch (e: BceServiceException) {

                                                ThreadUtils.runOnUiThread {
                                                    ll_life_introduce_loading.visibility = View.GONE
                                                    ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                                }

                                                Log.i("guo", "Error ErrorCode: " + e.errorCode)
                                                Log.i("guo", "Error RequestId: " + e.requestId)
                                                Log.i("guo", "Error StatusCode: " + e.statusCode)
                                                Log.i("guo", "Error ErrorType: " + e.errorType)

                                            } catch (e: UnknownHostException) {
                                                e.printStackTrace()

                                                ThreadUtils.runOnUiThread {
                                                    ll_life_introduce_loading.visibility = View.GONE
                                                    ToastUtils.showShort("网络请求错误，请检查网络后稍后重试")
                                                }

                                            }

                                        }.start()
                                    }

                                }

                                override fun onError(e: Throwable) {

                                    Log.i("guo", "throwable :$e")

                                    ToastUtils.showShort("图片解析失败，请稍后再试")

                                }
                            }).launch()


                    }.start()


                }
                1 -> {
                    // 修改生活照描述

                    Log.i("guo", "开始进行修改")

                    if (introduce.isNotEmpty()) {
                        updateDescribe(photoId, introduce)
                    } else {
                        ll_life_introduce_loading.visibility = View.GONE
                        ToastUtils.showShort("请输入生活照描述")
                    }

                }
            }

        }

        ll_life_introduce_loading.setOnClickListener {

        }

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

    // 更新生活照描述
    private fun updateDescribe(photoId: String, content: String) {

        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.PHOTO_ID] = photoId
        map[Contents.CONTENT] = content
        doUpdateDescribePresent.doUpdateDescribe(map)

    }


    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoUpdateDescribeSuccess(updateDescribeBean: UpdateDescribeBean?) {

        ll_life_introduce_loading.visibility = View.GONE

        if (updateDescribeBean != null) {
            if (updateDescribeBean.code == 200) {
                // 需要返回给前一个界面的值
                val intent = intent
                intent.putExtra("path", picPath)
                intent.putExtra("url", picUrl)
                intent.putExtra("id", updateDescribeBean.data[0].toString())
                intent.putExtra("text", introduce)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                ToastUtils.showShort(updateDescribeBean.msg)
            }
        }

    }

    override fun onDoUpdateDescribeError() {

        ll_life_introduce_loading.visibility = View.GONE
        ToastUtils.showShort("修改描述失败，请稍后再试")
    }

    override fun onDoUploadPhotoSuccess(uploadPhotoBean: UploadPhotoBean?) {
        ll_life_introduce_loading.visibility = View.GONE


        // 上传成功,带数据返回上一页
        if (uploadPhotoBean != null) {
            if (uploadPhotoBean.code == 200) {

                // 需要返回给前一个界面的值
                val intent = intent
                intent.putExtra("path", picPath)
                intent.putExtra("url", picUrl)
                intent.putExtra("id", uploadPhotoBean.data[0].toString())
                intent.putExtra("text", introduce)
                setResult(RESULT_OK, intent)
                finish()

            } else {
                ToastUtils.showShort(uploadPhotoBean.msg)
            }
        }

    }

    override fun onDoUploadPhotoError() {
        ll_life_introduce_loading.visibility = View.GONE
        ToastUtils.showShort("网络请求失败，请稍后再试")

    }

    override fun onDestroy() {
        super.onDestroy()

        doUploadPhotoPresent.unregisterCallback(this)
        doUpdateDescribePresent.unregisterCallback(this)

    }

}