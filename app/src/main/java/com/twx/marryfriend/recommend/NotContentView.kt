package com.twx.marryfriend.recommend

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.item_recommend_not_content.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotContentView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defSty:Int=0):FrameLayout(context,attributeSet,defSty) {
    init {
        inflate(context, R.layout.item_recommend_not_content,this)
    }

    fun refreshView(scope: CoroutineScope){

        scope.launch {
            UserInfo.getNextNotFillIn2(context) { permission, pair ->
                if (pair!=null) {
                    upImageTip.setImageResource(pair.first)
                    upUserInfo.setOnClickListener {
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
                    upUserInfo.visibility= View.GONE
                }
            }
        }
    }
}