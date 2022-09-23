package com.twx.marryfriend.set.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.ItemBean
import com.twx.marryfriend.bean.set.SetDataBean
import com.twx.marryfriend.bean.vip.BlackListData

/**
 * @author: Administrator
 * @date: 2022/7/28
 */
class BlacklistAdapter(private val mList: MutableList<BlackListData>) :
    RecyclerView.Adapter<BlacklistAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
        fun onItemRemoveClick(v: View?, position: Int)
        fun onItemChatClick(v: View?, position: Int)
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

        val avatar: ImageView = view.findViewById(R.id.iv_detail_blacklist_avatar)
        val nick: TextView = view.findViewById(R.id.tv_detail_blacklist_nick)
        val sex: ImageView = view.findViewById(R.id.iv_detail_blacklist_sex)
        val info: TextView = view.findViewById(R.id.tv_detail_blacklist_info)

        val remove: LinearLayout = view.findViewById(R.id.ll_detail_blacklist_remove)
        val chat: ImageView = view.findViewById(R.id.iv_detail_blacklist_chat)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_set_blacklist, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        if (mList[position].user_sex == 1) {
            Glide.with(mContext)
                .load(mList[position].image_url)
                .error(R.drawable.ic_dialog_avatar_male)
                .placeholder(R.drawable.ic_dialog_avatar_male)
                .into(holder.avatar)
        } else {
            Glide.with(mContext)
                .load(mList[position].image_url)
                .error(R.drawable.ic_dialog_avatar_female)
                .placeholder(R.drawable.ic_dialog_avatar_female)
                .into(holder.avatar)
        }


        holder.nick.text = mList[position].nick

        var info = ""

        if (mList[position].work_city_str != "") {
            info = "${mList[position].work_city_str}"
        }

        info = "$info 丨 ${mList[position].age}岁"

        if (mList[position].occupation_str != "") {
            info = "$info 丨 ${mList[position].occupation_str}"
        }

        holder.info.text = info

        holder.remove.setOnClickListener {
            mOnItemClickListener?.onItemRemoveClick(it, position)
        }

        holder.chat.setOnClickListener {
            mOnItemClickListener?.onItemChatClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}