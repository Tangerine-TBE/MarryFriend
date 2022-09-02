package com.twx.marryfriend.mine.comment.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.DiscussList
import com.twx.marryfriend.bean.mine.WhoDiscussMeBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.mine.comment.RecentCommentAdapter
import com.twx.marryfriend.net.callback.mine.IGetWhoDiscussMeCallback
import com.twx.marryfriend.net.impl.mine.getWhoDiscussMePresentImpl
import kotlinx.android.synthetic.main.fragment_comment_mine.*
import java.util.*

/**
 * 谁评论我
 */

class CommentMineFragment : Fragment(), IGetWhoDiscussMeCallback,
    RecentCommentAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var isRequest = true

    private var mList: MutableList<DiscussList> = arrayListOf()

    // 上一次请求的时间，用来判断是否已读
    private var mLastTime: MutableList<String> = arrayListOf()

    private lateinit var adapter: RecentCommentAdapter

    private lateinit var getWhoDiscussMePresent: getWhoDiscussMePresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_comment_mine, container, false)
    }

    fun newInstance(context: Context): CommentMineFragment {
        val fragment = CommentMineFragment()
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

        getWhoDiscussMePresent = getWhoDiscussMePresentImpl.getsInstance()
        getWhoDiscussMePresent.registerCallback(this)

        adapter = RecentCommentAdapter(mList, "mine", mLastTime)
        adapter.setOnItemClickListener(this)

        rv_comment_mime_container.layoutManager = LinearLayoutManager(mContext)
        rv_comment_mime_container.adapter = adapter

        sfl_comment_mime_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_comment_mime_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_comment_mime_refresh.autoRefresh()

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        sfl_comment_mime_refresh.setOnRefreshListener {

            Log.i("guo", "刷新沙墟")
            // 刷新数据
            currentPaper = 1
            getCommentMineData(currentPaper)
            sfl_comment_mime_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_comment_mime_refresh.setOnLoadMoreListener {
            getCommentMineData(currentPaper)
            sfl_comment_mime_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getCommentMineData(page: Int) {
        isRequest = true
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getWhoDiscussMePresent.getWhoDiscussMe(map, page)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    currentPaper = 1
                    getCommentMineData(currentPaper)
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetWhoDiscussMeSuccess(whoDiscussMeBean: WhoDiscussMeBean?) {
        if (whoDiscussMeBean != null) {
            if (whoDiscussMeBean.data.list.isNotEmpty()) {

                if (isRequest) {
                    isRequest = false
                    SPStaticUtils.put(Constant.LAST_COMMENT_TIME_REQUEST,
                        whoDiscussMeBean.data.server_time)

                    ll_comment_mime_empty?.visibility = View.GONE

                    if (currentPaper == 1) {
                        mList.clear()
                    }

                    currentPaper++

                    for (i in 0.until(whoDiscussMeBean.data.list.size)) {
                        mList.add(whoDiscussMeBean.data.list[i])
                    }

                    mLastTime.clear()
                    mLastTime.add(SPStaticUtils.getString(Constant.LAST_COMMENT_ME_TIME_REQUEST,
                        "1970-01-01 00:00:00"))

                    adapter.notifyDataSetChanged()

                    SPStaticUtils.put(Constant.LAST_COMMENT_ME_TIME_REQUEST,
                        whoDiscussMeBean.data.server_time)

                }


            }
        }

        if (sfl_comment_mime_refresh != null) {
            sfl_comment_mime_refresh.finishRefresh(true)
            sfl_comment_mime_refresh.finishLoadMore(true)
        }
    }

    override fun onGetWhoDiscussMeCodeError() {
        if (sfl_comment_mime_refresh != null) {
            sfl_comment_mime_refresh.finishRefresh(false)
            sfl_comment_mime_refresh.finishLoadMore(false)
        }
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivityForResult(context?.let {
            DynamicOtherShowActivity.getIntent(
                it,
                mList[position].id,
                mList[position].user_id.toInt())
        }, 0)
    }

}