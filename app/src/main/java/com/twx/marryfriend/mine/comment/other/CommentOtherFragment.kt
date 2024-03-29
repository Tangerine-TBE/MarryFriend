package com.twx.marryfriend.mine.comment.other

import android.content.Context
import android.os.Bundle
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
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.DiscussList
import com.twx.marryfriend.bean.mine.MeDiscussWhoBean
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.mine.comment.RecentCommentAdapter
import com.twx.marryfriend.mine.comment.mine.CommentMineFragment
import com.twx.marryfriend.net.callback.mine.IGetMeDiscussWhoCallback
import com.twx.marryfriend.net.impl.mine.getMeDiscussWhoPresentImpl
import com.twx.marryfriend.net.impl.mine.getWhoDiscussMePresentImpl
import kotlinx.android.synthetic.main.fragment_comment_mine.*
import kotlinx.android.synthetic.main.fragment_comment_other.*
import java.util.*

/**
 * 我评论谁
 */
class CommentOtherFragment : Fragment(), IGetMeDiscussWhoCallback,
    RecentCommentAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var isRequest = true

    private var mList: MutableList<DiscussList> = arrayListOf()

    // 上一次请求的时间，用来判断是否已读
    private var mLastTime: MutableList<String> = arrayListOf()

    private lateinit var adapter: RecentCommentAdapter

    private lateinit var getMeDiscussWhoPresent: getMeDiscussWhoPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_comment_other, container, false)
    }

    fun newInstance(context: Context): CommentOtherFragment {
        val fragment = CommentOtherFragment()
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

        getMeDiscussWhoPresent = getMeDiscussWhoPresentImpl.getsInstance()
        getMeDiscussWhoPresent.registerCallback(this)

        adapter = RecentCommentAdapter(mList, "other", mLastTime)
        adapter.setOnItemClickListener(this)

        rv_comment_other_container.layoutManager = LinearLayoutManager(mContext)
        rv_comment_other_container.adapter = adapter

        sfl_comment_other_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_comment_other_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_comment_other_refresh.autoRefresh()
    }

    private fun initData() {
    }

    private fun initPresent() {

    }

    private fun initEvent() {
        sfl_comment_other_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getCommentOtherData(currentPaper)
            sfl_comment_other_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_comment_other_refresh.setOnLoadMoreListener {
            getCommentOtherData(currentPaper)
            sfl_comment_other_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getCommentOtherData(page: Int) {



            isRequest = true
            val map: MutableMap<String, String> = TreeMap()
            map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
            map[Contents.PAGE] = page.toString()
            map[Contents.SIZE] = "10"
            getMeDiscussWhoPresent.getMeDiscussWho(map)




    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetMeDiscussWhoSuccess(meDiscussWhoBean: MeDiscussWhoBean?) {
        if (meDiscussWhoBean != null) {
            if (meDiscussWhoBean.data.list.isNotEmpty()) {

                if (isRequest) {
                    isRequest = false

                    ll_comment_other_empty?.visibility = View.GONE

                    if (currentPaper == 1) {
                        mList.clear()
                    }

                    currentPaper++

                    for (i in 0.until(meDiscussWhoBean.data.list.size)) {
                        mList.add(meDiscussWhoBean.data.list[i])
                    }

                    mLastTime.clear()
                    mLastTime.add(SPStaticUtils.getString(Constant.LAST_COMMENT_OTHER_TIME_REQUEST,
                        "1970-01-01 00:00:00"))

                    adapter.notifyDataSetChanged()

                    SPStaticUtils.put(Constant.LAST_COMMENT_OTHER_TIME_REQUEST,
                        meDiscussWhoBean.data.server_time)

                }


            }
        }

        if (sfl_comment_other_refresh != null) {
            sfl_comment_other_refresh.finishRefresh(true)
            sfl_comment_other_refresh.finishLoadMore(true)
        }
    }

    override fun onGetMeDiscussWhoCodeError() {
        if (sfl_comment_other_refresh != null) {
            sfl_comment_other_refresh.finishRefresh(false)
            sfl_comment_other_refresh.finishLoadMore(false)
        }
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivity(context?.let {
            DynamicOtherShowActivity.getIntent(it,
                mList[position].id,
                mList[position].user_id.toInt())
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        getMeDiscussWhoPresent.unregisterCallback(this)

    }

}