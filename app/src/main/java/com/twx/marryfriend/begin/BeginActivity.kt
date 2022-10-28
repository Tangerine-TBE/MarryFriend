package com.twx.marryfriend.begin

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupAnimation
import com.lxj.xpopup.impl.FullScreenPopupView
import com.message.ImInit
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper
import com.mobile.auth.gatewayauth.ResultCode
import com.mobile.auth.gatewayauth.TokenResultListener
import com.mobile.auth.gatewayauth.model.TokenRet
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.AutoLoginBean
import com.twx.marryfriend.begin.custom.config.BaseUIConfig
import com.twx.marryfriend.begin.custom.utils.ExecutorManager
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.guide.info.GetInfoActivity
import com.twx.marryfriend.login.LoginActivity
import com.twx.marryfriend.net.callback.IDoAutoLoginCallback
import com.twx.marryfriend.net.impl.doAutoLoginPresentImpl
import com.twx.marryfriend.set.web.SetWebActivity
import com.twx.marryfriend.utils.SpLoginUtil
import com.twx.marryfriend.utils.SpUtil
import com.umeng.analytics.MobclickAgent
import java.util.*

class BeginActivity : MainBaseViewActivity(), IDoAutoLoginCallback {

    val AUTH_SECRET =
        "5Z/38d9XNQ5HDw9qYt6e0lvJfnFolhg3t+3deOgkQCJKwVxUYes1NHLjRNTEM9jKxzRLscIOBo/KkIxczkjgACt4EIkmqYrSSfEAvxGtYltNxSZcazVViGxVQS6RKHLWuNJA2/bg2T5QixDps8eV+Z4VqCT+09VAkE5zX4JuAS8JavAPjCRzILYkP/z1TPKxOgKJfjr3MRgloQO6VIzY/NdqMKvF3tlX7meLgivnIBpHucj4i/N3N7yYwHuLjRXbFjkTV5KNtfwfnuT5MWQi+axzGuihMX4U7QAeV3q4XPxko5zSc+X4MGji2jltoHQ7"

    // 手机号登录是否同意用户协议
    private var agreePermission = false

    private var mUIConfig: BaseUIConfig? = null

    private var mProgressDialog: ProgressDialog? = null

    private var mTokenResultListener: TokenResultListener? = null
    private lateinit var mPhoneNumberAuthHelper: PhoneNumberAuthHelper

    private lateinit var doAutoLoginPresent: doAutoLoginPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_begin

    override fun initView() {
        super.initView()

        doAutoLoginPresent = doAutoLoginPresentImpl.getsInstance()
        doAutoLoginPresent.registerCallback(this)


        //准备进入登录流程
        MobclickAgent.onEvent(this, "10000_login_process");

        sdkInit(AUTH_SECRET)
        ImInit.init(application)
        mUIConfig = BaseUIConfig.init(0, this, mPhoneNumberAuthHelper)

    }

    override fun initLoadData() {
        super.initLoadData()

    }

    override fun initPresent() {
        super.initPresent()

        if (!SPStaticUtils.getBoolean(Constant.USER_IS_LOGIN, false)) {
            if (NetworkUtils.getMobileDataEnabled()) {
                // 一键登录
                oneKeyLogin()
            } else {
                // 手机号登录
                showPhoneLoginDialog()
            }
        } else {
            // 跳过登录界面
            startActivity(GetInfoActivity.getIntent(this,
                1,
                SPStaticUtils.getString(Constant.ME_NAME), true))

            this.finish()

        }
    }

    override fun initEvent() {
        super.initEvent()
    }

    private fun showPhoneLoginDialog() {

        //不满足一键登录条件,进入验证码登录
        MobclickAgent.onEvent(this, "10002_goto_sms_login");

        XPopup.Builder(this).dismissOnTouchOutside(false).dismissOnBackPressed(false)
            .isDestroyOnDismiss(true).popupAnimation(PopupAnimation.ScaleAlphaFromCenter)
            .asCustom(PhoneLoginDialog(this)).show()
    }

