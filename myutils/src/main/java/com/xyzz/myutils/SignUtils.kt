package com.xyzz.myutils

import android.content.Context
import android.content.pm.PackageManager
import java.security.MessageDigest

fun String.toMd5():String{
    val string=this
    if (string.isEmpty()){
        return string
    }
    val md5= MessageDigest.getInstance("MD5")
    val byteArray=md5.digest(string.toByteArray())
    return try {
        val result=StringBuilder()
        byteArray.forEach {
            var t=Integer.toHexString(it.toInt() and 0xff)
            if (t.length==1){
                t= "0$t"
            }
            result.append(t)
        }
        result.toString()
    }catch (e: Exception){
        ""
    }
}

fun ByteArray.toMd5():String{
    if (this.isEmpty()){
        return ""
    }
    val md5= MessageDigest.getInstance("MD5")
    val byteArray=md5.digest(this)
    return try {
        val result=StringBuilder()
        byteArray.forEach {
            var t=Integer.toHexString(it.toInt() and 0xff)
            if (t.length==1){
                t= "0$t"
            }
            result.append(t)
        }
        result.toString()
    }catch (e: Exception){
        ""
    }
}

fun Context.getSign():String?{//caae455783afb444ff33e4e8e796d304
    return packageManager
            .getPackageInfo(packageName, PackageManager.GET_SIGNATURES)//GET_SIGNING_CERTIFICATES
//            .signingInfo.apkContentsSigners
            .signatures
            .find { it!=null }
            ?.toByteArray()
            ?.let { bytes ->
                val md= MessageDigest.getInstance("MD5")
                md.update(bytes)
                md.digest().map {
                    Integer.toHexString(it.toInt() and 0xFF)
                }.map {
                    if (it.length<2){
                        "0$it"
                    }else{
                        it
                    }
                }.let {
                    val str= java.lang.StringBuilder()
                    it.forEach {
                        str.append(it)
                    }
                    str.toString()
                }
            }
}

fun Context.isSignOk():Boolean{
    return getSign()=="caae455783afb444ff33e4e8e796d304"
}
