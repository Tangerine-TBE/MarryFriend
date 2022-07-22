package com.twx.marryfriend.friend

import android.Manifest
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
import com.kingja.loadsir.core.LoadSir
import com.twx.marryfriend.DefEmptyDataCallBack
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.RecommendBean
import com.twx.marryfriend.recommend.LocationUtils
import com.twx.marryfriend.recommend.PlayAudio
import com.twx.marryfriend.recommend.RecommendViewModel
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.toast
import kotlinx.android.synthetic.main.activity_friend_info.*
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
    private val loadService by lazy {
        LoadSir.getDefault()
            .register(contentView){
            loadData()
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
    private var userItem:RecommendBean?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
        initListener()
        isLikeMe.visibility=View.GONE
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
                loadService.showCallback(DefEmptyDataCallBack::class.java)
                return@launch
            }else{
                myActionBar.setTitle(item.getNickname())
                loadService.showSuccess()
            }
            userItem=item
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
                albumPhotoCount.text=(item.getAlbumPhoto().size.toString()+"张照片")
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
                    Glide.with(aboutMePhoto).load(item.getAboutMePhoto()).into(aboutMePhoto)
                }
            }
            //语音介绍
            voiceIntroduce.apply {
                if (item.getVoiceUrl()!=null){
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
                        }else{
                            playVoice(item,{
                                //playAnim
                                (playAnim.drawable as? AnimationDrawable)?.start()
                                view.isSelected=true
                            },{
                                //播放完成
                                (playAnim.drawable as? AnimationDrawable)?.also {
                                    it.selectDrawable(0)
                                    it.stop()
                                }

                                /*if (RecommendAdapter.isFirstPushVoice()){
                                    firstViewSwitcher.visibility= View.VISIBLE
                                    if (firstViewSwitcher.currentView!=firstAddVoice){
                                        firstViewSwitcher.showNext()
                                    }
                                    firstAddVoice.setOnClickListener {
                                        if (firstViewSwitcher.visibility!= View.GONE){
                                            firstViewSwitcher.visibility= View.GONE
                                        }
                                        RecommendAdapter.useFirstPushVoice()
                                        //TODO 去上传语音界面
                                        toast(holder.itemView.context,"去上传语音界面")
                                    }
                                }*/
                            },{
                                //出错了
                            })
                        }
                    }
                    uploadVoice.setOnClickListener {
                        //TODO
                        toast(it.context,"TODO 说点什么来开启你们的对话吧！")
                    }
                }else{
                    this.visibility= View.GONE
                }
            }
            //我的相册
            myAlbum.apply {
                item.getAlbumPhoto().also {
                    if (it.isEmpty()){
                        this.visibility= View.GONE
                    }else{
                        this.visibility= View.VISIBLE
                        photoCount.text=(it.size.toString()+"张照片")
                        myAlbumPreview.setImageData(it)
                    }
                }
                myAlbumSeeMore.setOnClickListener {
                    toast(it.context,"TODO 查看相册")
                }
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
                if (item.isRealName()){
                    realNameDes.text=(item.getRealNameNumber()?:"")
                    headPorDes.text=("头像是用户本人真实照片，已通过人脸对比。")
                }else{
                    realNameDes.text=(item.getRealNameNumber()?:"未认证")
                    headPorDes.text=("未认证。")
                }
                if (item.isHeadIdentification()){
                    headPorDes.text=("头像是用户本人真实照片，已通过人脸对比。")
                }else{
                    headPorDes.text=("未认证。")
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
                toMyDynamic.setOnClickListener {
                    toast(it.context,"TODO 跳到动态")
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

    private fun initListener(){
        //简介模块
        briefIntroduction.apply {
            LocationUtils.observeLocation(this@FriendInfoActivity){
                val taLongitude=userItem?.getLongitude()
                val taLatitude=userItem?.getLatitude()
                if (taLatitude!=null&&taLongitude!=null){
                    distance.text=try {
                        val distance= LocationUtils.getDistance(taLatitude,taLongitude,it.latitude,it.longitude)+0.5f
                        "距离您${distance.toInt()}米"
                    }catch (e:Throwable){
                        e.message
                    }
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
            sendFlowers.setOnClickListener {
                superLike(userItem?:return@setOnClickListener)
            }
            care.setOnClickListener {
                like(userItem?:return@setOnClickListener)
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
    }

    /**
     * 左滑、不喜欢
     */
    private fun disLike(item:RecommendBean){
        lifecycleScope.launch (){
            loadingDialog.show()
            try {
                recommendViewModel.disLike(item.getId())
            }catch (e:Exception){
                toast(e.message)
            }
            loadingDialog.dismiss()
        }
    }

    /**
     * 右滑、喜欢
     */
    private fun like(item: RecommendBean){
        loadingDialog.show()
        lifecycleScope.launch (){
            try {
                recommendViewModel.like(item.getId())
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
                recommendViewModel.superLike(item.getId())
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