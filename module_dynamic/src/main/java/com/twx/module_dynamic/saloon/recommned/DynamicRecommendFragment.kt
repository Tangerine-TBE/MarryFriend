package com.twx.module_dynamic.saloon.recommned

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.twx.module_base.constant.Contents
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.LikeCancelBean
import com.twx.module_dynamic.bean.LikeClickBean
import com.twx.module_dynamic.bean.TrendSaloonBean
import com.twx.module_dynamic.bean.TrendSaloonList
import com.twx.module_dynamic.net.callback.IDoLikeCancelCallback
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback
import com.twx.module_dynamic.net.callback.IGetTrendSaloonCallback
import com.twx.module_dynamic.net.impl.doLikeCancelPresentImpl
import com.twx.module_dynamic.net.impl.doLikeClickPresentImpl
import com.twx.module_dynamic.net.impl.getTrendSaloonPresentImpl
import com.twx.module_dynamic.preview.image.ImagePreviewActivity
import com.twx.module_dynamic.preview.video.VideoPreviewActivity
import com.twx.module_dynamic.saloon.adapter.SaloonAdapter
import com.twx.module_dynamic.show.others.DynamicOtherShowActivity
import kotlinx.android.synthetic.main.activity_dynamic_mine_like.*
import kotlinx.android.synthetic.main.fragment_dynamic_friend.*
import kotlinx.android.synthetic.main.fragment_recommend.*
import java.io.Serializable
import java.util.*

