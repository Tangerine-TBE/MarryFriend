package com.twx.marryfriend.recommend

import android.Manifest
import android.animation.Animator
import android.animation.TimeInterpolator
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.message.ImMessageManager
import com.twx.marryfriend.*
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.bean.vip.SVipGifEnum
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.dialog.*
import com.twx.marryfriend.enumeration.HomeCardAction
import com.twx.marryfriend.friend.FriendInfoViewModel
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.main.MainActivity
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.message.viewmodel.ImChatViewModel
import com.twx.marryfriend.recommend.widget.CardConfig
import com.twx.marryfriend.recommend.widget.RecommendGuideView
import com.twx.marryfriend.recommend.widget.SlideCardCallback
import com.twx.marryfriend.recommend.widget.SlideCardLayoutManager
import com.twx.marryfriend.search.SearchParamActivity
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import com.xyzz.myutils.show.wLog
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.android.synthetic.main.item_recommend_not_content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RecommendFragment : Fragment(R.layout.fragment_recommend){
    private val recommendAdapter by lazy {
        RecommendAdapter(lifecycleScope,imChatViewModel)
    }
    private val imChatViewModel by lazy {
        ViewModelProvider(this).get(ImChatViewModel::class.java)
    }
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog()
            .create(requireContext())
            .setCancelable(false)
            .setMessage("请稍后...")
    }
    private val coinInsufficientDialog by lazy {
        ReChargeCoinDialog(requireActivity())
    }
    private var touchHelper:ItemTouchHelper?=null
    private val followReportDialog by lazy {
        FollowReportDialog(requireContext())
    }
    private val friendInfoViewModel by lazy {
        ViewModelProvider(this).get(FriendInfoViewModel::class.java)
    }
    private val uploadHeadDialog by lazy {
        UploadHeadDialog(requireContext())
    }
    private var preUserVip=UserInfo.getUserVipLevel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test.setOnClickListener {
            toast(UserInfo.getUserId().toString())
            if (BuildConfig.DEBUG){
                startActivity(ImChatActivity.getIntent(requireContext(), ImMessageManager.MY_HELPER_ID))
            }
        }
        UserInfo.getUserId().also {
            val text=if (it==null){
                "id为空"
            }else{
                if (it.toIntOrNull()==null){
                    "id不是int类型,id:${it}"
                }else{
                    return@also
                }
            }
            AlertDialog.Builder(requireContext())
                .setMessage(text)
                .setPositiveButton("确定"){dialog,which->

                }
                .show()
        }
        cardSwipeView.adapter=recommendAdapter
        val cardCallback = SlideCardCallback()
        touchHelper = ItemTouchHelper(cardCallback)
        cardCallback.removeItemAction={ direction ->
            if (uploadHeadDialog.showUploadHeadDialog()){
                recommendAdapter.notifyDataSetChanged()
            }else{
                val e=recommendAdapter.getTopItem()
                if (direction==ItemTouchHelper.LEFT){
                    guideActionCompleteHandler(HomeCardAction.leftSlide)
                    lifecycleScope.launch {
                        cardCallback.swipeEnabled=false
                        disLike(e)
                        recommendAdapter.notifyDataSetChanged()
                        cardCallback.swipeEnabled=true
                    }
                }else if (direction==ItemTouchHelper.RIGHT){
                    guideActionCompleteHandler(HomeCardAction.rightSlide)
                    lifecycleScope.launch {
                        cardCallback.swipeEnabled=false
                        like(e)
                        recommendAdapter.notifyDataSetChanged()
                        cardCallback.swipeEnabled=true
                    }
                }
            }
        }
        cardSwipeView.layoutManager= SlideCardLayoutManager()
        cardSwipeView.apply {
            this.itemAnimator?.addDuration = 0
            this.itemAnimator?.changeDuration = 0
            this.itemAnimator?.moveDuration = 0
            this.itemAnimator?.removeDuration = 0
            (this.itemAnimator as? SimpleItemAnimator)?.setSupportsChangeAnimations(false)
        }
