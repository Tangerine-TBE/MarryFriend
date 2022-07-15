package com.twx.module_dynamic.mine

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.annotation.Nullable
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.SPStaticUtils
import com.blankj.utilcode.util.ToastUtils
import com.luck.picture.lib.decoration.WrapContentLinearLayoutManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.twx.module_base.base.MainBaseViewActivity
import com.twx.module_base.constant.Constant
import com.twx.module_base.constant.Contents
import com.twx.module_dynamic.R
import com.twx.module_dynamic.bean.MyTrendsList
import com.twx.module_dynamic.bean.MyTrendsListBean
import com.twx.module_dynamic.mine.adapter.MyDynamicAdapter
import com.twx.module_dynamic.net.callback.IGetMyTrendsListCallback
import com.twx.module_dynamic.net.impl.getMyTrendsListPresentImpl
import com.twx.module_dynamic.preview.image.ImagePreviewActivity
import com.twx.module_dynamic.preview.video.VideoPreviewActivity
import com.twx.module_dynamic.send.DynamicSendActivity
import com.twx.module_dynamic.show.mine.DynamicMineShowActivity
import kotlinx.android.synthetic.main.activity_my_dynamic.*
import java.io.Serializable
import java.util.*


class MyDynamicActivity : MainBaseViewActivity(), IGetMyTrendsListCallback {

    // 大图展示时进入时应该展示点击的那张图片
    private var imageIndex = 0

    private var trendType = 0

    private var currentPaper = 1

    private lateinit var linearLayoutManager: LinearLayoutManager

    private var trendList: MutableList<MyTrendsList> = arrayListOf()

    private lateinit var adapter: MyDynamicAdapter

    private lateinit var getMyTrendsListPresent: getMyTrendsListPresentImpl

    override fun getLayoutView(): Int = R.layout.activity_my_dynamic

    override fun initView() {
        super.initView()

        getMyTrendsListPresent = getMyTrendsListPresentImpl.getsInstance()
        getMyTrendsListPresent.registerCallback(this)

        adapter = MyDynamicAdapter(trendList)
        linearLayoutManager =
            WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_dynamic_mine_container.layoutManager = linearLayoutManager
        rv_dynamic_mine_container.adapter = adapter

        srl_dynamic_mine_refresh.setRefreshHeader(ClassicsHeader(this))
        srl_dynamic_mine_refresh.setRefreshFooter(ClassicsFooter(this))

        initEmojiCompat()

    }


    private fun initEmojiCompat() {
        val config: EmojiCompat.Config
        // Use a downloadable font for EmojiCompat
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs)
        config = FontRequestEmojiCompatConfig(applicationContext, fontRequest)

