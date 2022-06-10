package com.twx.marryfriend.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.gyf.barlibrary.ImmersionBar
import com.twx.marryfriend.R

open class BaseActivity : FragmentActivity() {

    private lateinit var mImmersionBar: ImmersionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityFullWindow()

        mImmersionBar = ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .navigationBarColor(R.color.white)

        mImmersionBar.transparentStatusBar().init()

    }

    open fun setActivityFullWindow() {

    }

    fun visible(vararg views: View) {
        for (view in views) {
            view.visibility = View.VISIBLE
        }
    }

    fun invisible(vararg views: View) {
        for (view in views) {
            view.visibility = View.INVISIBLE
        }
    }


    fun gone(vararg views: View) {
        for (view in views) {
            view.visibility = View.GONE
        }
    }

    open fun release() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mImmersionBar.destroy()
        release()

    }
}