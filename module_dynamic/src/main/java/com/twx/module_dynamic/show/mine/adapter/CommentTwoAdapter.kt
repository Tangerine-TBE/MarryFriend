package com.twx.module_dynamic.show.mine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.twx.module_base.utils.TimeUtil
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.CommentOneList
import com.twx.module_dynamic.bean.CommentTwoList
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/8
 */
class CommentTwoAdapter(private var mList: List<CommentTwoList>) :
    RecyclerView.Adapter<CommentTwoAdapter.ViewHolder>(), View.OnClickListener {

//    private var mList: List<CommentTwoList> = arrayListOf()

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

//    public fun setChildList(list: List<CommentTwoList>) {
//        this.mList = list
//    }

    interface OnItemClickListener {
        fun onItemClick(v: View?, position: Int)
        fun onAvatarClick(v: View?, position: Int)
        fun onReplyAvatarClick(v: View?, position: Int)
        fun onReplyClick(v: View?, position: Int)
    }

    public fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    override fun onClick(v: View?) {
        if (v != null) {
            mOnItemClickListener?.onItemClick(v, v.tag as Int)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.iv_detail_comment_child_avatar)
        val name: TextView = view.findViewById(R.id.tv_detail_comment_child_name)
        val sex: ImageView = view.findViewById(R.id.iv_detail_comment_child_sex)
        val replyText: TextView = view.findViewById(R.id.reply)
        val reply: TextView = view.findViewById(R.id.tv_detail_comment_child_reply)
        val content: TextView = view.findViewById(R.id.tv_detail_comment_child_content)
        val time: TextView = view.findViewById(R.id.tv_detail_comment_child_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_comment_child, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)


        //动态设置ImageView的宽高，根据自己每行item数量计算
        //dm.widthPixels-dip2px(20)即屏幕宽度-左右10dp+10dp=20dp再转换为px的宽度，最后/3得到每个item的宽高

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        Glide.with(mContext).load(mList[position].last_img_url).into(holder.avatar)


        if (mList[position].first_nick != "") {
            holder.reply.text = "${mList[position].first_nick}"
        } else {
            holder.reply.visibility = View.GONE
            holder.replyText.visibility = View.GONE
        }


        holder.name.text = mList[position].last_nick

        if (mList[position].last_sex == 1) {
            Glide.with(mContext).load(R.drawable.ic_male).into(holder.sex)
        } else {
            Glide.with(mContext).load(R.drawable.ic_female).into(holder.sex)
        }

        holder.content.text = mList[position].content

        holder.time.text = TimeUtil.getCommonTime(mList[position].create_time)


        holder.avatar.setOnClickListener {
            mOnItemClickListener?.onAvatarClick(it, position)
        }

        holder.reply.setOnClickListener {
            mOnItemClickListener?.onReplyAvatarClick(it, position)
        }

        holder.content.setOnClickListener {
            mOnItemClickListener?.onReplyClick(it, position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

}