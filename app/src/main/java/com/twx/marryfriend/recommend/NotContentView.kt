package com.twx.marryfriend.recommend

import android.content.Context
import android.util.AttributeSet
import android.view.View
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
        scope.launch {
            UserInfo.getNextNotFillIn2(context,scope).also { pair ->
                if (pair!=null) {
                    upImageTip.setImageResource(pair.first)
                    upUserInfo.setOnClickListener {
                        context?.startActivity(pair.second?:return@setOnClickListener)
                    }
                }else{
                    upUserInfo.visibility= View.GONE
                }
            }
        }
    }
}