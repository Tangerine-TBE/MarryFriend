package com.twx.marryfriend.recommend

import android.Manifest
import android.animation.Animator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hyphenate.easeim.MainActivity
import com.kingja.loadsir.core.LoadSir
import com.message.ImMessageManager
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.IntentManager
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.recommend.RecommendBean
import com.twx.marryfriend.dialog.OneClickHelloDialog
import com.twx.marryfriend.dialog.ReChargeCoinDialog
import com.twx.marryfriend.dialog.SendFlowerDialog
import com.twx.marryfriend.enumeration.HomeCardAction
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.recommend.widget.*
import com.twx.marryfriend.search.SearchParamActivity
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.setExpandableText
import com.xyzz.myutils.show.iLog
import com.xyzz.myutils.show.toast
import com.xyzz.myutils.show.wLog
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.android.synthetic.main.item_recommend_not_content.*
import kotlinx.coroutines.launch

class RecommendFragment : Fragment(R.layout.fragment_recommend){
    private val recommendAdapter by lazy {
        RecommendAdapter(lifecycleScope)
    }
    private val loadSir by lazy {
        LoadSir.Builder()
            .addCallback(LoadingCallback())
//            .addCallback(MutualLikeCallback())
//            .addCallback(NotContentCallback())
            .setDefaultCallback(LoadingCallback::class.java)
            .build()
    }
    private val loadService by lazy {
        loadSir.register(contentViewSwitch
        ) {
            iLog("重加载")
        }
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
        ReChargeCoinDialog(requireContext())
    }
    private var touchHelper:ItemTouchHelper?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        test.setOnClickListener {
//            startActivity(Intent(requireContext(),BeginActivity::class.java))
            startActivity(Intent(requireContext(), MainActivity::class.java))
//            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
        cardSwipeView.adapter=recommendAdapter
        val cardCallback = SlideCardCallback()
        touchHelper = ItemTouchHelper(cardCallback)
        cardCallback.removeItemAction={
            if (recommendViewModel.haveMoreRecommend.value==false){
                recommendAdapter.notifyDataSetChanged()
                openVip()
            }else{
                val e=recommendAdapter.removeAt(0)
                if (it==ItemTouchHelper.LEFT){
                    guideActionCompleteHandler(HomeCardAction.leftSlide)
                    iLog("不喜欢")
                    disLike(e)
                }else if (it==ItemTouchHelper.RIGHT){
                    guideActionCompleteHandler(HomeCardAction.rightSlide)
                    iLog("喜欢")
                    like(e)
                }
                if (recommendAdapter.getData().isEmpty()){
                    showView(ViewType.notContent)
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
        loadData()
        initListener()
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationUtils.startLocation()
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
        }else{
            guideView.showGuide()
            recommendAdapter.itemAction={
                guideActionCompleteHandler(it)
            }
        }
//        showView(ViewType.content)
//        showView(ViewType.mutual)
        Glide.with(myHead).load(UserInfo.getHeadPortrait()).into(myHead)
        recommendViewModel.haveMoreRecommend.observe(viewLifecycleOwner) {

        }
        showFillInOrOneClickHello()
    }

    private fun showFillInOrOneClickHello(){
        viewLifecycleOwner.lifecycleScope.launch {
            if (UserInfo.getNextNotFillIn(requireContext(),this)!=null&&IntentManager.isOpenOneFillIn()){
                startActivity(IntentManager.toFillInDialogIntent(requireContext()))
            }else{
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
        loadService?.showCallback(LoadingCallback::class.java)
        viewLifecycleOwner.lifecycleScope.launch(){
            try {
                val list=recommendViewModel.loadRecommendUserId()
                val data=if (list.isEmpty()){
                    emptyList<RecommendBean>()
                }else{
                    recommendViewModel.loadRecommendUserInfo(list)
                }
                recommendAdapter.setData(data)
                loadService?.showSuccess()
                if(data.isEmpty()){
                    showView(ViewType.notContent)
                }else{
                    showView(ViewType.content)
                }
                swipeRefreshLayout.isRefreshing=false
            }catch (e:Exception){
                wLog(e.stackTraceToString())
                swipeRefreshLayout.isRefreshing=false
                showView(ViewType.notContent)
                toast(e.message)
                if (BuildConfig.DEBUG) {
                    loadService?.showSuccess()
                }
            }
            try {
                val lastDynamic=recommendViewModel.loadLaseDynamic()
                Glide.with(lastDynamicImage).load(lastDynamic.data?.image_url).placeholder(R.mipmap.ic_launcher).into(lastDynamicImage)
                lastDynamicTitle.text=lastDynamic.data?.label
                lastDynamicDes.setExpandableText(lastDynamic.data?.text_content?:"", 3, "查看更多>", "收起")
                lastDynamicView.setOnClickListener {
                    toast("最后一条动态")
                }
            }catch (e:Exception){
                wLog(e.stackTraceToString())
            }
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
        recommendAdapter.superLikeAction={
            guideView.guideComplete(HomeCardAction.clickFlower)
            superLike(it)
        }
        recommendAdapter.likeAction={item,view->
            if (recommendViewModel.haveMoreRecommend.value==false){
                openVip()
            }else{
                view
                    .animate()
                    ?.alpha(0f)
                    ?.rotation(10f)
                    ?.translationX(view.width /2f)
                    ?.setDuration(500)
                    ?.setListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            view.apply {
                                this.alpha=1f
                                this.rotation=0f
                                this.translationX=0f
                            }
                            recommendAdapter.removeAt(0)
                            iLog("喜欢")
                            like(item)
                            if (recommendAdapter.getData().isEmpty()){
                                showView(ViewType.notContent)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                    })
                    ?.start()
            }
        }
        recommendAdapter.disLikeAction={item,view->
            if (recommendViewModel.haveMoreRecommend.value==false){
                openVip()
            }else{
                view
                    .animate()
                    ?.alpha(0f)
                    ?.rotation(-10f)
                    ?.translationX(view.width /-2f)
                    ?.setDuration(500)
                    ?.setListener(object : Animator.AnimatorListener{
                        override fun onAnimationStart(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            view.apply {
                                this.alpha=1f
                                this.rotation=0f
                                this.translationX=0f
                            }
                            recommendAdapter.removeAt(0)
                            iLog("不喜欢")
                            disLike(item)
                            if (recommendAdapter.getData().isEmpty()){
                                showView(ViewType.notContent)
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                    })
                    ?.start()
            }
        }
        sendMsg.setOnClickListener {
            toast("给她发消息")
        }
        closeMutual.setOnClickListener {
            showView(ViewType.content)
        }
        LocationUtils.frontBackstageLiveData(this)
    }

    override fun onResume() {
        super.onResume()
        if (recommendAdapter.itemCount>0){
            recommendAdapter.lifeView?.refreshView(lifecycleScope)
        }else{
            notContent.refreshView(lifecycleScope)
        }
    }

    /**
     * 左滑、不喜欢
     */
    private fun disLike(item: RecommendBean){
        if (RecommendGuideView.isShowGuide()){
            iLog("引导期间")
            return
        }
        if (BuildConfig.DEBUG){
            return
        }
        viewLifecycleOwner.lifecycleScope.launch (){
            loadingDialog.show()
            try {
                recommendViewModel.disLike(item.getId(),{
                    openVip()
                })
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
        if (RecommendGuideView.isShowGuide()){
            iLog("引导期间")
            return
        }
        if (BuildConfig.DEBUG){
            return
        }
        loadingDialog.show()
        viewLifecycleOwner.lifecycleScope.launch (){
            try {
                val str=recommendViewModel.like(item.getId(),{
                    showView(ViewType.mutual)
                    Glide.with(taHead).load(UserInfo.getHeadPortrait()).into(taHead)
                },{
                    openVip()
                })
                ImMessageManager.sendTextMsg(item.getId().toString(), UserInfo.getGreetText())
                toast(str)
            }catch (e:Exception){
                e.message?.also {
                    toast(it)
                }
            }
            loadingDialog.dismiss()
        }
    }

    /**
     * 超级喜欢、送花
     */
    private fun superLike(item: RecommendBean){
        if (RecommendGuideView.isShowGuide()){
            iLog("引导期间")
            return
        }
        SendFlowerDialog.sendFlowerTip(requireContext()){
            viewLifecycleOwner.lifecycleScope.launch (){
                loadingDialog.show()
                try {
                    recommendViewModel.superLike(item.getId()) {
                        coinInsufficientDialog.show()
                    }
                    ImMessageManager.sendFlower(item.getId().toString())
                    toast("送花成功")
                }catch (e:Exception){
                    toast(e.message)
                }
                loadingDialog.dismiss()
            }
        }
    }

    private fun openVip(){
        startActivity(IntentManager.getVipIntent(requireContext()))
//        AlertDialog.Builder(requireContext())
//            .setMessage("需要开通会员解锁更多")
//            .setPositiveButton("去开通"){_,_->
//                startActivity(IntentManager.getVipIntent(requireContext()))
//            }
//            .setNegativeButton("取消"){_,_->
//
//            }
//            .show()
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
                if(UserInfo.isVip()){
                    moreContent.text="查看更多动态"
                    moreContent.setOnClickListener {
                        iLog("查看更多动态")
                        toast("查看更多动态")
                    }
                    val success=recommendViewModel.startCountDownTimer {
                        loadData()
                    }
                    if (success){
                        recommendViewModel.countDownTimerLiveData.observe(viewLifecycleOwner){
                            recomendTime.text=it
                        }
                    }
                }else{
                    moreContent.text="查看更多嘉宾"
                    moreContent.setOnClickListener {
                        iLog("查看更多嘉宾")
                        openVip()
                    }
                }

                guideView.onDataChange(false)
                cardSwipeView.visibility=View.GONE
                mutualLike.visibility=View.GONE
                notContent.visibility=View.VISIBLE
            }
        }
    }
}