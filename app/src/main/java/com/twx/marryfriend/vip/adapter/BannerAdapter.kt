package com.twx.marryfriend.vip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R

class BannerAdapter(val list: List<Int>) : RecyclerView.Adapter<BannerAdapter.ViewHolder>() ,View.OnClickListener{

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pic: ImageView = view.findViewById(R.id.iv_banner_pic)
    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    override fun onClick(v: View?) {
        if (v != null) {
            mOnItemClickListener?.onItemClick(v, v.tag as Int)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_banner, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.pic.setImageResource(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}
