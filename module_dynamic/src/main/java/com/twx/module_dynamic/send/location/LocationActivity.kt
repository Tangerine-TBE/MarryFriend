package com.twx.module_dynamic.send.location

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SPStaticUtils
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.PlaceSearchBean
import com.twx.module_dynamic.bean.Poi
import com.twx.module_dynamic.net.callback.IDoPlaceSearchCallback
import com.twx.module_dynamic.net.impl.doPlaceSearchPresentImpl
import com.twx.module_dynamic.send.adapter.LocationAdapter
import kotlinx.android.synthetic.main.activity_location.*
import java.util.*

class LocationActivity : MainBaseViewActivity(), IDoPlaceSearchCallback {

    private var currentPaper = 1

    // 选择地点名字
    private var name = ""

    // 选择地点坐标
    private var address = ""

    // 当前定位坐标
    private var location = ""

    private var mList: MutableList<Poi> = arrayListOf()

    private lateinit var adapter: LocationAdapter

    private lateinit var doPlaceSearch: doPlaceSearchPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_location

    override fun initView() {
        super.initView()

        location = intent.getStringExtra("location").toString()

        doPlaceSearch = doPlaceSearchPresentImpl.getsInstance()
        doPlaceSearch.registerCallback(this)


        adapter = LocationAdapter(mList)
        rv_address_search_container.layoutManager = LinearLayoutManager(this)
        rv_address_search_container.adapter = adapter


        rv_address_search_container.isNestedScrollingEnabled = false;

        // 瀑布流加载刷新
        rv_address_search_container.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (rv_address_search_container.canScrollVertically(1)) {                    //lastVisibleItem >= totalItemCount - 4 为可自定义的值，代表在还有4个item即可加载完所有的item时进行加载；dy > 0判断屏幕是否还可以向下滑动
                    doPlaceSearch(currentPaper)
                }
            }
        })

    }

    override fun initLoadData() {
        super.initLoadData()

        doPlaceSearch(1)
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        adapter.setOnItemClickListener(object : LocationAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                // 取出坐标，与地址
                name = mList[position].name
                address = mList[position].location

                val intent = intent
                intent.putExtra("name", name)
                intent.putExtra("address", address)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

    }

    // 搜索附近地点
    private fun doPlaceSearch(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.KEY] = "c3f19644948b0b0134b70c5bd424cc5f"
        map[Contents.LOCATION] = location
        map[Contents.PAGE] = page.toString()
        doPlaceSearch.doPlaceSearch(map)

    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoPlaceSearchSuccess(placeSearchBean: PlaceSearchBean) {
        currentPaper++
        for (i in 0.until(placeSearchBean.pois.size)) {
            mList.add(placeSearchBean.pois[i])
        }
        adapter.notifyDataSetChanged()

    }

    override fun onDoPlaceSearchError() {

    }

}