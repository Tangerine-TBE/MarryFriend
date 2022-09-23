package com.twx.marryfriend.mine.focus.mine

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.DiscussList
import com.twx.marryfriend.bean.mine.WhoFocusMeBean
import com.twx.marryfriend.bean.mine.WhoFocusMeList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.mine.comment.RecentCommentAdapter
import com.twx.marryfriend.mine.comment.mine.CommentMineFragment
import com.twx.marryfriend.net.callback.mine.IGetWhoFocusMeCallback
import com.twx.marryfriend.net.impl.mine.getWhoFocusMePresentImpl
import com.twx.marryfriend.vip.VipActivity
import kotlinx.android.synthetic.main.fragment_comment_mine.*
import kotlinx.android.synthetic.main.fragment_focus_mine.*
import java.util.*

/**
 * 谁关注我界面
 */
class FocusMineFragment : Fragment(), IGetWhoFocusMeCallback, FocusMineAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<WhoFocusMeList> = arrayListOf()

    private lateinit var adapter: FocusMineAdapter

    private lateinit var getWhoFocusMePresent: getWhoFocusMePresentImpl


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_focus_mine, container, false)
    }

    fun newInstance(context: Context): FocusMineFragment {
        val fragment = FocusMineFragment()
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

        getWhoFocusMePresent = getWhoFocusMePresentImpl.getsInstance()
        getWhoFocusMePresent.registerCallback(this)

        adapter = FocusMineAdapter(mList)
        adapter.setOnItemClickListener(this)

        rv_focus_mime_container.layoutManager = LinearLayoutManager(mContext)
        rv_focus_mime_container.adapter = adapter

        sfl_focus_mime_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_focus_mime_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_focus_mime_refresh.autoRefresh()
    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {
        sfl_focus_mime_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getFocusMineData(currentPaper)
            sfl_focus_mime_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_focus_mime_refresh.setOnLoadMoreListener {
            getFocusMineData(currentPaper)
            sfl_focus_mime_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getFocusMineData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getWhoFocusMePresent.getWhoFocusMe(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetWhoFocusMeSuccess(whoFocusMeBean: WhoFocusMeBean?) {
        if (whoFocusMeBean != null) {
            if (whoFocusMeBean.data.list.isNotEmpty()) {

                SPStaticUtils.put(Constant.LAST_FOCUS_TIME_REQUEST, whoFocusMeBean.data.server_time)

                ll_focus_mime_empty?.visibility = View.GONE

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(whoFocusMeBean.data.list.size)) {
                    mList.add(whoFocusMeBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_focus_mime_refresh != null) {
            sfl_focus_mime_refresh.finishRefresh(true)
            sfl_focus_mime_refresh.finishLoadMore(true)
        }
    }

    override fun onGetWhoFocusMeCodeError() {
        if (sfl_focus_mime_refresh != null) {
            sfl_focus_mime_refresh.finishRefresh(false)
            sfl_focus_mime_refresh.finishLoadMore(false)
        }
    }

    override fun onItemClick(v: View?, position: Int) {

        if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 2) {
            startActivity(context?.let {
                FriendInfoActivity.getIntent(it,
                    mList[position].host_uid)
            })
        } else {
            startActivity(context?.let { VipActivity.getSVipIntent(it) })
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        getWhoFocusMePresent.unregisterCallback(this)

    }

}