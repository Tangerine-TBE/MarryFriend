package com.twx.marryfriend.mine.like.mine

import android.content.Context
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_like_mine.*
import java.util.*

/**
 * 其他人点赞我
 */
class LikeMineFragment : Fragment(), IGetWhoLikeMeCallback, RecentLikeAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<WhoLikeMeList> = arrayListOf()

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

        adapter = RecentLikeAdapter(mList, "mine")
        adapter.setOnItemClickListener(this)

        rv_like_mime_container.layoutManager = LinearLayoutManager(mContext)
        rv_like_mime_container.adapter = adapter

        sfl_like_mime_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_like_mime_refresh.setRefreshFooter(ClassicsFooter(mContext))

    }

    private fun initData() {
        getLikeMineData(currentPaper)
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
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getWhoLikeMePresent.getWhoLikeMe(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetWhoLikeMeSuccess(whoLikeMeBean: WhoLikeMeBean?) {
        if (whoLikeMeBean != null) {
            if (whoLikeMeBean.data.list.isNotEmpty()) {

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(whoLikeMeBean.data.list.size)) {
                    mList.add(whoLikeMeBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
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
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivity(context?.let {
            DynamicOtherShowActivity.getIntent(
                it,
                mList[position].id,
                mList[position].user_id.toInt()
            )
        })
    }

}