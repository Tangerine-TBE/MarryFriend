package com.message.custom

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hyphenate.chat.EMCustomMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeim.R
import com.hyphenate.easeui.delegate.EaseMessageAdapterDelegate
import com.hyphenate.easeui.interfaces.MessageListItemClickListener
import com.hyphenate.easeui.viewholder.EaseChatRowViewHolder
import com.hyphenate.easeui.widget.chatrow.EaseChatRow
import com.message.chat.CustomEvent
import com.message.chat.HELPER_STYLE_1

class MyHelperAdapterDelegate: EaseMessageAdapterDelegate<EMMessage, EaseChatRowViewHolder>(){
    companion object{
        var sexAction:(()->Int)?=null
    }

    override fun isForViewType(item: EMMessage?, position: Int): Boolean {
        if (item?.type != EMMessage.Type.CUSTOM){
            return false
        }
        val body= item.body as? EMCustomMessageBody
        val event=body?.event()

        val t=CustomEvent.values().filter {
            it.category==HELPER_STYLE_1
        }.map { it.code }

        return t.any { it==event }
    }

    override fun getEaseChatRow(parent: ViewGroup?, isSender: Boolean): EaseChatRow {
        return object :EaseChatRow(parent?.context,isSender){
            val flowerContent by lazy {
                findViewById<View>(R.id.flowerContent)
            }
            val helperTipIcon by lazy {
                findViewById<ImageView>(R.id.helperTipIcon)
            }
            val helperTipIconForeground by lazy {
                findViewById<ImageView>(R.id.helperTipIconForeground)
            }
            val helperTipText by lazy {
                findViewById<TextView>(R.id.helperTipText)
            }
            val divisionLine by lazy {
                findViewById<View>(R.id.divisionLine)
            }
            val gotoHandel by lazy {
                findViewById<TextView>(R.id.gotoHandel)
            }
            val userHead by lazy {
                findViewById<ImageView>(R.id.userHead)
            }

            val body by lazy {
                message.body as? EMCustomMessageBody
            }
            val img by lazy {
                body?.params?.get("img")
            }
            val msg by lazy {
                body?.params?.get("msg")
            }

            override fun onInflateView() {
                inflater.inflate(R.layout.custom_my_helper_msg, this)
            }

            override fun onFindViewById() {

            }

            override fun onSetUpView() {
                userHead.setImageResource(R.mipmap.ic_launcher)//设置小秘书图标
                val type=CustomEvent.codeToEvent(body?.event())
                if (type!=null){
                    flowerContent.setOnClickListener {
                        ImCustomEventListenerManager.click(it,type,message)
                    }
                }
                helperTipIconForeground.setImageBitmap(null)
                helperTipText.text=msg
                val defHad=if (sexAction?.invoke()==1){
                    R.mipmap.ic_my_helper_head_man_def
                }else{
                    R.mipmap.ic_my_helper_head_woman_def
                }
                when(type){
                    CustomEvent.dazhaohu_str->{//填写打招呼
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.putong_xihuan -> {//普通喜欢
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)//
                        goneLine()
                    }
                    CustomEvent.touxiang_pass->{//头像通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        goneLine()
                    }
                    CustomEvent.touxiang_fail -> {//头像失败
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        Glide.with(helperTipIconForeground)
                            .load(R.mipmap.ic_my_helper_fail)
                            .into(helperTipIconForeground)
                        gotoHandel.text="马上上传"
                    }
                    CustomEvent.yuying_pass -> {//语音通过
                        helperTipIcon.setImageResource(R.mipmap.ic_my_helper_voice_def)
                        goneLine()
                    }
                    CustomEvent.yuying_fail -> {//语音失败
                        helperTipIcon.setImageResource(R.mipmap.ic_my_helper_voice_def)
                        Glide.with(helperTipIconForeground)
                            .load(R.mipmap.ic_my_helper_fail)
                            .into(helperTipIconForeground)
                        gotoHandel.text="重新上传"
                    }
                    CustomEvent.shiming_pass -> {//实名通过
                        helperTipIcon.setImageResource(R.mipmap.ic_my_helper_realname_pass)
                        goneLine()
                    }
                    CustomEvent.shiming_fail -> {//实名失败
                        helperTipIcon.setImageResource(R.mipmap.ic_my_helper_realname_fail)
                        gotoHandel.text="立即实名"
                    }
                    CustomEvent.xiangce_pass->{//相册通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        goneLine()
                    }
                    CustomEvent.xiangce_fail -> {
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        helperTipIconForeground.setImageResource(R.mipmap.ic_my_helper_fail)
                        gotoHandel.text="重新上传"
                    }
                    CustomEvent.shenghuo_pass -> {//生活照通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        goneLine()
                    }
                    CustomEvent.shenghuo_fail -> {
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        helperTipIconForeground.setImageResource(R.mipmap.ic_my_helper_fail)
                        gotoHandel.text="重新上传"
                    }
                    CustomEvent.dongtai_pass -> {//动态通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        goneLine()
                    }
                    CustomEvent.dongtai_fail -> {
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(defHad)
                            .error(defHad)
                            .into(helperTipIcon)
                        helperTipIconForeground.setImageResource(R.mipmap.ic_my_helper_fail)
                        gotoHandel.text="重新发布"
                    }
                    CustomEvent.jubao_pass -> {//举报通过
                        helperTipIcon.setImageResource(R.mipmap.ic_my_helper_report)
                        goneLine()
                    }
                    CustomEvent.jubao_fail -> {
                        helperTipIcon.setImageResource(R.mipmap.ic_my_helper_report)
                        goneLine()
                    }
                    CustomEvent.HELPER_VIP_EXPIRE->{
                        helperTipIcon.setImageResource(R.mipmap.ic_vip_expire)
                        gotoHandel.text="立即续费"
                    }
                    else->{

                    }
                }
            }

            private fun goneLine(){
                divisionLine.visibility=View.GONE
                gotoHandel.visibility=View.GONE
            }
        }
    }

    override fun createViewHolder(
        view: View,
        itemClickListener: MessageListItemClickListener?
    ): EaseChatRowViewHolder {
        return EaseChatRowViewHolder(view,itemClickListener)
    }
}