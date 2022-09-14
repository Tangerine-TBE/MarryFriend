package com.twx.marryfriend.mutual

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.message.mutual.MutualLikeData
import com.twx.marryfriend.databinding.BaseDataBindingViewHolder

class MutualLikeAdapter:RecyclerView.Adapter<BaseDataBindingViewHolder>() {
    private val listData = ArrayList<MutualLikeData>()

    fun setData(list: List<MutualLikeData>){
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDataBindingViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_mutual_like,parent,false)
        return BaseDataBindingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder, position: Int) {
        val item=listData[position]
        holder.itemView.also {
            if (it is ItemMutualLikeView){
                it.setData(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}