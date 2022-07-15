package com.twx.module_dynamic.saloon.friend

import android.content.Context
import android.content.Intent
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
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.*
import com.twx.module_dynamic.net.callback.IDoLikeCancelCallback
import com.twx.module_dynamic.net.callback.IDoLikeClickCallback
import com.twx.module_dynamic.net.callback.IGetTrendFocusCallback
import com.twx.module_dynamic.net.impl.doLikeCancelPresentImpl
import com.twx.module_dynamic.net.impl.doLikeClickPresentImpl
import com.twx.module_dynamic.net.impl.getTrendFocusPresentImpl
import com.twx.module_dynamic.preview.image.ImagePreviewActivity
import com.twx.module_dynamic.preview.video.VideoPreviewActivity
import com.twx.module_dynamic.saloon.adapter.SaloonFocusAdapter
import com.twx.module_dynamic.show.others.DynamicOtherShowActivity
import kotlinx.android.synthetic.main.fragment_dynamic_friend.*
import java.io.Serializable
import java.util.*

class DynamicFriendFragment : Fragment(), IGetTrendFocusCallback, IDoLikeClickCallback,
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

    private var mTrendList: MutableList<TrendFocusList> = arrayListOf()

    private lateinit var adapter: SaloonFocusAdapter

    private lateinit var getTrendFocusPresent: getTrendFocusPresentImpl
    private lateinit var doLikeClickPresent: doLikeClickPresentImpl
    private lateinit var doLikeCancelPresent: doLikeCancelPresentImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_dynamic_friend, container, false)
    }

    fun newInstance(context: Context): DynamicFriendFragment {
        val fragment = DynamicFriendFragment()
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

        getTrendFocusPresent = getTrendFocusPresentImpl.getsInstance()
        getTrendFocusPresent.registerCallback(this)

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

        adapter = SaloonFocusAdapter(mTrendList, mEduData)

        rv_dynamic_focus_container.adapter = adapter
        rv_dynamic_focus_container.layoutManager = LinearLayoutManager(context)

        srl_dynamic_focus_refresh.setRefreshHeader(ClassicsHeader(mContext));
        srl_dynamic_focus_refresh.setRefreshFooter(ClassicsFooter(mContext));

    }

    private fun initData() {
        getTrendFocus(mode, max, min)
    }

    private fun initPresent() {

    }

    private fun initEvent() {

        srl_dynamic_focus_refresh.setOnRefreshListener {
            // 刷新数据
            mode = "first"
            max = 3
            min = 2
            getTrendFocus(mode, max, min)
            srl_dynamic_focus_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        srl_dynamic_focus_refresh.setOnLoadMoreListener {
            // 刷新数据
            mode = "down"
            getTrendFocus(mode, max, min)
            srl_dynamic_focus_refresh.finishLoadMore(2000/*,false*/);//传入false表示刷新失败
        }

        adapter.setOnVideoClickListener(object : SaloonFocusAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {
                val intent = Intent(mContext, VideoPreviewActivity::class.java)
                intent.putExtra("videoUrl", mTrendList[position].video_url)
                intent.putExtra("name", mTrendList[position].nick)
                startActivity(intent)
            }
        })

        adapter.setOnItemClickListener(object : SaloonFocusAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val intent = Intent(context, DynamicOtherShowActivity::class.java)
                intent.putExtra("trendId", mTrendList[position].id)
                intent.putExtra("userId", mTrendList[position].user_id)
                intent.putExtra("sex", mTrendList[position].user_sex)
                startActivity(intent)
            }
        })

        adapter.setOnAvatarClickListener(object : SaloonFocusAdapter.OnAvatarClickListener {
            override fun onAvatarClick(v: View?, position: Int) {
                ToastUtils.showShort("头像,进入资料详情界面")
            }
        })

        adapter.setOnFocusClickListener(object : SaloonFocusAdapter.OnFocusClickListener {
            override fun onFocusClick(v: View?, position: Int) {
                ToastUtils.showShort("消息，进入消息界面")
            }
        })

        adapter.setOnLikeClickListener(object : SaloonFocusAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {
                ToastUtils.showShort("点赞，给该用户点赞")
            }
        })

        adapter.setOnCommentClickListener(object : SaloonFocusAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {
                ToastUtils.showShort("评论，跳转到动态详情界面，显示评论")
            }
        })

        adapter.setOnOneClickListener(object : SaloonFocusAdapter.OnOneClickListener {
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

        adapter.setOnTwoClickListener(object : SaloonFocusAdapter.OnTwoClickListener {
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

        adapter.setOnThreeClickListener(object : SaloonFocusAdapter.OnThreeClickListener {
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

        adapter.setOnFourClickListener(object : SaloonFocusAdapter.OnFourClickListener {
            override fun onFourClick(v: View?, position: Int) {
                ToastUtils.showShort("four")
                imageIndex = 3

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

        adapter.setOnFiveClickListener(object : SaloonFocusAdapter.OnFiveClickListener {
            override fun onFiveClick(v: View?, position: Int) {
                ToastUtils.showShort("five")
                imageIndex = 4

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

        adapter.setOnSixClickListener(object : SaloonFocusAdapter.OnSixClickListener {
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

        adapter.setOnSevenClickListener(object : SaloonFocusAdapter.OnSevenClickListener {
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

        adapter.setOnEightClickListener(object : SaloonFocusAdapter.OnEightClickListener {
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

        adapter.setOnNineClickListener(object : SaloonFocusAdapter.OnNineClickListener {
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
    private fun getTrendFocus(mode: String, max: Int, min: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.UP_DOWN] = mode
        map[Contents.MAX_ID] = max.toString()
        map[Contents.MIN_ID] = min.toString()
        map[Contents.SIZE] = 10.toString()
        getTrendFocusPresent.getTrendFocus(map)
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

    override fun onDoLikeClickSuccess(likeClickBean: LikeClickBean?) {

    }

    override fun onDoLikeClickError() {

    }

    override fun onDoLikeCancelSuccess(likeCancelBean: LikeCancelBean?) {

    }

    override fun onLikeCancelError() {

    }

    override fun onGetTrendFocusSuccess(trendFocusBean: TrendFocusBean) {

        if (trendFocusBean.data.list.isNotEmpty()) {
            ll_dynamic_focus_empty.visibility = View.GONE

            val mIdList: MutableList<Int> = arrayListOf()

            if (mode == "first") {
                mTrendList.clear()
            }

            mTrendList.clear()
            for (i in 0.until(trendFocusBean.data.list.size)) {
                mTrendList.add(trendFocusBean.data.list[i])
                mIdList.add(trendFocusBean.data.list[i].id)
            }

            max = Collections.max(mIdList)
            min = Collections.min(mIdList)

            adapter.notifyDataSetChanged()

            srl_dynamic_focus_refresh.finishRefresh(true)
            srl_dynamic_focus_refresh.finishLoadMore(true)

        } else {
            srl_dynamic_focus_refresh.finishRefresh(true)
            srl_dynamic_focus_refresh.finishLoadMore(true)
        }

    }

    override fun onGetTrendFocusError() {
        srl_dynamic_focus_refresh.finishRefresh(false)
        srl_dynamic_focus_refresh.finishLoadMore(false)
    }

}