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

class ToolAdapter(val list: List<ItemBean>) : RecyclerView.Adapter<ToolAdapter.ViewHolder>(),
    View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_vip_tool)
        val text: TextView = view.findViewById(R.id.tv_vip_tool)
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
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_vip_tool, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext).load(list[position].icon).into(holder.icon)
        holder.text.text = list[position].title

    }

    override fun getItemCount(): Int {
        return list.size
    }

}
