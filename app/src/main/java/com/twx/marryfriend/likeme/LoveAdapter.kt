package com.twx.marryfriend.likeme

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.likeme.LikeMeItemBean
import com.twx.marryfriend.bean.recommend.Sex
import com.xyzz.myutils.display.DateDisplayManager
import com.xyzz.myutils.rsBlur
import com.xyzz.myutils.show.iLog

class LoveAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private val listData = ArrayList<LikeMeItemBean>()
    var itemAction: ((LikeMeItemBean) -> Unit)? = null

    fun setData(list: List<LikeMeItemBean>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addAllData(list: List<LikeMeItemBean>) {
        listData.addAll(list)
        notifyItemRangeInserted(listData.size - list.size, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_love, parent, false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item = listData[position]
//        holder.setImage(R.id.itemLoveImg,item.image_url,!UserInfo.isVip())
        holder.getView<ImageView>(R.id.itemLoveImg).also {
            if (!item.image_url.isNullOrEmpty()) {
                val glide = Glide.with(it)
                    .load(item.image_url)
                    .placeholder(Sex.toSex(item.user_sex).smallHead)
                    .error(Sex.toSex(item.user_sex).smallHead)
                if (UserInfo.isVip()) {
                    glide.into(it)
                } else {
                    glide.transform(object : CenterCrop() {
                        override fun transform(
                            pool: BitmapPool,
                            toTransform: Bitmap,
                            outWidth: Int,
                            outHeight: Int,
                        ): Bitmap {
                            return toTransform.rsBlur(it.context, 20)
                        }
                    }).into(it)
                }
            } else {
                iLog("photoPath为空")
            }
        }
        holder.setText(R.id.itemLoveOnLineTime,
            DateDisplayManager.getLoveDateImpl(item.online_time ?: throw Exception("时间格式异常"),
                "yyyy-MM-dd HH:mm:ss").toText())
        holder.setText(R.id.itemLovePlace, item.work_city_str ?: "")
        holder.setText(R.id.itemLoveAge, item.age.toString())
        holder.setText(R.id.itemLoveOccupation, item.occupation_str ?: "")
        holder.itemView.setOnClickListener {
            itemAction?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }
}