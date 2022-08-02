package com.twx.marryfriend.dynamic.saloon.tip.like

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
import com.twx.marryfriend.bean.dynamic.LikeTipBean
import com.twx.marryfriend.bean.dynamic.LikeTipList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.saloon.tip.comment.CommentFragment
import com.twx.marryfriend.dynamic.saloon.tip.comment.CommentTipAdapter
import com.twx.marryfriend.dynamic.show.mine.DynamicMineShowActivity
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.net.callback.dynamic.IGetTrendTipsCallback
import com.twx.marryfriend.net.impl.dynamic.getTrendTipsPresentImpl
import kotlinx.android.synthetic.main.activity_dynamic_mine_like.*
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_like.*
import java.util.*

/**
 * 点赞未读列表
 * */
class LikeFragment : Fragment(), IGetTrendTipsCallback {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<LikeTipList> = arrayListOf()

    private lateinit var adapter: LikeTipAdapter

    private lateinit var getTrendTipsPresent: getTrendTipsPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_like, container, false)
    }

    fun newInstance(context: Context): LikeFragment {
        val fragment = LikeFragment()
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

        getTrendTipsPresent = getTrendTipsPresentImpl.getsInstance()
        getTrendTipsPresent.registerCallback(this)

        adapter = LikeTipAdapter(mList)

        ll_dynamic_tip_like_container.layoutManager = LinearLayoutManager(mContext)
        ll_dynamic_tip_like_container.adapter = adapter


        sfl_dynamic_tip_like_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_dynamic_tip_like_refresh.setRefreshFooter(ClassicsFooter(mContext))

    }

    private fun initData() {
        getLikeData(currentPaper)
    }

    private fun initPresent() {

    }

    private fun initEvent() {

        sfl_dynamic_tip_like_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getLikeData(currentPaper)
            sfl_dynamic_tip_like_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_dynamic_tip_like_refresh.setOnLoadMoreListener {
            getLikeData(currentPaper)
            sfl_dynamic_tip_like_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

        adapter.setOnItemClickListener(object : LikeTipAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {

                if (mList[position].user_id == SPStaticUtils.getString(Constant.USER_ID, "13")) {
                    ToastUtils.showShort("本人的动态")
                    startActivity(context?.let {
                        DynamicMineShowActivity.getIntent(
                            it,
                            mList[position].id)
                    })
                } else {
                    ToastUtils.showShort("他人的动态")
                    startActivity(context?.let {
                        DynamicOtherShowActivity.getIntent(
                            it,
                            mList[position].id,
                            mList[position].user_id.toInt())
                    })
                }
            }
        })

    }

    private fun getLikeData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getTrendTipsPresent.getTrendTips(map, page)
    }

    override fun onLoading() {

    }

    override fun onError() {
    }

    override fun onGetTrendTipsSuccess(likeTipBean: LikeTipBean?) {

        if (likeTipBean != null) {
            if (likeTipBean.data.list.isNotEmpty()) {

//                ll_dynamic_mine_like_empty.visibility = View.GONE

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(likeTipBean.data.list.size)) {
                    mList.add(likeTipBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_dynamic_tip_like_refresh != null){
            sfl_dynamic_tip_like_refresh.finishRefresh(true)
            sfl_dynamic_tip_like_refresh.finishLoadMore(true)
        }

    }

    override fun onGetTrendTipsError() {

        if (sfl_dynamic_tip_like_refresh != null){
            sfl_dynamic_tip_like_refresh.finishRefresh(false)
            sfl_dynamic_tip_like_refresh.finishLoadMore(false)
        }

    }

}