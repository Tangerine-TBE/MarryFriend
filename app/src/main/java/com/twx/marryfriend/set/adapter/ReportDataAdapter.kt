package com.twx.marryfriend.set.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.R

/**
 * @author: Administrator
 * @date: 2022/5/31
 */
class ReportDataAdapter(private val mList: MutableList<String>) :
    RecyclerView.Adapter<ReportDataAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
        fun onItemCloseClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    override fun onClick(v: View?) {
        if (v != null) {
            mOnItemClickListener?.onItemClick(v, v.tag as Int)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pic: ImageView = view.findViewById(R.id.riv_detail_report_pic)
        val close: ImageView = view.findViewById(R.id.iv_detail_report_close)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_report_data, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        if (mList[position] == "add") {
            holder.close.visibility = View.GONE
            Glide.with(mContext)
                .load(R.mipmap.icon_report_add)
                .into(holder.pic)
        } else {
            holder.close.visibility = View.VISIBLE
            Glide.with(mContext)
                .load(mList[position])
                .into(holder.pic)
        }

        holder.close.setOnClickListener {
            mOnItemClickListener?.onItemCloseClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}