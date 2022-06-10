package com.twx.marryfriend.login

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.blankj.utilcode.util.*
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.FaceVerifyBean
import com.twx.marryfriend.bean.PhoneLoginBean
import com.twx.marryfriend.bean.VerifyCodeBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.guide.info.GetInfoActivity
import com.twx.marryfriend.login.retrieve.RetrieveActivity
import com.twx.marryfriend.net.callback.IDoFaceVerifyCallback
import com.twx.marryfriend.net.callback.IDoPhoneLoginCallback
import com.twx.marryfriend.net.callback.IGetVerifyCodeCallback
import com.twx.marryfriend.net.impl.doFaceVerifyPresentImpl
import com.twx.marryfriend.net.impl.doPhoneLoginPresentImpl
import com.twx.marryfriend.net.impl.getVerifyCodePresentImpl
import com.twx.marryfriend.utils.SpUtil
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLDecoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : MainBaseViewActivity(), IGetVerifyCodeCallback, IDoPhoneLoginCallback,
    IDoFaceVerifyCallback {

    private var phone = ""

    // 是否位于填写电话号码界面
    private var isPhone = true

    private val mBeginTime: Long = 60000
    private var isCurrentDown = false
    private var mCountDownTimer: CountDownTimer? = null

    private lateinit var getVerifyCodePresent: getVerifyCodePresentImpl
    private lateinit var doPhoneLoginPresent: doPhoneLoginPresentImpl

    private lateinit var doFaceVerifyPresent: doFaceVerifyPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_login

    override fun initView() {
        super.initView()

        getVerifyCodePresent = getVerifyCodePresentImpl.getsInstance()
        getVerifyCodePresent.registerCallback(this)

        doPhoneLoginPresent = doPhoneLoginPresentImpl.getsInstance()
        doPhoneLoginPresent.registerCallback(this)

        doFaceVerifyPresent = doFaceVerifyPresentImpl.getsInstance()
        doFaceVerifyPresent.registerCallback(this)

    }

    override fun initLoadData() {
        super.initLoadData()

        var channelName: String = "1.0"
        channelName = this.packageManager.getPackageInfo(this.packageName, 0).versionName

        SPStaticUtils.put(Constant.VERSION, channelName)


        getBitmap("https://baidu-ai.bj.bcebos.com/face/faces.jpg")

    }

    override fun initPresent() {
        super.initPresent()

        // 获取焦点
        et_login_phone.isFocusable = true;
        et_login_phone.isFocusableInTouchMode = true;
        et_login_phone.requestFocus();

        // 弹出软键盘
//        KeyboardUtils.showSoftInput(this)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    override fun initEvent() {
        super.initEvent()

        et_login_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length == 11) {
                    tv_login_code.setBackgroundResource(R.drawable.shape_bg_common_next)
                } else {
                    tv_login_code.setBackgroundResource(R.drawable.shape_bg_common_next_non)
                }
            }

        })

        tv_login_code.setOnClickListener {

            KeyboardUtils.hideSoftInput(this)

            phone = et_login_phone.text.toString().trim { it <= ' ' }

            if (RegexUtils.isMobileExact(phone)) {

                val map: MutableMap<String, String> = TreeMap()
                map[Contents.Mobile] = phone
                getVerifyCodePresent.getVerifyCode(map)

            } else {
                ToastUtils.showShort("请输入正确手机号")
            }

        }

        tv_login_reset.setOnClickListener {
            ToastUtils.showShort("此处需要找回手机号")

            val intent = Intent(this, RetrieveActivity::class.java)
            startActivity(intent)

//            val map: MutableMap<String, String> = TreeMap()
//            map["access_token"] =
//                "24.4b751c0ec563309da71a3aa85d43236f.2592000.1656486335.282335-26278103"
//            map["Content-Type"] = "application/json"
//            map["image_type"] = "BASE64"
////            map["image"] = str.toString()
//
//            map["image"] = "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"
//            map["id_card_number"] = "421081199911123210"
//            map["quality_control"] = "NONE"
//            map["name"] = URLDecoder.decode("张三", "UTF-8")
//            map["liveness_control"] = "NONE"
//            map["spoofing_control"] = "NONE"
//            doFaceVerifyPresent.doFaceVerify(map)



        }

        ll_code_input.setOnClickListener {

            et_code_input.isFocusable = true;
            et_code_input.isFocusableInTouchMode = true;
            et_code_input.requestFocus();

            val imm: InputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_code_input, 0)

        }

        ll_code_input.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {

                Log.i("guo", "text : ${ClipboardUtils.getText().toString()}")

                Log.i("guo", "VERIFY  : ${isVerifyCode(ClipboardUtils.getText().toString())}")

                if (ClipboardUtils.getText().toString().isNotEmpty()) {
                    if (isVerifyCode(ClipboardUtils.getText().toString())) {
                        et_code_input.setText(ClipboardUtils.getText())
                    }
                }

                return false
            }
        })

        et_code_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {

                tv_code_one.text = ""
                tv_code_two.text = ""
                tv_code_three.text = ""
                tv_code_four.text = ""
                tv_code_five.text = ""

                when (s.length) {
                    0 -> {
                        tv_code_one.text = ""
                        tv_code_two.text = ""
                        tv_code_three.text = ""
                        tv_code_four.text = ""
                        tv_code_five.text = ""
                    }
                    1 -> {
                        tv_code_one.text = s.subSequence(0, 1)
                    }
                    2 -> {
                        tv_code_one.text = s.subSequence(0, 1)
                        tv_code_two.text = s.subSequence(1, 2)
                    }
                    3 -> {
                        tv_code_one.text = s.subSequence(0, 1)
                        tv_code_two.text = s.subSequence(1, 2)
                        tv_code_three.text = s.subSequence(2, 3)
                    }
                    4 -> {
                        tv_code_one.text = s.subSequence(0, 1)
                        tv_code_two.text = s.subSequence(1, 2)
                        tv_code_three.text = s.subSequence(2, 3)
                        tv_code_four.text = s.subSequence(3, 4)
                    }
                    5 -> {
                        tv_code_one.text = s.subSequence(0, 1)
                        tv_code_two.text = s.subSequence(1, 2)
                        tv_code_three.text = s.subSequence(2, 3)
                        tv_code_four.text = s.subSequence(3, 4)
                        tv_code_five.text = s.subSequence(4, 5)
                    }

                }

                //输入完5个验证码 自动请求验证
                if (s.length == 5) {

                    val unique = DeviceUtils.getUniqueDeviceId()

                    KeyboardUtils.hideSoftInput(this@LoginActivity)

                    val map: MutableMap<String, String> = TreeMap()
                    map[Contents.Mobile] = phone
                    map[Contents.VERIFY_CODE] = s.toString()
                    map[Contents.DEVICE_CODE] = unique
                    map[Contents.VERSION] = SPStaticUtils.getString(Constant.VERSION, "_360")
                    map[Contents.PLATFORM] = SPStaticUtils.getString(Constant.CHANNEL, "_360")
                    map[Contents.PACKAGE_ENGLISH] = "com.twx.marryfriend"
                    map[Contents.SYSTEM] = "1"
                    map[Contents.PACKAGE_CHINESE] = "婚恋"
                    doPhoneLoginPresent.doPhoneLogin(map)

                }
            }
        })


        tv_code_resend.setOnClickListener {
            startCurrentDownTimer()

            val map: MutableMap<String, String> = TreeMap()
            map[Contents.Mobile] = phone
            getVerifyCodePresent.getVerifyCode(map)

        }

    }

    // 验证码倒计时
    private fun startCurrentDownTimer() {
        tv_code_resend.isEnabled = false
        isCurrentDown = true
        mCountDownTimer = object : CountDownTimer(mBeginTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tv_code_resend.text = "重新发送（${millisUntilFinished.div(1000).toString()}s）"
            }

            override fun onFinish() {
                tv_code_resend.text = "重新发送"
                tv_code_resend.isEnabled = true
                isCurrentDown = false
            }
        }.start()
    }

    // 判断是否为验证码
    private fun isVerifyCode(str: String): Boolean {

        var isVerifyCode = false

        isVerifyCode = if (str.length == 5) {
            val pattern: Pattern = Pattern.compile("[0-9]*")
            val isNum: Matcher = pattern.matcher(str)
            isNum.matches()
        } else {
            false
        }
        return isVerifyCode
    }


    fun getBitmap(url: String?): Bitmap? {
        var bm: Bitmap? = null
        Thread {
            try {
                val iconUrl = URL(url)
                val conn: URLConnection = iconUrl.openConnection()
                val http: HttpURLConnection = conn as HttpURLConnection
                val length: Int = http.contentLength
                conn.connect()
                // 获得图像的字符流
                val `is`: InputStream = conn.getInputStream()
                val bis = BufferedInputStream(`is`, length)
                bm = BitmapFactory.decodeStream(bis)

                Log.i("guo", "bitmap")
                bitmapToBase64(bm)

                bis.close()
                `is`.close() // 关闭流
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()

        return bm

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

    override fun onDoPhoneLoginSuccess(phoneLoginBean: PhoneLoginBean) {

        if (phoneLoginBean.code == "200") {
            ToastUtils.showShort("登陆成功，跳转至资料填写界面")

            // 存储一下资料

            SpUtil.saveUserInfo(phoneLoginBean)

            val intent = Intent(this, GetInfoActivity::class.java)
            startActivity(intent)
            this.finish()
        } else {
            ToastUtils.showShort(phoneLoginBean.msg)
        }

    }

    override fun onDoPhoneLoginError() {
        ToastUtils.showShort("登录失败，请稍后再试")
    }

    override fun onGetVerifyCodeSuccess(verifyCodeBean: VerifyCodeBean) {
        ToastUtils.showShort(verifyCodeBean.msg)

        if (isPhone) {
            tv_code_phone.text = phone

            ll_login_phone.visibility = View.GONE
            ll_login_code.visibility = View.VISIBLE
            startCurrentDownTimer()
            isPhone = false
        }

    }

    override fun onGetVerifyCodeError() {
        ToastUtils.showShort("验证码发送失败，请重试")
    }

}