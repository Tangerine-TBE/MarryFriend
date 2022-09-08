package com.twx.marryfriend

import com.google.gson.Gson
import com.hyphenate.easeui.domain.EaseUser
import com.message.ImUserInfoService

private val gson by lazy {
    Gson()
}
fun EaseUser.getUserExt(): ImUserInfoService.Ext?{
    if (this.ext==null){
        return null
    }else{
        return gson.fromJson(this.ext, ImUserInfoService.Ext::class.java)
    }
}