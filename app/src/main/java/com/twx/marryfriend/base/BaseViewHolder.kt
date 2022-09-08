package com.twx.marryfriend.base

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.rsBlur


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

    fun setImage(viewId: Int,resId:Int,isRs:Boolean=false):ImageView{
        return getView<ImageView>(viewId).also{
            val glide=Glide.with(context)
                .load(resId)
            if (isRs){
                glide.transform(object : CenterCrop(){
                    override fun transform(
                        pool: BitmapPool,
                        toTransform: Bitmap,
                        outWidth: Int,
                        outHeight: Int
                    ): Bitmap {
                        return toTransform.rsBlur(context,20)
                    }
                }).into(it)
            }else {
                glide.into(it)
            }
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

    fun setImage(viewId: Int,strPath: String,def:Int):ImageView{
        return getView<ImageView>(viewId).also {
            Glide.with(context)
                .load(strPath)
                .error(def)
                .placeholder(def)
                .into(it)
        }
    }

    fun setImage(viewId: Int,photoPath:String?,isRs:Boolean=false,perDefImg:Int?=null):ImageView{
        return getView<ImageView>(viewId).also {
            if (!photoPath.isNullOrEmpty()) {

                val glide=Glide.with(context)
                    .load(photoPath)

                    if (isRs){
                        glide.transform(object : CenterCrop(){
                            override fun transform(
                                pool: BitmapPool,
                                toTransform: Bitmap,
                                outWidth: Int,
                                outHeight: Int
                            ): Bitmap {
                                return toTransform.rsBlur(context,20)
                            }
                        }).into(it)
                    }else {
                        glide.into(it)
                    }
            }else{
                iLog("photoPath为空")
            }
        }
    }


}