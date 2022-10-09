package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.forEach
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.xyzz.myutils.show.toast
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
            this.visibility=View.GONE
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
            if (imageList.size<=i){
                if (life_images_recycler_view.childCount>i){
                    val view=life_images_recycler_view.get(i)
                    view.visibility=View.GONE
                    if (view is ImageView){
                        view.setImageBitmap(null)
                    }
                }
            }else{
                val imageItemView=
                    if (life_images_recycler_view.childCount>i){
                        life_images_recycler_view.get(i)
                    }else{
                        LayoutInflater.from(context).inflate(R.layout.item_life_image,life_images_recycler_view,false).also {
                            life_images_recycler_view.addView(it)
                        }
                    }
                if (imageItemView.visibility!=View.VISIBLE){
                    imageItemView.visibility=View.VISIBLE
                }
                val item=this.imageList[i]
                imageItemView.findViewById<TextView>(R.id.life_title).setText(item.title)
                imageItemView.findViewById<TextView>(R.id.life_des).setText(item.des)
                imageItemView.findViewById<ImageView>(R.id.life_image).also {
                    Glide
                        .with(it)
                        .load(item.imgUrl)
                        .placeholder(R.drawable.ic_big_default_pic)
                        .error(R.drawable.ic_big_default_pic)
                        .into(it)
                }
            }
        }
    }


    fun refreshView() {
        UserInfo.getNextNotFillIn(context) { permission, pair ->
            if (pair!=null) {
                upLifeImg.setImageResource(pair.first)
                upLoadLife.setOnClickListener {
                    if(!permission.isNullOrEmpty()){
                        pair.second?:return@setOnClickListener
                        XXPermissions.with(context)
                            .permission(permission)
                            .request(object : OnPermissionCallback {
                                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                                    context?.startActivity(pair.second?:return)
                                }
                                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                                    super.onDenied(permissions, never)
                                    context.toast(context,"请授予应用相应权限")
                                }
                            })
                    }else{
                        context?.startActivity(pair.second?:return@setOnClickListener)
                    }
                }
            }else{
                upLoadLife.visibility= View.GONE
            }
        }
    }
}