package com.twx.marryfriend.mine.view.mine

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.ImUserInfoHelper
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.WhoSeeMeBean
import com.twx.marryfriend.bean.mine.WhoSeeMeList
import com.twx.marryfriend.bean.vip.VipGifEnum
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.message.ImChatActivity
import com.twx.marryfriend.net.callback.mine.IGetWhoSeeMeCallback
import com.twx.marryfriend.net.impl.mine.getWhoSeeMePresentImpl
import com.twx.marryfriend.vip.VipActivity
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
        map[Contents.PAGE] = page.toString()
        map[Contents.SIZE] = "10"
        getWhoSeeMePresent.getWhoSeeMe(map)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    sfl_view_mime_refresh.autoRefresh()
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetWhoSeeMeSuccess(whoSeeMeBean: WhoSeeMeBean?) {
        if (whoSeeMeBean != null) {
            if (whoSeeMeBean.data.list.isNotEmpty()) {

                SPStaticUtils.put(Constant.LAST_VIEW_TIME_REQUEST, whoSeeMeBean.data.server_time)

                ll_view_mime_empty?.visibility = View.GONE


                if (currentPaper == 1) {
                    mList.clear()
                }

                val idList = arrayListOf<String>()


                currentPaper++
                for (i in 0.until(whoSeeMeBean.data.list.size)) {

                    if (whoSeeMeBean.data.list[i].nick != null) {

                        mList.add(whoSeeMeBean.data.list[i])

                        idList.add(whoSeeMeBean.data.list[i].host_uid.toString())

                    }

                }

                ImUserInfoHelper.addFriendInfo(idList)

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
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onItemClick(v: View?, position: Int) {
        ToastUtils.showShort("资料详情界面")

        Log.i("guo", "vip : ${SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0)}")

        if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 0) {
            startActivityForResult(context?.let {
                VipActivity.getVipIntent(it, 0, VipGifEnum.SeeMe)
            }, 0)
        } else {
            startActivity(context?.let {
                FriendInfoActivity.getIntent(it, mList[position].host_uid)
            })
        }

    }

    override fun onChatClick(v: View?, position: Int) {


        Log.i("guo", "vip : ${SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0)}")

        if (SPStaticUtils.getInt(Constant.USER_VIP_LEVEL, 0) == 0) {

            startActivityForResult(context?.let {
                VipActivity.getVipIntent(it, 0, VipGifEnum.Message)
            }, 0)

        } else {

            val identity = mList[position].identity_status == 1

            startActivity(context?.let {
                ImChatActivity.getIntent(it, mList[position].host_uid.toString())
            })

        }


    }

    override fun onDestroy() {
        super.onDestroy()

        getWhoSeeMePresent.unregisterCallback(this)

    }

}