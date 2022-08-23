package com.twx.marryfriend.coin

import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.vip.CoinRecordBean
import com.twx.marryfriend.bean.vip.CoinRecordList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.net.callback.vip.IGetCoinRecordCallback
import com.twx.marryfriend.net.impl.vip.getCoinRecordPresentImpl
import kotlinx.android.synthetic.main.activity_coin_record.*
import kotlinx.android.synthetic.main.fragment_like.*
import java.util.*

class CoinRecordActivity : MainBaseViewActivity(), IGetCoinRecordCallback {

    private var currentPaper = 1

    private var mList: MutableList<CoinRecordList> = arrayListOf()

    private lateinit var adapter: CoinRecordAdapter

    private lateinit var getCoinRecordPresent: getCoinRecordPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_coin_record

    override fun initView() {
        super.initView()

        getCoinRecordPresent = getCoinRecordPresentImpl.getsInstance()
        getCoinRecordPresent.registerCallback(this)

        adapter = CoinRecordAdapter(mList)

        rv_coin_record_container.layoutManager = LinearLayoutManager(this)
        rv_coin_record_container.adapter = adapter

        sfl_coin_record_refresh.setRefreshHeader(ClassicsHeader(this))
        sfl_coin_record_refresh.setRefreshFooter(ClassicsFooter(this))

    }

    override fun initLoadData() {
        super.initLoadData()
        getCoinRecord(currentPaper)
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_coin_record_finish.setOnClickListener {
            finish()
        }

        sfl_coin_record_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getCoinRecord(currentPaper)
            sfl_coin_record_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_coin_record_refresh.setOnLoadMoreListener {
            getCoinRecord(currentPaper)
            sfl_coin_record_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getCoinRecord(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getCoinRecordPresent.getCoinRecord(map, page)


    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetCoinRecordSuccess(coinRecordBean: CoinRecordBean?) {
        if (coinRecordBean != null) {
            if (coinRecordBean.code == 200) {
                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(coinRecordBean.data.list.size)) {
                    mList.add(coinRecordBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_coin_record_refresh != null) {
            sfl_coin_record_refresh.finishRefresh(true)
            sfl_coin_record_refresh.finishLoadMore(true)
        }

    }

    override fun onGetCoinRecordCodeError() {
        if (sfl_coin_record_refresh != null) {
            sfl_coin_record_refresh.finishRefresh(false)
            sfl_coin_record_refresh.finishLoadMore(false)
        }
    }

}