package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.net.utils.BuildConfig.DEBUG
import kotlinx.android.synthetic.main.item_recommend_life_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LifeView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyle:Int=0):FrameLayout(context,attrs,defStyle) {
    class LifeImage(val imgUrl:String,val title:String,val des:String)
    private val imageList by lazy {
        ArrayList<LifeImage>()
    }

    init {
        inflate(context,R.layout.item_recommend_life_view,this)
    }

    fun setImageData(imageList: List<LifeImage>){
        if (imageList.isEmpty()){
            life_images_recycler_view.visibility=View.GONE
            life_images_recycler_view.forEach {
                if (it is ImageView){
                    it.setImageBitmap(null)
                }
            }
        }else{
            life_images_recycler_view.visibility=View.VISIBLE
        }
        life_count.setText("${imageList.size}张生活照")
        this.imageList.clear()
        this.imageList.addAll(imageList)
        for (i in 0 until 5){
            if (i>=imageList.size){
                for (j in i until life_images_recycler_view.childCount){
                    life_images_recycler_view.get(i).visibility=View.GONE
                }
                break
            }
            val imageItemView=
                if (life_images_recycler_view.childCount>i){
                    life_images_recycler_view.get(i)
                }else{
                    LayoutInflater.from(context).inflate(R.layout.item_life_image,life_images_recycler_view,false).also {
                        life_images_recycler_view.addView(it)
                    }
                }
            if (imageItemView.visibility!=View.VISIBLE){
                imageItemView.visibility=View.GONE
            }
            val item=this.imageList[i]
            imageItemView.findViewById<TextView>(R.id.life_title).setText(item.title)
            imageItemView.findViewById<TextView>(R.id.life_des).setText(item.des)
            imageItemView.findViewById<ImageView>(R.id.life_image).also {
                Glide
                    .with(it)
                    .load(item.imgUrl)
                    .placeholder(R.drawable.ic_big_default_pic)
                    .into(it)
            }
        }
    }


    fun refreshView(scope: CoroutineScope){
        scope.launch {
            UserInfo.getNextNotFillIn(context,scope).also { pair ->
                if (pair!=null) {
                    upLifeImg.setImageResource(pair.first)
                    upLoadLife.setOnClickListener {
                        context?.startActivity(pair.second?:return@setOnClickListener)
                    }
                }else{
                    upLoadLife.visibility= View.GONE
                }
            }
        }
    }
}