        config.setReplaceAll(true)
            .registerInitCallback(object : EmojiCompat.InitCallback() {
                override fun onInitialized() {
                    Log.i("guo", "EmojiCompat initialized")
                }

                override fun onFailed(@Nullable throwable: Throwable?) {
                    Log.e("guo", "EmojiCompat initialization failed", throwable)
                }
            })
        EmojiCompat.init(config)
    }

    override fun initLoadData() {
        super.initLoadData()
        getFirstTrendsList()
    }

    override fun initPresent() {
        super.initPresent()
    }

    override fun initEvent() {
        super.initEvent()

        ll_dynamic_mine_all.setOnClickListener {
            currentPaper = 1
            trendType = 0
            getFirstTrendsList()
        }

        ll_dynamic_mine_pic.setOnClickListener {
            currentPaper = 1
            trendType = 1
            getFirstTrendsList()
        }

        ll_dynamic_mine_video.setOnClickListener {
            currentPaper = 1
            trendType = 2
            getFirstTrendsList()
        }

        ll_dynamic_mine_text.setOnClickListener {
            currentPaper = 1
            trendType = 3
            getFirstTrendsList()
        }

        srl_dynamic_mine_refresh.setOnRefreshListener {
            // 刷新数据
            currentPaper = 1
            trendType = 0
            getFirstTrendsList()
            srl_dynamic_mine_refresh.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        }

        srl_dynamic_mine_refresh.setOnLoadMoreListener {
            getMoreTrendsList(currentPaper)
            srl_dynamic_mine_refresh.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        }

        adapter.setOnItemClickListener(object : MyDynamicAdapter.OnItemClickListener {
            override fun onItemClick(v: View?, position: Int) {
                val intent = Intent(this@MyDynamicActivity, DynamicMineShowActivity::class.java)
                intent.putExtra("id", trendList[position].id)
                startActivity(intent)
            }
        })

        adapter.setOnLikeClickListener(object : MyDynamicAdapter.OnLikeClickListener {
            override fun onLikeClick(v: View?, position: Int) {
                ToastUtils.showShort("不能给自己点赞")
            }
        })

        adapter.setOnCommentClickListener(object : MyDynamicAdapter.OnCommentClickListener {
            override fun onCommentClick(v: View?, position: Int) {
                val intent = Intent(this@MyDynamicActivity, DynamicMineShowActivity::class.java)
                intent.putExtra("id", trendList[position].id)
                startActivity(intent)
            }
        })

        adapter.setOnOneClickListener(object : MyDynamicAdapter.OnOneClickListener {
            override fun onOneClick(v: View?, position: Int) {
                imageIndex = 0

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnTwoClickListener(object : MyDynamicAdapter.OnTwoClickListener {
            override fun onTwoClick(v: View?, position: Int) {
                imageIndex = 1

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnThreeClickListener(object : MyDynamicAdapter.OnThreeClickListener {
            override fun onThreeClick(v: View?, position: Int) {
                imageIndex = 2

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnFourClickListener(object : MyDynamicAdapter.OnFourClickListener {
            override fun onFourClick(v: View?, position: Int) {
                imageIndex = 3

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 2
                }

                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnFiveClickListener(object : MyDynamicAdapter.OnFiveClickListener {
            override fun onFiveClick(v: View?, position: Int) {
                imageIndex = 4

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }

                // 图片展示问题，需要调整一下imageIndex

                if (images.size == 4) {
                    imageIndex = 3
                }

                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnSixClickListener(object : MyDynamicAdapter.OnSixClickListener {
            override fun onSixClick(v: View?, position: Int) {
                imageIndex = 5

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnSevenClickListener(object : MyDynamicAdapter.OnSevenClickListener {
            override fun onSevenClick(v: View?, position: Int) {
                imageIndex = 6

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnEightClickListener(object : MyDynamicAdapter.OnEightClickListener {
            override fun onEightClick(v: View?, position: Int) {
                imageIndex = 7

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnNineClickListener(object : MyDynamicAdapter.OnNineClickListener {
            override fun onNineClick(v: View?, position: Int) {
                imageIndex = 8

                val images: MutableList<String> =
                    trendList[position].image_url.split(",") as MutableList<String>
                for (i in 0.until(images.size)) {
                    images[i] = images[i].replace(" ", "")
                }
                val intent = Intent(this@MyDynamicActivity, ImagePreviewActivity::class.java)
                intent.putExtra("imageList", images as Serializable)
                intent.putExtra("imageIndex", imageIndex)
                startActivity(intent)
            }
        })

        adapter.setOnVideoClickListener(object : MyDynamicAdapter.OnVideoClickListener {
            override fun onVideoClick(v: View?, position: Int) {
                val intent = Intent(this@MyDynamicActivity, VideoPreviewActivity::class.java)
                intent.putExtra("videoUrl", trendList[position].video_url)
                intent.putExtra("name", "")
                startActivity(intent)
            }
        })

        iv_dynamic_mine_send.setOnClickListener {
            val intent = Intent(this, DynamicSendActivity::class.java)
            startActivity(intent)
        }

    }


    // 初次加载我的动态列表
    private fun getFirstTrendsList() {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.TRENDS_TYPE] = trendType.toString()
        getMyTrendsListPresent.getMyTrendsList(map, 1, 10)
    }

    // 加载更多我的动态列表
    private fun getMoreTrendsList(currentPaper: Int) {
        val map: MutableMap<String, String> = TreeMap()
        map[Contents.USER_ID] = SPStaticUtils.getString(Constant.USER_ID, "13")
        map[Contents.TRENDS_TYPE] = trendType.toString()
        getMyTrendsListPresent.getMyTrendsList(map, currentPaper, 10)
    }

    override fun onLoading() {

    }

    override fun onError() {

    }

    override fun onGetMyTrendsListSuccess(myTrendsListBean: MyTrendsListBean) {

        if (myTrendsListBean.data.list.isNotEmpty()) {

            if (currentPaper == 1) {
                trendList.clear()
            }

            currentPaper++

            srl_dynamic_mine_refresh.finishRefresh();//传入false表示刷新失败

            when (trendType) {
                0 -> {
                    //  获取全部数据，此时应该更新数据
                    tv_dynamic_mine_all.text = myTrendsListBean.data.total.all.toString()
                    tv_dynamic_mine_pic.text = myTrendsListBean.data.total.image.toString()
                    tv_dynamic_mine_video.text = myTrendsListBean.data.total.video.toString()
                    tv_dynamic_mine_text.text = myTrendsListBean.data.total.wenzi.toString()

                    for (i in 0.until(myTrendsListBean.data.list.size)) {
                        trendList.add(myTrendsListBean.data.list[i])
                    }

                }
                1 -> {
                    for (i in 0.until(myTrendsListBean.data.list.size)) {
                        if (myTrendsListBean.data.list[i].trends_type == 1) {
                            trendList.add(myTrendsListBean.data.list[i])
                        }
                    }
                }
                2 -> {
                    for (i in 0.until(myTrendsListBean.data.list.size)) {
                        if (myTrendsListBean.data.list[i].trends_type == 2) {
                            trendList.add(myTrendsListBean.data.list[i])
                        }
                    }
                }
                3 -> {
                    for (i in 0.until(myTrendsListBean.data.list.size)) {
                        if (myTrendsListBean.data.list[i].trends_type == 3) {
                            trendList.add(myTrendsListBean.data.list[i])
                        }
                    }
                }
            }

            ll_dynamic_mine_empty.visibility = View.GONE
            rv_dynamic_mine_container.visibility = View.VISIBLE

            adapter.notifyDataSetChanged()

        }

        srl_dynamic_mine_refresh.finishRefresh(true)
        srl_dynamic_mine_refresh.finishLoadMore(true)

    }

    override fun onGetMyTrendsListCodeError() {
        srl_dynamic_mine_refresh.finishRefresh(false)
        srl_dynamic_mine_refresh.finishLoadMore(false)
    }

}