package com.xyzz.myutils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.xyzz.myutils.show.iLog

class LifecycleCallbacks private constructor(): Application.ActivityLifecycleCallbacks {
    companion object{
        private const val AD_APP_BACKGROUND_TIME="app_back_time"
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED){
            LifecycleCallbacks()
        }
        fun getAppBackgroundTime():Long{
            return System.currentTimeMillis()-SPUtil.instance.getLong(AD_APP_BACKGROUND_TIME)
        }
    }

    var isFront=false
        private set(value) {
            field=value
            frontBackstageLiveData.value=value
        }
    val frontBackstageLiveData by lazy {
        MutableLiveData<Boolean>()
    }
    private val runActivity=ArrayList<Activity>()
    private var activityFrontCount=0
    private val appFrontBackstage by lazy { ArrayList<AppFrontBackstage>() }

    fun addFrontBackstageListener(listener:AppFrontBackstage){
        appFrontBackstage.add(listener)
    }

    fun removeFrontBackstageListener(listener:AppFrontBackstage){
        appFrontBackstage.remove(listener)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        runActivity.add(p0)
    }

    override fun onActivityStarted(p0: Activity) {
        activityFrontCount++
        if (!isFront) {
            isFront = true
            iLog("进入前台","前后台切换")
            appFrontBackstage.forEach {
                it.appEnterForeground(p0)
            }
        }
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
        activityFrontCount--
        if (activityFrontCount==0) {
            iLog("进入后台","前后台切换")
            isFront=false
            SPUtil.instance.putLong(AD_APP_BACKGROUND_TIME, System.currentTimeMillis())
            appFrontBackstage.forEach {
                it.enterBackstage(p0)
            }
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {
        runActivity.remove(p0)
    }

    interface AppFrontBackstage{
        fun appEnterForeground(activity: Activity)

        fun enterBackstage(activity: Activity)
    }

    interface AppEnterQuit{
        /**
         * App进入前台
         */
        fun enterForeground(activity: Activity, time:Long)

        /**
         * activity进入前台
         */
        fun onActivityStarted(activity: Activity)
    }
}