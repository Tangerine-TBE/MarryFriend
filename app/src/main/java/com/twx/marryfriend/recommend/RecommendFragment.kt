package com.twx.marryfriend.recommend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.twx.marryfriend.BuildConfig
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.RecommendBean
import com.twx.marryfriend.recommend.widget.*
import com.twx.marryfriend.search.SearchActivity
import com.xyzz.myutils.iLog
import com.xyzz.myutils.loadingdialog.LoadingDialogManager
import com.xyzz.myutils.toast
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.item_recommend_mutual_like.*
import kotlinx.coroutines.*

class RecommendFragment : Fragment(){

    private val recommendAdapter by lazy {
        RecommendAdapter()
    }
    private val loadSir by lazy {
        LoadSir.Builder()
            .addCallback(LoadingCallback())
//            .addCallback(ErrorCallback())
            .setDefaultCallback(LoadingCallback::class.java)
            .build()
    }
    private var loadService: LoadService<*>?=null
    private val recommendViewModel by lazy {
        ViewModelProvider(this).get(RecommendViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialogManager.createLoadingDialog()
            .create(requireContext())
            .setCancelable(false)
            .setMessage("请稍后...")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView=inflater.inflate(R.layout.fragment_recommend,container,false)
        loadService = loadSir.register(rootView
        ) {
            iLog("重加载")
        }
        return loadService?.loadLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        R.layout.item_recommend_guide
        cardSwipeView.adapter=recommendAdapter
        val cardCallback = SlideCardCallback()
        val touchHelper = ItemTouchHelper(cardCallback)
        cardCallback.removeItemAction={
            val e=recommendAdapter.removeAt(0)
            if (it==ItemTouchHelper.LEFT){
                iLog("不喜欢")
                disLike(e)
            }else if (it==ItemTouchHelper.RIGHT){
                iLog("喜欢")
                like(e)
            }
            if (recommendAdapter.getData().isEmpty()){
                showView(ViewType.notContent)
            }
        }
        cardSwipeView.layoutManager= SlideCardLayoutManager()
        touchHelper.attachToRecyclerView(cardSwipeView)
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
//        showView(ViewType.content)
    }

    private fun loadData(){
        loadService?.showCallback(LoadingCallback::class.java)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO){
            try {
                val list=recommendViewModel.loadRecommendUserId()
                val data=recommendViewModel.loadRecommendUserInfo(list)
                withContext(Dispatchers.Main){
                    if (BuildConfig.DEBUG&&data.isNotEmpty()){
                        val listData=ArrayList(data)
                        while (listData.size<30){
                            listData.add(data[0].copy())
                        }
                        recommendAdapter.setData(listData)
                    }else{
                        recommendAdapter.setData(data)
                    }
                    loadService?.showSuccess()
                    if(data.isEmpty()){
                        showView(ViewType.notContent)
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    loadService
                    toast(e.message)
                    if (BuildConfig.DEBUG) {
                        loadService?.showSuccess()
                    }
                }
            }
        }
    }

    private fun initListener(){
        heartbeat.setOnClickListener {
            startActivity(Intent(requireContext(),SearchActivity::class.java))
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

    private fun disLike(item:RecommendBean){
        loadingDialog.show()
        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            try {
                recommendViewModel.disLike(item.getId())
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    toast(e.message)
                }
            }
            withContext(Dispatchers.Main){
                loadingDialog.dismiss()
            }
        }
    }

    private fun like(item: RecommendBean){
        loadingDialog.show()
        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            try {
                val isMutualLike=recommendViewModel.like(item.getId())
                withContext(Dispatchers.Main){
                    if (isMutualLike){
                        showView(ViewType.mutual)
                    }
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    toast(e.message)
                }
            }
            withContext(Dispatchers.Main){
                loadingDialog.dismiss()
            }
        }
    }

    private fun superLike(item: RecommendBean){
        loadingDialog.show()
        viewLifecycleOwner.lifecycleScope.launch (Dispatchers.IO){
            try {
                recommendViewModel.superLike(item.getId())
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    toast(e.message)
                }
            }
            withContext(Dispatchers.Main){
                loadingDialog.dismiss()
            }
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