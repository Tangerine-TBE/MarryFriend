package com.twx.marryfriend.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.forEachIndexed
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.search.SearchResultItem
import com.xyzz.myutils.show.toast

class SearchResultAdapter:RecyclerView.Adapter<BaseViewHolder>() {
    private val listData=ArrayList<SearchResultItem>()
    var chatAction:((SearchResultItem)->Unit)?=null
    var likeAction:((SearchResultItem,View)->Unit)?=null
    var itemAction:((SearchResultItem)->Unit)?=null

    fun setData(list: List<SearchResultItem>){
//        R.layout.item_load_complete
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addAllData(list: List<SearchResultItem>){
//        R.layout.item_load_complete
        listData.addAll(list)
        notifyItemRangeInserted(listData.size-list.size,list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_search_result,parent,false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=listData[position]
        holder.setText(R.id.searchItemName,item.nick?:"")
        Glide.with(holder.itemView)
            .load(item.getHeadImage())
            .placeholder(UserInfo.getReversedDefHeadImage())
            .into(holder.getView(R.id.searchItemHead))
        if (!item.isRealName()&&!item.isRealImage()){
            holder.getView<View>(R.id.searchItemRealInfo).visibility=View.GONE
        }else{
            holder.getView<View>(R.id.searchItemRealInfo).visibility=View.VISIBLE
        }
        if (item.isRealName()){
            holder.getView<View>(R.id.searchItemRealName).visibility=View.VISIBLE
        }else{
            holder.getView<View>(R.id.searchItemRealName).visibility=View.GONE
        }
        if (item.isRealImage()){
            holder.getView<View>(R.id.searchItemRealImage).visibility=View.VISIBLE
        }else{
            holder.getView<View>(R.id.searchItemRealImage).visibility=View.GONE
        }
        holder.getView<View>(R.id.searchItemVip).visibility=if (item.isVip()) View.VISIBLE else View.GONE
        holder.getView<TextView>(R.id.searchItemPhotoCount).text="${item.img_count?:0}张照片"
        holder.getView<TextView>(R.id.searchItemDynamicCount).text="${item.ted_count?:0}条动态"
//        holder.getView<TextView>(R.id.searchItemViewpointCount).text="${item.ted_count}条观点"

        val searchItemChipGroup=holder.getView<ChipGroup>(R.id.searchItemChipGroup)
        item.getLabels().also {
            if(it.isEmpty()){
                searchItemChipGroup.visibility=View.GONE
            }else{
                searchItemChipGroup.visibility=View.VISIBLE
            }
            searchItemChipGroup.forEachIndexed { index, view ->
                if (index<it.size){
                    view.visibility=View.VISIBLE
                }else{
                    view.visibility=View.GONE
                }
            }
        }.forEachIndexed { index, label ->
            val itemChipView=if (searchItemChipGroup.childCount>index){
                searchItemChipGroup.getChildAt(index)
            }else{
                LayoutInflater.from(searchItemChipGroup.context).inflate(R.layout.item_search_result_chip,searchItemChipGroup,false).also {
                    searchItemChipGroup.addView(it)
                }
            }
            itemChipView.findViewById<ImageView>(R.id.itemSearchChipIcon).apply {
                Glide.with(this).load(label.icon).into(this)
            }
            itemChipView.findViewById<TextView>(R.id.itemSearchChipLabel).text=label.label
        }

        holder.getView<TextView>(R.id.searchItemText).also {
            if (item.introduce_self.isNullOrBlank()){
                it.visibility=View.GONE
            }else{
                it.visibility=View.VISIBLE
            }
        }.text=item.introduce_self
        holder.getView<View>(R.id.searchItemChat).setOnClickListener {
            chatAction?.invoke(item)
        }
        holder.getView<View>(R.id.searchItemLike).also {
            it.isSelected=item.isLike()
        }.setOnClickListener {
            if (it.isSelected){
                toast(it.context,"已经喜欢过对方啦")
            }else {
                likeAction?.invoke(item, it)
            }
        }
        holder.itemView.setOnClickListener {
            itemAction?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}