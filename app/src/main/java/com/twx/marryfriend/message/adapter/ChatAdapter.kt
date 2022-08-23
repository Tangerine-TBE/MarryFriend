package com.twx.marryfriend.message.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMMessageBody
import com.message.chat.*
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.databinding.BaseDataBindingViewHolder
import com.twx.marryfriend.message.model.ChatItemModel
import com.twx.marryfriend.message.views.ChatImageMsgItemView
import com.twx.marryfriend.message.views.ChatRedFlowerItemView
import com.twx.marryfriend.message.views.ChatTextMsgItemView
import com.twx.marryfriend.message.views.ChatVoiceItemView
import com.xyzz.myutils.show.toast

class ChatAdapter(val friendId:String?,val nickname:String?,val headImage:String?,):RecyclerView.Adapter<BaseDataBindingViewHolder>() {
    companion object{
        private const val CMD_TYPE=1
        private const val SEND_FLOWER_TYPE=2
        private const val FILE_TYPE=3
        private const val IMAGE_TYPE=4
        private const val LOCATION_TYPE=5
        private const val TXT_TYPE=6
        private const val VIDEO_TYPE=7
        private const val VOICE_TYPE=8

        private const val SECURITY_TIP_TYPE=9
        private const val UPLOAD_HEAD_TYPE=10
        private const val FIRST_MESSAGE_TYPE=11
    }
    enum class SysTipTyp(val viewType: Int,val layoutId:Int){
        securityTip(SECURITY_TIP_TYPE, R.layout.item_sys_chat_tip_msg),
        uploadHead(UPLOAD_HEAD_TYPE, R.layout.item_upload_head),
        firstMessage(FIRST_MESSAGE_TYPE, R.layout.item_first_msg_vip);
    }

    private val listData=ArrayList<ChatItemModel<Message<out EMMessageBody>>>()
    private val sysTipMsg=ArrayList<Pair<Int,SysTipTyp>>()

