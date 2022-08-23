package com.twx.marryfriend.mutual

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.twx.marryfriend.bean.message.mutual.MutualLikeData
import com.twx.marryfriend.databinding.ItemMutualLikeBinding

class ItemMutualLikeView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?=null, defSty:Int=0):
    FrameLayout(context,attributeSet,defSty) {
    //    init {
//        layoutParams= LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
//    }
//
    private val itemView by lazy {
        DataBindingUtil.bind<ItemMutualLikeBinding>(this)
    }

    var mutualLikeData: MutualLikeData?=null


    fun setData(mutualLikeData: MutualLikeData){
        itemView?.mutualLikeData=mutualLikeData
        this.mutualLikeData=mutualLikeData
//        removeAllViews()
//        addView(itemView.root)
    }
}