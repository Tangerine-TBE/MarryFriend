package com.twx.marryfriend.set.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.ItemBean
import com.twx.marryfriend.bean.set.SetDataBean
import com.twx.marryfriend.bean.set.SetSwitchBean

/**
 * @author: Administrator
 * @date: 2022/7/28
 */
class SetSwitchAdapter(private val mList: MutableList<SetSwitchBean>) :
    RecyclerView.Adapter<SetSwitchAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_detail_set_switch_icon)
        val name: TextView = view.findViewById(R.id.tv_detail_set_switch_name)
        val switch: Switch = view.findViewById(R.id.sw_detail_set_switch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_set_switch, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext)
            .load(mList[position].icon)
            .into(holder.icon)

        holder.name.text = mList[position].title

        holder.switch.isChecked = mList[position].switch

        holder.switch.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onClick(v: View?) {

    }

}