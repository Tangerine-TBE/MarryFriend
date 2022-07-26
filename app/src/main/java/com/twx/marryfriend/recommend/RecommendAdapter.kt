package com.twx.marryfriend.recommend

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPStaticUtils
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.R
import com.twx.marryfriend.base.BaseViewHolder
import com.twx.marryfriend.bean.RecommendBean
import com.twx.marryfriend.enumeration.HomeCardAction
import com.twx.marryfriend.recommend.widget.LifeView
import com.twx.marryfriend.recommend.widget.MyNestedScrollView
import com.twx.marryfriend.recommend.widget.PicturePreviewView
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class RecommendAdapter() :RecyclerView.Adapter<BaseViewHolder>(){
    companion object{
        private const val IS_FIRST_LISTENER_VOICE_KEY="first_listener_voice"
        private const val IS_FIRST_PUSH_VOICE="first_push_voice"
        private fun isFirstListenerVoice():Boolean{
            return SPStaticUtils.getBoolean(IS_FIRST_LISTENER_VOICE_KEY,true)
        }
        private fun useFirstListenerVoice(){
            SPStaticUtils.put(IS_FIRST_LISTENER_VOICE_KEY,false)
        }

        private fun isFirstPushVoice():Boolean{
            return SPStaticUtils.getBoolean(IS_FIRST_PUSH_VOICE,true)
        }
        private fun useFirstPushVoice(){
            SPStaticUtils.put(IS_FIRST_PUSH_VOICE,false)
        }
    }
    private val mainScope by lazy { MainScope() }
    private val listData=ArrayList<RecommendBean>()
    var openLocationPermissionAction:(()->Unit)?=null
    var disLikeAction:((RecommendBean,View)->Unit)?=null
    var likeAction:((RecommendBean,View)->Unit)?=null
    var superLikeAction:((RecommendBean)->Unit)?=null
    var myLongitude:Double?=null
    var myLatitude:Double?=null
    private var currentPlayVoiceItem:RecommendBean?=null
    var itemAction:((HomeCardAction?)->Unit)?=null

    fun getData():List<RecommendBean>{
        return listData
    }

    fun setData(list: List<RecommendBean>){
        listData.clear()
        listData.addAll(list)
        if (BuildConfig.DEBUG){
            for (i in 0 until 10){
                listData.add(RecommendBean())
            }
        }
        notifyDataSetChanged()
    }

    fun removeAt(index:Int):RecommendBean{
        val e=listData.removeAt(index)
        if (e==currentPlayVoiceItem){
            stopVoice()
        }
        notifyItemRemoved(index)
        return e
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.item_recommend,parent,false)
        return BaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.getView<View>(R.id.homeLike).alpha=0f
        holder.getView<View>(R.id.homeDislike).alpha=0f
        val item=listData[position]
        //简介模块
        holder.getView<View>(R.id.briefIntroduction).apply {
            val taLongitude=item.getLongitude()
            val taLatitude=item.getLatitude()
            val distanceView=holder.getView<View>(R.id.distanceView)
            if (taLatitude!=null&&taLatitude!=0.0&&taLongitude!=null&&taLongitude!=0.0){
                distanceView.visibility=View.VISIBLE
                val distance=holder.getView<TextView>(R.id.distance)
                if(ContextCompat.checkSelfPermission(holder.itemView.context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    val myLongitude= myLongitude
                    val myLatitude=myLatitude
                    if (taLatitude!=null&&taLongitude!=null&&myLongitude!=null&&myLatitude!=null){
                        distance.text=try {
                            val distance= LocationUtils.getDistance(taLatitude,taLongitude,myLatitude,myLongitude)+0.5f
                            "距离您${distance.toInt()}米"
                        }catch (e:Throwable){
                            e.message
                        }
                    }
                }else{
                    distance.text = "点击查看与TA的距离"
                    distanceView.setOnClickListener {
                        openLocationPermissionAction?.invoke()
                    }
                }
            }else{
                distanceView.visibility=View.GONE
            }
            holder.getView<View>(R.id.itemSetting).setOnClickListener {
                toast(it.context,"TODO 设置")
            }
            holder.setImage(R.id.recommendPhoto,item.getHeadImg())
            holder.setText(R.id.itemNickname,item.getNickname())
            if (item.isRealName()){
                holder.getView<View>(R.id.realNameView).visibility=View.VISIBLE
            }else{
                holder.getView<View>(R.id.realNameView).visibility=View.GONE
            }
            if (item.isVip()){
                holder.getView<View>(R.id.vipLabel).visibility=View.VISIBLE
            }else{
                holder.getView<View>(R.id.vipLabel).visibility=View.GONE
            }
            holder.setText(R.id.age,item.getAge().toString()+"岁")
            holder.setText(R.id.occupation,item.getOccupation())
            holder.setText(R.id.education,item.getSchoolName())
            holder.setText(R.id.dynamicCount,item.getDynamicCount().toString()+"条动态")//上面的
            holder.setText(R.id.albumPhotoCount,item.getAlbumPhoto().size.toString()+"张照片")
        }
        //关于我
        holder.getView<View>(R.id.selfIntroduction).apply {
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
                holder.getView<TextView>(R.id.aboutMe).also {
                    if (text.isNotBlank()){
                        it.visibility=View.VISIBLE
                        it.text = text
                    }else{
                        it.visibility=View.GONE
                    }
                }
            }
            holder.getView<ImageView>(R.id.aboutMePhoto).also {
                if (item.getAboutMePhoto().isNullOrBlank()){
                    it.visibility=View.GONE
                }else{
                    it.visibility=View.VISIBLE
                    Glide.with(it).load(item.getAboutMePhoto()).placeholder(R.drawable.ic_big_default_pic).into(it)
                }
            }
        }
        //语音介绍
        holder.getView<View>(R.id.voiceIntroduce).apply {
            if (!item.getVoiceUrl().isNullOrBlank()){
                this.visibility=View.VISIBLE
                holder.setText(R.id.voiceDuration,item.getVoiceDurationStr())
                val firstViewSwitcher=holder.getView<ViewSwitcher>(R.id.firstViewSwitcher)
                val firstVoiceTip=holder.getView<View>(R.id.firstVoiceTip)
                val firstAddVoice=holder.getView<View>(R.id.firstAddVoice)
                if (isFirstListenerVoice()){
                    firstViewSwitcher.visibility=View.VISIBLE
                    if (firstViewSwitcher.currentView!=firstVoiceTip){
                        firstViewSwitcher.showNext()
                    }
                }else{
                    firstViewSwitcher.visibility=View.GONE
                }
                val playAnim=holder.getView<ImageView>(R.id.playAnim)
                holder.getView<View>(R.id.playVoice).setOnClickListener { view ->
                    if (firstViewSwitcher.visibility!=View.GONE) {
                        useFirstListenerVoice()
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

                            if (isFirstPushVoice()){
                                firstViewSwitcher.visibility=View.VISIBLE
                                if (firstViewSwitcher.currentView!=firstAddVoice){
                                    firstViewSwitcher.showNext()
                                }
                                firstAddVoice.setOnClickListener {
                                    if (firstViewSwitcher.visibility!=View.GONE){
                                        firstViewSwitcher.visibility=View.GONE
                                    }
                                    useFirstPushVoice()
                                    //TODO 去上传语音界面
                                    toast(holder.itemView.context,"去上传语音界面")
                                }
                            }
                        },{
                            //出错了
                        })
                    }
                }
                holder.getView<View>(R.id.uploadVoice).setOnClickListener {
                    //TODO
                    toast(it.context,"TODO 说点什么来开启你们的对话吧！")
                }
            }else{
                this.visibility=View.GONE
            }
        }
        //我的相册
        holder.getView<View>(R.id.myAlbum).apply {
            item.getAlbumPhoto().also {
                if (it.isEmpty()){
                    this.visibility=View.GONE
                }else{
                    this.visibility=View.VISIBLE
                    holder.setText(R.id.photoCount,it.size.toString()+"张照片")
                    holder.getView<PicturePreviewView>(R.id.myAlbumPreview).setImageData(it)
                }
            }
            holder.getView<View>(R.id.myAlbumSeeMore).setOnClickListener {
                toast(it.context,"TODO 查看相册")
            }
        }
        //我的标签
        holder.getView<View>(R.id.myLabel).apply {
            holder.getView<ChipGroup>(R.id.baseChipGroup).also { chipGroup ->
                val demandLabel=item.getBaseLabel()
                for (i in 0 until demandLabel.size-chipGroup.children.filterIsInstance<Chip>().toList().size){
                    val chip=LayoutInflater.from(holder.itemView.context).inflate(R.layout.item_chip_base,chipGroup,false)
                    chipGroup.addView(chip)
                }

                val chips=chipGroup.children.filterIsInstance<Chip>()
                chips.forEachIndexed { index, view ->
                    if (demandLabel.size>index){
                        view.visibility=View.VISIBLE
                        val label=demandLabel[index]
                        view.setChipIconResource(label.icon)
                        view.text = label.label
                    }else{
                        view.visibility=View.GONE
                    }
                }
            }
            holder.getView<ChipGroup>(R.id.demandChipGroup).also { chipGroup ->
                val demandLabel=item.getDemandLabel()
                for (i in 0 until demandLabel.size-chipGroup.children.filterIsInstance<Chip>().toList().size){
                    val chip=LayoutInflater.from(holder.itemView.context).inflate(R.layout.item_chip_base,chipGroup,false)
                    chipGroup.addView(chip)
                }

                val chips=chipGroup.children.filterIsInstance<Chip>()
                chips.forEachIndexed { index, view ->
                    if (demandLabel.size>index){
                        view.visibility=View.VISIBLE
                        val label=demandLabel[index]
                        view.setChipIconResource(label.icon)
                        view.text = label.label
                    }else{
                        view.visibility=View.GONE
                    }
                }
            }
        }
