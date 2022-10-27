package com.twx.marryfriend.friend

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.message.ImMessageManager
import com.twx.marryfriend.*
import com.twx.marryfriend.bean.Sex
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.dialog.FollowReportDialog
import com.twx.marryfriend.dialog.ReChargeCoinDialog
import com.twx.marryfriend.dialog.SendFlowerDialog
import com.twx.marryfriend.dialog.UploadHeadDialog
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.message.viewmodel.ImChatViewModel
import com.twx.marryfriend.recommend.LocationUtils
import com.twx.marryfriend.recommend.PlayAudio
import com.twx.marryfriend.recommend.RecommendCall
import com.twx.marryfriend.recommend.RecommendViewModel
import com.twx.marryfriend.recommend.widget.LifeView
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_friend_info.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.coroutines.launch

class FriendInfoActivity:AppCompatActivity(R.layout.activity_friend_info) {
    companion object{
        private const val USER_ID_KEY="user_id_k"
        fun getIntent(context: Context, userId: Int?):Intent?{
            if (userId==null){
                toast(context,"id 不能为空")
                return null
            }
            val intent=Intent(context,FriendInfoActivity::class.java)
            intent.putExtra(USER_ID_KEY,userId)
            return intent
        }
    }
    private val userId by lazy {
        val id=intent?.getIntExtra(USER_ID_KEY,-1)?:-1
        if (id!=-1){
            id
        }else{
            null
        }
    }
    private val friendInfoViewModel by lazy {
        ViewModelProvider(this).get(FriendInfoViewModel::class.java)
    }
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog()
            .create(this)
            .setCancelable(false)
            .setMessage("请稍后...")
    }
    private val followReportDialog by lazy {
        FollowReportDialog(this).also {
            if (userItem?.isFollow()==true){
                it.setFollowText("取消关注")
            }else{
                it.setFollowText("关注")
            }
            it.setFollowListener{ //关注或者取消关注
                if (userId?.toString()==UserInfo.getUserId()){
                    toast("自己不能关注自己")
                    return@setFollowListener
                }
                lifecycleScope.launch {
                    if (userItem?.isFollow()==true){
                        try {
                            friendInfoViewModel.unFollow(userId?:return@launch)
                            userItem?.clearFollow()
                            toast("取消关注成功")
                        }catch (e:Exception){
                            toast("取消关注失败，${e.message}")
                        }
                    }else{
                        try {
                            friendInfoViewModel.follow(userId?:return@launch)
                            userItem?.addFollow()
                            toast("关注成功")
                        }catch (e:Exception){
                            toast("关注失败，${e.message}")
                        }
                    }
                    if (userItem?.isFollow()==true){
                        it.setFollowText("取消关注")
                    }else{
                        it.setFollowText("关注")
                    }
                }
            }

            it.setReportId (userItem?.getId()?:return@also )
        }
    }
    private var userItem: RecommendBean?=null
    private val coinInsufficientDialog by lazy {
        ReChargeCoinDialog(this)
    }
    private val uploadHeadDialog by lazy {
        UploadHeadDialog(this)
    }
    private val imChatViewModel by lazy {
        ViewModelProvider(this).get(ImChatViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        Glide.with(myHead).load(UserInfo.getHeadPortrait())
            .placeholder(UserInfo.getUserSex().smallHead)
            .error(UserInfo.getUserSex().smallHead).into(myHead)
    }

    override fun onResume() {
        super.onResume()
        life_view.refreshView()
    }

    private fun loadData(){
        lifecycleScope.launch{
            loadingDialog.show()
            val id=userId?:return@launch toast("无法获取用户id")
            val item=try {
                friendInfoViewModel.loadUserInfo(id)
            }catch (e:Exception){
                toast(e.message)
                null
            }
            loadingDialog.dismiss()
            if (item==null){
                return@launch
            }else{
                myActionBar.setTitle(item.getNickname())
            }
            //同性不能喜欢
            if(item.getUserSex()==UserInfo.getUserSex()){
                if (BuildConfig.DEBUG){
                    toast("同性不能喜欢")
                }
                care2.visibility=View.GONE
                care.visibility=View.GONE
                dislike2.visibility=View.GONE
                dislike.visibility=View.GONE
                sendFlowers.visibility=View.GONE
                sendFlowers2.visibility=View.GONE
            }
            if (item.isILikeTa()){
                care2.visibility=View.GONE
                care.visibility=View.GONE
                dislike2.visibility=View.GONE
                dislike.visibility=View.GONE
            }
            sexIcon.setImageResource(item.getUserSex().sexIcon)
            userItem=item
            initListener()
            isLikeMe.visibility=if (item.isTaLikeMe()){
                if(item.isILikeTa()){
                    likeTip.text="你们相互喜欢！"
                }else{
                    likeTip.text="${if (item.getUserSex()==Sex.male) "他" else "她"}喜欢了你！"
                }
                View.VISIBLE
            }else{
                View.GONE
            }
            userIdText.text="用户ID:${item.getId()}"
            report.setOnClickListener {
                startActivity(IntentManager.getReportIntent(this@FriendInfoActivity,item.getId()))
            }
            var isBlock=false
            lifecycleScope.launch {
                isBlock=
                    try {
                        imChatViewModel.getOurRelationship(item.getId().toString()).woPingBiTa
                    }catch (e:Exception){
                        false
                    }
                if (isBlock){
                    blacklist.text="取消屏蔽"
                }else{
                    blacklist.text="屏蔽"
                }
            }
            blacklist.setOnClickListener {
                lifecycleScope.launch{
                    if(isBlock){
                        try {
                            imChatViewModel.removeBlockList(item.getId().toString()?:return@launch)
                            toast("取消屏蔽成功")
                            blacklist.text="屏蔽"
                            isBlock=false
                        }catch (e:Exception){
                            toast(e.message)
                        }
                    }else{
                        try {
                            imChatViewModel.addBlockList(item.getId()?.toString()?:return@launch)
                            toast("屏蔽成功")
                            blacklist.text="取消屏蔽"
                            isBlock=true
                        }catch (e:Exception){
                            toast(e.message)
                        }
                    }
                }
            }
            //简介模块
            briefIntroduction.apply {
                itemSetting.setOnClickListener {
                    followReportDialog.show()
                    if (BuildConfig.DEBUG){
                        toast("用户id："+userId.toString())
                    }
                }
                item.getUserSex()
                Glide.with(this)
                    .load(item.getHeadImg())
                    .placeholder(item.getUserSex().homeBigHead)
                    .error(item.getUserSex().homeBigHead)
                    .into(recommendPhoto)
                itemNickname.text=item.getNickname()
                if (item.isRealName()){
                    realNameView.visibility= View.VISIBLE
                }else{
                    realNameView.visibility= View.GONE
                }
                if (item.isVip()){
                    vipLabel.visibility= View.VISIBLE
                }else{
                    vipLabel.visibility= View.GONE
                }
                item.getAge().also {
                    if (it==null){
                        age.visibility=View.GONE
                    }else{
                        age.text=(it.toString()+"岁")
                    }
                }
                occupation.text=(item.getOccupation())
                education.text=(item.getSchoolName())
                dynamicCount.text=(item.getDynamicCount().toString()+"条动态")//上面的
                albumPhotoCount.text=(item.getLifePhoto().size.toString()+"张照片")
            }
            val isInterdiction=item.blaklist?.isInterdiction()?:false
            //用户被封禁
            if (isInterdiction.xor(interdictionViewViewSwitcher.currentView==InterdictionView)){
                interdictionViewViewSwitcher.showNext()
            }
            if (isInterdiction){
                jubao.setOnClickListener {
                    startActivity(IntentManager.getReportIntent(this@FriendInfoActivity,item?.getId()))
                }
                shuoming.text="该用户（ID：${item.getId()}）账号存在异常，为了您的征婚安全，该账号已被限制。"
                itemNickname2.text=item.getNickname().ifBlank {
                    item.getId().toString()
                }
                isLikeMe.isVisible=false
                selfIntroduction.isVisible=false
                expectedTA.isVisible=false
                voiceIntroduce.isVisible=false
                myAlbum.isVisible=false
                myLabel.isVisible=false
                myAuthentication.isVisible=false
                myDynamic.isVisible=false
                life_view.isVisible=false
                richang.isVisible=false//
                itemInteraction.isVisible=false
                sendAction.isVisible=false
                return@launch
            }
            //关于我
            selfIntroduction.apply {
                val stringBuilder=StringBuilder()
                if (!item.getAboutMe().isNullOrBlank()){
                    stringBuilder.append(item.getAboutMe()+"\n\n")
                }
                if (!item.getAboutMeHobby().isNullOrBlank()){
                    stringBuilder.append(item.getAboutMeHobby())
                }
                stringBuilder.toString().also { text->
                    if (text.isBlank()){
                        this.visibility=View.GONE
                        return@apply
                    }else{
                        this.visibility=View.VISIBLE
                    }
                    if (text.isNotBlank()){
                        aboutMe.visibility=View.VISIBLE
                        aboutMe.text = text
                    }else{
                        aboutMe.visibility=View.GONE
                    }
                }
            }
            //我心中的TA
            expectedTA.apply {
                item.getExpectedTa().also {
                    if(it.isNullOrBlank()){
                        this.visibility=View.GONE
                    }else{
                        this.visibility=View.VISIBLE
                        expectedTAText.text=(it)
                    }
                }
            }
            //语音介绍
            voiceIntroduce.apply {
                if (!item.getVoiceUrl().isNullOrBlank()){
                    this.visibility= View.VISIBLE
                    voiceDuration.text=(item.getVoiceDurationStr())
                    if (/*RecommendAdapter.isFirstListenerVoice()*/false){
                        firstViewSwitcher.visibility= View.VISIBLE
                        if (firstViewSwitcher.currentView!=firstVoiceTip){
                            firstViewSwitcher.showNext()
                        }
                    }else{
                        firstViewSwitcher.visibility= View.GONE
                    }
                    playVoice.setOnClickListener { view ->
                        if (firstViewSwitcher.visibility!= View.GONE) {
                            /*RecommendAdapter.useFirstListenerVoice()*/
                            firstViewSwitcher.visibility = View.GONE
                        }
                        if (view.isSelected){
                            view.isSelected=false
                            pauseVoice(item)
                            (playAnim.drawable as? AnimationDrawable)?.also {
                                it.selectDrawable(0)
                                it.stop()
                            }
                        }else{
                            playVoice(item,{
                                //playAnim
                                (playAnim.drawable as? AnimationDrawable)?.start()
                                view.isSelected=true
                            },{
                                view.isSelected=false
                                //播放完成
                                (playAnim.drawable as? AnimationDrawable)?.also {
                                    it.selectDrawable(0)
                                    it.stop()
                                }
                            },{
                                view.isSelected=false
                                //出错了
                            })
                        }
                    }
                    uploadVoice.setOnClickListener {
                        lifecycleScope.launch {
                            IntentManager.getUpFillInVoiceIntent(it.context)?.also {
                                startActivity(it)
                            }
                        }
                    }
                }else{
                    this.visibility= View.GONE
                }
            }
            //我的相册
            myAlbum.apply {
                this.visibility=View.GONE
//                item.getLifePhoto().also {
//                    if (it.isEmpty()){
//                        this.visibility= View.GONE
//                    }else{
//                        this.visibility= View.VISIBLE
//                        photoCount.text=(it.size.toString()+"张照片")
//                        myAlbumPreview.setImageData(it)
//                    }
//                }
//                myAlbumSeeMore.setOnClickListener {
//                    IntentManager.getPhotoPreviewIntent(it.context,)
//                }
            }
            //我的标签
            myLabel.apply {
                baseChipGroup.also { chipGroup ->
                    val demandLabel=item.getBaseLabel()
                    for (i in 0 until demandLabel.size-chipGroup.children.filterIsInstance<Chip>().toList().size){
                        val chip= LayoutInflater.from(this@FriendInfoActivity).inflate(R.layout.item_chip_base,chipGroup,false)
                        chipGroup.addView(chip)
                    }

                    val chips=chipGroup.children.filterIsInstance<Chip>()
                    chips.forEachIndexed { index, view ->
                        if (demandLabel.size>index){
                            view.visibility= View.VISIBLE
                            val label=demandLabel[index]
                            view.setChipIconResource(label.icon)
                            view.text = label.label
                        }else{
                            view.visibility= View.GONE
                        }
                    }
                }
                demandChipGroup.also { chipGroup ->
                    val demandLabel=item.getDemandLabel()
                    for (i in 0 until demandLabel.size-chipGroup.children.filterIsInstance<Chip>().toList().size){
                        val chip= LayoutInflater.from(this@FriendInfoActivity).inflate(R.layout.item_chip_base,chipGroup,false)
                        chipGroup.addView(chip)
                    }

                    val chips=chipGroup.children.filterIsInstance<Chip>()
                    chips.forEachIndexed { index, view ->
                        if (demandLabel.size>index){
                            view.visibility= View.VISIBLE
                            val label=demandLabel[index]
                            view.setChipIconResource(label.icon)
                            view.text = label.label
                        }else{
                            view.visibility= View.GONE
                        }
                    }
                }
            }
//我的认证
            myAuthentication.apply {
                idCardInfo.isSelected=
                if (item.isRealName()){
                    realNameTitle.text="已认证"
                    realNameDes.text=(item.getRealNameNumber()?:"")
                    true
                }else{
                    realNameTitle.text="未认证"
                    realNameDes.text=("未认证")
                    false
                }
                headPorInfo.isSelected=
                if (item.isHeadIdentification()){
                    headPorTitle.text="头像已认证"
                    headPorDes.text=("头像是用户本人真实照片，已通过人脸对比。")
                    true
                }else{
                    headPorTitle.text="头像未认证"
                    headPorDes.text=("头像未认证。")
                    false
                }
            }
            //我的动态
            myDynamic.apply {
                item.getDynamic().also { list ->
                    if (list.isEmpty()){
                        this.visibility= View.GONE
                    }else{
                        this.visibility= View.VISIBLE
                        myDynamicCount.text=("查看所有"+item.getDynamicCount().toString()+"条动态")
                        val imageList=list.flatMap {
                            it.image_url?.replace(", ",",")?.split(",")?: emptyList()
                        }.filter { it.isNotBlank() }
                        val video=list.flatMap {
                            it.video_url?.replace(", ",",")?.split(",")?: emptyList()
                        }.filter { it.isNotBlank() }
                        if (imageList.isEmpty()){
                            dynamicPreview.visibility= View.GONE
                            dynamicText.text=list.firstOrNull()?.text_content.also {
                                if (it.isNullOrBlank()){
                                    this.visibility=View.GONE
                                }
                            }
                        }else {
                            dynamicPreview.setImageData(imageList,video)
                        }
                    }
                }
                toMyDynamic.setOnClickListener {view->
                    IntentManager.getDynamicIntent(view.context,item.getId(),item.getUserSex().code,item.getNickname(),item.getHeadImg())?.also {
                        startActivity(it)
                    }
                }
            }
            //生活
            life_view.apply {
                item.getLifePhoto().also {
                    if (it.isEmpty()){
                        this.visibility=View.GONE
                    }else{
                        this.visibility=View.VISIBLE
                        this.setImageData(it.map {
                            LifeView.LifeImage(it.image_url?:"",it.file_name?:"",it.content?:"")
                        })
                    }
                }
            }
            //底部送花等隐藏
            bottomActionTip.isVisible=dislike.isVisible||sendFlowers.isVisible||care.isVisible
            bottomAction.isVisible=bottomActionTip.isVisible
            nestedScrollView.apply {
                if (!bottomAction.isVisible){
                    richang.visibility= View.GONE
                    return@launch
                }
                var oldScroll=0
                var scrollDY=0
                this.setOnScrollChangeListener (object : NestedScrollView.OnScrollChangeListener{
                    override fun onScrollChange(
                        v: NestedScrollView?,
                        scrollX: Int,
                        scrollY: Int,
                        oldScrollX: Int,
                        oldScrollY: Int
                    ) {
                        scrollDY=scrollY-oldScroll
//                    itemInteraction
                        if (richang.bottom-this@apply.height<=scrollY){
                            richang.visibility= View.GONE
                            itemInteraction.visibility= View.VISIBLE
                            itemInteraction2.visibility= View.GONE
                        }else{
                            richang.visibility= View.VISIBLE
                            itemInteraction.visibility= View.GONE
                            itemInteraction2.visibility= View.VISIBLE
                        }
                    }
                })
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initListener(){
        //简介模块
        distanceView.visibility=View.GONE
        briefIntroduction.apply {
            LocationUtils.observeLocation(this@FriendInfoActivity){
                val taLongitude=userItem?.getLongitude()
                val taLatitude=userItem?.getLatitude()
                if (taLatitude!=null&&taLongitude!=null&&it!=null&&it.latitude!=0.0){
                    distanceView.visibility=View.VISIBLE
                    distance.text=try {
                        val distance= LocationUtils.getDistance(taLatitude,taLongitude,it.latitude,it.longitude)+0.5f
                        "距离您${distance.toInt()}米"
                    }catch (e:Throwable){
                        e.message
                    }
                }else{
                    distanceView.visibility=View.GONE
                }
            }
            if(ContextCompat.checkSelfPermission(this@FriendInfoActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                LocationUtils.startLocation()
            }else{
                distance.text = "点击查看与TA的距离"
                distance.setOnClickListener {
                    XXPermissions.with(this@FriendInfoActivity)
                        .permission(Permission.ACCESS_COARSE_LOCATION)
                        .permission(Permission.ACCESS_FINE_LOCATION)
                        .request(object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean,
                            ) {
                                if (ContextCompat.checkSelfPermission(this@FriendInfoActivity,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                    LocationUtils.startLocation()
                                }
                            }
                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean,
                            ) {

                            }
                        })
                }
            }
        }

        //发出动作，喜欢、不喜欢、送花
        sendAction.apply {
            care.isSelected=(userItem?.isILikeTa()?:false)
            care2.isSelected=(userItem?.isILikeTa()?:false)
            sendFlowers.setOnClickListener {
                if (uploadHeadDialog.showUploadHeadDialog()){
                    return@setOnClickListener
                }
                superLike(userItem?:return@setOnClickListener)
            }
            care.setOnClickListener {
                if (uploadHeadDialog.showUploadHeadDialog()){
                    return@setOnClickListener
                }
                if (it.isSelected){
                    toast("已经喜欢过了")
                    return@setOnClickListener
                }
                like(userItem?:return@setOnClickListener)
            }
            dislike.setOnClickListener {
                if (uploadHeadDialog.showUploadHeadDialog()){
                    return@setOnClickListener
                }
                disLike(userItem?:return@setOnClickListener)
            }

            sendFlowers2.setOnClickListener {
                if (uploadHeadDialog.showUploadHeadDialog()){
                    return@setOnClickListener
                }
                sendFlowers.performClick()
            }
            care2.setOnClickListener {
                if (uploadHeadDialog.showUploadHeadDialog()){
                    return@setOnClickListener
                }
                care.performClick()
            }
            dislike2.setOnClickListener {
                if (uploadHeadDialog.showUploadHeadDialog()){
                    return@setOnClickListener
                }
                dislike.performClick()
            }
        }
        closeMutual.setOnClickListener {
            if (friendViewSwitcher.currentView==mutualLike){
                friendViewSwitcher.showNext()
            }
        }

        if (UserInfo.getUserId()==userId?.toString()){
            sendFlowers2.visibility=View.GONE
            sendFlowers.visibility=View.GONE
        }
    }

    /**
     * 左滑、不喜欢
     */
    private fun disLike(item: RecommendBean){
        lifecycleScope.launch (){
            loadingDialog.show()
            recommendViewModel.otherDisLike(userId?:return@launch toast("id 为空")).also {
                if (it.code==200){

                }else{
                    if (it.code== RecommendCall.RECOMMEND_NOT_HAVE){
                        startActivity(IntentManager.getVipIntent(this@FriendInfoActivity, vipGif = VipGifEnum.MoreView))
                    }
                    toast(it.msg)
                }
            }
            loadingDialog.dismiss()
        }
    }

    /**
     * 右滑、喜欢
     */
    private fun like(item: RecommendBean) {
        loadingDialog.show()
        lifecycleScope.launch (){
            recommendViewModel.otherLike(userId?:return@launch toast("id 为空")){
                if (friendViewSwitcher.currentView!=mutualLike){
                    friendViewSwitcher.showNext()
                    Glide.with(taHead).load(item.getHeadImg())
                        .placeholder(item.getUserSex().smallHead)
                        .placeholder(item.getUserSex().smallHead).into(taHead)
                    sendMsg.setOnClickListener {
                        startActivity(ImChatActivity.getIntent(this@FriendInfoActivity,item.getId().toString()))
                    }
                }
            }.also {t->
                if (t.code==200){
                    care.isSelected=true
                    care2.isSelected=true
                    toast("喜欢成功")
                }else{
                    if (t.code==RecommendCall.RECOMMEND_NOT_HAVE){
                        startActivity(IntentManager.getVipIntent(this@FriendInfoActivity, vipGif = VipGifEnum.MoreView))
                    }
                    toast(t.msg)
                }
            }
            loadingDialog.dismiss()
        }
    }



    /**
     * 超级喜欢、送花
     */
    private fun superLike(item: RecommendBean){
        SendFlowerDialog.sendFlowerTip(this){
            lifecycleScope.launch (){
                loadingDialog.show()
                recommendViewModel.otherSuperLike(userId?:return@launch toast("id 为空")){
                    coinInsufficientDialog.show()
                }.also {
                    if (it.code==200){
                        ImMessageManager.getFlowerMsg(item.getId().toString())?.also {
                            ImMessageManager.sendMsg(it)
                        }
                        toast("送花成功")
                    }else{
                        toast(it.msg)
                    }
                }
                loadingDialog.dismiss()
            }
        }
    }

    private fun playVoice(item: RecommendBean, onPlay:()->Unit, onCompletion:()->Unit, onError:(String)->Unit){
        val path=item.getVoiceUrl()
        if (path==null){
            onError.invoke("语音url为空")
            return iLog("语音url为空")
        }
        PlayAudio.play(path,onPlay,onCompletion,onError)
    }

    private fun pauseVoice(item: RecommendBean){
        PlayAudio.pause()
    }

    private fun stopVoice(){
        PlayAudio.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVoice()
    }
}