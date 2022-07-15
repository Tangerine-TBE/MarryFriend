package com.twx.marryfriend.mine.user.target.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R

/**
 * @author: Administrator
 * @date: 2022/6/22
 */

class JobAddressAdapter(private val mNameList: MutableList<String>) :
    RecyclerView.Adapter<JobAddressAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemCloseClickListener: OnItemCloseClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    interface OnItemCloseClickListener {
        fun onItemCloseClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    fun setOnItemCloseClickListener(listener: OnItemCloseClickListener) {
        this.mOnItemCloseClickListener = listener
    }

    override fun onClick(v: View?) {
        if (v != null) {
            mOnItemClickListener?.onItemClick(v, v.tag as Int)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_detail_target_job_name)
        val close: ImageView = view.findViewById(R.id.iv_detail_target_job_close)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_target_job_address, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.name.text = mNameList[position]
        holder.close.setOnClickListener {
            mOnItemCloseClickListener?.onItemCloseClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return mNameList.size
    }

}