package com.twx.marryfriend.mine.focus.mine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.makeramen.roundedimageview.RoundedImageView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.WhoFocusMeList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.DataProvider
import jp.wasabeef.glide.transformations.BlurTransformation

class FocusMineAdapter(private val mList: MutableList<WhoFocusMeList>) :
    RecyclerView.Adapter<FocusMineAdapter.ViewHolder>(), View.OnClickListener {

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

        val avatar: RoundedImageView = view.findViewById(R.id.riv_detail_focus_mine_avatar)

        val detail: TextView = view.findViewById(R.id.tv_detail_focus_mine_detail)
        val day: TextView = view.findViewById(R.id.tv_detail_focus_mine_day)

        val edu: TextView = view.findViewById(R.id.tv_detail_focus_mine_edu)
        val income: TextView = view.findViewById(R.id.tv_detail_focus_mine_income)

        val info: TextView = view.findViewById(R.id.tv_detail_focus_mine_info)
        val introduce: TextView = view.findViewById(R.id.tv_detail_focus_mine_introduce)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_focus_mine, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        if (mList[position].user_sex == 1) {
            if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 2) {
                Glide.with(mContext)
                    .load(mList[position].image_url)
                    .error(R.drawable.ic_mine_male_default)
                    .placeholder(R.drawable.ic_mine_male_default)
                    .into(holder.avatar)
            } else {
                Glide.with(mContext)
                    .load(mList[position].image_url)
                    .error(R.drawable.ic_mine_male_default)
                    .placeholder(R.drawable.ic_mine_male_default)
                    .apply(bitmapTransform(BlurTransformation(25)))
                    .into(holder.avatar)
            }
        } else {
            if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 2) {
                Glide.with(mContext)
                    .load(mList[position].image_url)
                    .error(R.drawable.ic_mine_female_default)
                    .placeholder(R.drawable.ic_mine_female_default)
                    .into(holder.avatar)
            } else {
                Glide.with(mContext)
                    .load(mList[position].image_url)
                    .error(R.drawable.ic_mine_female_default)
                    .placeholder(R.drawable.ic_mine_female_default)
                    .apply(bitmapTransform(BlurTransformation(25)))
                    .into(holder.avatar)
            }
        }

        if (mList[position].hometown_province_str != "" &&
            mList[position].work_city_str != "" &&
            mList[position].occupation_str != ""
        ) {
            holder.detail.text =
                "${mList[position].hometown_province_str}人，在${mList[position].work_city_str}做${mList[position].occupation_str}"
        } else {

            if (mList[position].user_sex == 1) {
                holder.detail.text = "${mList[position].age}的小哥哥"
            } else {
                holder.detail.text = "${mList[position].age}的小姐姐"
            }
        }

        holder.day.text =
            "关注你${
                (-TimeUtils.getTimeSpanByNow(mList[position].create_time,
                    TimeConstants.DAY)) + 1
            }天"

        if (mList[position].education >= 3) {
            holder.edu.visibility = View.VISIBLE
        } else {
            holder.edu.visibility = View.GONE
        }

        if (mList[position].salary_range != "") {
            if (DataProvider.SuperOneCity.contains(mList[position].work_city_str)) {
                if (mList[position].salary_range.toInt() >= 5) {
                    holder.income.visibility = View.VISIBLE
                } else {
                    holder.income.visibility = View.GONE
                }
            } else if (DataProvider.OneCity.contains(mList[position].work_city_str)) {
                if (mList[position].salary_range.toInt() >= 4) {
                    holder.income.visibility = View.VISIBLE
                } else {
                    holder.income.visibility = View.GONE
                }
            } else {
                if (mList[position].salary_range.toInt() >= 3) {
                    holder.income.visibility = View.VISIBLE
                } else {
                    holder.income.visibility = View.GONE
                }
            }
        } else {
            holder.income.visibility = View.GONE
        }

        if (mList[position].introduce_self != "") {
            holder.introduce.visibility = View.VISIBLE
            holder.introduce.text = mList[position].introduce_self
        } else {
            holder.introduce.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}