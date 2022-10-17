package com.twx.marryfriend

import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.SPStaticUtils
import com.google.gson.Gson
import com.hjq.permissions.Permission
import com.hyphenate.chat.EMCustomMessageBody
import com.message.ImMessageManager
import com.message.chat.CustomEvent
import com.twx.marryfriend.bean.InterdictionBean
import com.twx.marryfriend.bean.Sex
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.utils.SpUtil
import com.xyzz.myutils.NetworkUtil
import com.xyzz.myutils.SPUtil
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.wLog
import org.json.JSONObject

object UserInfo :ILoginListener{
    init {
        ImMessageManager.newMessageLiveData.observeForever { list ->
            list?.forEach {
                if (it.from==ImMessageManager.MY_HELPER_ID){
                    iLog("收到小秘书消息")
                    val body=it.body
                    if (body is EMCustomMessageBody){
                        val event=body.event()
                        val params=body.params
                        iLog("参数，${params}")
                        when(event){
                            CustomEvent.interdi_pass.code->{
                                iLog("收到封禁消息")
                                val interdictionBean=InterdictionBean().apply {
                                    this.blacklist_close_time=params.get("expire")
                                    this.blacklist_permanent=params.get("permanent")?.toIntOrNull()?:0
                                    this.blacklist_status=params.get("blacklist_status")?.toIntOrNull()?:0
                                }
                                InterdictionBean.putInterdictionState(interdictionBean)
                            }
                            CustomEvent.interdi_fail.code->{
                                iLog("收到解封消息")
                                InterdictionBean.putInterdictionState(null)
                            }
                            CustomEvent.touxiang_fail.code->{
                                iLog("头像审核不通过")
                                val status=params.get("hreaStatus")?.toIntOrNull()
                                val hreaUrl=params.get("hreaUrl")
                                if (status==null||hreaUrl==null){
                                    SpUtil.updateAvatar()
                                }else{
                                    SpUtil.updateAvatar(hreaUrl,status)
                                }
                            }
                        }
                    }
                }

            }
        }
        NetworkUtil.addResponseListener {
            try {
                val jsonObject=JSONObject(it)
                if (jsonObject.getInt("code")==456){
                    val data=jsonObject.getJSONObject("data")
                    val interdictionBean= InterdictionBean().also {
                        it.blacklist_status=data.getInt("blacklist_status")
                        it.blacklist_permanent=data.getInt("blacklist_permanent")
                        it.blacklist_close_time=data.getString("blacklist_close_time")
                    }
                    UserInfo.putInterdiction(interdictionBean)
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
            }

        }
    }

    fun getLoginListener():ILoginListener=this

    override fun onLoginSuccess(userId:String){

    }

    override fun onLogOut(){

    }

    fun putInterdiction(interdictionBean:InterdictionBean?){
        InterdictionBean.putInterdictionState(interdictionBean)
    }

    fun isInterdiction():Boolean{
        return InterdictionBean.getInterdictionState()
    }

    fun updateUserInfo(){
        val url="${Contents.USER_URL}/marryfriend/LoginRegister/getFive"
        val map= mapOf(
            "user_id" to (UserInfo.getUserId()?:return)
        )

        NetworkUtil.sendPostSecret(url,map,{ response ->
            try {
                eLog("成功","更新用户信息")
            }catch (e:Exception){
                eLog(e.stackTraceToString(),"更新用户信息")
            }
        },{
            eLog(it,"更新用户信息")
        })
    }

    fun getPhone():String?{
        return SPStaticUtils.getString(Constant.USER_ACCOUNT,null)
    }

    fun getUserId():String?{
//        val phone=SPStaticUtils.getString(Constant.USER_ACCOUNT,null)

//        if(BuildConfig.DEBUG&&
//            (phone=="15270318482"||phone=="17370452215")){//3是男的
//            if (Build.MANUFACTURER.contains("xiaomi",true)){
//                return "15"
//            }
//            return "2"//3,4,6,11,16//
//        }

//        if (BuildConfig.DEBUG&&isTestDv()){
//            return "134"
//        }
        return SPStaticUtils.getString(Constant.USER_ID, null)
    }

    fun isTestDv():Boolean{
        val phone=SPStaticUtils.getString(Constant.USER_ACCOUNT,null)
        return BuildConfig.DEBUG&&
            (phone=="15270318482"||phone=="17370452215")
    }

    fun getImgHead():String{
        return SPStaticUtils.getString(Constant.ME_AVATAR, "")
    }

    fun getNickname():String{
        return SPStaticUtils.getString(Constant.ME_NAME, "未填写")
    }

    fun getUserVipLevel():Int{
        return SPStaticUtils.getInt(Constant.USER_VIP_LEVEL,0)
//        return 0
    }

    fun isVip():Boolean{
        if (BuildConfig.DEBUG){
            iLog("DEBUG,默认成为会员")
            return true
        }
        return getUserVipLevel()>0
    }

    fun isSuperVip():Boolean{
        if (BuildConfig.DEBUG){
            iLog("DEBUG,默认成为超级会员")
            return true
        }
        return getUserVipLevel()>1
    }
    fun getHeadPortrait():String{
        return SPStaticUtils.getString(
            Constant.ME_AVATAR)
    }
    /**
     * 1：男
     * 2：女
     */
    fun getOriginalUserSex():Int{
//        if(BuildConfig.DEBUG){
//            return 1
//        }
        return SPStaticUtils.getInt(Constant.ME_SEX, 2)
    }

    fun getUserSex():Sex{
        return Sex.toSex(getOriginalUserSex())
    }

    fun isSexMail(c:Int?):Boolean{
        return c==1
    }

    fun reversalSex(sex:Int):Int{
        return (sex%2)+1
    }

    fun getDefHeadImage():Int{
        val sex= getOriginalUserSex()
        return if (sex==1){
            R.drawable.ic_mine_male_default
        }else{
            R.drawable.ic_mine_female_default
        }
    }

    fun getReversedDefHeadImage():Int{
        val sex= reversalSex(getOriginalUserSex())
        return if (sex==1){
            R.drawable.ic_mine_male_default
        }else{
            R.drawable.ic_mine_female_default
        }
    }

    fun getReversedDefHelloHeadImage():Int{
        val sex= reversalSex(getOriginalUserSex())
        return if (sex==1){
            R.mipmap.ic_def_hello_male
        }else{
            R.mipmap.ic_def_hello_female
        }
    }

    fun getDefHeadImage(sex: Int):String{
        if (sex==1){
//            R.mipmap.icon_mine_female_default
//            R.mipmap.icon_mine_male_default
            return "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimg.tukuppt.com%2Fpng_preview%2F00%2F09%2F06%2FPhiqi5eUUD.jpg!%2Ffw%2F780&imgrefurl=https%3A%2F%2Fwww.tukuppt.com%2Fmuban%2Fenxkwrae.html&tbnid=Q_a4GmZuCbOVMM&vet=12ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygmegUIARCPAg..i&docid=ysTt9zGjULxHLM&w=780&h=724&q=%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F&ved=2ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygmegUIARCPAg"
        }else{
            return "https://www.google.com/imgres?imgurl=https%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F01c13f5b56bd8ba8012036be3d28b7.png%401280w_1l_2o_100sh.png&imgrefurl=https%3A%2F%2Fm.zcool.com.cn%2Fwork%2FZMjkxOTkxNDA%3D.html&tbnid=4rBY98RMBUB6SM&vet=12ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygTegUIARDkAQ..i&docid=91X0_gUlFywQoM&w=265&h=265&q=%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F&ved=2ahUKEwjAkN_xw4n5AhWvRvUHHX4sCx0QMygTegUIARDkAQ"
        }

    }

    fun getGreetText():String?{
        return SPStaticUtils.getString(Constant.ME_GREET)
    }

    fun isHaveLifePhoto():Boolean{
        return !SPStaticUtils.getString(Constant.ME_LIFE_PHOTO_ONE_ID).isNullOrBlank()
    }
    fun isRealName():Boolean{
        return SPStaticUtils.getBoolean(Constant.IS_IDENTITY_VERIFY, false)
    }
    fun isHaveHeadImage():Boolean{
        return !SPStaticUtils.getString(Constant.ME_AVATAR).isNullOrBlank()||
                !SPStaticUtils.getString(Constant.ME_AVATAR_AUDIT).isNullOrBlank()
    }
    fun isFillInHobby():Boolean{//爱好
        return !SPStaticUtils.getString(Constant.ME_HOBBY).isNullOrBlank()
    }

    fun isFillInGreet():Boolean{//招呼
        return !SPStaticUtils.getString(Constant.ME_GREET).isNullOrBlank()
    }
    fun isFillInIntroduce():Boolean{//介绍
        return !SPStaticUtils.getString(Constant.ME_INTRODUCE).isNullOrBlank()
    }
    fun isFillInVoice():Boolean{//语音介绍
        return !SPStaticUtils.getString(Constant.ME_VOICE).isNullOrBlank()
    }

    fun getNextNotFillIn(context: Context, action:(permission:Array<String>?, intent:Pair<Int,Intent?>?)->Unit){
        if(!UserInfo.isHaveLifePhoto()){//生活
            action.invoke(null,R.mipmap.ic_item_up_load_life to IntentManager.getUpLifeIntent(context))
            return
        }
        if(!UserInfo.isRealName()){//实名
            action.invoke(null,R.mipmap.ic_item_up_real_name to IntentManager.getUpRealNameIntent(context))
            return
        }
        if(!UserInfo.isHaveHeadImage()){//头像
            action.invoke(null,R.mipmap.ic_item_up_head_image to IntentManager.getUpHeadImageIntent(context))
            return
        }
        if(!UserInfo.isFillInHobby()){//兴趣
            action.invoke(null,R.mipmap.ic_item_up_fill_in_hobby to IntentManager.getUpFillInHobbyIntent(context))
            return
        }
        if(!UserInfo.isFillInGreet()){//招呼
            action.invoke(null,R.mipmap.ic_item_up_fill_in_greet to IntentManager.getUpFillInGreetIntent(context))
            return
        }
        if(!UserInfo.isFillInIntroduce()){//介绍
            action.invoke(null,R.mipmap.ic_item_up_fill_in_introduce to IntentManager.getUpFillInIntroduceIntent(context))
            return
        }
        if(!UserInfo.isFillInVoice()){//语音
            action.invoke(arrayOf(Permission.RECORD_AUDIO, Permission.MANAGE_EXTERNAL_STORAGE),R.mipmap.ic_item_up_fill_in_voice to IntentManager.getUpFillInVoiceIntent(context,true))
            return
        }else{
            action.invoke(null,null)
            return
        }
    }

    fun getNextNotFillIn2(context:Context, action:(permission:Array<String>?, intent:Pair<Int,Intent?>?)->Unit){
        if(!UserInfo.isHaveLifePhoto()){//生活
             action.invoke(null,R.mipmap.ic_item_up_load_life_l to IntentManager.getUpLifeIntent(context))
            return
        }
        if(!UserInfo.isRealName()){//实名
             action.invoke(null,R.mipmap.ic_item_up_real_name_l to IntentManager.getUpRealNameIntent(context))
            return
        }
        if(!UserInfo.isHaveHeadImage()){//头像
             action.invoke(null,R.mipmap.ic_item_up_head_image_l to IntentManager.getUpHeadImageIntent(context))
            return
        }
        if(!UserInfo.isFillInHobby()){//兴趣
             action.invoke(null,R.mipmap.ic_item_up_fill_in_hobby_l to IntentManager.getUpFillInHobbyIntent(context))
            return
        }
        if(!UserInfo.isFillInGreet()){//招呼
             action.invoke(null,R.mipmap.ic_item_up_fill_in_greet_l to IntentManager.getUpFillInGreetIntent(context))
            return
        }
        if(!UserInfo.isFillInIntroduce()){//介绍
             action.invoke(null,R.mipmap.ic_item_up_fill_in_introduce_l to IntentManager.getUpFillInIntroduceIntent(context))
            return
        }
        if(!UserInfo.isFillInVoice()){//语音
            action.invoke(arrayOf(Permission.RECORD_AUDIO, Permission.MANAGE_EXTERNAL_STORAGE),R.mipmap.ic_item_up_fill_in_voice_l to IntentManager.getUpFillInVoiceIntent(context,true))
            return
        }else{
             action.invoke(null,null)
            return
        }
    }
}