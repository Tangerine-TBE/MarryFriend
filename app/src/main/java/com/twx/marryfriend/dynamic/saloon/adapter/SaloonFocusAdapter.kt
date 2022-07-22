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
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.TrendFocusList
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/4
 */
class SaloonFocusAdapter(
    private val mList: MutableList<TrendFocusList>,
    private val mEduData: MutableList<String>,
) :
    RecyclerView.Adapter<SaloonFocusAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    private var mOnAvatarClickListener: OnAvatarClickListener? = null

    private var mOnFocusClickListener: OnFocusClickListener? = null

    private var mOnLikeClickListener: OnLikeClickListener? = null

    private var mOnCommentClickListener: OnCommentClickListener? = null

    private var mOnOneClickListener: OnOneClickListener? = null

    private var mOnTwoClickListener: OnTwoClickListener? = null

    private var mOnThreeClickListener: OnThreeClickListener? = null

    private var mOnFourClickListener: OnFourClickListener? = null

    private var mOnFiveClickListener: OnFiveClickListener? = null

    private var mOnSixClickListener: OnSixClickListener? = null

    private var mOnSevenClickListener: OnSevenClickListener? = null

    private var mOnEightClickListener: OnEightClickListener? = null

    private var mOnNineClickListener: OnNineClickListener? = null

    private var mOnVideoClickListener: OnVideoClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
    }

    interface OnAvatarClickListener {
        fun onAvatarClick(v: View?, position: Int)
    }

    interface OnFocusClickListener {
        fun onFocusClick(v: View?, position: Int)
    }

    interface OnLikeClickListener {
        fun onLikeClick(v: View?, position: Int)
    }

    interface OnCommentClickListener {
        fun onCommentClick(v: View?, position: Int)
    }

    interface OnOneClickListener {
        fun onOneClick(v: View?, position: Int)
    }

    interface OnTwoClickListener {
        fun onTwoClick(v: View?, position: Int)
    }

    interface OnThreeClickListener {
        fun onThreeClick(v: View?, position: Int)
    }

    interface OnFourClickListener {
        fun onFourClick(v: View?, position: Int)
    }

    interface OnFiveClickListener {
        fun onFiveClick(v: View?, position: Int)
    }

    interface OnSixClickListener {
        fun onSixClick(v: View?, position: Int)
    }

    interface OnSevenClickListener {
        fun onSevenClick(v: View?, position: Int)
    }

    interface OnEightClickListener {
        fun onEightClick(v: View?, position: Int)
    }

    interface OnNineClickListener {
        fun onNineClick(v: View?, position: Int)
    }

    interface OnVideoClickListener {
        fun onVideoClick(v: View?, position: Int)
    }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    fun setOnAvatarClickListener(listener: OnAvatarClickListener) {
        this.mOnAvatarClickListener = listener
    }

    fun setOnFocusClickListener(listener: OnFocusClickListener) {
        this.mOnFocusClickListener = listener
    }

    fun setOnLikeClickListener(listener: OnLikeClickListener) {
        this.mOnLikeClickListener = listener
    }

    fun setOnCommentClickListener(listener: OnCommentClickListener) {
        this.mOnCommentClickListener = listener
    }

    fun setOnOneClickListener(listener: OnOneClickListener) {
        this.mOnOneClickListener = listener
    }

    fun setOnTwoClickListener(listener: OnTwoClickListener) {
        this.mOnTwoClickListener = listener
    }

    fun setOnThreeClickListener(listener: OnThreeClickListener) {
        this.mOnThreeClickListener = listener
    }

    fun setOnFourClickListener(listener: OnFourClickListener) {
        this.mOnFourClickListener = listener
    }

    fun setOnFiveClickListener(listener: OnFiveClickListener) {
        this.mOnFiveClickListener = listener
    }

    fun setOnSixClickListener(listener: OnSixClickListener) {
        this.mOnSixClickListener = listener
    }

    fun setOnSevenClickListener(listener: OnSevenClickListener) {
        this.mOnSevenClickListener = listener
    }

    fun setOnEightClickListener(listener: OnEightClickListener) {
        this.mOnEightClickListener = listener
    }

    fun setOnNineClickListener(listener: OnNineClickListener) {
        this.mOnNineClickListener = listener
    }

    fun setOnVideoClickListener(listener: OnVideoClickListener) {
        this.mOnVideoClickListener = listener
    }

    override fun onClick(v: View?) {
        if (v != null) {
            mOnItemClickListener?.onItemClick(v, v.tag as Int)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val avatar: ImageView = view.findViewById(R.id.riv_detail_dynamic_other_avatar)
        val name: TextView = view.findViewById(R.id.tv_detail_dynamic_other_name)
        val identity: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_identity)
        val vip: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_vip)
        val info: TextView = view.findViewById(R.id.tv_detail_dynamic_other_info)

        val text: TextView = view.findViewById(R.id.tv_detail_dynamic_other_text)


        val location: TextView = view.findViewById(R.id.tv_detail_dynamic_other_location)
        val time: TextView = view.findViewById(R.id.tv_detail_dynamic_other_time)

        val ivLike: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_like)
        val tvLike: TextView = view.findViewById(R.id.tv_detail_dynamic_other_like)

        val ivComment: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_comment)
        val tvComment: TextView = view.findViewById(R.id.tv_detail_dynamic_other_comment)

        val llVideo: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_other_video)
        val llOne: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_other_one)
        val llTwo: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_other_two)
        val llThree: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_other_three)

        val video: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_video)


        // 点击事件
        val detail: RelativeLayout = view.findViewById(R.id.rl_detail_dynamic_other_detail)
        val focus: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_focus)

        val one: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_one)
        val two: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_two)
        val three: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_three)
        val four: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_four)
        val five: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_five)
        val six: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_six)
        val seven: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_seven)
        val eight: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_eight)
        val nine: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_nine)

        val like: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_like)
        val comment: ImageView = view.findViewById(R.id.iv_detail_dynamic_other_comment)

        val flVideo: FrameLayout = view.findViewById(R.id.fl_detail_dynamic_other_video)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_dynamic_other, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext).load(R.drawable.ic_base_chat).into(holder.focus)

        initPic(holder, position)

        holder.detail.setOnClickListener {
            mOnAvatarClickListener?.onAvatarClick(it, position)
        }

        holder.focus.setOnClickListener {
            mOnFocusClickListener?.onFocusClick(it, position)
        }

        holder.like.setOnClickListener {
            mOnLikeClickListener?.onLikeClick(it, position)
        }

        holder.comment.setOnClickListener {
            mOnCommentClickListener?.onCommentClick(it, position)
        }

        holder.one.setOnClickListener {
            mOnOneClickListener?.onOneClick(it, position)
        }

        holder.two.setOnClickListener {
            mOnTwoClickListener?.onTwoClick(it, position)
        }

        holder.three.setOnClickListener {
            mOnThreeClickListener?.onThreeClick(it, position)
        }

        holder.four.setOnClickListener {
            mOnFourClickListener?.onFourClick(it, position)
        }

        holder.five.setOnClickListener {
            mOnFiveClickListener?.onFiveClick(it, position)
        }

        holder.six.setOnClickListener {
            mOnSixClickListener?.onSixClick(it, position)
        }

        holder.seven.setOnClickListener {
            mOnSevenClickListener?.onSevenClick(it, position)
        }

        holder.eight.setOnClickListener {
            mOnEightClickListener?.onEightClick(it, position)
        }

        holder.nine.setOnClickListener {
            mOnNineClickListener?.onNineClick(it, position)
        }

        holder.flVideo.setOnClickListener {
            mOnVideoClickListener?.onVideoClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun initPic(holder: ViewHolder, position: Int) {

        Glide.with(mContext).load(mList[position].headface).into(holder.avatar)
        holder.name.text = mList[position].nick

        if (mList[position].vip_level > 0) {
            holder.vip.visibility = View.VISIBLE
        }

        if (mList[position].identity_status == 1) {
            Glide.with(mContext).load(R.mipmap.icon_identify_success)
                .into(holder.identity)
        } else {
            Glide.with(mContext).load(R.mipmap.icon_identify_non)
                .into(holder.identity)
        }

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


        if (mList[position].text_content != "") {
            holder.text.text = mList[position].text_content
        } else {
            holder.text.visibility = View.GONE
        }

        if (mList[position].image_url != "") {
            // 有图片信息

            val mPicList: MutableList<String> =
                mList[position].image_url.split(",") as MutableList<String>

            for (i in 0.until(mPicList.size)) {
                mPicList[i] = mPicList[i].replace(" ", "")
            }

            holder.llVideo.visibility = View.GONE

            when (mPicList.size) {
                1 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.GONE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.INVISIBLE
                    holder.three.visibility = View.INVISIBLE
                    holder.four.visibility = View.GONE
                    holder.five.visibility = View.GONE
                    holder.six.visibility = View.GONE
                    holder.seven.visibility = View.GONE
                    holder.eight.visibility = View.GONE
                    holder.nine.visibility = View.GONE
                }
                2 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.GONE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.INVISIBLE
                    holder.four.visibility = View.GONE
                    holder.five.visibility = View.GONE
                    holder.six.visibility = View.GONE
                    holder.seven.visibility = View.GONE
                    holder.eight.visibility = View.GONE
                    holder.nine.visibility = View.GONE
                }
                3 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.GONE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.three)
                    holder.four.visibility = View.GONE
                    holder.five.visibility = View.GONE
                    holder.six.visibility = View.GONE
                    holder.seven.visibility = View.GONE
                    holder.eight.visibility = View.GONE
                    holder.nine.visibility = View.GONE
                }
                4 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.VISIBLE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.INVISIBLE
                    holder.four.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.four)
                    holder.five.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[3]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.five)
                    holder.six.visibility = View.INVISIBLE
                    holder.seven.visibility = View.GONE
                    holder.eight.visibility = View.GONE
                    holder.nine.visibility = View.GONE
                }
                5 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.VISIBLE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.three)
                    holder.four.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[3]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.four)
                    holder.five.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[4]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.five)
                    holder.six.visibility = View.INVISIBLE
                    holder.seven.visibility = View.GONE
                    holder.eight.visibility = View.GONE
                    holder.nine.visibility = View.GONE
                }
                6 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.VISIBLE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.three)
                    holder.four.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[3]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.four)
                    holder.five.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[4]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.five)
                    holder.six.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[5]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.six)
                    holder.seven.visibility = View.GONE
                    holder.eight.visibility = View.GONE
                    holder.nine.visibility = View.GONE
                }
                7 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.VISIBLE
                    holder.llThree.visibility = View.VISIBLE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.three)
                    holder.four.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[3]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.four)
                    holder.five.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[4]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.five)
                    holder.six.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[5]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.six)
                    holder.seven.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[6]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.seven)
                    holder.eight.visibility = View.INVISIBLE
                    holder.nine.visibility = View.INVISIBLE
                }
                8 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.VISIBLE
                    holder.llThree.visibility = View.VISIBLE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.three)
                    holder.four.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[3]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.four)
                    holder.five.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[4]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.five)
                    holder.six.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[5]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.six)
                    holder.seven.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[6]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.seven)
                    holder.eight.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[7]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.eight)
                    holder.nine.visibility = View.INVISIBLE
                }
                9 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.VISIBLE
                    holder.llThree.visibility = View.VISIBLE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[0]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.one)
                    holder.two.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[1]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.two)
                    holder.three.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[2]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.three)
                    holder.four.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[3]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.four)
                    holder.five.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[4]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.five)
                    holder.six.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[5]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.six)
                    holder.seven.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[6]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.seven)
                    holder.eight.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[7]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.eight)
                    holder.nine.visibility = View.VISIBLE
                    Glide.with(mContext).load(mPicList[8]).error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default).into(holder.nine)
                }
            }
        } else {

            if (mList[position].video_url != "") {
                // 有视频信息
                holder.llVideo.visibility = View.VISIBLE
                Glide.with(mContext).load(mList[position].video_url).into(holder.video)
                holder.llOne.visibility = View.GONE
                holder.llTwo.visibility = View.GONE
                holder.llThree.visibility = View.GONE

            } else {
                holder.llVideo.visibility = View.GONE
                holder.llOne.visibility = View.GONE
                holder.llTwo.visibility = View.GONE
                holder.llThree.visibility = View.GONE
            }

        }

        if (mList[position].position != "") {
            holder.location.text = mList[position].position
        } else {
            holder.location.visibility = View.GONE
        }

        val timeSECSpan = TimeUtils.getTimeSpan(TimeUtils.getNowString(),
            mList[position].create_time,
            TimeConstants.SEC)

        val timeMINSpan = TimeUtils.getTimeSpan(TimeUtils.getNowString(),
            mList[position].create_time,
            TimeConstants.MIN)

        val timeHOURSpan = TimeUtils.getTimeSpan(TimeUtils.getNowString(),
            mList[position].create_time,
            TimeConstants.HOUR)

        if (timeSECSpan < 120) {
            holder.time.text = "一分钟前"
        } else {
            if (timeMINSpan < 60) {
                holder.time.text = "${timeMINSpan}分钟前"
            } else {
                if (timeHOURSpan < 24) {
                    holder.time.text = "${timeHOURSpan}小时前"
                } else {
                    if (timeHOURSpan < 48) {
                        val day =
                            TimeUtils.getValueByCalendarField(mList[position].create_time,
                                Calendar.DAY_OF_YEAR)
                        val nowDay = TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
                            Calendar.DAY_OF_YEAR)

                        // 判断是不是昨天
                        if (day - nowDay == -1) {
                            holder.time.text = "昨天"
                        }
                        // 判断是不是前天
                        if (day - nowDay == -2) {
                            holder.time.text = "2天前"
                        }
                    } else {
                        val day = TimeUtils.getValueByCalendarField(mList[position].create_time,
                            Calendar.DAY_OF_YEAR)
                        val nowDay = TimeUtils.getValueByCalendarField(TimeUtils.getNowDate(),
                            Calendar.DAY_OF_YEAR)
                        holder.time.text = "${nowDay - day}天前"
                    }
                }
            }
        }

        holder.tvLike.text = mList[position].like_count.toString()
        holder.tvComment.text = mList[position].discuss_count.toString()

    }

}