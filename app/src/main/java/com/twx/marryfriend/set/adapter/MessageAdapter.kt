package com.twx.marryfriend.set.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.set.MessageSwitchBean
import com.twx.marryfriend.bean.set.SetSwitchBean

/**
 * @author: Administrator
 * @date: 2022/7/28
 */
class MessageAdapter(private val mList: MutableList<MessageSwitchBean>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>(), View.OnClickListener {

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
        val name: TextView = view.findViewById(R.id.tv_detail_message_switch_name)
        val subtitle: TextView = view.findViewById(R.id.tv_detail_message_switch_subtitle)
        val switch: Switch = view.findViewById(R.id.sw_detail_message_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_message_switch, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        holder.name.text = mList[position].title

        if (mList[position].isDouble) {
            holder.subtitle.visibility = View.VISIBLE
            holder.subtitle.text = mList[position].subtitle
        } else {
            holder.subtitle.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}