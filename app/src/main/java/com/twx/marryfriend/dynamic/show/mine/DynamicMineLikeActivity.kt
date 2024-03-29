package com.twx.marryfriend.dynamic.show.mine

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.marryfriend.R
import com.twx.marryfriend.base.MainBaseViewActivity
import com.twx.marryfriend.bean.dynamic.LikeList
import com.twx.marryfriend.bean.dynamic.LikeListBean
import com.twx.marryfriend.constant.Contents
import com.twx.marryfriend.dynamic.show.mine.adapter.DynamicMineLikeAdapter
import com.twx.marryfriend.friend.FriendInfoActivity
import com.twx.marryfriend.net.callback.dynamic.IGetLikeListCallback
import com.twx.marryfriend.net.impl.dynamic.getLikeListPresentImpl
import kotlinx.android.synthetic.main.activity_dynamic_mine_like.*
import java.util.*

class DynamicMineLikeActivity : MainBaseViewActivity(),
    IGetLikeListCallback {

    private var currentPaper = 1

    private var trendId = 0
    private var userId = 0

    private var mLikeList: MutableList<LikeList> = arrayListOf()
    private var mEduData: MutableList<String> = arrayListOf()

    private lateinit var adapter: DynamicMineLikeAdapter

    private lateinit var getLikeListPresent: getLikeListPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_dynamic_mine_like

    override fun initView() {
        super.initView()

        trendId = intent.getIntExtra("trendId", 0)
        userId = intent.getIntExtra("userId", 0)

        getLikeListPresent = getLikeListPresentImpl.getsInstance()
        getLikeListPresent.registerCallback(this)

        mEduData.add("")
        mEduData.add("大专以下")
        mEduData.add("大专")
        mEduData.add("本科")
        mEduData.add("硕士")
        mEduData.add("博士")

        adapter = DynamicMineLikeAdapter(mLikeList, mEduData)


        ll_dynamic_mine_like_container.layoutManager = LinearLayoutManager(this)
        ll_dynamic_mine_like_container.adapter = adapter


        sfl_dynamic_mine_like_refresh.setRefreshHeader(ClassicsHeader(this))
        sfl_dynamic_mine_like_refresh.setRefreshFooter(ClassicsFooter(this))

    }

    override fun initLoadData() {
        super.initLoadData()

        getLikeList(currentPaper)

    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        iv_dynamic_mine_like_finish.setOnClickListener {
            finish()
        }

        sfl_dynamic_mine_like_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            getLikeList(currentPaper)
            sfl_dynamic_mine_like_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        sfl_dynamic_mine_like_refresh.setOnLoadMoreListener {
            getLikeList(currentPaper)
            sfl_dynamic_mine_like_refresh.finishLoadMore(2000);//传入false表示刷新失败
        }

        adapter.setOnItemClickListener(object : DynamicMineLikeAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                startActivity(FriendInfoActivity.getIntent(
                    this@DynamicMineLikeActivity,
                    mLikeList[position].guest_uid
                ))
            }
        })

    }

    private fun getLikeList(Page: Int) {


        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TRENDS_ID] = trendId.toString()
        map[Contents.HOST_UID] = userId.toString()
        map[Contents.PAGE] = Page.toString()
        map[Contents.SIZE] = "50"
        getLikeListPresent.getLikeList(map)


    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetLikeListSuccess(likeListBean: LikeListBean) {

        if (likeListBean.data.list.isNotEmpty()) {
            ll_dynamic_mine_like_empty.visibility = View.GONE
            if (currentPaper == 1) {
                mLikeList.clear()
            }
            currentPaper++
            for (i in 0.until(likeListBean.data.list.size)) {
                mLikeList.add(likeListBean.data.list[i])
            }
            adapter.notifyDataSetChanged()
        }

        sfl_dynamic_mine_like_refresh.finishRefresh(true)
        sfl_dynamic_mine_like_refresh.finishLoadMore(true)

    }

    override fun onGetLikeListCodeError() {
        sfl_dynamic_mine_like_refresh.finishRefresh(false)
        sfl_dynamic_mine_like_refresh.finishLoadMore(false)

        ToastUtils.showShort("网络请求失败，请稍后再试")

    }

    override fun onDestroy() {
        super.onDestroy()

        getLikeListPresent.unregisterCallback(this)

    }


}