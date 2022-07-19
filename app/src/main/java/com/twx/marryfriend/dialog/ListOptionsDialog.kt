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
import androidx.core.view.children
import androidx.core.view.iterator
import com.aigestudio.wheelpicker.WheelPicker
import com.blankj.utilcode.util.ConvertUtils
import com.twx.marryfriend.R
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.dialog_list_options.*

abstract class ListOptionsDialog<I>(context: Context, private val listData:List<I>, val result:((List<I>)->Unit)?=null): Dialog(context) {

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_list_options)
    }
    private val resultList by lazy {
        ArrayList<I>()
    }
    private var preResult :List<I>?=null
    private var currentItem:I?=listData.firstOrNull()
    var maxContent=Int.MAX_VALUE

    fun setTitle(title:String):ListOptionsDialog<I>{
        titleView.text=title
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firstOption.data=listData.map { getFirstText(it) }
        firstOption.init()

        initListener()
    }

    private fun initListener(){
        firstOption.setOnItemSelectedListener { picker, data, position ->
            currentItem=listData[position]
        }
        submitBtn.setOnClickListener {
            result?.invoke(resultList)
            preResult=resultList.toList()
            dismiss()
        }
        closeBtn.setOnClickListener {
            dismiss()
        }
        setOnDismissListener {
            preResult?.also {
                resultList.clear()
                resultList.addAll(it)
            }
            resultList.forEach {item->
                if (chipGroup.findViewWithTag<View>(item)==null){
                    addLabelViewItem(item)
                }
            }
            chipGroup.children.toList().forEachIndexed { _, view ->
                if (view.tag!=null){
                    if (!resultList.contains(view.tag)){
                        chipGroup.removeView(view)
                    }
                }
            }
        }
        clickAdd.setOnClickListener { view ->
            val item=currentItem?:return@setOnClickListener
            if (resultList.find {
                    item==it
                }!=null){
                return@setOnClickListener toast(context,"已经存在请勿重复添加")
            }
            if (resultList.size>=maxContent){
                return@setOnClickListener toast(context,"最多添加${maxContent}个")
            }
            val needRemoveView=addItemCollectMutex(item,resultList)
            if (needRemoveView.isNotEmpty()){
                chipGroup.iterator().asSequence().toList().forEach {
                    if (needRemoveView.contains(it.tag)){
                        chipGroup.removeView(it)
                    }
                }
                resultList.removeAll(needRemoveView.toSet())
            }
            resultList.add(item)
            addLabelViewItem(item)
        }
    }

    private fun addLabelViewItem(tag:I){
        val itemView= LayoutInflater.from(this.context).inflate(R.layout.item_dailog_multiple_secondary_options_chip,chipGroup,false)
        itemView.findViewById<TextView>(R.id.chipText).text="${getFirstText(tag)}"
        itemView.tag = tag
        if (chipGroup.childCount>0){
            chipGroup.addView(itemView,chipGroup.childCount-1)
        }else{
            chipGroup.addView(itemView)
        }
        itemView.findViewById<View>(R.id.chipClose).setOnClickListener {
            chipGroup.removeView(itemView)
            resultList.remove(tag)
        }
    }

    override fun show() {
        super.show()
        preResult=resultList.toList()
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

    /**
     * 收集互斥的item
     * @param item 需要添加的item
     * @param list 当前存在的item集合
     * @return 与当前添加的item互斥的item集合
     */
    protected open fun addItemCollectMutex(item:I, list:List<I>):List<I>{
        return emptyList()
    }

    abstract fun getFirstText(i:I):String
}