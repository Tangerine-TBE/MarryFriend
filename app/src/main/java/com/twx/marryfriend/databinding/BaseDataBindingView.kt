package com.twx.marryfriend.databinding

import android.view.View

interface BaseDataBindingView<D> {
    fun getRootView():View

    fun setData(data: D?)
}