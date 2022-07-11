package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.ConvertUtils
import com.twx.marryfriend.R
import kotlinx.android.synthetic.main.dialog_secondary_options.*

abstract class SecondaryOptionsDialog<T,I>(context: Context, private val listData:List<Pair<T,List<I>>>, val result:((T,I)->Unit)?=null): Dialog(context) {

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_secondary_options)
    }
    private var currentHead:Pair<T,List<I>>?=null
    private var currentItem:I?=null

    fun setTitle(title:String):SecondaryOptionsDialog<T,I>{
        titleView.text=title
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstOption.data=listData.map { getFirstText(it.first) }
        secondOption.data= listData.firstOrNull()?.second?.map { getSecondText(it) }
        firstOption.init()
        secondOption.init()

        initListener()
    }

    private fun initListener(){
        firstOption.setOnItemSelectedListener { picker, data, position ->
            secondOption.data=listData[position].second.map { getSecondText(it) }
            currentItem=null
            currentHead=listData[position]
        }
        secondOption.setOnItemSelectedListener { picker, data, position ->
            currentItem= currentHead?.second?.get(position) ?:return@setOnItemSelectedListener
        }
        submitBtn.setOnClickListener {
            if (currentHead==null){
                currentHead=listData.firstOrNull()
            }
            if (currentItem==null){
                currentItem=listData.find {
                    it==currentHead
                }?.second?.firstOrNull()
            }
            result?.invoke(currentHead?.first?:return@setOnClickListener dismiss(),currentItem?:return@setOnClickListener dismiss())
            dismiss()
        }
        skipBtn.setOnClickListener {
            dismiss()
        }
        closeBtn.setOnClickListener {
            dismiss()
        }
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