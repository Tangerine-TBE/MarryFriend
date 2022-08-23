package com.twx.marryfriend.message.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.utils.emoji.EmojiUtils

class ChatEmojiAdapter:RecyclerView.Adapter<BaseViewHolder>() {
    private val listData by lazy {
        EmojiUtils.getEmojiList().map {
            //将当前 code 转换为 16 进制数
            val hex = it.toInt(16)
            //将当前 16 进制数转换成字符数组
            val chars = Character.toChars(hex)
            //将当前字符数组转换成 TextView 可加载的 String 字符串
            String(chars)
        }
    }
    var itemViewClick:((String)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_emoji,parent,false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=listData[position]
        holder.setText(R.id.emojiText,item)
        holder.itemView.setOnClickListener {
            itemViewClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}