class DynamicRecommendFragment : Fragment(), IGetTrendSaloonCallback, IDoLikeClickCallback,
    IDoLikeCancelCallback {

    // 数据加载模式
    private var mode = "first"

    // 当前页的最大动态id
    private var max = 3

    // 当前页的最小动态id
    private var min = 2

    private lateinit var mContext: Context

    // 大图展示时进入时应该展示点击的那张图片
    private var imageIndex = 0

    private var mTrendList: MutableList<TrendSaloonList> = arrayListOf()

    private lateinit var adapter: SaloonAdapter

    private lateinit var getTrendSaloonPresent: getTrendSaloonPresentImpl
    private lateinit var doLikeClickPresent: doLikeClickPresentImpl
    private lateinit var doLikeCancelPresent: doLikeCancelPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_recommend, container, false)
    }

    fun newInstance(context: Context): DynamicRecommendFragment {
        val fragment = DynamicRecommendFragment()
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

        getTrendSaloonPresent = getTrendSaloonPresentImpl.getsInstance()
        getTrendSaloonPresent.registerCallback(this)

        doLikeClickPresent = doLikeClickPresentImpl.getsInstance()
        doLikeClickPresent.registerCallback(this)

        doLikeCancelPresent = doLikeCancelPresentImpl.getsInstance()
        doLikeCancelPresent.registerCallback(this)

        val mEduData: MutableList<String> = arrayListOf()
        mEduData.add("大专以下")
        mEduData.add("大专")
        mEduData.add("本科")
        mEduData.add("硕士")
        mEduData.add("博士")
        mEduData.add("博士以上")

        adapter = SaloonAdapter(mTrendList, mEduData)

        rv_dynamic_recommend_container.adapter = adapter
        rv_dynamic_recommend_container.layoutManager = LinearLayoutManager(context)

        srl_dynamic_recommend_refresh.setRefreshHeader(ClassicsHeader(mContext));
        srl_dynamic_recommend_refresh.setRefreshFooter(ClassicsFooter(mContext));

    }

    private fun initData() {

        getTrendSaloon(mode, max, min)

    }

    private fun initPresent() {

    }

    private fun initEvent() {

        srl_dynamic_recommend_refresh.setOnRefreshListener {
            // 刷新数据
            mode = "first"
            max = 3
            min = 2
            getTrendSaloon(mode, max, min)
            srl_dynamic_recommend_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        srl_dynamic_recommend_refresh.setOnLoadMoreListener {
            // 刷新数据
            mode = "down"
            getTrendSaloon(mode, max, min)
            srl_dynamic_recommend_refresh.finishLoadMore(2000/*,false*/);//传入false表示刷新失败
        }

        adapter.setOnVideoClickListener(object : SaloonAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {
                val intent = Intent(mContext, VideoPreviewActivity::class.java)
                intent.putExtra("videoUrl", mTrendList[position].video_url)
                intent.putExtra("name", mTrendList[position].nick)
                startActivity(intent)
            }
        })

        adapter.setOnItemClickListener(object : SaloonAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                ToastUtils.showShort("动态详情界面，显示评论")
                val intent = Intent(context, DynamicOtherShowActivity::class.java)
                intent.putExtra("trendId", mTrendList[position].id)
                intent.putExtra("userId", mTrendList[position].user_id)
                intent.putExtra("sex", mTrendList[position].user_sex)
                startActivity(intent)

            }
        })

        adapter.setOnAvatarClickListener(object : SaloonAdapter.OnAvatarClickListener {
            override fun onAvatarClick(v: View?, position: Int) {
                ToastUtils.showShort("头像,进入资料详情界面")
            }
        })

        adapter.setOnFocusClickListener(object : SaloonAdapter.OnFocusClickListener {
            override fun onFocusClick(v: View?, position: Int) {
                ToastUtils.showShort("关注，需要判断模式，未关注的时候，和关注的时候分开判断")
            }
        })

        adapter.setOnLikeClickListener(object : SaloonAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {
                ToastUtils.showShort("点赞，给该用户点赞")
            }
        })

        adapter.setOnCommentClickListener(object : SaloonAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {
                ToastUtils.showShort("评论，跳转到动态详情界面，显示评论")
            }
        })

        adapter.setOnOneClickListener(object : SaloonAdapter.OnOneClickListener {
            override fun onOneClick(v: View?, position: Int) {
                ToastUtils.showShort("one")
                imageIndex = 0

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnTwoClickListener(object : SaloonAdapter.OnTwoClickListener {
            override fun onTwoClick(v: View?, position: Int) {
                ToastUtils.showShort("two")
                imageIndex = 1

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnThreeClickListener(object : SaloonAdapter.OnThreeClickListener {
            override fun onThreeClick(v: View?, position: Int) {
                ToastUtils.showShort("three")
                imageIndex = 2

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnFourClickListener(object : SaloonAdapter.OnFourClickListener {
            override fun onFourClick(v: View?, position: Int) {
                ToastUtils.showShort("four")
                imageIndex = 3

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 2
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnFiveClickListener(object : SaloonAdapter.OnFiveClickListener {
            override fun onFiveClick(v: View?, position: Int) {
                ToastUtils.showShort("five")
                imageIndex = 4


                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 3
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnSixClickListener(object : SaloonAdapter.OnSixClickListener {
            override fun onSixClick(v: View?, position: Int) {
                ToastUtils.showShort("six")
                imageIndex = 5

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnSevenClickListener(object : SaloonAdapter.OnSevenClickListener {
            override fun onSevenClick(v: View?, position: Int) {
                ToastUtils.showShort("seven")
                imageIndex = 6

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnEightClickListener(object : SaloonAdapter.OnEightClickListener {
            override fun onEightClick(v: View?, position: Int) {
                ToastUtils.showShort("eight")
                imageIndex = 7

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnNineClickListener(object : SaloonAdapter.OnNineClickListener {
            override fun onNineClick(v: View?, position: Int) {
                ToastUtils.showShort("nine")
                imageIndex = 8

                val images: MutableList<String> =
                    mTrendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

    }

    // 获取动态列表
    private fun getTrendSaloon(mode: String, max: Int, min: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.UP_DOWN] = mode
        map[Contents.MAX_ID] = max.toString()
        map[Contents.MIN_ID] = min.toString()
        map[Contents.SIZE] = 10.toString()
        getTrendSaloonPresent.getTrendSaloon(map)
    }

    // 动态点赞
    private fun doLikeClick(trendId: Int, hostUid: Int, guestUid: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TREND_ID] = trendId.toString()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doLikeClickPresent.doLikeClick(map)
    }

    // 取消点赞
    private fun doLikeCancelClick(trendId: Int, hostUid: Int, guestUid: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.TREND_ID] = trendId.toString()
        map[Contents.HOST_UID] = hostUid.toString()
        map[Contents.GUEST_UID] = guestUid.toString()
        doLikeCancelPresent.doLikeCancel(map)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onDoLikeCancelSuccess(likeCancelBean: LikeCancelBean?) {

    }

    override fun onLikeCancelError() {

    }

    override fun onDoLikeClickSuccess(likeClickBean: LikeClickBean?) {

    }

    override fun onDoLikeClickError() {

    }

    override fun onGetTrendSaloonSuccess(trendSaloonBean: TrendSaloonBean) {

        if (trendSaloonBean.data.list.isNotEmpty()) {
            ll_dynamic_recommend_empty.visibility = View.GONE

            val mIdList: MutableList<Int> = arrayListOf()

            if (mode == "first") {
                mTrendList.clear()
            }

            for (i in 0.until(trendSaloonBean.data.list.size)) {
                mTrendList.add(trendSaloonBean.data.list[i])
                mIdList.add(trendSaloonBean.data.list[i].id)
            }

            max = Collections.max(mIdList)
            min = Collections.min(mIdList)

            adapter.notifyDataSetChanged()

            srl_dynamic_recommend_refresh.finishRefresh(true)
            srl_dynamic_recommend_refresh.finishLoadMore(true)

        } else {
            srl_dynamic_recommend_refresh.finishRefresh(true)
            srl_dynamic_recommend_refresh.finishLoadMore(true)
        }
    }

    override fun onGetTrendSaloonError() {
        srl_dynamic_recommend_refresh.finishRefresh(false)
        srl_dynamic_recommend_refresh.finishLoadMore(false)
    }

}