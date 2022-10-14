package com.twx.marryfriend.mine.view.other

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.ImUserInfoHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.MeSeeWhoBean
import com.twx.marryfriend.bean.mine.MeSeeWhoList
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.net.callback.mine.IGetMeSeeWhoCallback
import com.twx.marryfriend.net.impl.mine.getMeSeeWhoPresentImpl
import com.twx.marryfriend.vip.VipActivity
import kotlinx.android.synthetic.main.fragment_view_other.*
import java.util.*

/**
 * 我看过别人
 */
class ViewOtherFragment : Fragment(), IGetMeSeeWhoCallback, ViewOtherAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<MeSeeWhoList> = arrayListOf()

    private lateinit var adapter: ViewOtherAdapter

    private lateinit var getMeSeeWhoPresent: getMeSeeWhoPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_view_other, container, false)
    }

    fun newInstance(context: Context): ViewOtherFragment {
        val fragment = ViewOtherFragment()
        fragment.mContext = context
        return fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initPresent()
        initEvent()
    }

    private fun initView() {
        getMeSeeWhoPresent = getMeSeeWhoPresentImpl.getsInstance()
        getMeSeeWhoPresent.registerCallback(this)

        adapter = ViewOtherAdapter(mList)

        adapter.setOnItemClickListener(this)

        rv_view_other_container.layoutManager = LinearLayoutManager(mContext)
        rv_view_other_container.adapter = adapter

        sfl_view_other_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_view_other_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_view_other_refresh.autoRefresh()

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {
        sfl_view_other_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getViewOtherData(currentPaper)
            sfl_view_other_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_view_other_refresh.setOnLoadMoreListener {
            getViewOtherData(currentPaper)
            sfl_view_other_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getViewOtherData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.PAGE] = page.toString()
        map[Contents.SIZE] = "10"
        getMeSeeWhoPresent.getMeSeeWho(map)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetMeSeeWhoSuccess(meSeeWhoBean: MeSeeWhoBean?) {
        if (meSeeWhoBean != null) {
            if (meSeeWhoBean.data.list.isNotEmpty()) {

                ll_view_other_empty?.visibility = View.GONE

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++

                val idList = arrayListOf<String>()

                for (i in 0.until(meSeeWhoBean.data.list.size)) {
                    mList.add(meSeeWhoBean.data.list[i])

                    idList.add(meSeeWhoBean.data.list[i].host_uid.toString())
                }

                ImUserInfoHelper.addFriendInfo(idList)

                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_view_other_refresh != null) {
            sfl_view_other_refresh.finishRefresh(true)
            sfl_view_other_refresh.finishLoadMore(true)
        }
    }

    override fun onGetMeSeeWhoCodeError() {
        if (sfl_view_other_refresh != null) {
            sfl_view_other_refresh.finishRefresh(false)
            sfl_view_other_refresh.finishLoadMore(false)
        }
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivity(context?.let { FriendInfoActivity.getIntent(it, mList[position].guest_uid) })
    }

    override fun onChatClick(v: View?, position: Int) {
        ToastUtils.showShort("聊天界面跳转")

        if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 0) {

            startActivity(context?.let { VipActivity.getVipIntent(it, 0, VipGifEnum.Message) })

        } else {

            val identity = mList[position].identity_status == 1
            startActivity(context?.let {
                ImChatActivity.getIntent(
                    it,
                    mList[position].guest_uid.toString()
                )
            })

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        getMeSeeWhoPresent.unregisterCallback(this)

    }

}