package com.twx.marryfriend.databinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BaseDataBindingViewHolder<V: BaseDataBindingView<D>,D> constructor(private val view: V): RecyclerView.ViewHolder(view.getRootView()) {
    companion object{
        @JvmStatic
        @BindingAdapter("android:src")
        fun ImageView.src(src:String?){
            Glide.with(this).load(src).into(this)
        }
    }

    fun setData(data:D?){
        view.setData(data)
    }
}