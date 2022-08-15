package com.twx.marryfriend.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPStaticUtils
import com.bumptech.glide.Glide
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.one_hello.OneClickHelloItemBean
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.dialog_one_click_hello.*
import java.text.SimpleDateFormat
import java.util.*

class OneClickHelloDialog(context: Context,private val data:List<OneClickHelloItemBean>,private val sendAction:(List<OneClickHelloItemBean>?)->Unit):Dialog(context) {
    companion object{
        private const val ONE_CLICK_HELLO="one_click_hello"
        fun isSendHello():Boolean{
            val date=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
            return !SPStaticUtils.getBoolean(ONE_CLICK_HELLO+"_"+date,false)
        }

        fun onSendHello(){
            val date=SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date(System.currentTimeMillis()))
            SPStaticUtils.put(ONE_CLICK_HELLO+"_"+date,true)
        }
    }

    init {
        window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        window?.decorView?.setPadding(0,0,0,0)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.dialog_one_click_hello)
        Glide.with(one_click_heart).load(R.drawable.ic_one_click_heart).placeholder(R.drawable.ic_one_click_heart).into(one_click_heart)
    }
    private var resultList:List<OneClickHelloItemBean>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onSendHello()
//        one_click_recycler_view.layoutManager=GridLayoutManager(context,3)
//        one_click_recycler_view.adapter=OneClickHelloAdapter(data){
//            resultList=it
//        }
        initView(data){
            resultList=it
        }
        one_click_close.setOnClickListener {
            dismiss()
        }
        one_click_send.setOnClickListener {
            if (resultList.isNullOrEmpty()){
                toast(context,"您还未选择用户")
                return@setOnClickListener
            }
            sendAction.invoke(resultList)
            dismiss()
        }
    }

    private fun initView(data: List<OneClickHelloItemBean>,choiceCall:(List<OneClickHelloItemBean>)->Unit){
        val choiceData=ArrayList<OneClickHelloItemBean>(data)
        choiceCall.invoke(data)

        val users= arrayOf(user1,user2,user3,
            user4,user5,user6,
            user7,user8,user9)
        val imgs=arrayOf(one_click_user_img,one_click_user_img2,one_click_user_img3,
            one_click_user_img4,one_click_user_img5,one_click_user_img6,
            one_click_user_img7,one_click_user_img8,one_click_user_img9)
        val nicknames=arrayOf(nickname1,nickname2,nickname3,
            nickname4,nickname5,nickname6,
            nickname7,nickname8,nickname9)
        users.forEach {
            it.isSelected=true
        }
        users.slice(data.size until users.size).forEach {
            it.visibility= View.GONE
        }
        data.forEachIndexed { index, item ->
            if (index<users.size){
                imgs[index].also {
                    Glide.with(it).load(item.image_url).placeholder(UserInfo.getReversedDefHelloHeadImage()).into(it)
                }
                nicknames[index].text=item.nick?:""
                users[index].setOnClickListener {
                    it.isSelected=!it.isSelected
                    if (it.isSelected){
                        choiceData.add(item)
                    }else{
                        choiceData.remove(item)
                    }
                    choiceCall.invoke(choiceData)
                }
            }
        }
    }
    
    class OneClickHelloAdapter(val data: List<OneClickHelloItemBean>,val choiceCall:(List<OneClickHelloItemBean>)->Unit):RecyclerView.Adapter<BaseViewHolder>(){
        private val choiceData=ArrayList<OneClickHelloItemBean>(data)
        init {
            choiceCall.invoke(data)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_one_click,parent,false)
            return BaseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            val item=data[position]
//            holder.setImage(R.id.one_click_img,item.image_url)
            holder.getView<ImageView>(R.id.one_click_img).also {
                Glide.with(it).load(item.image_url).placeholder(UserInfo.getReversedDefHelloHeadImage()).into(it)
            }
            holder.setText(R.id.one_click_name,item.nick?:"")
            holder.itemView.isSelected=choiceData.contains(item)
            holder.itemView.setOnClickListener {
                it.isSelected=!it.isSelected
                if (it.isSelected){
                    choiceData.add(item)
                }else{
                    choiceData.remove(item)
                }
                choiceCall.invoke(choiceData)
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }
    }
}