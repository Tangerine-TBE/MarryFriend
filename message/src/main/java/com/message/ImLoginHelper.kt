package com.message

import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.easecallkit.EaseCallKit
import com.hyphenate.easeim.DemoHelper
import com.hyphenate.easeim.common.enums.Status
import com.hyphenate.easeim.section.login.viewmodels.LoginFragmentViewModel
import com.hyphenate.easeim.section.login.viewmodels.SplashViewModel
import com.hyphenate.easeui.domain.EaseUser
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.toMd5

object ImLoginHelper {

    private val mFragmentViewModel by lazy {
        LoginFragmentViewModel(ImInit.application)
    }
    private val splashViewModel: SplashViewModel by lazy {
        SplashViewModel(ImInit.application)
    }
    private var success:((EaseUser?)->Unit)?=null
    private var error:((Int,String)->Unit)?=null

    init {
        mFragmentViewModel.loginObservable.observeForever{
            when(it.status){
                Status.SUCCESS -> {
                    success?.invoke(it.data)
                    ImMessageManager.startMessageListener()
                    iLog("环信登录成功")
                }
                Status.ERROR -> {
                    error?.invoke(it.errorCode,it.message)
                    iLog("环信登录失败,code=${it.errorCode},message=${it.message}")
                }
                Status.LOADING -> {

                }
                else->{
                    error?.invoke(-1,"null")
                }
            }
        }
    }
    var currentUserId:String?=null
        private set


    fun login(mUserName:String,success:(EaseUser?)->Unit,error:(Int,String)->Unit){
        currentUserId=mUserName
        logout({
            mFragmentViewModel.login(mUserName, mUserName.toMd5(), false)
            this.success=success
            this.error=error
        },{code,msg->
            Handler(Looper.getMainLooper()).post {
                mFragmentViewModel.login(mUserName, mUserName.toMd5(), false)
                this.success=success
                this.error=error
            }
        })
    }

    fun logout(success:()->Unit,error:(Int,String)->Unit) {
        DemoHelper.instance.logout(true, object : EMCallBack {
            override fun onSuccess() {
                Handler(Looper.getMainLooper()).post {
                    success.invoke()
                }
                iLog("环信退出登录成功")
            }

            override fun onProgress(progress: Int, status: String) {}

            override fun onError(code: Int, message: String) {
                Handler(Looper.getMainLooper()).post {
                    error.invoke(code,message)
                }
                iLog("环信退出登录失败,code=${code},message=${message}")
            }
        })
    }



    /*    private fun autoLogin(mUserName:String,success:(EaseUser?)->Unit,error:(Int,String)->Unit){
        val userId=DemoHelper.instance.currentUser
//        val userId=EMClient.getInstance().currentUser
        if (userId!=mUserName&&userId!=null){
            logout({
                error.invoke(0,"用户不一致")
            },{code,msg->{
                error.invoke(code,msg)
            }})
        }else{
            splashViewModel.loginData.observeForever{
                when(it.status){
                    Status.SUCCESS -> {
                        success.invoke(DemoHelper.instance.userProfileManager.currentUserInfo)
                        iLog("自动登录成功")
                    }
                    Status.ERROR -> {
                        error.invoke(it.errorCode,it.message)
                        iLog("自动登录失败,code=${it.errorCode},message=${it.message}")
                    }
                    Status.LOADING -> {
                        iLog("正在自动登录")
                    }
                    else->{
                        error.invoke(it.errorCode,it.message)
                        iLog("自动登录失败,code=${it.errorCode},message=${it.message}")
                    }
                }
            }
        }
    }*/
}