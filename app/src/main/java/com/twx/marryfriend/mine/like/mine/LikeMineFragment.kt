package com.twx.marryfriend.mine.like.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.mine.DiscussList
import com.twx.marryfriend.bean.mine.WhoLikeMeBean
import com.twx.marryfriend.bean.mine.WhoLikeMeList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.mine.comment.RecentCommentAdapter
import com.twx.marryfriend.mine.comment.mine.CommentMineFragment
import com.twx.marryfriend.mine.like.RecentLikeAdapter
import com.twx.marryfriend.net.callback.mine.IGetWhoLikeMeCallback
import com.twx.marryfriend.net.impl.mine.getWhoDiscussMePresentImpl
import com.twx.marryfriend.net.impl.mine.getWhoLikeMePresentImpl
import kotlinx.android.synthetic.main.fragment_comment_mine.*
import kotlinx.android.synthetic.main.fragment_focus_mine.*
import kotlinx.android.synthetic.main.fragment_like_mine.*
import java.util.*

/**
 * 其他人点赞我
 */
class LikeMineFragment : Fragment(), IGetWhoLikeMeCallback, RecentLikeAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var isRequest = true

    private var mList: MutableList<WhoLikeMeList> = arrayListOf()

    // 上一次请求的时间，用来判断是否已读
    private var mLastTime: MutableList<String> = arrayListOf()

    private lateinit var adapter: RecentLikeAdapter

    private lateinit var getWhoLikeMePresent: getWhoLikeMePresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_like_mine, container, false)
    }

    fun newInstance(context: Context): LikeMineFragment {
        val fragment = LikeMineFragment()
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

        getWhoLikeMePresent = getWhoLikeMePresentImpl.getsInstance()
        getWhoLikeMePresent.registerCallback(this)

        adapter = RecentLikeAdapter(mList, "mine", mLastTime)
        adapter.setOnItemClickListener(this)

        rv_like_mime_container.layoutManager = LinearLayoutManager(mContext)
        rv_like_mime_container.adapter = adapter

        sfl_like_mime_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_like_mime_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_like_mime_refresh.autoRefresh()

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        sfl_like_mime_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getLikeMineData(currentPaper)
            sfl_like_mime_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_like_mime_refresh.setOnLoadMoreListener {
            getLikeMineData(currentPaper)
            sfl_like_mime_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getLikeMineData(page: Int) {



            isRequest = true
            val map: MutableMap<String, String> = TreeMap()
            map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
            map[Contents.PAGE] = page.toString()
            map[Contents.SIZE] = "10"
            getWhoLikeMePresent.getWhoLikeMe(map)




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    currentPaper = 1
                    getLikeMineData(currentPaper)
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetWhoLikeMeSuccess(whoLikeMeBean: WhoLikeMeBean?) {
        if (whoLikeMeBean != null) {
            if (whoLikeMeBean.data.list.isNotEmpty()) {

                if (isRequest) {
                    isRequest = false

                    SPStaticUtils.put(Constant.LAST_LIKE_TIME_REQUEST,
                        whoLikeMeBean.data.server_time)

                    ll_like_mime_empty?.visibility = View.GONE

                    if (currentPaper == 1) {
                        mList.clear()
                    }
                    currentPaper++
                    for (i in 0.until(whoLikeMeBean.data.list.size)) {
                        mList.add(whoLikeMeBean.data.list[i])
                    }

                    mLastTime.clear()
                    mLastTime.add(SPStaticUtils.getString(Constant.LAST_LIKE_ME_TIME_REQUEST,
                        "1970-01-01 00:00:00"))

                    adapter.notifyDataSetChanged()

                    SPStaticUtils.put(Constant.LAST_LIKE_ME_TIME_REQUEST,
                        whoLikeMeBean.data.server_time)

                }

            }
        }

        if (sfl_like_mime_refresh != null) {
            sfl_like_mime_refresh.finishRefresh(true)
            sfl_like_mime_refresh.finishLoadMore(true)
        }
    }

    override fun onGetWhoLikeMeCodeError() {
        if (sfl_like_mime_refresh != null) {
            sfl_like_mime_refresh.finishRefresh(false)
            sfl_like_mime_refresh.finishLoadMore(false)
        }
        ToastUtils.showShort("网络请求失败，请稍后再试")
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivityForResult(context?.let {
            DynamicOtherShowActivity.getIntent(
                it,
                mList[position].id,
                mList[position].user_id.toInt()
            )
        }, 0)
    }

    override fun onDestroy() {
        super.onDestroy()

        getWhoLikeMePresent.unregisterCallback(this)

    }

}