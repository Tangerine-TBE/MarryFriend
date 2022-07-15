package com.twx.marryfriend.love

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.likeme.LikeMeItemBean
import com.xyzz.myutils.display.DateDisplayManager

class LoveAdapter:RecyclerView.Adapter<BaseViewHolder>() {
    private val listData=ArrayList<LikeMeItemBean>()

    fun setData(list: List<LikeMeItemBean>){
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addAllData(list: List<LikeMeItemBean>){
        listData.addAll(list)
        notifyItemRangeInserted(listData.size-list.size,list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_love,parent,false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=listData[position]

        holder.setImage(R.id.itemLoveImg,item.image_url,!UserInfo.isVip())
        holder.setText(R.id.itemLoveOnLineTime,
            DateDisplayManager.getLoveDateImpl(item.online_time?:throw Exception("时间格式异常"),"yyyy-MM-dd HH:mm:ss").toText())
        holder.setText(R.id.itemLovePlace,item.work_city_str?:"")
        holder.setText(R.id.itemLoveAge, item.age.toString())
        holder.setText(R.id.itemLoveOccupation,item.occupation_str?:"")
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}