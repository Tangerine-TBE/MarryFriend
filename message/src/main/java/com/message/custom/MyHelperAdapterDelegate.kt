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
            val helperTipText by lazy {
                findViewById<TextView>(R.id.helperTipText)
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
                when(type){
                    CustomEvent.dazhaohu_str->{//填写打招呼
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="你给别人打招呼,又没有填打招呼语句的时候,每天一次"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.putong_xihuan -> {//普通喜欢
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)//
                        helperTipText.text="普通喜欢"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.touxiang_pass->{//头像通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="您好,您的头像已经通过了我们的人工审核,祝愿你在这里早日找到合适的对象"
                        gotoHandel.text="重新上传"
                    }
                    CustomEvent.touxiang_fail -> {//头像失败
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="您好,XXX,您的头像需要修改,更好的资料才能遇到更好的人,请您认证填写资料哦"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.yuying_pass -> {//语音通过
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="您好,您的语音已经通过了我们的人工审核,祝愿你在这里早日找到合适的对象"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.yuying_fail -> {//语音失败
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="您好,XXX,您的语音需要修改,更好的资料才能遇见更好的人,请您认证填写资料哦"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.shiming_pass -> {//实名通过
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="您好,您的实名认证已经通过了我们的人工审核,祝愿你在这里早日找到合适的对象"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.shiming_fail -> {//实名失败
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="您好,您的实名认证不通过,需要修改,更好的资料才能遇见更好的人,请您认真填写资料哦"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.xiangce_pass->{//相册通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="您的相册已经通过了我们的人工审核,祝愿你在这里早日找到合适的对象"
                        gotoHandel.text="重新上传"
                    }
                    CustomEvent.xiangce_fail -> {
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="相册失败"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.shenghuo_pass -> {//生活照通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="您的生活照已经通过了我们的人工审核,祝愿你在这里早日找到合适的对象"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.shenghuo_fail -> {
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="生活失败"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.dongtai_pass -> {//动态通过
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="您的动态已经通过了我们的人工审核,祝愿你在这里早日找到合适的对象"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.dongtai_fail -> {
                        Glide.with(helperTipIcon).load(img)
                            .placeholder(R.mipmap.ic_fill_in_say_hello)
                            .error(R.mipmap.ic_fill_in_say_hello)
                            .into(helperTipIcon)
                        helperTipText.text="您上传的动态没有通过审核,请重新上传"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.jubao_pass -> {//举报通过
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="您好,您的举报已经通过,用户XX已经被 封禁/3天/30天/永久/封禁"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.jubao_fail -> {
                        helperTipIcon.setImageResource(R.mipmap.ic_fill_in_say_hello)
                        helperTipText.text="举报失败"
                        gotoHandel.text="马上填写"
                    }
                    CustomEvent.HELPER_VIP_EXPIRE->{
                        helperTipIcon.setImageResource(R.mipmap.ic_vip_expire)
                        helperTipText.text="您的会员即将到期，立即续费，继续享有尊贵会员！"
                        gotoHandel.text="立即续费"
                    }
                    else->{

                    }
                }
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