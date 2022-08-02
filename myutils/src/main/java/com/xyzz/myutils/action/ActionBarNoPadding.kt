package com.xyzz.myutils.action

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.gyf.immersionbar.ImmersionBar
import com.xyzz.myutils.R
import kotlinx.android.synthetic.main.item_action_bar_no_padding_myutils.view.*

class ActionBarNoPadding @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defSty:Int=0):FrameLayout(context,attrs,defSty) {
    init {
        val typedArray=context.obtainStyledAttributes(attrs,R.styleable.ActionBar)
        val bright=typedArray.getBoolean(R.styleable.ActionBar_isBright,true)
        if (bright){
            LayoutInflater.from(context).inflate(R.layout.item_action_bar_no_padding_myutils,this,true)
        }else{
            LayoutInflater.from(context).inflate(R.layout.item_action_bar_no_padding_myutils,this,true)
        }
        this.setPadding(0,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,22f,resources.displayMetrics).toInt(),0,0)
        if (context is Activity){
            ImmersionBar.with(context)
                .statusBarDarkFont(bright)
                .init()
            goBack.setOnClickListener {
                context.finish()
            }
        }
        titleText.text=typedArray.getString(R.styleable.ActionBar_title)
        typedArray.recycle()
    }

    fun setTitle(text:CharSequence){
        titleText.text=text
    }
}