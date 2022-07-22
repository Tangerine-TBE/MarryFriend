package com.twx.marryfriend.dynamic.saloon.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.TrendTipList
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/4
 */
class TipsAdapter(private val mList: MutableList<TrendTipList>,
) :
    RecyclerView.Adapter<TipsAdapter.ViewHolder>(), View.OnClickListener {

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

        val avatar: RoundedImageView = view.findViewById(R.id.riv_detail_tip_avatar)
        val name: TextView = view.findViewById(R.id.tv_detail_tip_name)
        val content: TextView = view.findViewById(R.id.tv_detail_tip_content)
        val time: TextView = view.findViewById(R.id.tv_detail_tip_time)
        val trent: RoundedImageView = view.findViewById(R.id.riv_detail_tip_trent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_dynamic_tips, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position


    }

    override fun getItemCount(): Int {
        return mList.size
    }

}