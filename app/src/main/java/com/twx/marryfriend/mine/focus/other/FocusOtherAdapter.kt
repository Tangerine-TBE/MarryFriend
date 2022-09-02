package com.twx.marryfriend.mine.focus.other

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.CommentTipList
import com.twx.marryfriend.bean.mine.MeFocusWhoBean
import com.twx.marryfriend.bean.mine.MeFocusWhoList
import com.twx.marryfriend.constant.DataProvider
import com.twx.marryfriend.utils.SpUtil
import com.twx.marryfriend.utils.TimeUtil

class FocusOtherAdapter(private val mList: MutableList<MeFocusWhoList>) :
    RecyclerView.Adapter<FocusOtherAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
        fun onMoreClick(v: View?, position: Int)
        fun onChatClick(v: View?, position: Int)
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

        val container: LinearLayout = view.findViewById(R.id.ll_detail_focus_other_container)

        val avatar: RoundedImageView = view.findViewById(R.id.riv_detail_focus_other_avatar)

        val name: TextView = view.findViewById(R.id.tv_detail_focus_other_name)
        val trueAvatar: ImageView = view.findViewById(R.id.iv_detail_focus_other_avatar)
        val identify: ImageView = view.findViewById(R.id.iv_detail_focus_other_identify)
        val vip: ImageView = view.findViewById(R.id.iv_detail_focus_other_vip)
        val more: ImageView = view.findViewById(R.id.iv_detail_focus_other_more)

        val info: TextView = view.findViewById(R.id.tv_detail_focus_other_info)

        val act: TextView = view.findViewById(R.id.tv_detail_focus_other_act)

        val chat: LinearLayout = view.findViewById(R.id.ll_detail_focus_other_chat)
        val chatText: TextView = view.findViewById(R.id.tv_detail_focus_other_chat)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_focus_other, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        initView(holder, position)

        holder.container.setOnClickListener {
            mOnItemClickListener?.onItemClick(it, position)
        }

        holder.more.setOnClickListener {
            mOnItemClickListener?.onMoreClick(it, position)
        }

        holder.chat.setOnClickListener {
            mOnItemClickListener?.onChatClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    private fun initView(holder: ViewHolder, position: Int) {

        if (mList[position].user_sex == 1) {
            Glide.with(mContext)
                .load(mList[position].image_url)
                .error(R.drawable.ic_mine_female_default)
                .placeholder(R.drawable.ic_mine_female_default)
                .into(holder.avatar)
        } else {
            Glide.with(mContext)
                .load(mList[position].image_url)
                .error(R.drawable.ic_mine_male_default)
                .placeholder(R.drawable.ic_mine_male_default)
                .into(holder.avatar)
        }

        holder.name.text = mList[position].nick

        if (mList[position].identity_status == 1) {
            holder.identify.visibility = View.VISIBLE
        } else {
            holder.identify.visibility = View.GONE
        }

        if (mList[position].real_face == 1) {
            holder.trueAvatar.visibility = View.VISIBLE
        } else {
            holder.trueAvatar.visibility = View.GONE
        }

        when (SpUtil.getVipLevel(mList[position].close_time_low, mList[position].close_time_high)) {
            0 -> {
                holder.vip.visibility = View.GONE
            }
            1 -> {
                holder.vip.visibility = View.VISIBLE
                holder.vip.setImageResource(R.drawable.ic_vip)
            }
            2 -> {
                holder.vip.visibility = View.VISIBLE
                holder.vip.setImageResource(R.drawable.ic_svip)
            }
        }

        val city = mList[position].work_city_str

        val year = mList[position].age

        val job = mList[position].occupation_str

        val edu = DataProvider.EduData[mList[position].education]

        holder.info.text = "$city  ${year}岁  $job  $edu"

    }


}