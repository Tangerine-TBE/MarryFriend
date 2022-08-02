package com.twx.marryfriend.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BaseDataBindingViewHolder constructor(val dataBindingView: BaseDataBindingView): RecyclerView.ViewHolder(dataBindingView.getRootView()) {
    companion object{
        @JvmStatic
        @BindingAdapter("android:src")
        fun ImageView.src(src:String?){
            Glide.with(this).load(src).into(this)
        }
    }
}