    fun sdkInit(secretInfo: String?) {
        mTokenResultListener = object : TokenResultListener {
            override fun onTokenSuccess(s: String) {
                hideLoadingDialog()
                var tokenRet: TokenRet? = null
                try {
                    tokenRet = TokenRet.fromJson(s)
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS == tokenRet.code) {
                    }
                    if (ResultCode.CODE_SUCCESS == tokenRet.code) {
                        getResultWithToken(tokenRet.token)
                        mPhoneNumberAuthHelper.setAuthListener(null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onTokenFailed(s: String) {
                hideLoadingDialog()
                var tokenRet: TokenRet? = null
                try {
                    tokenRet = TokenRet.fromJson(s)
                    if (ResultCode.CODE_ERROR_USER_CANCEL == tokenRet?.code) {
                        //模拟的是必须登录 否则直接退出app的场景
                        this@BeginActivity.finish()
                    } else {
                        // 手机号登录
                        showPhoneLoginDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mPhoneNumberAuthHelper.setAuthListener(null)
            }
        }
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(this, mTokenResultListener)
        mPhoneNumberAuthHelper.reporter?.setLoggerEnable(true)
        mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo)
    }


    /**
     * 进入app就需要登录的场景使用
     */
    private fun oneKeyLogin() {
        mPhoneNumberAuthHelper =
            PhoneNumberAuthHelper.getInstance(applicationContext, mTokenResultListener)
        mUIConfig?.configAuthPage()
        getLoginToken(5000)
    }

    /**
     * 拉起授权页
     * @param timeout 超时时间
     */
    private fun getLoginToken(timeout: Int) {

        //满足一键登录条件,弹出一键登录框
        MobclickAgent.onEvent(this, "10001_quick_login_alert");

        mPhoneNumberAuthHelper.getLoginToken(this, timeout)
        showLoadingDialog("正在唤起授权页")

    }

    fun getResultWithToken(token: String?) {
        ExecutorManager.run(Runnable {

            val map: MutableMap<String, String> = TreeMap()
            val unique =
                Settings.System.getString(application.contentResolver, Settings.Secure.ANDROID_ID)
            if (token != null) {


                    map[Contents.ALI_TOKEN] = token
                    map[Contents.EQUIPMENT_NUMBER] = unique
                    map[Contents.USER_VERSION] = getVersion()
                    map[Contents.USER_PLATFORM] = SPStaticUtils.getString(Constant.CHANNEL, "_360")
                    map[Contents.USER_PACKAGE] = "com.jiaou.love"
                    map[Contents.USER_SYSTEM] = 1.toString()
                    map[Contents.USER_PKG_CHN] = "未来佳偶婚恋交友"
                    doAutoLoginPresent.doAutoLogin(map)


            }
        })
    }

    // 获取版本号
    private fun getVersion(): String {
        var channelName: String = "1.0"
        val packageManager: PackageManager = this.packageManager
        val info: PackageInfo = packageManager.getPackageInfo(this.packageName, 0)
        channelName = info.versionName
        return channelName
    }

    private fun showLoadingDialog(hint: String?) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        mProgressDialog!!.setMessage(hint)
        mProgressDialog!!.setCancelable(true)
        mProgressDialog!!.show()
    }

    fun hideLoadingDialog() {
        mProgressDialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoAutoLoginSuccess(autoLoginBean: AutoLoginBean?) {

        if (autoLoginBean != null) {
            if (autoLoginBean.code == "200") {

                //点击一键登录,登录成功
                MobclickAgent.onEvent(this, "10003_quick_login_success");

                hideLoadingDialog()

                // 存储一下资料
                SpUtil.storeVipInfo(autoLoginBean.data.close_time_low,
                    autoLoginBean.data.close_time_high)

                SpLoginUtil.saveUserInfo(autoLoginBean)

                startActivity(GetInfoActivity.getIntent(this,
                    autoLoginBean.data.kind_type,
                    autoLoginBean.data.nick))
                this.finish()

            } else {

                //点击一键登录,登录失败
                MobclickAgent.onEvent(this, "10004_quick_login_fail");

                ToastUtils.showShort(autoLoginBean.msg)
            }
        }

    }

    override fun onDoAutoLoginError() {

        //点击一键登录,登录失败
        MobclickAgent.onEvent(this, "10004_quick_login_fail");

        ToastUtils.showShort("网络请求失败，请稍后再试")

    }

    inner class TextViewSpan1 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            startActivity(SetWebActivity.getIntent(this@BeginActivity,
                "隐私政策",
                DataProvider.WebUrlData[2].url))
        }
    }

    inner class TextViewSpan2 : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = resources.getColor(R.color.service_color)
        }

        override fun onClick(widget: View) {
            startActivity(SetWebActivity.getIntent(this@BeginActivity,
                "用户协议",
                DataProvider.WebUrlData[1].url))
        }
    }

    inner class PhoneLoginDialog(context: Context) : FullScreenPopupView(context) {

        override fun getImplLayoutId(): Int = R.layout.dialog_login_pwd

        override fun onCreate() {
            super.onCreate()

            val check = findViewById<ImageView>(R.id.iv_dialog_login_pwd_check)
            val permission = findViewById<TextView>(R.id.tv_dialog_login_pwd_check)

            findViewById<TextView>(R.id.tv_dialog_login_pwd).setOnClickListener {

                if (agreePermission) {

                    //一键登录页面点击其他手机号登录
                    MobclickAgent.onEvent(context, "10005_quick_login_otherphone");

                    val intent = Intent(this@BeginActivity, LoginActivity::class.java)
                    startActivity(intent)

                    this@BeginActivity.finish()

                } else {
                    ToastUtils.showShort("您还未同意隐私政策与用户协议")
                }
            }

            check.setOnClickListener {
                if (agreePermission) {
                    check.setImageResource(R.drawable.ic_login_uncheck)
                } else {
                    check.setImageResource(R.drawable.ic_login_check)
                }
                agreePermission = !agreePermission
            }

            val str = "已阅读并同意 《隐私政策》 和 《用户协议》 "
            val stringBuilder = SpannableStringBuilder(str)
            val span1 = TextViewSpan1()
            stringBuilder.setSpan(span1, 6, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val span2 = TextViewSpan2()
            stringBuilder.setSpan(span2, 15, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            permission.text = stringBuilder
            permission.movementMethod = LinkMovementMethod.getInstance()

        }

        override fun onDismiss() {
            super.onDismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        doAutoLoginPresent.unregisterCallback(this)
    }

}