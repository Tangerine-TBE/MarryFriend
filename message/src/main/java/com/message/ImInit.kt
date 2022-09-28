package com.message

import android.app.Application
import com.hyphenate.easeim.common.livedatas.LiveDataBus

object ImInit {
    lateinit var application: Application
    val imLoginState by lazy {
        LiveDataBus.get().with("im_login_state",Boolean::class.java)
    }
    fun init(application: Application){
        this.application=application
        ImUserManager.init(application)
    }
}