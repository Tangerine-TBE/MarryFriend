package com.twx.marryfriend.recommend

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import kotlinx.android.synthetic.main.item_recommend_not_content.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotContentView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defSty:Int=0):FrameLayout(context,attributeSet,defSty) {
    init {
        inflate(context, R.layout.item_recommend_not_content,this)
    }

    fun refreshView(scope: CoroutineScope){
        if(!UserInfo.isHaveLifePhoto()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_load_life)
            upUserInfo.setOnClickListener {
                context?.startActivity(IntentManager.getUpLifeIntent(context)?:return@setOnClickListener)
            }
            return
        }
        if(!UserInfo.isRealName()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_real_name)
            upUserInfo.setOnClickListener {
                context?.startActivity(IntentManager.getUpRealNameIntent(context)?:return@setOnClickListener)
            }
            return
        }
        if(!UserInfo.isHaveHeadImage()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_head_image)
            upUserInfo.setOnClickListener {
                context?.startActivity(IntentManager.getUpHeadImageIntent(context)?:return@setOnClickListener)
            }
            return
        }
        if(!UserInfo.isFillInHobby()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_fill_in_hobby)
            upUserInfo.setOnClickListener {
                context?.startActivity(IntentManager.getUpFillInHobbyIntent(context)?:return@setOnClickListener)
            }
            return
        }
        if(!UserInfo.isFillInGreet()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_fill_in_greet)
            upUserInfo.setOnClickListener {
                context?.startActivity(IntentManager.getUpFillInGreetIntent(context)?:return@setOnClickListener)
            }
            return
        }
        if(!UserInfo.isFillInIntroduce()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_fill_in_introduce)
            upUserInfo.setOnClickListener {
                context?.startActivity(IntentManager.getUpFillInIntroduceIntent(context)?:return@setOnClickListener)
            }
            return
        }
        if(!UserInfo.isFillInVoice()){
            upImageTip.setImageResource(R.mipmap.ic_item_up_fill_in_voice)
            upUserInfo.setOnClickListener {
                scope.launch {
                    context?.startActivity(IntentManager.getUpFillInVoiceIntent(context)?:return@launch)
                }
            }
            return
        }
    }
}