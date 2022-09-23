package com.twx.marryfriend.set.black

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.set.DeleteBlackListBean
import com.twx.marryfriend.bean.vip.BlackListBean
import com.twx.marryfriend.bean.vip.BlackListData
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.net.callback.mine.IDoDeleteBlackListCallback
import com.twx.marryfriend.net.callback.mine.IGetBlackListCallback
import com.twx.marryfriend.net.impl.mine.doDeleteBlackListPresentImpl
import com.twx.marryfriend.net.impl.mine.getBlackListPresentImpl
import com.twx.marryfriend.set.adapter.BlacklistAdapter
import kotlinx.android.synthetic.main.activity_black_list.*
import kotlinx.android.synthetic.main.activity_coin_record.*
import java.util.*

class BlackListActivity : MainBaseViewActivity(), IGetBlackListCallback, IDoDeleteBlackListCallback,
    BlacklistAdapter.OnItemClickListener {

    private var currentPaper = 1

    private var mList: MutableList<BlackListData> = arrayListOf()

    private lateinit var adapter: BlacklistAdapter


    private lateinit var getBlackListPresent: getBlackListPresentImpl
    private lateinit var doDeleteBlackListPresent: doDeleteBlackListPresentImpl

    // 移除黑名单的position
    private var deletePosition = 0

    override fun getLayoutView(): Int = R.layout.activity_black_list

    override fun initView() {
        super.initView()

        getBlackListPresent = getBlackListPresentImpl.getsInstance()
        getBlackListPresent.registerCallback(this)

        doDeleteBlackListPresent = doDeleteBlackListPresentImpl.getsInstance()
        doDeleteBlackListPresent.registerCallback(this)

        adapter = BlacklistAdapter(mList)
        adapter.setOnItemClickListener(this)

        rv_blacklist_container.layoutManager = GridLayoutManager(this, 2)
        rv_blacklist_container.adapter = adapter

        sfl_blacklist_refresh.setRefreshHeader(ClassicsHeader(this))
        sfl_blacklist_refresh.setRefreshFooter(ClassicsFooter(this))
    }

    override fun initLoadData() {
        super.initLoadData()
        getBlackList(currentPaper)
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_blacklist_finish.setOnClickListener {
            finish()
        }

        sfl_blacklist_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getBlackList(currentPaper)
            sfl_blacklist_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }


        sfl_blacklist_refresh.setOnLoadMoreListener {
            getBlackList(currentPaper)
            sfl_blacklist_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    // 获取黑名单列表
    private fun getBlackList(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getBlackListPresent.getBlackList(map, page)
    }

    // 移除黑名单
    private fun deleteBlackList(guestId: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.HOST_UID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.GUEST_UID] = guestId.toString()
        doDeleteBlackListPresent.doDeleteBlackList(map)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoDeleteBlackListSuccess(deleteBlackListBean: DeleteBlackListBean?) {
        if (deleteBlackListBean != null) {
            if (deleteBlackListBean.code == 200) {
                ToastUtils.showShort("移出黑名单成功")

                mList.removeAt(deletePosition)
                adapter.notifyDataSetChanged()

            }
        }
    }

    override fun onDoDeleteBlackListCodeError() {

    }

    override fun onGetBlackListSuccess(blackListBean: BlackListBean?) {

        if (blackListBean != null) {
            if (blackListBean.code == 200) {

                if (blackListBean.data.isNotEmpty()) {
                    ll_blacklist_empty.visibility = View.GONE
                }

                if (currentPaper == 1) {
                    mList.clear()
                }

                currentPaper++
                for (i in 0.until(blackListBean.data.size)) {
                    mList.add(blackListBean.data[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_blacklist_refresh != null) {
            sfl_blacklist_refresh.finishRefresh(true)
            sfl_blacklist_refresh.finishLoadMore(true)
        }

    }

    override fun onGetBlackListCodeError() {
        if (sfl_blacklist_refresh != null) {
            sfl_blacklist_refresh.finishRefresh(false)
            sfl_blacklist_refresh.finishLoadMore(false)
        }
    }


    override fun onItemClick(v: View?, position: Int) {

    }

    override fun onItemRemoveClick(v: View?, position: Int) {
        // 移除黑名单
        deletePosition = position
        deleteBlackList(mList[position].guest_uid)

    }

    override fun onItemChatClick(v: View?, position: Int) {

        startActivity(
            ChatActivity.getIntent(this,
                mList[position].guest_uid.toString(),
                mList[position].nick,
                mList[position].image_url,
                true
            )
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        getBlackListPresent.unregisterCallback(this)
        doDeleteBlackListPresent.unregisterCallback(this)
    }

}