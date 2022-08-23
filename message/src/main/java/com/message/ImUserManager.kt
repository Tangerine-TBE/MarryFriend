package com.message

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.os.Process
import com.hyphenate.EMCallBack
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.BuildConfig
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMOptions
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog

//https://docs-im.easemob.com/im/android/basics/message
object ImUserManager {
    fun init(application: Application){
        val pid = Process.myPid()
        val processAppName = application.getAppName(pid)

        if (processAppName == null ||processAppName!=application.packageName) {
            return
        }
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

    /**
     * 监听用户连接状态
     */
    private val connectionListener by lazy {
        object : EMConnectionListener{
            override fun onConnected() {
                iLog("用户连接")
            }

            override fun onDisconnected(errorCode: Int) {
                iLog("连接断开 onDisconnect $errorCode")
                when (errorCode) {
                    EMError.USER_REMOVED -> {
                        iLog("账号被后台删除")
                    }
                    EMError.USER_LOGIN_ANOTHER_DEVICE -> {
                        iLog("异地登录")
                        logout({

                        },{code, message ->

                        })
                    }
                    EMError.SERVER_SERVICE_RESTRICTED -> {

                    }
                    EMError.USER_KICKED_BY_CHANGE_PASSWORD -> {

                    }
                    EMError.USER_KICKED_BY_OTHER_DEVICE -> {

                    }
                }
            }

        }
    }

    fun createOrLogin(username: String,pwd: String="123456"){
        iLog("当前登录的用户${EMClient.getInstance().currentUser}")
        if (!EMClient.getInstance().currentUser.isNullOrBlank()){
            if (EMClient.getInstance().currentUser==username){
                iLog("当前用户已登录，重新登录刷新信息")
//                onLoginSuccess()
                login(username,pwd)
                return
            }else{
                logout({
                    try {
                        createAccount(username,pwd)
                    }catch (e:Exception){
                        iLog(e.stackTraceToString())
                    }
                    login(username,pwd)
                },{code, message ->

                })
            }
        }else{
            try {
                createAccount(username,pwd)
            }catch (e:Exception){
                iLog(e.stackTraceToString())
            }
            login(username,pwd)
        }
    }

    fun createAccount(username:String,pwd:String){//注册用户名会自动转为小写字母，所以建议用户名均以小写注册。
        EMClient.getInstance().createAccount(username, pwd)//同步方法
        iLog("注册成功")
    }

    fun login(userName:String,password:String){
        iLog("用户:${userName},正在登录")
        EMClient.getInstance().login(userName, password, object : EMCallBack {
            //回调
            override fun onSuccess() {
                iLog( "登录聊天服务器成功！")
                onLoginSuccess()
            }

            override fun onProgress(progress: Int, status: String) {}

            override fun onError(code: Int, message: String) {
                //SERVER_SERVING_DISABLED(305)
                when(code){
                    200->{
                        iLog("用户已经登录,code:${code},msg:${message}")
                        onLoginSuccess()
                    }
                    305->{
                        iLog("用户被封禁,code:${code},msg:${message}")
                    }
                    204->{
                        iLog("登录失败,code:${code},msg:${message}")
                        iLog("用户不存在,重新注册")
                        try {
                            createAccount(userName,password)
                            login(userName, password)
                        }catch (e:Exception){
                            eLog(e.stackTraceToString())
                        }
                    }
                    else->{
                        iLog("登录失败,code:${code},msg:${message}")
                    }
                }
            }
        })
    }

    fun logout(success:()->Unit,fail:(code: Int, message: String)->Unit){
        iLog("退出当前账户")
        EMClient.getInstance().logout(true, object : EMCallBack {
            override fun onSuccess() {
                iLog("退出当前账户成功")
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

    /**
     * 开始监听用户连接状态
     */
    fun connectionListener(){
        EMClient.getInstance().removeConnectionListener(connectionListener)
        EMClient.getInstance().addConnectionListener(connectionListener)
    }

    private fun onLoginSuccess(){
        EMClient.getInstance().groupManager().loadAllGroups()
        EMClient.getInstance().chatManager().loadAllConversations()
        ImMessageManager.startMessageListener()
    }

    private fun Context.getAppName(pID: Int): String? {
        var processName: String? = null
        val am = this.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val l: List<*> = am.runningAppProcesses
        val i = l.iterator()
        val pm = this.packageManager
        while (i.hasNext()) {
            val info: ActivityManager.RunningAppProcessInfo =
                i.next() as ActivityManager.RunningAppProcessInfo
            try {
                if (info.pid === pID) {
                    processName = info.processName
                    return processName
                }
            } catch (e: Exception) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName
    }
}