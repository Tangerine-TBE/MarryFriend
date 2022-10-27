package com.message

import android.app.Application
import com.hyphenate.easeim.ImDemoInit

object ImInit {
    lateinit var application: Application
    fun init(application: Application){
        this.application=application
        ImDemoInit.initIm(application)
    }
}