package com.hyphenate.easeim

import android.app.Application
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.hyphenate.easeim.common.interfaceOrImplement.UserActivityLifecycleCallbacks
import com.hyphenate.easeim.common.utils.PreferenceManager
import com.hyphenate.util.EMLog
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

object ImDemoInit{
    @JvmStatic
    var application: Application? = null
        private set

    @JvmStatic
    val lifecycleCallbacks = UserActivityLifecycleCallbacks()

    fun initIm(application: Application){
        this.application = application
        application.initHx()
        application.registerActivityLifecycleCallbacks()
        closeAndroidPDialog()
    }

    private fun Application.initHx() {
        // 初始化PreferenceManager
        PreferenceManager.init(this)
        // init hx sdk
        if (/*DemoHelper.getInstance().autoLogin*/true) {
            EMLog.i("DemoApplication", "application initHx")
            DemoHelper.instance.init(this)
        }
    }

    private fun Application.registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }


    init {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            ClassicsHeader.REFRESH_HEADER_LASTTIME = context.getString(R.string.last_update)
            ClassicsHeader.REFRESH_HEADER_PULLDOWN = context.getString(R.string.pull_down)
            ClassicsHeader.REFRESH_HEADER_REFRESHING = context.getString(R.string.refreshing)
            ClassicsHeader.REFRESH_HEADER_RELEASE = context.getString(R.string.release_refresh)
            ClassicsHeader.REFRESH_HEADER_FINISH = context.getString(R.string.refresh_finish)
            ClassicsHeader.REFRESH_HEADER_FAILED = context.getString(R.string.refresh_failed)
            ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate) //指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter.REFRESH_FOOTER_LOADING = context.getString(R.string.be_loading)
            ClassicsFooter.REFRESH_FOOTER_FINISH = context.getString(R.string.loaded)
            ClassicsFooter.REFRESH_FOOTER_FAILED = context.getString(R.string.load_failed)
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate)
        }
    }

    /**
     * 为了兼容5.0以下使用vector图标
     */
    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    /**
     * 解决androidP 第一次打开程序出现莫名弹窗
     * 弹窗内容“detected problems with api ”
     */
    private fun closeAndroidPDialog() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            try {
                val aClass = Class.forName("android.content.pm.PackageParser\$Package")
                val declaredConstructor = aClass.getDeclaredConstructor(
                    String::class.java
                )
                declaredConstructor.setAccessible(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val cls = Class.forName("android.app.ActivityThread")
                val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
                declaredMethod.isAccessible = true
                val activityThread = declaredMethod.invoke(null)
                val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
                mHiddenApiWarningShown.isAccessible = true
                mHiddenApiWarningShown.setBoolean(activityThread, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}