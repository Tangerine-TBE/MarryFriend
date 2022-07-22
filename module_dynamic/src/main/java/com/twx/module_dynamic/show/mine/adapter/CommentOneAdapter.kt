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
import com.twx.module_dynamic.bean.CommentBean
import com.twx.module_dynamic.bean.CommentOneData
import com.twx.module_dynamic.bean.CommentOneList
import com.twx.module_dynamic.bean.CommentTwoList
import retrofit2.http.POST
import java.util.*

/**
 * @author: Administrator
 * @date: 2022/7/8
 */
class CommentOneAdapter(private val mList: MutableList<CommentBean>) :
    RecyclerView.Adapter<CommentOneAdapter.ViewHolder>(), View.OnClickListener,
    View.OnLongClickListener {

    private lateinit var mContext: Context

    private var mOnItemClickListener: OnItemClickListener? = null

    private var mOnItemLongClickListener: OnItemLongClickListener? = null


    interface OnItemClickListener {
        fun onItemClick(v: View?, positionOne: Int)

        fun onItemAvatarClick(v: View?, positionOne: Int)
        fun onItemContentClick(v: View?, positionOne: Int)
        fun onItemMoreClick(v: View?, positionOne: Int)

        fun onItemChildAvatarClick(v: View?, positionOne: Int)
        fun onItemChildContentClick(v: View?, positionOne: Int)

        fun onChildClick(positionOne: Int, two: Int)
        fun onChildAvatarClick(positionOne: Int, two: Int)
        fun onChildReplyClick(positionOne: Int, two: Int)
        fun onChildReplyAvatarClick(positionOne: Int, two: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(v: View?, positionOne: Int)

        fun onItemChildContentLongClick(v: View?, positionOne: Int)

        fun onChildContentLongClick(positionOne: Int, two: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.mOnItemLongClickListener = listener
    }

    override fun onClick(v: View?) {
        if (v != null) {
            mOnItemClickListener?.onItemClick(v, v.tag as Int)
        }
    }

    override fun onLongClick(v: View?): Boolean {
        return true
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val user: RelativeLayout = view.findViewById(R.id.rl_detail_comment_parent_user)
        val avatar: RoundedImageView = view.findViewById(R.id.iv_detail_comment_parent_avatar)
        val name: TextView = view.findViewById(R.id.tv_detail_comment_parent_name)
        val sex: ImageView = view.findViewById(R.id.iv_detail_comment_parent_sex)

        val info: TextView = view.findViewById(R.id.tv_detail_comment_parent_info)
        val content: TextView = view.findViewById(R.id.tv_detail_comment_parent_content)
        val time: TextView = view.findViewById(R.id.tv_detail_comment_parent_time)

        val child: LinearLayout = view.findViewById(R.id.ll_detail_comment_parent_child)
        val childInfo: LinearLayout = view.findViewById(R.id.ll_detail_comment_parent_child_name)
        val childAvatar: RoundedImageView =
            view.findViewById(R.id.iv_detail_comment_parent_child_avatar)
        val childName: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_name)
        val childSex: ImageView = view.findViewById(R.id.tv_detail_comment_parent_child_sex)
        val childContent: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_content)
        val childTime: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_time)

        val childMore: TextView = view.findViewById(R.id.tv_detail_comment_parent_child_more)
        val container: RecyclerView =
            view.findViewById(R.id.rv_detail_comment_parent_child_container)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_comment_parent, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position

        holder.container.layoutManager = LinearLayoutManager(mContext)
        var mFrontAdapter = CommentTwoAdapter(mList[position].twoList)
        holder.container.adapter = mFrontAdapter

        mFrontAdapter.setOnItemClickListener(object : CommentTwoAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, positionTwo: Int) {
                mOnItemClickListener?.onChildClick(position, positionTwo)
            }

            override fun onAvatarClick(v: View?, positionTwo: Int) {
                mOnItemClickListener?.onChildAvatarClick(position, positionTwo)
            }

            override fun onReplyAvatarClick(v: View?, positionTwo: Int) {
                mOnItemClickListener?.onChildReplyAvatarClick(position, positionTwo)
            }

            override fun onReplyClick(v: View?, positionTwo: Int) {
                mOnItemClickListener?.onChildReplyClick(position, positionTwo)
            }

        })

        mFrontAdapter.setOnItemLongClickListener(object :
            CommentTwoAdapter.OnItemLongClickListener {
            override fun onItemLongClick(v: View?, positionTwo: Int) {
                mOnItemLongClickListener?.onChildContentLongClick(position, positionTwo)
            }
        })

        holder.user.setOnClickListener {
            mOnItemClickListener?.onItemAvatarClick(it, position)
        }

        holder.content.setOnClickListener {
            mOnItemClickListener?.onItemContentClick(it, position)
        }

        holder.content.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                mOnItemLongClickListener?.onItemLongClick(v, position)
                return true
            }
        })

        holder.childMore.setOnClickListener {
            mOnItemClickListener?.onItemMoreClick(it, position)
        }

        holder.childAvatar.setOnClickListener {
            mOnItemClickListener?.onItemChildAvatarClick(it, position)
        }

        holder.childInfo.setOnClickListener {
            mOnItemClickListener?.onItemChildAvatarClick(it, position)
        }

        holder.childContent.setOnClickListener {
            mOnItemClickListener?.onItemChildContentClick(it, position)
        }

        holder.childContent.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                mOnItemLongClickListener?.onItemChildContentLongClick(v, position)
                return true
            }
        })


        Glide.with(mContext).load(mList[position].list.img_one).into(holder.avatar)

        holder.name.text = mList[position].list.nick_one

        if (mList[position].list.sex_one == 1) {
            Glide.with(mContext).load(R.drawable.ic_male).into(holder.sex)
        } else {
            Glide.with(mContext).load(R.drawable.ic_female).into(holder.sex)
        }

        holder.content.text = mList[position].list.content_one

        holder.time.text = TimeUtil.getCommonTime(mList[position].list.time_one)


        when (mList[position].all) {
            0 -> {
                holder.child.visibility = View.GONE
            }
            1 -> {

                holder.child.visibility = View.VISIBLE

                Glide.with(mContext).load(mList[position].list.image_two).into(holder.childAvatar)
                holder.childName.text = mList[position].list.nick_two

                if (mList[position].list.sex_two == 1) {
                    Glide.with(mContext).load(R.drawable.ic_male).into(holder.childSex)
                } else {
                    Glide.with(mContext).load(R.drawable.ic_female).into(holder.childSex)
                }

                holder.childContent.text = mList[position].list.content_two


                if (mList[position].list.time_two != null) {
                    holder.childTime.text = TimeUtil.getCommonTime(mList[position].list.time_two)
                } else {
                    holder.childTime.visibility = View.GONE
                }


                holder.childMore.visibility = View.GONE
                holder.container.visibility = View.GONE

                if (mList[position].total == 0) {
                    holder.childMore.visibility = View.GONE
                }

            }
            else -> {

                holder.child.visibility = View.VISIBLE

                Glide.with(mContext).load(mList[position].list.image_two).into(holder.childAvatar)
                holder.childName.text = mList[position].list.nick_two

                if (mList[position].list.sex_two == 1) {
                    Glide.with(mContext).load(R.drawable.ic_male).into(holder.childSex)
                } else {
                    Glide.with(mContext).load(R.drawable.ic_female).into(holder.childSex)
                }

                holder.childContent.text = mList[position].list.content_two

                holder.childTime.text = TimeUtil.getCommonTime(mList[position].list.time_two)

                if (mList[position].total == 0) {
                    holder.childMore.visibility = View.GONE
                } else {
                    holder.childMore.visibility = View.VISIBLE
                    holder.childMore.text = "展开剩余${mList[position].total - 1}条回复"
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

}






