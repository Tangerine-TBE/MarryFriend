package com.twx.module_dynamic.emoji

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.emoji.widget.EmojiTextView
import androidx.recyclerview.widget.RecyclerView
import com.twx.module_dynamic.R

/**
 * @author: Administrator
 * @date: 2022/7/13
 */
class EmojiDetailAdapter(private val mList: MutableList<String>,
) :
    RecyclerView.Adapter<EmojiDetailAdapter.ViewHolder>(), View.OnClickListener {

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
        val emoji: EmojiTextView = view.findViewById(R.id.emoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_detail_emoji, parent, false)
        mContext = parent.context
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.emoji.text =getCompatEmojiString(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    //获取可兼容的 emoji 字符串
    private fun getCompatEmojiString(code: String): CharSequence? {
        //将当前 code 转换为 16 进制数
        val hex = code.toInt(16)
        //将当前 16 进制数转换成字符数组
        val chars = Character.toChars(hex)
        //将当前字符数组转换成 TextView 可加载的 String 字符串
        return String(chars)
    }

}