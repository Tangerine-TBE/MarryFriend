package com.twx.marryfriend.dynamic.send.location

import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.PlaceSearchBean
import com.twx.marryfriend.bean.dynamic.Poi
import com.twx.marryfriend.bean.dynamic.SearchBean
import com.twx.marryfriend.bean.dynamic.SearchData
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.send.adapter.LocationAdapter
import com.twx.marryfriend.dynamic.send.adapter.SearchAdapter
import kotlinx.android.synthetic.main.activity_location.*
import java.util.*

class LocationActivity : MainBaseViewActivity(),
    com.twx.marryfriend.net.callback.dynamic.IDoPlaceSearchCallback,
    com.twx.marryfriend.net.callback.dynamic.IBaiduSearchCallback {

    private var currentPaper = 1

    // 选择地点名字
    private var name = ""

    // 选择地点坐标
    private var address = ""

    // 当前定位坐标
    private var location = ""

    // 当前城市
    private var city = ""

    private var mList: MutableList<Poi> = arrayListOf()
    private var mSearchList: MutableList<SearchData> = arrayListOf()

    private lateinit var adapter: LocationAdapter
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var doPlaceSearch: com.twx.marryfriend.net.impl.dynamic.doPlaceSearchPresentImpl
    private lateinit var mBaiduSearchPresent: com.twx.marryfriend.net.impl.dynamic.BaiduSearchPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_location

    override fun initView() {
        super.initView()

        location = intent.getStringExtra("location").toString()
        city = intent.getStringExtra("city").toString()

        doPlaceSearch = com.twx.marryfriend.net.impl.dynamic.doPlaceSearchPresentImpl.getsInstance()
        doPlaceSearch.registerCallback(this)

        mBaiduSearchPresent = com.twx.marryfriend.net.impl.dynamic.BaiduSearchPresentImpl.getInstance()
        mBaiduSearchPresent.registerCallback(this)

        adapter = LocationAdapter(mList)
        rv_address_search_container.layoutManager = LinearLayoutManager(this)
        rv_address_search_container.adapter = adapter

        searchAdapter = SearchAdapter(mSearchList)
        rv_address_search_container2.layoutManager = LinearLayoutManager(this)
        rv_address_search_container2.adapter = searchAdapter

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

        et_search_container.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val query = et_search_container.text.toString().trim { it <= ' ' }
                val map: MutableMap<String, String> = TreeMap()
                map[Contents.QUERY] = query
                map[Contents.REGION] = city
                map[Contents.OUTPUT] = "json"
                map[Contents.AK] = "48vcqnOCS39h9H7xmijcdeCy"
                mBaiduSearchPresent.doBaiduSearch(map)
            }
            false
        }

        ll_address_search_none.setOnClickListener {
            // 取出坐标，与地址
            name = "不显示位置"
            address = "0,0"

            val intent = intent
            intent.putExtra("name", name)
            intent.putExtra("address", address)
            setResult(RESULT_OK, intent)
            finish()
        }

        adapter.setOnItemClickListener(object : LocationAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                // 取出坐标，与地址
                name = mList[position].name
                address = mList[position].location

                Log.i("guo", address)

                val intent = intent
                intent.putExtra("name", name)
                intent.putExtra("address", address)
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        searchAdapter.setOnItemClickListener(object : SearchAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {

                // 取出坐标，与地址
                name = mSearchList[position].name
                address =
                    "${mSearchList[position].location.lng},${mSearchList[position].location.lat}"

                Log.i("guo", address)

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

    override fun onBaiduSearchSuccess(searchBean: SearchBean) {
        ll_address_search_near.visibility = View.GONE
        ll_address_search_search.visibility = View.VISIBLE
        mSearchList.clear()
        for (i in 0.until(searchBean.results.size)) {
            mSearchList.add(searchBean.results[i])
        }
        searchAdapter.notifyDataSetChanged()
    }

    override fun onBaiduSearchError() {

    }

    override fun onDoPlaceSearchSuccess(placeSearchBean: PlaceSearchBean) {
        ll_address_search_near.visibility = View.VISIBLE
        ll_address_search_search.visibility = View.GONE
        currentPaper++
        for (i in 0.until(placeSearchBean.pois.size)) {
            mList.add(placeSearchBean.pois[i])
        }
        adapter.notifyDataSetChanged()

    }

    override fun onDoPlaceSearchError() {

    }

}