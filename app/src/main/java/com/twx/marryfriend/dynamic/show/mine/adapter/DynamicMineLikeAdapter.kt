package com.twx.marryfriend.dynamic.show.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.LikeList
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/4
 */
class DynamicMineLikeAdapter(
    private val mList: MutableList<LikeList>,
    private val mEduData: MutableList<String>,
) :
    RecyclerView.Adapter<DynamicMineLikeAdapter.ViewHolder>(),
    View.OnClickListener {

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

        val avatar: ImageView = view.findViewById(R.id.riv_detail_dynamic_like_avatar)
        val name: TextView = view.findViewById(R.id.tv_detail_dynamic_like_name)
        val sex: ImageView = view.findViewById(R.id.iv_detail_dynamic_like_sex)
        val info: TextView = view.findViewById(R.id.tv_detail_dynamic_like_info)
        val time: TextView = view.findViewById(R.id.tv_detail_dynamic_like_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_dynamic_like, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext).load(mList[position].image_url).into(holder.avatar)

        holder.name.text = mList[position].nick

        if (mList[position].user_sex == 1) {
            holder.sex.setImageResource(R.drawable.ic_male)
        } else {
            holder.sex.setImageResource(R.drawable.ic_female)
        }

        holder.time.text = "${
            mList[position].create_time.subSequence(0, 4)
        }/${
            mList[position].create_time.subSequence(5, 7)
        }/${mList[position].create_time.subSequence(8, 10)}"


        val x = TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
            Calendar.YEAR) - mList[position].age

        val year = (TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
            Calendar.YEAR) - mList[position].age).toString().substring(2, 4)

        val city = mList[position].work_city_str

        val edu = mEduData[mList[position].education]

        val job = if (mList[position].industry_str == "") {
            "${mList[position].industry_str}"
        } else {
            " ${mList[position].industry_str}/${mList[position].occupation_str}"
        }

        holder.info.text = "${year}年  $city  $edu  $job"

        holder.info

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}