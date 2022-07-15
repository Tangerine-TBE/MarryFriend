package com.twx.module_dynamic.preview.image.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.MyTrendsList
import com.twx.module_dynamic.mine.adapter.MyDynamicAdapter

/**
 * @author: Administrator
 * @date: 2022/7/5
 */
class ImagePreviewAdapter(private val mList: List<String>) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>(), View.OnClickListener {

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
        val iv: PhotoView = view.findViewById(R.id.pv_image_preview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_image_preview, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        Glide.with(mContext).load(mList[position]).into(holder.iv)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}