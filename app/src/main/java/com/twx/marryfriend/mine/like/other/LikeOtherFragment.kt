package com.twx.marryfriend.mine.like.other

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
import com.twx.marryfriend.bean.mine.MeLikeWhoBean
import com.twx.marryfriend.bean.mine.WhoLikeMeList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.mine.like.RecentLikeAdapter
import com.twx.marryfriend.mine.like.mine.LikeMineFragment
import com.twx.marryfriend.net.callback.mine.IGetMeLikeWhoCallback
import com.twx.marryfriend.net.impl.mine.getMeLikeWhoPresentImpl
import com.twx.marryfriend.net.impl.mine.getWhoLikeMePresentImpl
import kotlinx.android.synthetic.main.fragment_like_mine.*
import kotlinx.android.synthetic.main.fragment_like_other.*
import java.util.*

/**
 * 其他人点赞我
 */
class LikeOtherFragment : Fragment(), IGetMeLikeWhoCallback, RecentLikeAdapter.OnItemClickListener {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<WhoLikeMeList> = arrayListOf()

    private lateinit var adapter: RecentLikeAdapter

    private lateinit var getMeLikeWhoPresent: getMeLikeWhoPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_like_other, container, false)
    }

    fun newInstance(context: Context): LikeOtherFragment {
        val fragment = LikeOtherFragment()
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

        getMeLikeWhoPresent = getMeLikeWhoPresentImpl.getsInstance()
        getMeLikeWhoPresent.registerCallback(this)

        adapter = RecentLikeAdapter(mList, "other")
        adapter.setOnItemClickListener(this)

        rv_like_other_container.layoutManager = LinearLayoutManager(mContext)
        rv_like_other_container.adapter = adapter

        sfl_like_other_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_like_other_refresh.setRefreshFooter(ClassicsFooter(mContext))

    }

    private fun initData() {
        getLikeOtherData(currentPaper)
    }

    private fun initPresent() {

    }

    private fun initEvent() {
        sfl_like_other_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getLikeOtherData(currentPaper)
            sfl_like_other_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_like_other_refresh.setOnLoadMoreListener {
            getLikeOtherData(currentPaper)
            sfl_like_other_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

    }

    private fun getLikeOtherData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getMeLikeWhoPresent.getMeLikeWho(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetMeLikeWhoSuccess(meLikeWhoBean: MeLikeWhoBean?) {
        if (meLikeWhoBean != null) {
            if (meLikeWhoBean.data.list.isNotEmpty()) {

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(meLikeWhoBean.data.list.size)) {
                    mList.add(meLikeWhoBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_like_other_refresh != null) {
            sfl_like_other_refresh.finishRefresh(true)
            sfl_like_other_refresh.finishLoadMore(true)
        }
    }

    override fun onGetMeLikeWhoCodeError() {
        if (sfl_like_other_refresh != null) {
            sfl_like_other_refresh.finishRefresh(false)
            sfl_like_other_refresh.finishLoadMore(false)
        }
    }

    override fun onItemClick(v: View?, position: Int) {
        startActivity(context?.let {
            DynamicOtherShowActivity.getIntent(
                it,
                mList[position].id,
                SPStaticUtils.getString(Constant.USER_ID, "13").toInt())
        })
    }

}