package com.xyzz.myutils.show

import android.util.Log
import com.xyzz.myutils.BuildConfig

fun Any.dLog(text: String, tag: String = this.javaClass.simpleName){
    if (!BuildConfig.DEBUG){
        return
    }
    Log.d("myLog,$tag", text)
}

fun Any.iLog(text: String?, tag: String = this.javaClass.simpleName){
    if (!BuildConfig.DEBUG){
        return
    }
    Log.i("myLog,$tag", text ?: "null")
}

fun Any.wLog(text: String, tag: String = this.javaClass.simpleName){
//    if (!BuildConfig.DEBUG){
//        return
//    }
    Log.w("myLog,$tag", text)
}

fun Any.eLog(text: String, tag: String = this.javaClass.simpleName){
//    if (!BuildConfig.DEBUG){
//        return
//    }
    Log.e("myLog,$tag", text)
}