package com.twx.marryfriend

interface ILoginListener {
    fun onLoginSuccess(userId:String)

    fun onLogOut()
}