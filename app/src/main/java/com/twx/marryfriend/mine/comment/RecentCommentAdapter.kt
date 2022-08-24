package com.twx.marryfriend.mine.comment

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
import com.twx.marryfriend.bean.mine.DiscussList
import com.twx.marryfriend.utils.TimeUtil

class RecentCommentAdapter(private val mList: MutableList<DiscussList>) :
    RecyclerView.Adapter<RecentCommentAdapter.ViewHolder>(), View.OnClickListener {

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

        val avatar: RoundedImageView = view.findViewById(R.id.riv_detail_comment_avatar)

        val nick: TextView = view.findViewById(R.id.tv_detail_comment_nick)
        val time: TextView = view.findViewById(R.id.tv_detail_comment_time)

        val comment: TextView = view.findViewById(R.id.tv_detail_comment_content)

        val image: RoundedImageView = view.findViewById(R.id.riv_detail_comment_image)
        val video: FrameLayout = view.findViewById(R.id.fl_detail_comment_video)
        val videoPic: RoundedImageView = view.findViewById(R.id.riv_detail_comment_video)
        val text: TextView = view.findViewById(R.id.tv_detail_comment_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_comment, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext)
            .load(mList[position].headface)
            .into(holder.avatar)

        holder.nick.text = mList[position].nick

        holder.comment.text = mList[position].content

        holder.time.text = TimeUtil.getCommonTime(mList[position].create_time)

        when (mList[position].trends_type) {
            1 -> {
                // 图片动态
                holder.image.visibility = View.VISIBLE
                holder.video.visibility = View.GONE
                holder.text.visibility = View.GONE

                val images: MutableList<String> =
                    mList[position].image_url.split(",") as MutableList<String>

                if (images.size > 1) {
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