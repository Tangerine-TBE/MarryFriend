package com.twx.marryfriend.mine.user.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.DataLifeBean
import kotlinx.android.synthetic.main.fragment_data.*

/**
 * @author: Administrator
 * @date: 2022/5/31
 */
class DataLifePhotoAdapter(private val mList: MutableList<DataLifeBean>) :
    RecyclerView.Adapter<DataLifePhotoAdapter.ViewHolder>(), View.OnClickListener {

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
        val iv: ImageView = view.findViewById(R.id.iv_detail_data_life)
        val tv: TextView = view.findViewById(R.id.tv_detail_data_life)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_detail_data_life, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext)
            .load(mList[position].ImageUrl)
            .error(R.drawable.ic_data_life_default)
            .placeholder(R.drawable.ic_data_life_default)
            .into(holder.iv)

        when (mList[position].ImageState) {
            "0" -> {
                holder.tv.visibility = View.VISIBLE
                holder.tv.text = "审核中..."
            }
            "1" -> {
                holder.tv.visibility = View.GONE
            }
            "2" -> {
                holder.tv.visibility = View.VISIBLE
                holder.tv.text = "审核拒绝"
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}