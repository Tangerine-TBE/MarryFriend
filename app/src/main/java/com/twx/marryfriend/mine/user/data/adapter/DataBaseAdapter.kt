package com.twx.marryfriend.mine.user.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R

/**
 * @author: Administrator
 * @date: 2022/5/31
 */
class DataBaseAdapter(
    private val mNameList: MutableList<String>,
    private val mInfoList: MutableList<String>,
) :
    RecyclerView.Adapter<DataBaseAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
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
        val name: TextView = view.findViewById(R.id.tv_detail_data_name)
        val info: TextView = view.findViewById(R.id.tv_detail_data_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_detail_data, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.name.text = mNameList[position]
        if (mInfoList[position] == "") {
            holder.info.text = "未填写"
        } else {
            holder.info.text = mInfoList[position]
        }
    }

    override fun getItemCount(): Int {
        return mInfoList.size
    }

}