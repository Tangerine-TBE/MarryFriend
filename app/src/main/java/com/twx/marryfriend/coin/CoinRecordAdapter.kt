package com.twx.marryfriend.coin

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.vip.CoinRecordList
import com.twx.marryfriend.utils.TimeUtil

/**
 * @author: Administrator
 * @date: 2022/8/17
 */
class CoinRecordAdapter(private val mList: MutableList<CoinRecordList>) :
    RecyclerView.Adapter<CoinRecordAdapter.ViewHolder>(), View.OnClickListener {

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

        val detail: TextView = view.findViewById(R.id.tv_detail_coin_record_detail)
        val time: TextView = view.findViewById(R.id.tv_detail_coin_record_time)
        val sum: TextView = view.findViewById(R.id.tv_detail_coin_record_sum)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_coin_record, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        when (mList[position].projiect_kind) {
            1 -> {
                holder.detail.setTextColor(Color.parseColor("#404040"))
                holder.detail.text = "送花给 [${mList[position].receive_nick}]"
                holder.sum.setTextColor(Color.parseColor("#FF4444"))
                holder.sum.text = "-${mList[position].jinbi_operation}"
            }
            6 -> {
                holder.detail.setTextColor(Color.parseColor("#43A0FC"))
                holder.detail.text = "充值"
                holder.sum.setTextColor(Color.parseColor("#43A0FC"))
                holder.sum.text = "+${mList[position].jinbi_operation}"
            }
        }

        holder.time.text = TimeUtil.getCoinTime(mList[position].create_time)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}