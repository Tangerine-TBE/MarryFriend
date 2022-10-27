package com.message

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.hyphenate.EMCallBack
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.BuildConfig
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions
import com.hyphenate.exceptions.HyphenateException
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import com.xyzz.myutils.toMd5

//https://docs-im.easemob.com/im/android/basics/message
//https://docs-im.easemob.com/ccim/android/easeimkit
object ImUserManager {
    private fun init(application: Application){
        val options = EMOptions()
// 默认添加好友时，是不需要验证的，改成需要验证
        options.acceptInvitationAlways = false
// 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.autoTransferMessageAttachments = true
// 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true)
//初始化
        EMClient.getInstance().init(application, options)
//在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)
    }
    val userNameState by lazy {
        MutableLiveData<String?>()
    }
    /**
     * 监听用户连接状态
     */
    private val connectionListener by lazy {
        object : EMConnectionListener{
            override fun onConnected() {
//                userNameState.postValue(true)
                iLog("用户连接")
            }

            override fun onDisconnected(errorCode: Int) {
                iLog("连接断开 onDisconnect $errorCode")
                when (errorCode) {
                    EMError.USER_REMOVED -> {
                        iLog("账号被后台删除")
                        userNameState.postValue(null)
                    }
                    EMError.USER_LOGIN_ANOTHER_DEVICE -> {
                        iLog("异地登录")
                        userNameState.postValue(null)
                    }
                    EMError.SERVER_SERVICE_RESTRICTED -> {
                        userNameState.postValue(null)
                    }
                    EMError.USER_KICKED_BY_CHANGE_PASSWORD -> {
                        userNameState.postValue(null)
                    }
                    EMError.USER_KICKED_BY_OTHER_DEVICE -> {
                        userNameState.postValue(null)
                    }
                }
            }

        }
    }

    /**
     * 开始监听用户连接状态
     */
    fun connectionListener(){
        EMClient.getInstance().removeConnectionListener(connectionListener)
        EMClient.getInstance().addConnectionListener(connectionListener)
    }

    private fun createAccount(username:String,pwd:String){//注册用户名会自动转为小写字母，所以建议用户名均以小写注册。
        EMClient.getInstance().createAccount(username, pwd)//同步方法
        iLog("注册成功")
    }

    fun login(userName:String,success: () -> Unit,fail: (code: Int, message: String) -> Unit){
        val password="${userName}~!@#\$%^&*()_+".toMd5()
        if (EMClient.getInstance().currentUser.isNullOrBlank()){
            try {
                createAccount(userName, password)
            }catch (e: HyphenateException){
                wLog(e.stackTraceToString(),"创建用户失败")
            }
        }
        iLog("用户:${userName},正在登录")
        if (EMClient.getInstance().isLoggedIn){
            if (EMClient.getInstance().currentUser==userName){
                iLog("用户已经登录，直接返回登录")
                success.invoke()
                onLoginSuccess()
                return
            }else{
                iLog("当前环信登录账户与当前用户账户不匹配，退出环信账户;当前用户:${userName},环信用户:${EMClient.getInstance().currentUser}")
                try {
                    EMClient.getInstance().logout(true)
                    iLog("退出成功")
                }catch (e:Exception){
                    iLog("退出失败")
                    wLog(e.stackTraceToString())
                }
            }
        }
        EMClient.getInstance().login(userName, password, object : EMCallBack {
            //回调
            override fun onSuccess() {
                iLog( "登录聊天服务器成功！")
                success.invoke()
                onLoginSuccess()
            }

            override fun onProgress(progress: Int, status: String) {}

            override fun onError(code: Int, message: String) {
                //SERVER_SERVING_DISABLED(305)
                when(code){
                    200->{
                        iLog("用户已经登录,code:${code},msg:${message}")
//                        onLoginSuccess()
                    }
                    305->{
                        iLog("用户被封禁,code:${code},msg:${message}")
                    }
                    204->{
                        iLog("登录失败,code:${code},msg:${message}")
                        iLog("用户不存在,重新注册")
                    }
                    else->{
                        iLog("登录失败,code:${code},msg:${message}")
                    }
                }
                fail.invoke(code, message)
            }
        })
    }

    fun logout(success:()->Unit,fail:(code: Int, message: String)->Unit){
        iLog("退出当前账户")
        EMClient.getInstance().logout(true, object : EMCallBack {
            override fun onSuccess() {
                iLog("退出当前账户成功")
                userNameState.postValue(null)
                success.invoke()
            }

            override fun onProgress(progress: Int, status: String) {
                iLog("退出当前账户进度,${progress}")
            }

            override fun onError(code: Int, message: String) {
                iLog("退出当前账户失败,code:${code},message:${message}")
                fail.invoke(code, message)
            }
        })
    }

    private fun onLoginSuccess(){
        userNameState.postValue(EMClient.getInstance().currentUser)
        ImMessageManager.startMessageListener()
        ImUserManager.connectionListener()
        EMClient.getInstance().groupManager().loadAllGroups()
        EMClient.getInstance().chatManager().loadAllConversations()
    }
}