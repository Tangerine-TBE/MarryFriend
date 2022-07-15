package com.twx.module_dynamic.show.mine.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.makeramen.roundedimageview.RoundedImageView
import com.twx.module_base.utils.TimeUtil
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.CommentOneList
import com.twx.module_dynamic.bean.CommentTwoList
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/8
 */
class CommentOneAdapterOr(
    private val mList: MutableList<CommentOneList>,
    private val mChildList: MutableList<CommentTwoList>,
) {
//    RecyclerView.Adapter<CommentOneAdapterOr.ViewHolder>(), View.OnClickListener {
//
//    private lateinit var mContext: Context
//
//    private var size: MutableList<Int> = arrayListOf()
//
//    private var mOnItemClickListener: OnItemClickListener? = null
//
//    private var mOnMoreClickListener: OnMoreClickListener? = null
//
//    fun setSize(size: MutableList<Int>) {
//        this.size = size
//        notifyDataSetChanged()
//    }
//
//
//    interface OnItemClickListener {
//        fun onItemClick(v: View?, positionOne: Int)
//
//        fun onItemAvatarClick(v: View?, positionOne: Int)
//        fun onItemContentClick(v: View?, positionOne: Int)
//
//        fun onChildClick(positionOne: Int, two: Int)
//        fun onChildAvatarClick(positionOne: Int, two: Int)
//        fun onChildReplyClick(positionOne: Int, two: Int)
//        fun onChildReplyAvatarClick(positionOne: Int, two: Int)
//    }
//
//    interface OnMoreClickListener {
//        fun onMoreClick(v: View?, position: Int)
//    }
//
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.mOnItemClickListener = listener
//    }
//
//    fun setOnMoreClickListener(listener: OnMoreClickListener) {
//        this.mOnMoreClickListener = listener
//    }
//
//    override fun onClick(v: View?) {
//        if (v != null) {
//            mOnItemClickListener?.onItemClick(v, v.tag as Int)
//        }
//    }
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val user: RelativeLayout = view.findViewById(R.id.rl_detail_comment_parent_user)
//        val avatar: RoundedImageView = view.findViewById(R.id.iv_detail_comment_parent_avatar)
//        val name: TextView = view.findViewById(R.id.tv_detail_comment_parent_name)
//        val sex: ImageView = view.findViewById(R.id.iv_detail_comment_parent_sex)
//
//        val info: TextView = view.findViewById(R.id.tv_detail_comment_parent_info)
//        val content: TextView = view.findViewById(R.id.tv_detail_comment_parent_content)
//        val time: TextView = view.findViewById(R.id.tv_detail_comment_parent_time)
//
//        val child: LinearLayout = view.findViewById(R.id.ll_detail_comment_parent_child)
//        val childAvatar: RoundedImageView =
//            view.findViewById(R.id.iv_detail_comment_parent_child_avatar)
//        val childName: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_name)
//        val childSex: ImageView = view.findViewById(R.id.tv_detail_comment_parent_child_sex)
//        val childContent: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_content)
//        val childTime: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_time)
//
//        val childMore: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_more)
//        val container: RecyclerView = view.findViewById(R.id.rv_detail_comment_parent_child_container)
//
////        val itemAdapter: CommentTwoAdapter = CommentTwoAdapter()
//
//        fun showChildComment(list: MutableList<CommentTwoList>) {
//            itemAdapter.setChildList(list)
//            // //填充数据
//            container.adapter = itemAdapter
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.layout_comment_parent, parent, false)
//        mContext = parent.context
//        view.setOnClickListener(this)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.itemView.tag = position
//
//        holder.container.layoutManager = LinearLayoutManager(mContext)
//        var mFrontAdapter = CommentTwoAdapter()
//        mFrontAdapter.setChildList(mChildList)
//        holder.container.adapter = mFrontAdapter
//
//
//
//        holder.itemAdapter.setOnItemClickListener(object : CommentTwoAdapter.OnItemClickListener {
//            override fun onItemClick(v: View?, positionTwo: Int) {
//                mOnItemClickListener?.onChildClick(position, positionTwo)
//            }
//
//            override fun onAvatarClick(v: View?, positionTwo: Int) {
//                mOnItemClickListener?.onChildAvatarClick(position, positionTwo)
//            }
//
//            override fun onReplyAvatarClick(v: View?, positionTwo: Int) {
//                mOnItemClickListener?.onChildReplyAvatarClick(position, positionTwo)
//            }
//
//            override fun onReplyClick(v: View?, positionTwo: Int) {
//                mOnItemClickListener?.onChildReplyClick(position, positionTwo)
//            }
//
//        })
//
//        holder.user.setOnClickListener {
//            mOnItemClickListener?.onItemAvatarClick(it, position)
//        }
//
//        holder.content.setOnClickListener {
//            mOnItemClickListener?.onItemContentClick(it, position)
//        }
//
//        Glide.with(mContext).load(mList[position].img_one).into(holder.avatar)
//
//        holder.name.text = mList[position].nick_one
//
//        if (mList[position].sex_one == 1) {
//            Glide.with(mContext).load(R.drawable.ic_male).into(holder.sex)
//        } else {
//            Glide.with(mContext).load(R.drawable.ic_female).into(holder.sex)
//        }
//
//        holder.content.text = mList[position].content_one
//
//        holder.time.text = TimeUtil.getCommonTime(mList[position].time_one)
//
//        when (size[position]) {
//            0 -> {
//                holder.child.visibility = View.GONE
//            }
//            1 -> {
//
//                Glide.with(mContext).load(mList[position].image_two).into(holder.childAvatar)
//                holder.childName.text = mList[position].nick_two
//
//                if (mList[position].sex_two == 1) {
//                    Glide.with(mContext).load(R.drawable.ic_male).into(holder.childSex)
//                } else {
//                    Glide.with(mContext).load(R.drawable.ic_female).into(holder.childSex)
//                }
//
//                holder.childContent.text = mList[position].content_two
//
//
//                holder.childTime.text = TimeUtil.getCommonTime(mList[position].time_two)
//
//                holder.childMore.visibility = View.GONE
//                holder.container.visibility = View.GONE
//            }
//            else -> {
//
//                Glide.with(mContext).load(mList[position].image_two).into(holder.childAvatar)
//                holder.childName.text = mList[position].nick_two
//
//                if (mList[position].sex_two == 1) {
//                    Glide.with(mContext).load(R.drawable.ic_male).into(holder.childSex)
//                } else {
//                    Glide.with(mContext).load(R.drawable.ic_female).into(holder.childSex)
//                }
//
//                holder.childContent.text = mList[position].content_two
//
//                holder.childTime.text = TimeUtil.getCommonTime(mList[position].time_two)
//
//                holder.childMore.text = "展开剩余${size[position]}条回复"
//
//
//
//            }
//        }
//
//        holder.childMore.setOnClickListener {
//            mOnMoreClickListener?.onMoreClick(it, position)
//        }
//
//    }
//
//    override fun getItemCount(): Int {
//        return mList.size
//    }

}






