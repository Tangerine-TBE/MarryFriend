package com.twx.marryfriend.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xyzz.myutils.iLog

class BaseViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
    private val context: Context by lazy { itemView.context }
//    private val views= SparseArray<View>()

    fun <V : View> getView(viewId:Int):V{
        return itemView.findViewById<V>(viewId)
    }
//    val options: RequestOptions by lazy { RequestOptions() }

    fun setText(viewId: Int,text:Int):TextView{
        return getView<TextView>(viewId).also { it.setText(text) }
    }

    fun setText(viewId: Int,text:String):TextView{
        return getView<TextView>(viewId).also { it.text=text }
    }

    fun setImage(viewId: Int,resId:Int):ImageView{
        return getView<ImageView>(viewId).also{
            Glide.with(context)
                .load(resId)
//            .apply(options)
                .into(it)
        }
    }
    fun setImage(viewId: Int,drawable: Drawable):ImageView{
        return getView<ImageView>(viewId).also {
            Glide.with(context)
                .load(drawable)
                .into(it)
        }
    }
    fun setImage(viewId: Int,bitmap: Bitmap):ImageView{
        return getView<ImageView>(viewId).also {
            Glide.with(context)
                .load(bitmap)
                .into(it)
        }
    }
    fun setImage(viewId: Int,photoPath:String?):ImageView{
        return getView<ImageView>(viewId).also {
            if (!photoPath.isNullOrEmpty()) {
                Glide.with(context)
                    .load(photoPath)
                    .into(it)
            }else{
                iLog("photoPath为空")
            }
        }
    }
}