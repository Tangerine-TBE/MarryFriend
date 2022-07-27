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

    fun createAccount(username:String,pwd:String){//注册用户名会自动转为小写字母，所以建议用户名均以小写注册。
        EMClient.getInstance().createAccount(username, pwd)//同步方法
    }

    fun login(userName:String,password:String){
        EMClient.getInstance().login(userName, password, object : EMCallBack {
            //回调
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()
                ImMessageManager.startMessageListener()
                iLog( "登录聊天服务器成功！")
            }

            override fun onProgress(progress: Int, status: String) {}

            override fun onError(code: Int, message: String) {
                //SERVER_SERVING_DISABLED(305)
                when(code){
                    200->{
                        iLog("用户已经登录,code:${code},msg:${message}")
                    }
                    305->{
                        iLog("用户被封禁,code:${code},msg:${message}")
                    }
                    else->{
                        iLog("登录失败,code:${code},msg:${message}")
                    }
                }
            }
        })
    }

    fun logout(){
        EMClient.getInstance().logout(true, object : EMCallBack {
            override fun onSuccess() {
                
            }

            override fun onProgress(progress: Int, status: String) {
                
            }

            override fun onError(code: Int, message: String) {
                
            }
        })
    }

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
                        logout()
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
    fun connectionListener(){
        EMClient.getInstance().addConnectionListener(connectionListener)
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