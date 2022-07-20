package com.twx.marryfriend.ilove

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.ilike.ILikeItemBean

class LikeAdapter(private val isChat:Boolean=false):RecyclerView.Adapter<BaseViewHolder>() {
    private val listData = ArrayList<ILikeItemBean>()
    var chatAction:((ILikeItemBean)->Unit)?=null
    var sendFlowerAction:((ILikeItemBean)->Unit)?=null
    var itemAction:((ILikeItemBean)->Unit)?=null

    fun setData(list: List<ILikeItemBean>){
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_ilike,parent,false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=listData[position]
        holder.setImage(R.id.iLoveHead,item.image_url)
        holder.setText(R.id.iLoveNickName,item.nick?:"")
        holder.setText(R.id.iLikeAge,"${item.age}岁")
        holder.setText(R.id.iLikeHeight,"${item.height}cm")
        holder.setText(R.id.iLikeOccupation,item.occupation_str?:"")
        val switcherView=holder.getView<ViewSwitcher>(R.id.iLikeViewSwitcher)
        val iLikeSendFlowers=holder.getView<View>(R.id.iLikeSendFlowers)
        val iLikeChat=holder.getView<View>(R.id.iLikeChat)
        if ((switcherView.currentView==iLikeChat).xor(isChat)){
            switcherView.showNext()
        }
        iLikeSendFlowers.setOnClickListener {
            sendFlowerAction?.invoke(item)
        }
        iLikeChat.setOnClickListener {
            chatAction?.invoke(item)
        }
        holder.itemView.setOnClickListener {
            itemAction?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}