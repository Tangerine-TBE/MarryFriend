package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.material.chip.Chip
import com.twx.marryfriend.R
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.dialog_multiple_secondary_options.*

abstract class MultipleSecondaryOptionsDialog<T,I>(context: Context, private val listData:List<Pair<T,List<I>>>, val result:((List<Pair<T,I>>)->Unit)?=null): Dialog(context) {

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_multiple_secondary_options)
    }
    private val resultList by lazy {
        ArrayList<Pair<T,I>>()
    }
    var maxContent=Int.MAX_VALUE
    private var currentHead:Pair<T,List<I>>?=null
    private var currentItem:I?=null

    fun setTitle(title:String):MultipleSecondaryOptionsDialog<T,I>{
        titleView.text=title
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstOption.data=listData.map { getFirstText(it.first) }
        secondOption.data= listData.firstOrNull()?.second?.let {
            it.map { getSecondText(it) }
        }
        firstOption.init()
        secondOption.init()

        clickAdd.setOnClickListener { view ->
            val item=getCurrentChoice()?:return@setOnClickListener toast(context,"异常")
            if (resultList.find {
                    item.first==it.first&&item.second==it.second
                }!=null){
                return@setOnClickListener toast(context,"已经存在请勿重复添加")
            }
            if (resultList.size>=maxContent){
                return@setOnClickListener toast(context,"最多添加${maxContent}个")
            }
            resultList.add(item)
            val itemView=LayoutInflater.from(view.context).inflate(R.layout.item_dailog_multiple_secondary_options_chip,chipGroup,false)
            itemView.findViewById<TextView>(R.id.chipText).text="${getFirstText(item.first)}·${getSecondText(item.second)}"
            itemView.tag = item
            if (chipGroup.childCount>0){
                chipGroup.addView(itemView,chipGroup.childCount-1)
            }else{
                chipGroup.addView(itemView)
            }
            itemView.findViewById<View>(R.id.chipClose).setOnClickListener {
                chipGroup.removeView(itemView)
                resultList.remove(item)
            }
        }
        initListener()
        firstOption.selectedItemPosition=0
        secondOption.selectedItemPosition=0
    }

    private fun initListener(){
        firstOption.setOnItemSelectedListener { picker, data, position ->
            currentItem=null
            secondOption.data=listData[position].second.map { getSecondText(it) }
            secondOption.selectedItemPosition=0
            currentHead=listData[position]
        }
        secondOption.setOnItemSelectedListener { picker, data, position ->
            currentItem= currentHead?.second?.get(position) ?:return@setOnItemSelectedListener
        }
        submitBtn.setOnClickListener {
            result?.invoke(resultList)
            dismiss()
        }
        skipBtn.setOnClickListener {
            dismiss()
        }
        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun getCurrentChoice():Pair<T,I>?{
        if (currentHead==null){
            currentHead=listData.firstOrNull()
        }
        if (currentItem==null){
            currentItem=listData.find {
                it==currentHead
            }?.second?.firstOrNull()
        }
        return Pair(currentHead?.first?:return null,currentItem?:return null)
    }

    private fun WheelPicker.init(){
        // 是否为循环状态
        this.isCyclic = false
        // 当前选中的数据项文本颜色
        this.selectedItemTextColor = Color.parseColor("#FF4444")
        // 数据项文本颜色
        this.itemTextColor = Color.parseColor("#9A9A9A")
        // 滚轮选择器数据项之间间距
        this.itemSpace = ConvertUtils.dp2px(40F)
        // 是否有指示器
        this.setIndicator(true)
        // 滚轮选择器指示器颜色，16位颜色值
        this.indicatorColor = Color.parseColor("#FFF5F5")
        // 滚轮选择器是否显示幕布
        this.setCurtain(true)
        // 滚轮选择器是否有空气感
        this.setAtmospheric(true)
        // 滚轮选择器是否开启卷曲效果
        this.isCurved = true
        // 设置滚轮选择器数据项的对齐方式
        this.itemAlign = WheelPicker.ALIGN_CENTER
    }

    abstract fun getFirstText(t:T):String

    abstract fun getSecondText(i: I):String
}