//        touchHelper.attachToRecyclerView(cardSwipeView)
        CardConfig.initConfig(context)
        initListener()
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if (recommendViewSwitcher.currentView!=loadingView){
                recommendViewSwitcher.showNext()
            }
            if (!loadingView.isRunAnimation()){
                loadingView.startAnimation()
            }
            lifecycleScope.launch {
                delay(100)//百度定位sdk有可能没有初始化完成
                var isSuccess=false//百度定位sdk有可能没有初始化完成
                LocationUtils.startLocation(){
                    isSuccess=true
                    loadData()
                }
                delay(5000)//百度定位sdk有可能没有初始化完成
                if (!isSuccess){
                    LocationUtils.startLocation()
                    loadData()
                }
            }
        }else{
            loadData()
        }
        //监听位置变化
        LocationUtils.observeLocation(this){
            it?:return@observeLocation
            recommendAdapter.myLatitude=it.latitude
            recommendAdapter.myLongitude=it.longitude
            recommendAdapter.notifyDataSetChanged()
        }
        //是否展示引导
        if (!RecommendGuideView.isShowGuide()){
            touchHelper?.attachToRecyclerView(cardSwipeView)
            showFillInOrOneClickHello()
        }else{
            guideView.showGuide()
            recommendAdapter.itemAction={
                guideActionCompleteHandler(it)
            }
        }
        Glide.with(myHead)
            .load(UserInfo.getHeadPortrait())
            .placeholder(UserInfo.getDefHeadImage())
            .error(UserInfo.getDefHeadImage())
            .into(myHead)
    }

    private fun showFillInOrOneClickHello(){
        viewLifecycleOwner.lifecycleScope.launch {
            //一键打招呼
            if (OneClickHelloDialog.isSendHello()){
                try {
                    val oneClickHelloBean=recommendViewModel.loadOneClickHelloUserInfo()
                    val data=oneClickHelloBean.data
                    if (!data.isNullOrEmpty()){
                        OneClickHelloDialog(requireContext(),data) {
                            it?:return@OneClickHelloDialog
                            viewLifecycleOwner.lifecycleScope.launch {
                                loadingDialog.show()
                                try {
                                    recommendViewModel.sendHello(it)
                                    toast("打招呼已发出")
                                }catch (e:Exception){
                                    toast(e.message)
                                }
                                loadingDialog.dismiss()
                            }
                        }.show()
                    }
                }catch (e:Exception){
                    iLog("获取失败")
                }
            }else{
                UserInfo.getNextNotFillIn(requireContext()){ permissions, pair->
                    if (pair!=null&&IntentManager.isOpenOneFillIn()){
                        startActivity(IntentManager.toFillInDialogIntent(requireContext()))
                    }
                }
            }
        }
    }

    private fun guideActionCompleteHandler(action:HomeCardAction?){
        if (!RecommendGuideView.isShowGuide()){
            return
        }
        if (action!=null) {
            guideView.guideComplete(action)
            if (action==HomeCardAction.upSlide){
                touchHelper?.attachToRecyclerView(cardSwipeView)
            }
        }else{
            guideView.showGuide()
        }
    }

    fun dispatchTouchEvent(ev: MotionEvent?){
        if (ev?.action==MotionEvent.ACTION_UP){
            guideActionCompleteHandler(null)
        }
    }

    private fun loadData(){
        if (recommendViewSwitcher.currentView!=loadingView){
            recommendViewSwitcher.showNext()
        }
        if (!loadingView.isRunAnimation()){
            loadingView.startAnimation()
        }
        viewLifecycleOwner.lifecycleScope.launch(){
            try {
                val list=recommendViewModel.loadRecommendUserId()
                val data=if (list.isEmpty()){
                    emptyList<RecommendBean>()
                }else{
                    recommendViewModel.loadRecommendUserInfo(list)
                }
                recommendAdapter.setData(data)
                showContentView()
                if(data.isEmpty()){
                    showView(ViewType.notContent)
                }else{
                    showView(ViewType.content)
                }
                swipeRefreshLayout.isRefreshing=false
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                swipeRefreshLayout.isRefreshing=false
                toast(e.message)
                showContentView()
                showView(ViewType.notContent)
            }
            try {
                val lastDynamic=recommendViewModel.loadLaseDynamic()
                if(lastDynamic==null){
                    lastTextDynamicView.isVisible=false
                    lastDynamicView.isVisible=false
                }else{
                    lastTextDynamicView.isVisible=lastDynamic.image_url.isBlank()
                    lastDynamicView.isVisible= lastDynamic.image_url.isNotBlank()

                    if (lastDynamic.image_url.isBlank()){
                        lastTextDynamic.text=lastDynamic?.text_content
                    }else{
                        Glide.with(lastDynamicImage).load(lastDynamic.image_url.split(",").firstOrNull()).placeholder(R.mipmap.ic_launcher).into(lastDynamicImage)
                        lastDynamicDes/*.setExpandableText(lastDynamic?.text_content?:"", 30, "查看更多>", "收起")*/
                            .text=lastDynamic?.text_content
                    }
                    lastTextDynamicView.setOnClickListener {
                        lastDynamicView.performClick()
                    }
                    lastDynamicView.setOnClickListener {
                        val activity=requireActivity()
                        if (activity is MainActivity){
                            activity.addDynamicFragment(lastDynamic)
                        }else{
                            toast("查看更多")
                        }
                    }
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
            }
        }
    }

    private fun showContentView(){
        if (recommendViewSwitcher.currentView!=contentViewSwitch){
            recommendViewSwitcher.showNext()
        }
    }

    private fun initListener(){
        recommendSetting.setOnClickListener {
            startActivity(Intent(requireContext(),ILikeActivity::class.java))
        }
        swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
        heartbeat.setOnClickListener {
            startActivity(Intent(requireContext(),SearchParamActivity::class.java))
        }
        recommendAdapter.openLocationPermissionAction={
            XXPermissions.with(this)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>?,
                        all: Boolean,
                    ) {
                        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                            LocationUtils.startLocation()
                            recommendAdapter.notifyDataSetChanged()
                        }
                    }
                    override fun onDenied(
                        permissions: MutableList<String>?,
                        never: Boolean,
                    ) {

                    }
                })
        }
        recommendAdapter.superLikeAction={item,view->
            if(!uploadHeadDialog.showUploadHeadDialog()){
                SendFlowerDialog.sendFlowerTip(requireContext()){
                    if (view.isEnabled){
                        lifecycleScope.launch {
                            val t1=async (Dispatchers.Main){
                                superLike(item)
                            }
                            val t2=async (Dispatchers.Main) {
                                moveAnimationView(view,true)
                            }
                            t1.await()
                            t2.await()
                            guideView.guideComplete(HomeCardAction.clickFlower)
                            delay(50)
                            view.apply {
                                this.alpha = 1f
                                this.rotation = 0f
                                this.translationX = 0f
                            }
                            view.isEnabled=true
                        }
                        view.isEnabled=false
                    }else{
                        toast("请稍后")
                    }
                }
            }
        }
        recommendAdapter.likeAction={item,view->
            if(uploadHeadDialog.showUploadHeadDialog()){

            }else{
                iLog("开始执行喜欢")
                if (view.isEnabled){
                    lifecycleScope.launch {
                        val t1=async (Dispatchers.Main){
                            like(item)
                        }
                        val t2=async (Dispatchers.Main) {
                            moveAnimationView(view,true)
                        }
                        t1.await()
                        t2.await()
                        delay(50)
                        view.apply {
                            this.alpha = 1f
                            this.rotation = 0f
                            this.translationX = 0f
                        }
                        view.isEnabled=true
                    }
                    view.isEnabled=false
                }else{
                    toast("请稍后")
                }
            }
        }
        recommendAdapter.disLikeAction={item,view->
            if(uploadHeadDialog.showUploadHeadDialog()){

            }else{
                if (view.isEnabled){
                    lifecycleScope.launch {
                        val t1=async (Dispatchers.Main){
                            disLike(item)
                        }
                        val t2=async (Dispatchers.Main) {
                            moveAnimationView(view,false)
                        }
                        t1.await()
                        t2.await()
                        delay(50)
                        view.apply {
                            this.alpha = 1f
                            this.rotation = 0f
                            this.translationX = 0f
                        }
                        view.isEnabled=true
                    }
                    view.isEnabled=false
                }else{
                    toast("请稍后")
                }
            }
        }
        recommendAdapter.reportAction={
            startActivity(IntentManager.getReportIntent(requireContext(),it.getId()))
        }
        recommendAdapter.blacklistAction={blacklist,item,isBlock->
            lifecycleScope.launch{
                if(isBlock){
                    try {
                        imChatViewModel.removeBlockList(item.getId().toString()?:return@launch)
                        toast("取消屏蔽成功")
                        blacklist.text="屏蔽"
                    }catch (e:Exception){
                        toast(e.message)
                    }
                }else{
                    try {
                        imChatViewModel.addBlockList(item.getId()?.toString()?:return@launch)
                        toast("屏蔽成功")
                        blacklist.text="取消屏蔽"
                    }catch (e:Exception){
                        toast(e.message)
                    }
                }
            }
        }
        recommendAdapter.settingAction={
            settingDialog(it)
        }
        closeMutual.setOnClickListener {
            showView(ViewType.content)
        }
        LocationUtils.frontBackstageLiveData(this)
    }

    private suspend fun moveAnimationView(view: View, isLike:Boolean)=
        suspendCoroutine<Unit>{ continuation ->
            var isEnd=false
            val f=if (isLike)
                1
            else
                -1
            view
                .animate()
                ?.alpha(0f)
                ?.rotation(f*10f)
                ?.translationX(f*view.width /2f)
                ?.setDuration(500)
                ?.also {
                    it.interpolator=object :TimeInterpolator{
                        override fun getInterpolation(input: Float): Float {
                            if (cardSwipeView.childCount>=2){
                                val itemView=cardSwipeView.getChildAt(1)
                                itemView.rotation=0f
                                itemView.translationY=-(CardConfig.TRANS_Y_GAP * (1-input))
                                itemView.scaleX=1 - CardConfig.SCALE_GAP * (1-input)
                                itemView.scaleY=1 - CardConfig.SCALE_GAP * (1-input)
                            }
//                        for (i in min(cardSwipeView.childCount,CardConfig.MAX_SHOW_COUNT)-1 downTo 0 ){
//                            val itemView=cardSwipeView.getChildAt(1)
//                            if (itemView==view){
//                                continue
//                            }
//                            itemView.rotation=0f
//                            itemView.translationY=-(CardConfig.TRANS_Y_GAP * (i-input))
//                            itemView.scaleX=1 - CardConfig.SCALE_GAP * (i-input)
//                            itemView.scaleY=1 - CardConfig.SCALE_GAP * (i-input)
//                        }
                            return input
                        }
                    }
                }
                ?.setListener(object : Animator.AnimatorListener{
                    override fun onAnimationStart(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        if (!isEnd){
                            isEnd=true
                            continuation.resume(Unit)
                        }
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        iLog("动画取消了")
                        if (!isEnd){
                            isEnd=true
                            continuation.resume(Unit)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                })
                ?.start()
        }

    private fun settingDialog(userItem:RecommendBean){
        followReportDialog.also {
            if (userItem.isFollow()==true){
                it.setFollowText("取消关注")
            }else{
                it.setFollowText("关注")
            }
            val guest_uid=userItem.getId()
            it.setFollowListener{ //关注或者取消关注
                if (guest_uid?.toString()== UserInfo.getUserId()){
                    toast("自己不能关注自己")
                    return@setFollowListener
                }
                lifecycleScope.launch {
                    if (userItem?.isFollow()==true){
                        try {
                            friendInfoViewModel.unFollow(guest_uid?:return@launch)
                            userItem?.clearFollow()
                            toast("取消关注成功")
                        }catch (e:Exception){
                            toast("取消关注失败，${e.message}")
                        }
                    }else{
                        try {
                            friendInfoViewModel.follow(guest_uid?:return@launch)
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

            it.setReportId (userItem.getId())
        }.show()
    }

    override fun onStart() {
        super.onStart()
        if (preUserVip!=UserInfo.getUserVipLevel()) {
            iLog("vip等级发生了变化")
            preUserVip=UserInfo.getUserVipLevel()
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (recommendAdapter.itemCount>0){
            recommendAdapter.lifeView?.refreshView()
        }else{
            notContent.refreshView(lifecycleScope)
        }
    }

    /**
     * 左滑、不喜欢
     */
    private suspend fun disLike(item: RecommendBean){
        iLog("不喜欢")
        if (RecommendGuideView.isShowGuide()){
            iLog("引导期间")
            recommendAdapter.removeItem(item)
            recommendAdapter.addItem(item)
            if (recommendAdapter.getData().isEmpty()){
                showView(ViewType.notContent)
            }
            return
        }
//            loadingDialog.show()
        val t=recommendViewModel.disLike(item.getId())
//            loadingDialog.dismiss()

        if (t.code==200){
            notData()
            recommendAdapter.removeItem(item)
            if (recommendAdapter.getData().isEmpty()){
                showView(ViewType.notContent)
            }
        }else{
            if (t.code==RecommendCall.RECOMMEND_NOT_HAVE){
                notData()
            }
            toast(t.msg)
        }
    }

    /**
     * 右滑、喜欢
     */
    private suspend fun like(item: RecommendBean){
        iLog("喜欢")
        if (RecommendGuideView.isShowGuide()){
            iLog("引导期间")

            recommendAdapter.removeItem(item)
            recommendAdapter.addItem(item)
            if (recommendAdapter.getData().isEmpty()){
                showView(ViewType.notContent)
            }
            return
        }
        //            loadingDialog.show()
        val t=recommendViewModel.like(item.getId()) {
            showView(ViewType.mutual)
            Glide.with(taHead)
                .load(item.getHeadImg())
                .into(taHead)
            sendMsg.setOnClickListener {
                startActivity(ImChatActivity.getIntent(requireContext(),item.getId().toString()))
                showView(ViewType.content)
            }
        }
//            loadingDialog.dismiss()

        if (t.code==200){
            recommendAdapter.removeItem(item)
            if (recommendAdapter.getData().isEmpty()){
                showView(ViewType.notContent)
            }
        }else{
            if (t.code==RecommendCall.RECOMMEND_NOT_HAVE){
                notData()
            }
            toast(t.msg)
        }
    }

    /**
     * 超级喜欢、送花
     */
    private suspend fun superLike(item: RecommendBean){
        if (RecommendGuideView.isShowGuide()){
            iLog("引导期间")
            recommendAdapter.removeItem(item)
            recommendAdapter.addItem(item)
            if (recommendAdapter.getData().isEmpty()){
                showView(ViewType.notContent)
            }
            return
        }
        loadingDialog.show()
        recommendViewModel.superLike(item.getId()) {
            coinInsufficientDialog.show(item.getHeadImg())
        }.also {
            if (it.code==200){
                ImMessageManager.getFlowerMsg(item.getId().toString())?.also {
                    ImMessageManager.sendMsg(it)
                }
                recommendAdapter.removeAt(0)
                if (recommendAdapter.getData().isEmpty()){
                    showView(ViewType.notContent)
                }
            }else{
                toast(it.msg)
            }
        }
        loadingDialog.dismiss()
    }

    private fun notData(){
        recommendAdapter.cleanData()
        showView(ViewType.notContent)
    }

    enum class ViewType{
        content,mutual,notContent
    }
    private fun showView(type:ViewType){
        when(type){
            ViewType.content -> {
                guideView.onDataChange(true)
                cardSwipeView.visibility=View.VISIBLE
                mutualLike.visibility=View.GONE
                notContent.visibility=View.GONE
            }
            ViewType.mutual -> {
                cardSwipeView.visibility=View.GONE
                mutualLike.visibility=View.VISIBLE
                notContent.visibility=View.GONE
            }
            ViewType.notContent -> {
                if(UserInfo.isSuperVip()){
                    moreContent.text="查看更多动态"
                    moreContent.setOnClickListener {
                        iLog("查看更多动态")
                        val activity=requireActivity()
                        if (activity is MainActivity){
                            activity.addDynamicFragment(null)
                        }else{
                            toast("查看更多")
                        }
                    }
                }else{
                    moreContent.text="查看更多嘉宾"
                    moreContent.setOnClickListener {
                        iLog("查看更多嘉宾")
                        if (UserInfo.isVip()){
                            startActivity(IntentManager.getSuperVipIntent(requireContext(), sVipGifEnum = SVipGifEnum.MoreView))
                        }else{
                            startActivity(IntentManager.getVipIntent(requireContext(), vipGif = VipGifEnum.MoreView))
                        }
                    }
                }
                val success=recommendViewModel.startCountDownTimer {
                    iLog("到了推荐时间，刷新数据")
                    loadData()
                }
                if (success){
                    recommendViewModel.countDownTimerLiveData.observe(viewLifecycleOwner){
                        recomendTime.text=it
                    }
                }else{
                    recomendTime.text="明天中午12点整"
                }
                guideView.onDataChange(false)
                cardSwipeView.visibility=View.GONE
                mutualLike.visibility=View.GONE
                notContent.visibility=View.VISIBLE
            }
        }
    }
}