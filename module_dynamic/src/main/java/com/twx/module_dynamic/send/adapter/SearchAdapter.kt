package com.twx.module_dynamic.send.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.SearchData

/**
 * @author: Administrator
 * @date: 2021/12/16
 */
class SearchAdapter(private val mList: MutableList<SearchData>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>(),
    View.OnClickListener {

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
        val name: TextView = view.findViewById(R.id.tv_detail_address_search_name)
        val address: TextView = view.findViewById(R.id.tv_detail_address_search_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_address_search, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.name.text = mList[position].name
        if (mList[position].address == null && mList[position].address == "") {
            holder.address.visibility = View.GONE
        } else {
            holder.address.visibility = View.VISIBLE
            holder.address.text = mList[position].address
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}