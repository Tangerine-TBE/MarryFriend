package com.twx.marryfriend.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.twx.marryfriend.R
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.search.SearchResultItem

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_search_result,parent,false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item=listData[position]
        holder.setText(R.id.searchItemName,item.nick?:"")
        holder.setImage(R.id.searchItemHead,item.image_url)
        holder.getView<View>(R.id.searchItemRealName).visibility=if (item.isRealName()) View.VISIBLE else View.GONE
        holder.getView<View>(R.id.searchItemVip).visibility=if (item.isVip()) View.VISIBLE else View.GONE
        holder.getView<TextView>(R.id.searchItemPhotoCount).text="${item.img_count}张照片"
        holder.getView<TextView>(R.id.searchItemDynamicCount).text="${item.ted_count?:0}条动态"
//        holder.getView<TextView>(R.id.searchItemViewpointCount).text="${item.ted_count}条观点"

        val searchItemChipGroup=holder.getView<ChipGroup>(R.id.searchItemChipGroup)
        item.getLabels().forEach {
            val itemChipView=LayoutInflater.from(searchItemChipGroup.context).inflate(R.layout.item_search_result_chip,searchItemChipGroup,false)
            itemChipView.findViewById<ImageView>(R.id.itemSearchChipIcon).apply {
                Glide.with(this).load(it.icon).into(this)
            }
            itemChipView.findViewById<TextView>(R.id.itemSearchChipLabel).text=it.label
            searchItemChipGroup.addView(itemChipView)
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
            likeAction?.invoke(item,it)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}