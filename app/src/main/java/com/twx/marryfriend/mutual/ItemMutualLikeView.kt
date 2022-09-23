package com.twx.marryfriend.mutual

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.message.mutual.MutualLikeData
import com.twx.marryfriend.databinding.ItemMutualLikeBinding
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ImChatActivity
import kotlinx.android.synthetic.main.item_mutual_like.view.*

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

        if (mutualLikeData.user_sex==1){
            Glide.with(itemMutualLikeImg).load(mutualLikeData.image_url).placeholder(R.mipmap.ic_item_def_male).error(R.mipmap.ic_item_def_male).into(itemMutualLikeImg)
            Glide.with(sexIcon).load(R.mipmap.ic_mutual_male).into(sexIcon)
        }else{
            Glide.with(itemMutualLikeImg).load(mutualLikeData.image_url).placeholder(R.mipmap.ic_item_def_woman).error(R.mipmap.ic_item_def_woman).into(itemMutualLikeImg)
            Glide.with(sexIcon).load(R.mipmap.ic_mutual_women).into(sexIcon)
        }
        mutualChat.setOnClickListener {
            it.context.startActivity(ImChatActivity.getIntent(
                it.context,
                mutualLikeData.user_id?.toString()?:return@setOnClickListener
            ))
        }
        this.setOnClickListener {
            it.context.startActivity(FriendInfoActivity.getIntent(it.context,mutualLikeData.user_id?:return@setOnClickListener))
        }
//        removeAllViews()
//        addView(itemView.root)
    }
}