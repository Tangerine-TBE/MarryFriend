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
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.recommend.LocationUtils
import com.twx.marryfriend.recommend.PlayAudio
import com.twx.marryfriend.recommend.RecommendViewModel
import com.twx.marryfriend.recommend.widget.LifeView
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.eLog
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_friend_info.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.coroutines.launch

class FriendInfoActivity:AppCompatActivity(R.layout.activity_friend_info) {
    companion object{
        private const val USER_ID_KEY="user_id_k"
        fun getIntent(context: Context,userId:Int?):Intent?{
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
    private var userItem: RecommendBean?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        isLikeMe.visibility=View.GONE
        Glide.with(myHead).load(UserInfo.getHeadPortrait()).into(myHead)
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
            userItem=item
            initListener()
            //简介模块
            briefIntroduction.apply {
                itemSetting.setOnClickListener {
                    toast(it.context,"TODO 设置")
                }
                Glide.with(this).load(item.getHeadImg()).into(recommendPhoto)
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
                age.text=(item.getAge().toString()+"岁")
                occupation.text=(item.getOccupation())
                education.text=(item.getSchoolName())
                dynamicCount.text=(item.getDynamicCount().toString()+"条动态")//上面的
                albumPhotoCount.text=(item.getLifePhoto().size.toString()+"张照片")
            }
            //关于我
            selfIntroduction.apply {
                val stringBuilder=StringBuilder()
                if (item.getAboutMeLife().isNotBlank()){
                    stringBuilder.append(item.getAboutMeLife()+"\n\n")
                }
                if (item.getAboutMeWork().isNotBlank()){
                    stringBuilder.append(item.getAboutMeWork()+"\n\n")
                }
                if (item.getAboutMeHobby().isNotBlank()){
                    stringBuilder.append(item.getAboutMeHobby())
                }
                stringBuilder.toString().also { text->
                    if (text.isNullOrBlank()&&item.getAboutMePhoto().isNullOrBlank()){
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
                if (item.getAboutMePhoto().isNullOrBlank()){
                    aboutMePhoto.visibility=View.GONE
                }else{
                    aboutMePhoto.visibility=View.VISIBLE
                    Glide.with(aboutMePhoto).load(item.getAboutMePhoto()).placeholder(R.drawable.ic_big_default_pic).into(aboutMePhoto)
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
                    realNameDes.text=(item.getRealNameNumber()?:"")
                    true
                }else{
                    realNameDes.text=(item.getRealNameNumber()?:"未认证")
                    false
                }
                headPorInfo.isSelected=
                if (item.isHeadIdentification()){
                    headPorDes.text=("头像是用户本人真实照片，已通过人脸对比。")
                    true
                }else{
                    headPorDes.text=("未认证。")
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
                        val dynamic=list.first()
                        val imageList=dynamic.image_url?.split(",")
                        val video=dynamic.video_url?.let {
                            it.split(",")
                        }
                        if (imageList.isNullOrEmpty()){
                            myDynamic.visibility= View.GONE
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
            nestedScrollView.apply {
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
            care.isSelected=(userItem?.isLike()?:false)
            care2.isSelected=(userItem?.isLike()?:false)
            sendFlowers.setOnClickListener {
                superLike(userItem?:return@setOnClickListener)
            }
            care.setOnClickListener {
                if (it.isSelected){
                    toast("已经喜欢过了")
                    return@setOnClickListener
                }
                like()
            }
            dislike.setOnClickListener {
                disLike(userItem?:return@setOnClickListener)
            }

            sendFlowers2.setOnClickListener {
                sendFlowers.performClick()
            }
            care2.setOnClickListener {
                care.performClick()
            }
            dislike2.setOnClickListener {
                dislike.performClick()
            }
        }
        sendMsg.setOnClickListener {
            toast("给她发消息")
        }
        closeMutual.setOnClickListener {
            if (friendViewSwitcher.currentView==mutualLike){
                friendViewSwitcher.showNext()
            }
        }
    }

    /**
     * 左滑、不喜欢
     */
    private fun disLike(item: RecommendBean){
        lifecycleScope.launch (){
            loadingDialog.show()
            try {
                recommendViewModel.disLike(userId?:return@launch toast("id 为空"))
            }catch (e:Exception){
                eLog(e.stackTraceToString())
                toast(e.message)
            }
            loadingDialog.dismiss()
        }
    }

    /**
     * 右滑、喜欢
     */
    private fun like() {
        loadingDialog.show()
        lifecycleScope.launch (){
            try {
                toast(recommendViewModel.otherLike(userId?:return@launch toast("id 为空")){
                    if (friendViewSwitcher.currentView!=mutualLike){
                        friendViewSwitcher.showNext()
                        Glide.with(taHead).load(UserInfo.getHeadPortrait()).into(taHead)
                    }
                })
                care.isSelected=true
                care2.isSelected=true
            }catch (e:Exception){
                toast(e.message)
            }
            loadingDialog.dismiss()
        }
    }



    /**
     * 超级喜欢、送花
     */
    private fun superLike(item: RecommendBean){
        lifecycleScope.launch (){
            loadingDialog.show()
            try {
                recommendViewModel.superLike(userId?:return@launch toast("id 为空"))
            }catch (e:Exception){
                toast(e.message)
            }
            loadingDialog.dismiss()
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