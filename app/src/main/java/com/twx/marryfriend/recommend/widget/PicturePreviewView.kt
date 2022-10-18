package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.Group
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.net.utils.BuildConfig.DEBUG

class PicturePreviewView @JvmOverloads constructor(context: Context,attrs: AttributeSet?=null,defStyle:Int=0):FrameLayout(context,attrs,defStyle) {
//    private val pictureData=ArrayList<String>()
    private val previewView1 by lazy {
        LayoutInflater.from(context).inflate(R.layout.item_picture_preview_1_item,this,false) as ViewGroup
    }
    private val previewView6 by lazy {
        LayoutInflater.from(context).inflate(R.layout.item_picture_preview_6_item,this,false) as ViewGroup
    }
    private val previewView5 by lazy {
        LayoutInflater.from(context).inflate(R.layout.item_picture_preview_5_item,this,false) as ViewGroup
    }
    private val preImageViews by lazy { ArrayList<ImageView>() }

    init {
        if(DEBUG){
            addView(previewView6)
        }
    }

    fun setImageData(imageList: List<String>,videoList:List<String>?=null){
        clearImage()
        removeAllViews()
        when{
            !videoList.isNullOrEmpty()&&imageList.size>=4->{
                addView(previewView5)
                val list=ArrayList(imageList)
                list.add(0,videoList.first())
                previewView5.children.filterIsInstance<ImageView>().forEachIndexed { index, t ->
                    if (index<list.size) {
                        preImageViews.add(t)
                        Glide.with(t).load(list[index]).placeholder(R.drawable.ic_small_default_pic).into(t)
                    }else{
                        return@forEachIndexed
                    }
                }
            }
            imageList.size==1->{
                addView(previewView1)
                findViewById<ImageView>(R.id.picture1).also {
                    preImageViews.add(it)
                    Glide.with(it).load(imageList[0]).placeholder(R.drawable.ic_small_default_pic).into(it)
                }
            }
            imageList.size<=3->{
                addView(previewView6)
                findViewById<Group>(R.id.img456Group).visibility= View.GONE
                previewView6.children.filterIsInstance<ImageView>().forEachIndexed { index, t ->
                    if (index<imageList.size) {
                        preImageViews.add(t)
                        Glide.with(t).load(imageList[index]).placeholder(R.drawable.ic_small_default_pic).into(t)
                    }else{
                        return@forEachIndexed
                    }
                }
            }
            imageList.size==4->{
                addView(previewView6)
                findViewById<Group>(R.id.img456Group).visibility= View.VISIBLE
                findViewById<ImageView>(R.id.picture1).also {
                    preImageViews.add(it)
                    Glide.with(it).load(imageList[0]).placeholder(R.drawable.ic_small_default_pic).into(it)
                }
                findViewById<ImageView>(R.id.picture2).also {
                    preImageViews.add(it)
                    Glide.with(it).load(imageList[1]).placeholder(R.drawable.ic_small_default_pic).into(it)
                }
                findViewById<ImageView>(R.id.picture4).also {
                    preImageViews.add(it)
                    Glide.with(it).load(imageList[2]).placeholder(R.drawable.ic_small_default_pic).into(it)
                }
                findViewById<ImageView>(R.id.picture5).also {
                    preImageViews.add(it)
                    Glide.with(it).load(imageList[3]).placeholder(R.drawable.ic_small_default_pic).into(it)
                }
            }
            imageList.size>4->{
                addView(previewView6)
                findViewById<Group>(R.id.img456Group).visibility= View.VISIBLE
                previewView6.children.filterIsInstance<ImageView>().forEachIndexed { index, t ->
                    if (index<imageList.size){
                        preImageViews.add(t)
                        Glide.with(t).load(imageList[index]).placeholder(R.drawable.ic_small_default_pic).into(t)
                    }else{
                        t.setImageBitmap(null)
                    }
                }
            }
        }
    }

    fun clearImage(){
        preImageViews.forEach {
            it.setImageBitmap(null)
        }
        preImageViews.clear()
    }
}