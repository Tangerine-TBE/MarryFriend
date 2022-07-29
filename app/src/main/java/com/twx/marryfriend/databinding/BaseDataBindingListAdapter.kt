package com.twx.marryfriend.databinding

import androidx.recyclerview.widget.RecyclerView

abstract class BaseDataBindingListAdapter<V: BaseDataBindingView<D>,D>:RecyclerView.Adapter<BaseDataBindingViewHolder<V, D>>() {
    private val listData=ArrayList<D>()

    fun setData(list: List<D>?){
        listData.clear()
        listData.addAll(list?: emptyList())
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder<V, D>, position: Int) {
        holder.setData(listData[position])
    }
}