package com.twx.marryfriend.login.retrieve.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Log
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.ui.utils.IntentUtils
import com.baidu.idl.face.platform.utils.Base64Utils
import com.baidu.idl.face.platform.utils.DensityUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.FaceVerifyBean
import com.twx.marryfriend.net.callback.IDoFaceVerifyCallback
import com.twx.marryfriend.net.impl.doFaceVerifyPresentImpl
import kotlinx.android.synthetic.main.activity_collect_success.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URLDecoder
import java.util.*

open class CollectionSuccessActivity : MainBaseViewActivity(), IDoFaceVerifyCallback {

    protected var mDestroyType = ""

    private var str = ""

    private lateinit var doFaceVerifyPresent: doFaceVerifyPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_collect_success

    override fun initView() {
        super.initView()

        doFaceVerifyPresent = doFaceVerifyPresentImpl.getsInstance()
        doFaceVerifyPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()


        mDestroyType = intent.getStringExtra("destroyType")!!
        val bmpStr = IntentUtils.getInstance().bitmap
        if (TextUtils.isEmpty(bmpStr)) {
            return
        }
        var bmp = base64ToBitmap(bmpStr)
        saveImage(bmp)
        bmp = FaceSDKManager.getInstance().scaleImage(bmp,
            DensityUtils.dip2px(applicationContext, 97f),
            DensityUtils.dip2px(applicationContext, 97f))
        circle_head.setImageBitmap(bmp)

        Log.i("guo", "bmpStr : $bmpStr")

        str = bmpStr


//        SecRequest.sendMessage(this,bmpStr,0)



    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()


        btn_recollect.setOnClickListener {
            val map: MutableMap<String, String> = TreeMap()
            map["access_token"] =
                "24.4b751c0ec563309da71a3aa85d43236f.2592000.1656486335.282335-26278103"
            map["Content-Type"] = "application/json"
            map["image_type"] = "BASE64"
            map["image"] = str.toString()

//            map["image"] = "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"
            map["id_card_number"] = "421081199911123210"
            map["quality_control"] = "NONE"
            map["name"] = URLDecoder.decode("张三", "UTF-8")
            map["liveness_control"] = "NONE"
            map["spoofing_control"] = "NONE"
            doFaceVerifyPresent.doFaceVerify(map)

           Log.i("guo","str : $str")

        }

    }


    private fun saveImage(bitmap: Bitmap) {
        val path = PathUtils.getInternalAppCachePath() + File.separator + "1.jpeg"
        Log.i("guo", path)
        ImageUtils.save(bitmap, path, Bitmap.CompressFormat.PNG)
    }

    private fun base64ToBitmap(base64Data: String): Bitmap {
        val bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

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

        Log.i("guo", "su；$result")
        return result

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoFaceVerifySuccess(faceVerifyBean: FaceVerifyBean?) {

    }

    override fun onDoFaceVerifyError() {

    }

}