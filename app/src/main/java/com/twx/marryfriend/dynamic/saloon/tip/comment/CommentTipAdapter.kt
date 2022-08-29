package com.twx.marryfriend.dynamic.saloon.tip.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.CommentTipList
import com.twx.marryfriend.utils.TimeUtil

/**
 * @author: Administrator
 * @date: 2022/7/25
 */
class CommentTipAdapter(private val mList: MutableList<CommentTipList>) :
    RecyclerView.Adapter<CommentTipAdapter.ViewHolder>(), View.OnClickListener {

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

        val avatar: RoundedImageView = view.findViewById(R.id.riv_detail_tip_avatar)

        val nick: TextView = view.findViewById(R.id.tv_detail_tip_nick)
        val time: TextView = view.findViewById(R.id.tv_detail_tip_time)

        val comment: TextView = view.findViewById(R.id.tv_detail_tip_content)

        val image: RoundedImageView = view.findViewById(R.id.riv_detail_tip_image)
        val video: FrameLayout = view.findViewById(R.id.fl_detail_tip_video)
        val videoPic: RoundedImageView = view.findViewById(R.id.riv_detail_tip_video)
        val text: TextView = view.findViewById(R.id.tv_detail_tip_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_dynamic_tips, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        if (mList[position].user_sex == 1) {
            Glide.with(mContext)
                .load(mList[position].headface)
                .error(R.drawable.ic_mine_male_default)
                .placeholder(R.drawable.ic_mine_male_default)
                .into(holder.avatar)
        } else {
            Glide.with(mContext)
                .load(mList[position].headface)
                .error(R.drawable.ic_mine_female_default)
                .placeholder(R.drawable.ic_mine_female_default)
                .into(holder.avatar)
        }

        holder.nick.text = mList[position].nick

        holder.comment.text = mList[position].content

        holder.time.text = TimeUtil.getCommonTime(mList[position].discuss_time)

        when (mList[position].trends_type) {
            1 -> {
                // 图片动态
                holder.image.visibility = View.VISIBLE
                holder.video.visibility = View.GONE
                holder.text.visibility = View.GONE

                val images: MutableList<String> =
                    mList[position].image_url.split(",") as MutableList<String>

                if (images.size > 1){
                    for (i in 0.until(images.size)) {
                        if (images[i].contains(" ")) {
                            images[i] = images[i].replace(" ", "")
                        }
                    }
                }

                Glide.with(mContext)
                    .load(images[0])
                    .error(R.drawable.ic_dynamic_tip_default)
                    .placeholder(R.drawable.ic_dynamic_tip_default)
                    .into(holder.image)

            }
            2 -> {
                // 视频动态
                holder.image.visibility = View.GONE
                holder.video.visibility = View.VISIBLE
                holder.text.visibility = View.GONE
                Glide.with(mContext)
                    .load(mList[position].video_url)
                    .error(R.drawable.ic_dynamic_tip_default)
                    .placeholder(R.drawable.ic_dynamic_tip_default)
                    .into(holder.videoPic)
            }
            3 -> {
                // 纯文字动态
                holder.image.visibility = View.GONE
                holder.video.visibility = View.GONE
                holder.text.visibility = View.VISIBLE
                holder.text.text = mList[position].text_content
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }


}