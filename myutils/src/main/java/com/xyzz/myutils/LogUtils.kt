package com.xyzz.myutils

import android.util.Log

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

fun iLog(text: String){
    if (!BuildConfig.DEBUG){
        return
    }
    Log.i("myLog", text)
}
fun iLog(text: String, tag: String = ""){
    if (!BuildConfig.DEBUG){
        return
    }
    Log.i("myLog,$tag", text)
}

fun Any.eLog(text: String, tag: String = this.javaClass.simpleName){
//    if (!BuildConfig.DEBUG){
//        return
//    }
    Log.e("myLog,$tag", text)
}