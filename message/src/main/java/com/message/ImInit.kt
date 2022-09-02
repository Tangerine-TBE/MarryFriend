package com.message

import android.app.Application

object ImInit {
    lateinit var application: Application
    fun init(application: Application){
        this.application=application
        ImUserManager.init(application)
    }
}