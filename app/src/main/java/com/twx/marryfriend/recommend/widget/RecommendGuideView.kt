package com.twx.marryfriend.recommend.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.SPStaticUtils
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.enumeration.HomeCardAction
import kotlinx.android.synthetic.main.item_recommend_guide.view.*

class RecommendGuideView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defSty:Int=0):FrameLayout(context,attributeSet,defSty) {
    companion object{
        private const val IS_USE_FIRST="is_first_use"
        fun notShowGuide(){
            SPStaticUtils.put(IS_USE_FIRST,true)
        }
        fun isShowGuide():Boolean{
            return !SPStaticUtils.getBoolean(IS_USE_FIRST,false)
        }
    }
    init {
        inflate(context,R.layout.item_recommend_guide,this)
    }
    private var currentType:HomeCardAction?=HomeCardAction.upSlide

    fun guideComplete(type:HomeCardAction){
        if (type==currentType){
            when(currentType){
                HomeCardAction.upSlide -> {
                    showGuide(HomeCardAction.leftSlide)
                }
                HomeCardAction.leftSlide -> {
                    showGuide(HomeCardAction.rightSlide)
                }
                HomeCardAction.rightSlide -> {
                    showGuide(HomeCardAction.clickFlower)
                }
                HomeCardAction.clickFlower -> {
                    currentType=null
                    notShowGuide()
                    this.visibility=View.GONE
                }
            }
        }else{
            showGuide(currentType?:return)
        }
    }
    fun showGuide(){
        showGuide(currentType?:return)
    }

    fun noticeDataChange(haveData:Boolean){
        if (haveData){
            if (!isShowGuide()){
                return
            }
            if (this.visibility!=View.VISIBLE){
                this.visibility=View.VISIBLE
            }
        }else{
            if (this.visibility!=View.GONE){
                this.visibility=View.GONE
            }
        }
    }

    private fun showGuide(type:HomeCardAction){
        if (!isShowGuide()){
            return
        }
        showView()
        currentType=type
        when(type){
            HomeCardAction.upSlide -> {
                actionName.text="上滑"
                actionName.setTextColor(Color.parseColor("#FFFF40CC"))
                actionDes1.text="可以查看更多资料"
                actionDes2.text="更多了解这位嘉宾"
                Glide.with(this).load(R.drawable.guide_recommend_top).into(contentImg)
            }
            HomeCardAction.leftSlide -> {
                actionName.text="左滑"
                actionName.setTextColor(Color.parseColor("#FF5840FF"))
                actionDes1.text="表示对TA不感兴趣"
                actionDes2.text="并且切换到下一位嘉宾"
                Glide.with(this).load(R.drawable.guide_recommend_left).into(contentImg)
            }
            HomeCardAction.rightSlide -> {
                actionName.text="右滑"
                actionName.setTextColor(Color.parseColor("#FF5840FF"))
                actionDes1.text="可以喜欢TA"
                actionDes2.text="遇到心仪的就不要错过啦"
                Glide.with(this).load(R.drawable.guide_recommend_right).into(contentImg)
            }
            HomeCardAction.clickFlower -> {
                actionName.text="送花"
                actionName.setTextColor(Color.parseColor("#FFFF4444"))
                actionDes1.text="表示超级喜欢"
                actionDes2.text="通知对方已喜欢，并在其首页置顶，"
                actionDes3.text="配对率提升2倍"
                contentImg.visibility=View.GONE
                Glide.with(this).load(R.drawable.guide_recommend_click).into(contentImg2)
            }
        }
    }

    private fun showView(){
        if(this.visibility!=View.VISIBLE)
            this.visibility=View.VISIBLE
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action==MotionEvent.ACTION_DOWN){
            this.visibility=View.GONE
        }
        return super.onTouchEvent(event)
    }
}