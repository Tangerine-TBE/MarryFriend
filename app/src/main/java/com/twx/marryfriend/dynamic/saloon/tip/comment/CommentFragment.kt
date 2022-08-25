package com.twx.marryfriend.dynamic.saloon.tip.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.bean.dynamic.CommentTipBean
import com.twx.marryfriend.bean.dynamic.CommentTipList
import com.twx.marryfriend.constant.Constant
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.show.others.DynamicOtherShowActivity
import com.twx.marryfriend.net.callback.dynamic.IGetCommentTipsCallback
import com.twx.marryfriend.net.impl.dynamic.getCommentTipsPresentImpl
import kotlinx.android.synthetic.main.fragment_comment.*
import kotlinx.android.synthetic.main.fragment_view_mine.*
import java.util.*

/**
 * 评论未读列表
 * */
class CommentFragment : Fragment(), IGetCommentTipsCallback {

    private var currentPaper = 1

    private lateinit var mContext: Context

    private var mList: MutableList<CommentTipList> = arrayListOf()

    private lateinit var adapter: CommentTipAdapter

    private lateinit var getCommentTipsPresent: getCommentTipsPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    fun newInstance(context: Context): CommentFragment {
        val fragment = CommentFragment()
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

        getCommentTipsPresent = getCommentTipsPresentImpl.getsInstance()
        getCommentTipsPresent.registerCallback(this)


        adapter = CommentTipAdapter(mList)


        ll_dynamic_tip_comment_container.layoutManager = LinearLayoutManager(mContext)
        ll_dynamic_tip_comment_container.adapter = adapter


        sfl_dynamic_tip_comment_refresh.setRefreshHeader(ClassicsHeader(mContext))
        sfl_dynamic_tip_comment_refresh.setRefreshFooter(ClassicsFooter(mContext))

        sfl_dynamic_tip_comment_refresh.autoRefresh()

    }

    private fun initData() {

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        sfl_dynamic_tip_comment_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getCommentData(currentPaper)
            sfl_dynamic_tip_comment_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_dynamic_tip_comment_refresh.setOnLoadMoreListener {
            getCommentData(currentPaper)
            sfl_dynamic_tip_comment_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

        adapter.setOnItemClickListener(object : CommentTipAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                if (mList[position].user_id == SPStaticUtils.getString(Constant.USER_ID, "13")) {
                    ToastUtils.showShort("本人的动态")
                    startActivityForResult(context?.let {
                        DynamicOtherShowActivity.getIntent(
                            it,
                            mList[position].id,
                            SPStaticUtils.getString(Constant.USER_ID, "13").toInt()
                        )
                    }, 0)
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

    private fun getCommentData(page: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        getCommentTipsPresent.getCommentTips(map, page)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == FragmentActivity.RESULT_OK) {
            when (requestCode) {
                0 -> {
                    currentPaper = 1
                    getCommentData(currentPaper)
                }
            }
        }
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetCommentTipsSuccess(commentTipBean: CommentTipBean?) {
        if (commentTipBean != null) {
            if (commentTipBean.data.list.isNotEmpty()) {

                ll_dynamic_tip_comment_empty?.visibility = View.GONE

                if (currentPaper == 1) {
                    mList.clear()
                }
                currentPaper++
                for (i in 0.until(commentTipBean.data.list.size)) {
                    mList.add(commentTipBean.data.list[i])
                }
                adapter.notifyDataSetChanged()
            }
        }

        if (sfl_dynamic_tip_comment_refresh != null) {
            sfl_dynamic_tip_comment_refresh.finishRefresh(true)
            sfl_dynamic_tip_comment_refresh.finishLoadMore(true)
        }
    }

    override fun onGetCommentTipsError() {
        if (sfl_dynamic_tip_comment_refresh != null) {
            sfl_dynamic_tip_comment_refresh.finishRefresh(false)
            sfl_dynamic_tip_comment_refresh.finishLoadMore(false)
        }
    }

}