package com.twx.marryfriend.dynamic.mine.adapter

import android.content.Context
import android.text.TextWatcher
import android.text.format.DateFormat.getDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.MyTrendsList
import com.twx.marryfriend.utils.FolderTextView
import com.twx.marryfriend.utils.TimeUtil
import java.util.*


/**
 * @author: Administrator
 * @date: 2022/6/23
 */
class MyDynamicAdapter(private val mList: MutableList<MyTrendsList>) :
    RecyclerView.Adapter<MyDynamicAdapter.ViewHolder>(), View.OnClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

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
        fun onTextClick(v: View?, position: Int)
        fun onItemMoreClick(v: View?, position: Int)
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

        val text: FolderTextView = view.findViewById(R.id.tv_detail_dynamic_mine_text)

        val local: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_mine_location)
        val location: TextView = view.findViewById(R.id.tv_detail_dynamic_mine_location)
        val time: TextView = view.findViewById(R.id.tv_detail_dynamic_mine_time)
        val audit: TextView = view.findViewById(R.id.tv_detail_dynamic_mine_audit)

        val tvLike: TextView = view.findViewById(R.id.tv_detail_dynamic_mine_like)

        val tvComment: TextView = view.findViewById(R.id.tv_detail_dynamic_mine_comment)

        val llVideo: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_mine_video)
        val llOne: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_mine_one)
        val llTwo: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_mine_two)
        val llThree: LinearLayout = view.findViewById(R.id.ll_detail_dynamic_mine_three)

        val video: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_video)

        // 点击事件

        val more: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_more)

        val one: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_one)
        val two: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_two)
        val three: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_three)
        val four: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_four)
        val five: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_five)
        val six: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_six)
        val seven: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_seven)
        val eight: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_eight)
        val nine: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_nine)

        val ivLike: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_like)
        val ivComment: ImageView = view.findViewById(R.id.iv_detail_dynamic_mine_comment)

        val flVideo: FrameLayout = view.findViewById(R.id.fl_detail_dynamic_mine_video)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_dynamic_mine, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        initPic(holder, position)

        holder.more.setOnClickListener {
            mOnItemClickListener?.onItemMoreClick(it, position)
        }

        holder.text.setOnClickListener {
            Log.i("guo", "123456")
            mOnItemClickListener?.onTextClick(it, position)
        }

        holder.ivLike.setOnClickListener {
            mOnLikeClickListener?.onLikeClick(it, position)
        }

        holder.ivComment.setOnClickListener {
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

        if (mList[position].text_content != "") {
            holder.text.visibility = View.VISIBLE
            holder.text.text = mList[position].text_content
        } else {
            holder.text.visibility = View.GONE
        }

        if (mList[position].image_url != "") {
            // 有图片信息

            val mPicList: MutableList<String> =
                mList[position].image_url.split(",") as MutableList<String>

            for (i in 0.until(mPicList.size)) {
                if (mPicList[i].contains(" ")) {
                    mPicList[i] = mPicList[i].replace(" ", "")
                }
            }

            holder.llVideo.visibility = View.GONE

            when (mPicList.size) {
                1 -> {
                    holder.llOne.visibility = View.VISIBLE
                    holder.llTwo.visibility = View.GONE
                    holder.llThree.visibility = View.GONE

                    holder.one.visibility = View.VISIBLE
                    Glide.with(mContext)
                        .load(mPicList[0])
                        .error(R.drawable.ic_pic_default)
                        .placeholder(R.drawable.ic_pic_default)
                        .into(holder.one)
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
                Glide.with(mContext)
                    .load(mList[position].video_url)
                    .error(R.drawable.ic_video_default)
                    .placeholder(R.drawable.ic_video_default)
                    .into(holder.video)
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
            holder.local.visibility = View.GONE
        }

        TimeUtils.getValueByCalendarField(mList[position].create_time, Calendar.HOUR)

        holder.time.text = TimeUtil.getCommonTime(mList[position].create_time)

        if (mList[position].audit_status == 1) {
            holder.audit.visibility = View.GONE
        } else {
            holder.audit.visibility = View.VISIBLE
        }

        holder.tvLike.text = mList[position].like_count.toString()
        holder.tvComment.text = mList[position].discuss_count.toString()

    }

}