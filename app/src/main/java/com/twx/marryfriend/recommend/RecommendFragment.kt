package com.twx.marryfriend.recommend

import android.Manifest
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
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kingja.loadsir.core.LoadSir
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.R
import com.twx.marryfriend.UserInfo
import com.twx.marryfriend.bean.RecommendBean
import com.twx.marryfriend.enumeration.HomeCardAction
import com.twx.marryfriend.ilove.ILikeActivity
import com.twx.marryfriend.recommend.widget.*
import com.twx.marryfriend.search.SearchParamActivity
import com.xyzz.myutils.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.android.synthetic.main.item_recommend_not_content.*
import kotlinx.coroutines.launch

class RecommendFragment : Fragment(R.layout.fragment_recommend){
    private val recommendAdapter by lazy {
        RecommendAdapter()
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
    private var touchHelper:ItemTouchHelper?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardSwipeView.adapter=recommendAdapter
        val cardCallback = SlideCardCallback()
        touchHelper = ItemTouchHelper(cardCallback)
        cardCallback.removeItemAction={
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
        cardSwipeView.layoutManager= SlideCardLayoutManager()
//        touchHelper.attachToRecyclerView(cardSwipeView)
        CardConfig.initConfig(context)
        loadData()
        initListener()
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationUtils.startLocation()
        }
        LocationUtils.observeLocation(this){
            recommendAdapter.myLatitude=it.latitude
            recommendAdapter.myLongitude=it.longitude
            recommendAdapter.notifyDataSetChanged()
        }
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
                val data=recommendViewModel.loadRecommendUserInfo(list)
                if (BuildConfig.DEBUG&&data.isNotEmpty()){
                    val listData=ArrayList(data)
                    recommendAdapter.setData(listData)
                }else{
                    recommendAdapter.setData(data)
                }
                loadService?.showSuccess()
                if(data.isEmpty()){
                    showView(ViewType.notContent)
                }
                swipeRefreshLayout.isRefreshing=false
            }catch (e:Exception){
                swipeRefreshLayout.isRefreshing=false
                showView(ViewType.notContent)
                toast(e.message)
                if (BuildConfig.DEBUG) {
                    loadService?.showSuccess()
                }
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
        recommendAdapter.likeAction={
            like(it)
        }
        recommendAdapter.disLikeAction={
            disLike(it)
        }
        sendMsg.setOnClickListener {
            toast("给她发消息")
        }
        closeMutual.setOnClickListener {
            showView(ViewType.content)
        }
    }

    /**
     * 左滑、不喜欢
     */
    private fun disLike(item:RecommendBean){
        viewLifecycleOwner.lifecycleScope.launch (){
            loadingDialog.show()
//            try {
//                recommendViewModel.disLike(item.getId())
//            }catch (e:Exception){
//                toast(e.message)
//            }
            loadingDialog.dismiss()
        }
    }

    /**
     * 右滑、喜欢
     */
    private fun like(item: RecommendBean){
        loadingDialog.show()
        viewLifecycleOwner.lifecycleScope.launch (){
            try {
//                val isMutualLike=recommendViewModel.like(item.getId())
//                if (isMutualLike){
//                    showView(ViewType.mutual)
//                    Glide.with(taHead).load(UserInfo.getHeadPortrait()).into(taHead)
//                }
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
        viewLifecycleOwner.lifecycleScope.launch (){
            loadingDialog.show()
            try {
//                recommendViewModel.superLike(item.getId())
            }catch (e:Exception){
                toast(e.message)
            }
            loadingDialog.dismiss()
        }
    }

    enum class ViewType{
        content,mutual,notContent
    }
    private fun showView(type:ViewType){
        when(type){
            ViewType.content -> {
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
                cardSwipeView.visibility=View.GONE
                mutualLike.visibility=View.GONE
                notContent.visibility=View.VISIBLE
            }
        }
    }
}