package com.twx.marryfriend.vip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.ItemBean

class DialogAdapter(val list: List<String>) : RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    private lateinit var mContext: Context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_vip_dialog_pic)
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_vip_dialog_pic, parent, false)
        mContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        Glide.with(mContext)
            .load(list[position])
            .error(R.drawable.ic_pic_default)
            .placeholder(R.drawable.ic_pic_default)
            .into(holder.icon)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