    override fun getItemViewType(position: Int): Int {
        val sysUi=sysTipMsg.find { (itemCount-it.first)==position }
        if (sysUi!=null){
            return sysUi.second.viewType
        }else{
            var count=0
            for (i in 0 until sysTipMsg.size){
                if ((itemCount-sysTipMsg[i].first)<position){
                    count++
                }
            }

            val p=position-count

            return when(val d=listData[p].data){
                is CmdMessage -> {
                    CMD_TYPE
                }
                is CustomMessage -> {
                    when(d){
                        is SendFlowerMessage -> {
                            SEND_FLOWER_TYPE
                        }
                    }
                }
                is FileMessage -> {
                    FILE_TYPE
                }
                is ImageMessage -> {
                    IMAGE_TYPE
                }
                is LocationMessage -> {
                    LOCATION_TYPE
                }
                is TxtMessage -> {
                    TXT_TYPE
                }
                is VideoMessage -> {
                    VIDEO_TYPE
                }
                is VoiceMessage -> {
                    VOICE_TYPE
                }
                else -> {
                    0
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDataBindingViewHolder {
//        ChatImageRedFlowerItemView(parent)
        val view=when(viewType){
            TXT_TYPE->{
                ChatTextMsgItemView(parent.context)
            }
            IMAGE_TYPE->{
                ChatImageMsgItemView(parent.context)
            }
            VIDEO_TYPE->{
                ChatTextMsgItemView(parent.context)
            }
            FILE_TYPE->{
                null
            }
            LOCATION_TYPE->{
                null
            }
            VOICE_TYPE->{
                ChatVoiceItemView(parent.context)
            }
            CMD_TYPE->{
                null
            }
            SEND_FLOWER_TYPE->{
                ChatRedFlowerItemView(parent.context)
            }
            SysTipTyp.securityTip.viewType->{
                LayoutInflater.from(parent.context).inflate(SysTipTyp.securityTip.layoutId,parent,false)
            }
            SysTipTyp.uploadHead.viewType->{
                LayoutInflater.from(parent.context).inflate(SysTipTyp.uploadHead.layoutId,parent,false)
            }
            SysTipTyp.firstMessage.viewType->{
                LayoutInflater.from(parent.context).inflate(SysTipTyp.firstMessage.layoutId,parent,false)
            }
            else->{
                null
            }
        }
        return BaseDataBindingViewHolder(view?: View(parent.context))
    }

    override fun onBindViewHolder(holder: BaseDataBindingViewHolder, position: Int) {
        val sysUi=sysTipMsg.find { (itemCount-it.first)==position }?.second
        if (sysUi!=null){
            when(sysUi){
                SysTipTyp.securityTip -> {
                    holder.itemView.setOnClickListener {
                        toast(it.context,"安全")
                    }
                }
                SysTipTyp.uploadHead -> {
                    holder.itemView.setOnClickListener {
                        it.context.startActivity(IntentManager.getUpHeadImageIntent(it.context))
                    }
                }
                SysTipTyp.firstMessage -> {
                    holder.itemView.setOnClickListener {
                        toast(it.context,"开通vip，置顶")
                    }
                }
            }
        }else{
            var count=0
            for (i in 0 until sysTipMsg.size){
                if ((itemCount-sysTipMsg[i].first)<position){
                    count++
                }
            }
            val p=position-count
            val dataBindingView=holder.itemView
            val item=listData[p]
            val data=item.data
            when(data){
                is CmdMessage -> {

                }
                is CustomMessage -> {
                    when(data){
                        is SendFlowerMessage -> {
                            (dataBindingView as? ChatRedFlowerItemView)?.setData(item as ChatItemModel<SendFlowerMessage>)
                        }
                    }
                }
                is FileMessage -> {

                }
                is ImageMessage -> {
                    (dataBindingView as? ChatImageMsgItemView)?.setData(item as ChatItemModel<ImageMessage>)
                }
                is LocationMessage -> {

                }
                is TxtMessage -> {
                    (dataBindingView as? ChatTextMsgItemView)?.setData(item as ChatItemModel<TxtMessage>)
                }
                is VideoMessage -> {

                }
                is VoiceMessage -> {
                    (dataBindingView as? ChatVoiceItemView)?.setData(item as ChatItemModel<VoiceMessage>)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size+sysTipMsg.size
    }

    fun setData(list: List<ChatItemModel<Message<out EMMessageBody>>>){
        list.forEach {
            setWhoSendInfo(it)
        }

        listData.clear()
        listData.addAll(list)
        val sss=listData.asReversed()
        sss.forEachIndexed { index, chatItemModel ->
            val preModel=if (index-1>=0){
                sss[index-1]
            }else{
                null
            }
            setTimeVis(preModel,chatItemModel)
        }
        addSysTip(listData)
        notifyDataSetChanged()
    }

    fun addAllData(list: List<ChatItemModel<Message<out EMMessageBody>>>){
        list.forEach {
            setWhoSendInfo(it)
        }

        listData.addAll(list)
        val sss=listData.slice(listData.size-list.size-1 until listData.size)
        sss.forEachIndexed { index, chatItemModel ->
            val preModel=if (index-1>=0){
                sss[index-1]
            }else{
                null
            }
            setTimeVis(preModel,chatItemModel)
        }
        if (addSysTip(listData)){
            notifyItemRangeInserted(listData.size-list.size,list.size+1)
        }else{
            notifyItemRangeInserted(listData.size-list.size,list.size)
        }
    }

    fun addItem(message: Message<out EMMessageBody>){
        listData.add(0,ChatItemModel(message).also {
            val preModel=listData.firstOrNull()
            setTimeVis(preModel,it)

            setWhoSendInfo(it)
        })
        if (addSysTip(listData)){
            notifyItemRangeInserted(0,2)
        }else{
            notifyItemInserted(0)
        }
    }

    private fun addSysTip(list: List<ChatItemModel<Message<out EMMessageBody>>>):Boolean{
        if (list.all { !it.isISend }&&sysTipMsg.find { it.second==SysTipTyp.securityTip }==null){
            sysTipMsg.add(list.size+sysTipMsg.size+1 to SysTipTyp.securityTip)
        }else if (UserInfo.isHaveHeadImage()&&list.size>=3&&list.takeLast(3).all { it.isISend }&&sysTipMsg.find { it.second==SysTipTyp.uploadHead }==null){
            sysTipMsg.add(list.size+sysTipMsg.size+1 to SysTipTyp.uploadHead)
        }else if (UserInfo.isVip()&&list.size>=3&&list.takeLast(3).all { it.isISend }&&sysTipMsg.find { it.second==SysTipTyp.firstMessage }==null){
            sysTipMsg.add(list.size+sysTipMsg.size+1 to SysTipTyp.firstMessage)
        }else{
            return false
        }
        return true
    }

    fun onConversationRead() {
//        listData.forEach {
//            it.data.emMessage.isUnread=false
//        }
//        notifyDataSetChanged()
    }

    fun onMessageRead(message:List<EMMessage>){
//        listData.filter {chatItemModel->
//            message.find {
//                chatItemModel.data.emMessage.msgId==it.msgId
//            }!=null
//        }.forEach {
//            it.data.emMessage.isUnread=false
//        }
//        notifyDataSetChanged()
    }

    private fun setWhoSendInfo(it:ChatItemModel<Message<out EMMessageBody>>){
        if (it.data.from==friendId){
            it.imageHead=headImage
            it.nickname=nickname
            it.isISend=false
        }else{
            it.imageHead= UserInfo.getImgHead()
            it.nickname= UserInfo.getNickname()
            it.isISend=true
        }
    }

    private fun setTimeVis(preModel:ChatItemModel<Message<out EMMessageBody>>?,chatItemModel:ChatItemModel<Message<out EMMessageBody>>){
        chatItemModel.visibility=if (preModel==null){
            true
        }else{
            (chatItemModel.msgTime-3*60*1000L)>preModel.msgTime
        }
    }
}