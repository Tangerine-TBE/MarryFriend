package com.twx.module_base.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import com.twx.module_base.R

open class BaseApplication : Application() {

    private var onForegroundListener: OnForegroundListener? = null

    open fun getInstance(): BaseApplication? {
        if (baseApplication == null) {
            baseApplication = BaseApplication()
        }
        return baseApplication
    }

    companion object {
        lateinit var application: Application
        var appContext: Context? = null
        var baseApplication: BaseApplication? = null
        fun getContext(): Context {
            return appContext!!
        }

        var mMainHandler: Handler? = null
        fun getMainHandler(): Handler {
            return mMainHandler!!
        }

        lateinit var packName: String

    }


    @SuppressLint("RestrictedApi")
    override fun onCreate() {
        super.onCreate()
        application = this
        appContext = baseContext
        mMainHandler = Handler()
        packName = packageName


        initChild()

    }


    open fun initChild() {
    }


    interface OnForegroundListener {
        fun listener();
    }

    fun setOnForegroundListener(listener: OnForegroundListener) {
        this.onForegroundListener = listener
    }

}