//我的认证
        holder.getView<View>(R.id.myAuthentication).apply {
            holder.getView<View>(R.id.idCardInfo).isSelected=
            if (item.isRealName()){
                holder.setText(R.id.realNameDes,item.getRealNameNumber()?:"")
                true
            }else{
                holder.setText(R.id.realNameDes,item.getRealNameNumber()?:"未认证")
                false
            }
            holder.getView<View>(R.id.headPorInfo).isSelected=
            if (item.isHeadIdentification()){
                holder.setText(R.id.headPorDes,"头像是用户本人真实照片，已通过人脸对比。")
                true
            }else{
                holder.setText(R.id.headPorTitle,"未认证")
                holder.setText(R.id.headPorDes,"未认证。")
                false
            }
        }
        //我的动态
        holder.getView<View>(R.id.myDynamic).apply {
            item.getDynamic().also { list ->
                if (list.isEmpty()){
                    this.visibility=View.GONE
                }else{
                    this.visibility=View.VISIBLE
                    holder.setText(R.id.myDynamicCount,"查看所有"+item.getDynamicCount().toString()+"条动态")
                    val dynamic=list.first()
                    val imageList=dynamic.image_url?.split(",")
                    val video=dynamic.video_url?.let {
                        it.split(",")
                    }
                    if (imageList.isNullOrEmpty()){
                        holder.getView<View>(R.id.myDynamic).visibility=View.GONE
                    }else {
                        holder.getView<PicturePreviewView>(R.id.dynamicPreview).setImageData(imageList,video)
                    }
                }
            }
            holder.getView<View>(R.id.toMyDynamic).setOnClickListener {
                toast(it.context,"TODO 跳到动态")
            }
        }
        //生活
        holder.getView<LifeView>(R.id.life_view).apply {
            item.getAlbumPhoto().also {
                if (it.isEmpty()){
                    this.visibility=View.GONE
                }else{
                    this.visibility=View.VISIBLE
                    this.setImageData(it.map {
                        LifeView.LifeImage(it,"标题","暂时没有生活照接口")
                    })
                }
            }
        }

        //发出动作，喜欢、不喜欢、送花
        holder.getView<View>(R.id.sendAction).apply {
            holder.getView<View>(R.id.sendFlowers2).setOnClickListener {
                superLikeAction?.invoke(item)
            }
            holder.getView<View>(R.id.care2).setOnClickListener {
                likeAction?.invoke(item,holder.itemView)
            }
            holder.getView<View>(R.id.dislike2).setOnClickListener {
                disLikeAction?.invoke(item,holder.itemView)
            }

            holder.getView<View>(R.id.sendFlowers).setOnClickListener {
                superLikeAction?.invoke(item)
            }
            holder.getView<View>(R.id.care).setOnClickListener {
                likeAction?.invoke(item,holder.itemView)
            }
            holder.getView<View>(R.id.dislike).setOnClickListener {
                disLikeAction?.invoke(item,holder.itemView)
            }
        }

        holder.getView<MyNestedScrollView>(R.id.nestedScrollView).apply {
            val itemInteraction=holder.getView<View>(R.id.itemInteraction)
            val itemInteraction2=holder.getView<View>(R.id.itemInteraction2)

            var oldScroll=0
            var scrollDY=0
            val richang=holder.getView<View>(R.id.richang)
            this.setOnScrollChangeListener (object :NestedScrollView.OnScrollChangeListener{
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
                        richang.visibility=View.GONE
                        itemInteraction.visibility=View.VISIBLE
                        itemInteraction2.visibility=View.GONE
                    }else{
                        richang.visibility=View.VISIBLE
                        itemInteraction.visibility=View.GONE
                        itemInteraction2.visibility=View.VISIBLE
                    }
                }
            })
            if (itemAction!=null){
                this.actionUp={
                    oldScroll=this.scrollY
                    if (scrollDY>10){
                        itemAction?.invoke(HomeCardAction.upSlide)
                    }
                }
            }
        }
    }

    private fun playVoice(item:RecommendBean, onPlay:()->Unit, onCompletion:()->Unit, onError:(String)->Unit){
        val path=item.getVoiceUrl()
        if (path==null){
            onError.invoke("语音url为空")
            return iLog("语音url为空")
        }
        currentPlayVoiceItem=item
        PlayAudio.play(path,onPlay,onCompletion,onError)
    }

    private fun pauseVoice(item: RecommendBean){
        PlayAudio.pause()
    }

    private fun stopVoice(){
        currentPlayVoiceItem=null
        PlayAudio.stop()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mainScope.cancel()
    }
}