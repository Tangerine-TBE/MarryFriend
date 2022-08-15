package com.twx.marryfriend.databinding

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BaseDataBindingViewHolder constructor(view: View): RecyclerView.ViewHolder(view) {
    companion object{
        @JvmStatic
        @BindingAdapter("android:src")
        fun ImageView.src(src:String?){
            Glide.with(this).load(src).into(this)
        }
        @JvmStatic
        @BindingAdapter("android:src")
        fun ImageView.src(src:Uri?){
            Glide.with(this).load(src).into(this)
        }
    }
}