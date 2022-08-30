package com.twx.marryfriend.mine.view.mine

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
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.WhoFocusMeList
import com.twx.marryfriend.bean.mine.WhoSeeMeBean
import com.twx.marryfriend.bean.mine.WhoSeeMeList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ChatActivity
import com.twx.marryfriend.mine.focus.mine.FocusMineAdapter
import com.twx.marryfriend.mine.focus.mine.FocusMineFragment
import com.twx.marryfriend.net.callback.mine.IGetWhoSeeMeCallback
import com.twx.marryfriend.net.impl.mine.getWhoFocusMePresentImpl
import com.twx.marryfriend.net.impl.mine.getWhoSeeMePresentImpl
import com.twx.marryfriend.vip.VipActivity
import kotlinx.android.synthetic.main.fragment_focus_mine.*
import kotlinx.android.synthetic.main.fragment_like_mine.*
import kotlinx.android.synthetic.main.fragment_view_mine.*
import java.util.*

/**
 * 别人看过我
 */
class ViewMineFragment : Fragment(), IGetWhoSeeMeCallback, ViewMineAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<WhoSeeMeList> = arrayListOf()

    private lateinit var adapter: ViewMineAdapter

    private lateinit var getWhoSeeMePresent: getWhoSeeMePresentImpl


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_view_mine, container, false)
    }

    fun newInstance(context: Context): ViewMineFragment {
        val fragment = ViewMineFragment()
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

        getWhoSeeMePresent = getWhoSeeMePresentImpl.getsInstance()
        getWhoSeeMePresent.registerCallback(this)

        adapter = ViewMineAdapter(mList)

        adapter.setOnItemClickListener(this)

        rv_view_mime_container.layoutManager = LinearLayoutManager(mContext)
        rv_view_mime_container.adapter = adapter

        sfl_view_mime_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_view_mime_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_view_mime_refresh.autoRefresh()

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {
        sfl_view_mime_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getViewMineData(currentPaper)
            sfl_view_mime_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_view_mime_refresh.setOnLoadMoreListener {
            getViewMineData(currentPaper)
            sfl_view_mime_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getViewMineData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getWhoSeeMePresent.getWhoSeeMe(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetWhoSeeMeSuccess(whoSeeMeBean: WhoSeeMeBean?) {
        if (whoSeeMeBean != null) {
            if (whoSeeMeBean.data.list.isNotEmpty()) {

                ll_view_mime_empty?.visibility = View.GONE

                if (currentPaper == 1) {
                    mList.clear()
                }

                currentPaper++
                for (i in 0.until(whoSeeMeBean.data.list.size)) {
                    mList.add(whoSeeMeBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_view_mime_refresh != null) {
            sfl_view_mime_refresh.finishRefresh(true)
            sfl_view_mime_refresh.finishLoadMore(true)
        }
    }

    override fun onGetWhoSeeMeCodeError() {
        if (sfl_view_mime_refresh != null) {
            sfl_view_mime_refresh.finishRefresh(false)
            sfl_view_mime_refresh.finishLoadMore(false)
        }
    }

    override fun onItemClick(v: View?, position: Int) {
        ToastUtils.showShort("资料详情界面")
        startActivity(context?.let { FriendInfoActivity.getIntent(it, mList[position].guest_uid) })
    }

    override fun onChatClick(v: View?, position: Int) {


        if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 0) {

            startActivity(context?.let { VipActivity.getIntent(it, 0) })

        } else {

            val identity = mList[position].identity_status == 1
            startActivity(context?.let {
                ChatActivity.getIntent(
                    it,
                    mList[position].guest_uid.toString(),
                    mList[position].nick,
                    mList[position].image_url,
                    identity)
            })

        }


    }

}